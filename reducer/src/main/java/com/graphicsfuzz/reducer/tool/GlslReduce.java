/*
 * Copyright 2018 The GraphicsFuzz Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.graphicsfuzz.reducer.tool;

import com.graphicsfuzz.common.glslversion.ShadingLanguageVersion;
import com.graphicsfuzz.common.transformreduce.ShaderJob;
import com.graphicsfuzz.common.util.GlslParserException;
import com.graphicsfuzz.common.util.IRandom;
import com.graphicsfuzz.common.util.IdGenerator;
import com.graphicsfuzz.common.util.ParseTimeoutException;
import com.graphicsfuzz.common.util.RandomWrapper;
import com.graphicsfuzz.common.util.ReductionProgressHelper;
import com.graphicsfuzz.common.util.ShaderJobFileOperations;
import com.graphicsfuzz.common.util.ShaderKind;
import com.graphicsfuzz.reducer.IFileJudge;
import com.graphicsfuzz.reducer.ReductionDriver;
import com.graphicsfuzz.reducer.filejudge.CustomFileJudge;
import com.graphicsfuzz.reducer.reductionopportunities.ReducerContext;
import com.graphicsfuzz.util.ArgsUtil;
import com.graphicsfuzz.util.Constants;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlslReduce {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlslReduce.class);

  private static ArgumentParser getParser() {

    ArgumentParser parser = ArgumentParsers.newArgumentParser("glsl-reduce")
        .defaultHelp(true)
        .description("glsl-reduce takes a shader job `SHADER_JOB.json` "
            + "(a .json file alongside shader files with the same name, such as SHADER_JOB.frag "
            + "and/or SHADER_JOB.vert or SHADER_JOB.comp), "
            + "as well as further arguments or options to specify the interestingness test. "
            + "glsl-reduce will try to simplify the shaders to smaller, simpler shaders that are "
            + "still deemed \"interesting\".");

    // Required arguments
    parser.addArgument("shader-job")
          .help("Path of shader job to be reduced.  E.g. /path/to/shaderjob.json ")
          .type(File.class);

    // Optional positional argument
    parser.addArgument("interestingness-test")
        .help("A command to execute (plus any fixed arguments) to determine "
            + "whether a shader job is interesting.  The command will typically compile and/or "
            + "run the shader job and check some property.  An exit status of 0 indicates that the "
            + "shader job is interesting.  The shader job will be passed as an argument "
            + "(after any fixed arguments). Only allowed (and then also required) when performing "
            + "a custom reduction, which is the default. Use -- to ensure all command line "
            + "arguments that follow are parsed as positional arguments. "
            + "E.g.\n"
            + "glsl-reduce --preserve-semantics -- shader_job.json is-interesting --run-on-android")
        .nargs("*")
        .type(String.class);

    parser.addArgument("--output")
        .help("Directory to which reduction intermediate and final results will be written.")
        .setDefault(new File("."))
        .type(File.class);

    parser.addArgument("--preserve-semantics")
        .help("Only perform semantics-preserving reductions.")
        .action(Arguments.storeTrue());

    parser.addArgument("--max-steps")
        .help(
            "The maximum number of reduction steps to take before giving up and outputting the "
                + "final reduced file.")
        .setDefault(2000)
        .type(Integer.class);

    parser.addArgument("--verbose")
        .help("Emit detailed information related to the reduction process.")
        .action(Arguments.storeTrue());

    parser.addArgument("--seed")
        .help("Seed (unsigned 64 bit long integer) with which to initialize the random number "
            + "generator that is used to control reduction decisions.")
        .type(String.class);

    parser.addArgument("--timeout")
        .help(
            "Time in seconds after which checking interestingness of a shader job is aborted.")
        .setDefault(30)
        .type(Integer.class);

    parser.addArgument("--reference")
          .help("Path to reference .info.json result (with image result) for comparison.")
          .type(File.class);

    parser.addArgument("--stop-on-error")
          .help("Quit if something goes wrong during reduction; useful for testing.")
          .action(Arguments.storeTrue());

    parser.addArgument("--continue-previous-reduction")
          .help("Carry on from where a previous reduction attempt left off.  Requires the "
              + "temporary files written by the previous reduction to be intact, including the "
              + "presence of a " + Constants.REDUCTION_INCOMPLETE + " file.")
          .action(Arguments.storeTrue());

    parser.addArgument("--literals-to-uniforms")
          .help("A special mode where the only \"reduction\" performed is replacing literal "
              + "numbers with uniforms.")
          .action(Arguments.storeTrue());

    parser.addArgument("--no-ub-guards")
        .help("Do not emit guards against undefined behaviour (such as loop limiters and array "
            + "bounds clamping).")
        .action(Arguments.storeTrue());

    return parser;

  }

  public static void main(String[] args) {
    try {
      mainHelper(args);
    } catch (ArgumentParserException exception) {
      exception.getParser().handleError(exception);
      System.exit(1);
    } catch (Throwable ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  public static void mainHelper(
        String[] args)
      throws ArgumentParserException, IOException, ParseTimeoutException, InterruptedException,
      GlslParserException {

    ArgumentParser parser = getParser();

    Namespace ns = parser.parseArgs(args);

    final File workDir = ns.get("output");

    ShaderJobFileOperations fileOps = new ShaderJobFileOperations();

    // Create output dir
    fileOps.mkdir(workDir);

    final File inputShaderJobFile = ns.get("shader_job");

    String shaderJobShortName = FilenameUtils.removeExtension(inputShaderJobFile.getName());

    try {
      // TODO: integrate timeout into reducer
      @SuppressWarnings("unused") Integer timeout = ns.get("timeout");
      final Integer maxSteps = ns.get("max_steps");
      final Integer retryLimit = ns.get("retry_limit");
      final Boolean verbose = ns.get("verbose");
      final IRandom random = new RandomWrapper(ArgsUtil.getSeedArgument(ns));
      final boolean reduceEverywhere = !ns.getBoolean("preserve_semantics");
      final boolean addUbGuards = !ns.getBoolean("no_ub_guards");
      final boolean stopOnError = ns.get("stop_on_error");

      final boolean usingSwiftshader = ns.get("swiftshader");

      final boolean continuePreviousReduction = ns.get("continue_previous_reduction");

      final boolean literalsToUniforms = ns.get("literals_to_uniforms");

      final File referenceResultFile = ns.get("reference");

      final List<String> customJudgeScript = ns.get("interestingness_test");

      if (customJudgeScript.isEmpty()) {
        throw new RuntimeException("A custom reduction requires an interestingness test to be "
            + "specified.");
      }

      // Sanity check that the custom judge script is executable. However, don't fail if the
      // script can't be found, as the script/tool could be on the PATH.
      File scriptFile = new File(customJudgeScript.get(0));
      if (scriptFile.exists() && !scriptFile.canExecute()) {
        throw new RuntimeException("Custom judge script must be executable.");
      }

      // Check input files
      fileOps.assertShaderJobRequiredFilesExist(inputShaderJobFile);
      if (referenceResultFile != null) {
        fileOps.doesShaderJobResultFileExist(referenceResultFile);
      }

      if (continuePreviousReduction) {
        fileOps.assertExists(new File(workDir, Constants.REDUCTION_INCOMPLETE));
      }

      // Copy input files to output dir.
      File copiedShaderJobFile = new File(workDir, inputShaderJobFile.getName());
      fileOps.copyShaderJobFileTo(inputShaderJobFile, copiedShaderJobFile, true);

      File referenceResultFileCopy = null;
      if (referenceResultFile != null) {
        referenceResultFileCopy = new File(workDir, "reference_image.info.json");
        fileOps.copyShaderJobResultFileTo(referenceResultFile, referenceResultFileCopy, true);
      }

      IFileJudge fileJudge;

      File corpus = new File(workDir, "corpus");

      fileJudge =
          new CustomFileJudge(customJudgeScript);

      doReductionHelper(
          inputShaderJobFile,
          shaderJobShortName,
          random,
          fileJudge,
          workDir,
          maxSteps,
          reduceEverywhere,
          addUbGuards,
          continuePreviousReduction,
          literalsToUniforms,
          verbose,
          fileOps);

    }          catch (Throwable throwable) {

      final File exceptionFile =
          ReductionProgressHelper.getReductionExceptionFile(workDir, shaderJobShortName);

      fileOps.writeStringToFile(
          exceptionFile,
          ExceptionUtils.getStackTrace(throwable)
      );

      throw throwable;
    }
  }

  public static void doReductionHelper(
      File initialShaderJobFile,
      String outputShortName,
      IRandom random,
      IFileJudge fileJudge,
      File workDir,
      int stepLimit,
      boolean reduceEverywhere,
      boolean addUbGuards,
      boolean continuePreviousReduction,
      boolean literalsToUniforms,
      boolean verbose,
      ShaderJobFileOperations fileOps)
      throws IOException, ParseTimeoutException, InterruptedException, GlslParserException {
    final ShadingLanguageVersion shadingLanguageVersion =
        getGlslVersionForShaderJob(initialShaderJobFile, fileOps);
    final IdGenerator idGenerator = new IdGenerator();

    final int fileCountOffset = getFileCountOffset(
        workDir,
        outputShortName,
        continuePreviousReduction,
        fileOps);
    final String startingShaderJobShortName = getStartingShaderJobShortName(
        workDir,
        outputShortName,
        continuePreviousReduction,
        fileOps);

    if (continuePreviousReduction) {
      fileOps.assertExists(new File(workDir, Constants.REDUCTION_INCOMPLETE));
      fileOps.deleteFile(new File(workDir, Constants.REDUCTION_INCOMPLETE));
    }

    final File shaderJobFile = new File(workDir, startingShaderJobShortName + ".json");
    final ShaderJob initialState =
        fileOps.readShaderJobFile(
            shaderJobFile
        );

    new ReductionDriver(
        new ReducerContext(
            reduceEverywhere,
            addUbGuards,
            shadingLanguageVersion,
            random,
            idGenerator),
        verbose,
        fileOps,
        fileJudge,
        workDir,
        literalsToUniforms)
        .doReduction(
            initialState,
            outputShortName,
            fileCountOffset,
            stepLimit);
  }

  private static ShadingLanguageVersion getGlslVersionForShaderJob(
      File shaderFileJob,
      ShaderJobFileOperations fileOps)
      throws IOException {

    if (fileOps.doesShaderExist(shaderFileJob, ShaderKind.VERTEX)) {
      return ShadingLanguageVersion.getGlslVersionFromFirstTwoLines(
          fileOps.getFirstTwoLinesOfShader(shaderFileJob, ShaderKind.VERTEX));
    }
    if (fileOps.doesShaderExist(shaderFileJob, ShaderKind.FRAGMENT)) {
      return ShadingLanguageVersion.getGlslVersionFromFirstTwoLines(
          fileOps.getFirstTwoLinesOfShader(shaderFileJob, ShaderKind.FRAGMENT));
    }
    if (fileOps.doesShaderExist(shaderFileJob, ShaderKind.COMPUTE)) {
      return ShadingLanguageVersion.getGlslVersionFromFirstTwoLines(
          fileOps.getFirstTwoLinesOfShader(shaderFileJob, ShaderKind.COMPUTE));
    }
    throw new RuntimeException("Shader version not specified in any shader associated with"
        + "shader job " + shaderFileJob.getName());
  }

  private static String getStartingShaderJobShortName(
      File workDir,
      String shaderJobShortName,
      boolean continuePreviousReduction,
      ShaderJobFileOperations fileOps) throws IOException {

    if (!continuePreviousReduction) {
      return shaderJobShortName;
    }
    final int latestSuccessfulReduction =
        ReductionProgressHelper
            .getLatestReductionStepSuccess(workDir, shaderJobShortName, fileOps)
            .orElse(0);
    if (latestSuccessfulReduction == 0) {
      return shaderJobShortName;
    }
    return ReductionDriver.getReductionStepShaderJobShortName(
        shaderJobShortName,
        latestSuccessfulReduction, Optional.of("success"));
  }

  private static int getFileCountOffset(File workDir, String shaderJobShortName,
        boolean continuePreviousReduction, ShaderJobFileOperations fileOps) throws IOException {
    if (!continuePreviousReduction) {
      return 0;
    }
    return ReductionProgressHelper.getLatestReductionStepAny(
        workDir,
        shaderJobShortName,
        fileOps).orElse(0);
  }

  private static void throwExceptionForCustomReduction(String option) {
    throw new RuntimeException("The '--" + option + "' option is not compatible with a custom "
        + "reduction; details of judgement should all be captured in the interestingness test.");
  }

}
