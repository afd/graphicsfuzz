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

package com.graphicsfuzz.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.graphicsfuzz.alphanumcomparator.AlphanumComparator;
import com.graphicsfuzz.common.ast.TranslationUnit;
import com.graphicsfuzz.common.glslversion.ShadingLanguageVersion;
import com.graphicsfuzz.common.tool.PrettyPrinterVisitor;
import com.graphicsfuzz.common.tool.UniformValueSupplier;
import com.graphicsfuzz.common.transformreduce.GlslShaderJob;
import com.graphicsfuzz.common.transformreduce.ShaderJob;
import com.graphicsfuzz.util.ExecHelper;
import com.graphicsfuzz.util.ExecResult;
import com.graphicsfuzz.util.ToolHelper;
import com.graphicsfuzz.util.ToolPaths;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaderJobFileOperations {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderJobFileOperations.class);
  public static final String FUZZY_DIFF_KEY = "fuzzydiff";

  public boolean areImagesOfShaderResultsIdentical(
      File referenceShaderResultFile,
      File variantShaderResultFile) throws IOException {

    //noinspection deprecation: OK in this class.
    File reference = getUnderlyingImageFileFromShaderJobResultFile(referenceShaderResultFile);
    //noinspection deprecation: OK in this class.
    File variant = getUnderlyingImageFileFromShaderJobResultFile(variantShaderResultFile);

    LOGGER.info("Comparing: {} and {}.", reference, variant);
    boolean identical = FileUtils.contentEquals(reference, variant);
    LOGGER.info("Identical? {}", identical);

    return identical;
  }

  /**
   * Checks whether the shaders listed in shader job are valid.
   * @param shaderJobFile A shader job to check.
   * @param throwExceptionOnInvalid Request exception if the validation fails
   * @param isVulkan Tell the validator that this is a vulkan target
   * @return true if shaders pass validation.
   */
  public boolean areShadersValid(
      File shaderJobFile,
      boolean throwExceptionOnInvalid,
      boolean isVulkan)
      throws IOException, InterruptedException {
    for (ShaderKind shaderKind : ShaderKind.values()) {
      //noinspection deprecation: OK from within this class.
      final File shaderFile = getUnderlyingShaderFile(shaderJobFile, shaderKind);
      if (shaderFile.isFile() && !shaderIsValid(shaderFile, throwExceptionOnInvalid, isVulkan)) {
        return false;
      }
    }
    return true;
  }

  public boolean areShadersValid(
      File shaderJobFile,
      boolean throwExceptionOnInvalid)
      throws IOException, InterruptedException {
    return areShadersValid(shaderJobFile, throwExceptionOnInvalid, false);
  }

  public boolean areShadersValidShaderTranslator(
      File shaderJobFile,
      boolean throwExceptionOnInvalid)
      throws IOException, InterruptedException {
    for (ShaderKind shaderKind : ShaderKind.values()) {
      //noinspection deprecation: OK from within this class.
      final File shaderFile = getUnderlyingShaderFile(shaderJobFile, shaderKind);
      if (shaderFile.isFile()) {
        final ShadingLanguageVersion shadingLanguageVersion = ShadingLanguageVersion
            .getGlslVersionFromFirstTwoLines(getFirstTwoLinesOfShader(shaderJobFile, shaderKind));
        if (!shaderIsValidShaderTranslator(shaderFile, shadingLanguageVersion,
            throwExceptionOnInvalid)) {
          return false;
        }
      }
    }
    return true;
  }

  public void assertExists(File file) throws FileNotFoundException {
    if (!Files.exists(file.toPath())) {
      throw new FileNotFoundException("Could not find " + file);
    }
  }

  public void assertShaderJobRequiredFilesExist(File shaderJobFile) throws FileNotFoundException {
    assertIsShaderJobFile(shaderJobFile);
    String shaderJobFileNoExtension = FilenameUtils.removeExtension(shaderJobFile.toString());

    assertExists(shaderJobFile);

    boolean shaderExists = false;
    //noinspection ConstantConditions
    shaderExists |= new File(shaderJobFileNoExtension + ".frag").isFile();
    shaderExists |= new File(shaderJobFileNoExtension + ".vert").isFile();
    shaderExists |= new File(shaderJobFileNoExtension + ".comp").isFile();

    if (!shaderExists) {
      throw new FileNotFoundException(
          "Cannot find vertex, fragment or compute shader at "
              + shaderJobFileNoExtension
              + ".[vert/frag/comp]");
    }

  }

  public void copyFile(File srcFile, File destFile, boolean replaceExisting) throws IOException {
    if (replaceExisting) {
      Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } else {
      Files.copy(srcFile.toPath(), destFile.toPath());
    }
  }

  public void copyShaderJobFileTo(
      File shaderJobFileSource,
      File shaderJobFileDest,
      boolean replaceExisting) throws IOException {
    // TODO: Should we only move/copy particular files?
    copyOrMoveShaderJobFileTo(shaderJobFileSource, shaderJobFileDest, replaceExisting, true);
  }

  public void copyShaderJobResultFileTo(
      File shaderResultFileSource,
      File shaderResultFileDest,
      boolean replaceExisting) throws IOException {
    // TODO: Should we only move/copy particular files?
    copyOrMoveShaderJobResultFileTo(
        shaderResultFileSource,
        shaderResultFileDest,
        replaceExisting,
        true);
  }

  public void createFile(File file) throws IOException {
    Files.createFile(file.toPath());
  }

  public void deleteDirectory(File directory) throws IOException {
    FileUtils.deleteDirectory(directory);
  }

  public boolean deleteQuietly(File file) {
    return FileUtils.deleteQuietly(file);
  }

  public void deleteFile(File file) throws IOException {
    Files.delete(file.toPath());
  }

  public void deleteShaderJobFile(File shaderJobFile) throws IOException {
    assertIsShaderJobFile(shaderJobFile);

    // We conservatively delete specific files, instead of deleting all related files
    // returned from getShaderResultFileRelatedFiles().

    String shaderJobFileWithoutExtension = FilenameUtils.removeExtension(shaderJobFile.toString());

    deleteFile(new File(shaderJobFileWithoutExtension + ".json"));
    tryDeleteFile(new File(shaderJobFileWithoutExtension + ".vert"));
    tryDeleteFile(new File(shaderJobFileWithoutExtension + ".frag"));
    tryDeleteFile(new File(shaderJobFileWithoutExtension + ".primitives"));
    tryDeleteFile(new File(shaderJobFileWithoutExtension + ".license"));
    tryDeleteFile(new File(shaderJobFileWithoutExtension + ".prob"));

  }

  public void deleteShaderJobResultFile(File shaderJobResultFile) throws IOException {
    assertIsShaderJobResultFile(shaderJobResultFile);

    // We conservatively delete specific files, instead of deleting all related files
    // returned from getShaderResultFileRelatedFiles().

    String shaderJobResultFileWithoutExtension =
        FileHelper.removeEnd(shaderJobResultFile.toString(), ".info.json");

    deleteFile(new File(shaderJobResultFileWithoutExtension + ".info.json"));
    tryDeleteFile(new File(shaderJobResultFileWithoutExtension + ".png"));
    tryDeleteFile(new File(shaderJobResultFileWithoutExtension + ".txt"));

  }

  public boolean doesShaderExist(File shaderJobFile, ShaderKind shaderKind) {
    //noinspection deprecation: OK from within this class.
    File shaderFile = getUnderlyingShaderFile(shaderJobFile, shaderKind);
    return shaderFile.isFile();
  }

  public boolean doesShaderJobExist(File shaderJobFile) {
    assertIsShaderJobFile(shaderJobFile);
    return shaderJobFile.isFile();
  }

  public boolean doesShaderJobResultFileExist(File shaderJobResultFile) {
    assertIsShaderJobResultFile(shaderJobResultFile);
    return shaderJobResultFile.isFile();
  }

  /**
   * Does this shaderJobResultFile have an associated image result?
   *
   * <p>Perhaps we should be able to check this by reading the result file,
   * not by checking for the presence of a file. But this might be fine actually.
   */
  public boolean doesShaderJobResultFileHaveImage(File shaderJobResultFile) {
    File imageFile = getUnderlyingImageFileFromShaderJobResultFile(shaderJobResultFile);
    return imageFile.isFile();
  }

  /**
   * Check whether the given shaderJob a compute shader job.
   * @param shaderJobFile A shader job to check.
   * @return true if and only if this is a compute shader job.
   */
  public boolean isComputeShaderJob(File shaderJobFile) {
    return new File(FilenameUtils.removeExtension(shaderJobFile.toString()) + ".comp").isFile();
  }

  public long getFileLength(File file) {
    return file.length();
  }

  public String[] getFirstTwoLinesOfShader(File shaderJobFile, ShaderKind shaderKind)
      throws IOException {

    //noinspection deprecation: OK from within this class.
    File shaderFile = getUnderlyingShaderFile(shaderJobFile, shaderKind);

    try (BufferedReader br = new BufferedReader(new FileReader(shaderFile))) {
      final String firstLine = br.readLine();
      final String secondLine = br.readLine();
      return new String[] {firstLine, secondLine};
    }
  }

  public String getShaderContents(
      File shaderJobFile,
      ShaderKind shaderKind) throws IOException {

    //noinspection deprecation: OK from within this class.
    File shaderFile = getUnderlyingShaderFile(shaderJobFile, shaderKind);

    return readFileToString(shaderFile);
  }

  public String getShaderJobFileHash(File shaderJobFile) throws IOException {
    return getMD5(shaderJobFile);
  }

  public long getShaderLength(File shaderJobFile, ShaderKind shaderKind) {
    assertIsShaderJobFile(shaderJobFile);
    //noinspection deprecation: Fine in this class.
    return getUnderlyingShaderFile(shaderJobFile, shaderKind).length();
  }

  public PrintStream getStdOut() {
    return System.out;
  }

  /**
   * Get a shader file associated with a shader job.
   * This method should be avoided because the representation of a shader job may change
   * and any operations performed on the returned File cannot be mocked.
   * @deprecated
   *
   */
  public File getUnderlyingShaderFile(
      File shaderJobFile,
      ShaderKind shaderKind) {
    assertIsShaderJobFile(shaderJobFile);
    String shaderJobFileNoExtension = FilenameUtils.removeExtension(shaderJobFile.toString());
    return new File(shaderJobFileNoExtension + "." + shaderKind.getFileExtension());
  }

  public boolean isDirectory(File file) {
    return file.isDirectory();
  }

  public boolean isFile(File file) {
    return file.isFile();
  }

  public File[] listFiles(File directory, FilenameFilter filter) throws IOException {
    if (!isDirectory(directory)) {
      throw new IOException("Not a directory: " + directory);
    }
    File[] res = directory.listFiles(filter);

    if (res == null) {
      throw new IOException("Failed to enumerate files in " + directory);
    }
    return res;
  }

  /**
   * Files are sorted.
   */
  public File[] listShaderJobFiles(File directory, FilenameFilter filter) throws IOException {
    File[] files =
        listFiles(directory,
            (dir, name) -> name.endsWith(".json") && (filter == null || filter.accept(dir, name)));
    AlphanumComparator comparator = new AlphanumComparator();
    Arrays.sort(files, (o1, o2) -> comparator.compare(o1.toString(), o2.toString()));
    return files;
  }

  /**
   * Files are sorted.
   */
  public File[] listShaderJobFiles(File directory) throws IOException {
    return listShaderJobFiles(directory, null);
  }

  public void mkdir(File directory) throws IOException {
    Files.createDirectories(directory.toPath());
  }

  public void forceMkdir(File outputDir) throws IOException {
    FileUtils.forceMkdir(outputDir);
  }

  public void moveFile(File srcFile, File destFile, boolean replaceExisting) throws IOException {
    if (replaceExisting) {
      Files.move(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } else {
      Files.move(srcFile.toPath(), destFile.toPath());
    }
  }

  public void moveShaderJobFileTo(
      File shaderJobFileSource,
      File shaderJobFileDest,
      boolean replaceExisting) throws IOException {
    // TODO: Should we only move/copy particular files?
    copyOrMoveShaderJobFileTo(shaderJobFileSource, shaderJobFileDest, replaceExisting, false);
  }

  public void moveShaderJobResultFileTo(
      File shaderResultFileSource,
      File shaderResultFileDest,
      boolean replaceExisting) throws IOException {
    // TODO: Should we only move/copy particular files?
    copyOrMoveShaderJobResultFileTo(
        shaderResultFileSource,
        shaderResultFileDest,
        replaceExisting,
        false);
  }

  public byte[] readFileToByteArray(File file) throws IOException {
    return FileUtils.readFileToByteArray(file);
  }

  public String readFileToString(File file) throws IOException {
    return FileUtils.readFileToString(file, Charset.defaultCharset());
  }

  public List<String> readLines(File file) throws IOException {
    return FileUtils.readLines(file, Charset.defaultCharset());
  }

  public ShaderJob readShaderJobFile(File shaderJobFile)
      throws IOException, ParseTimeoutException, InterruptedException, GlslParserException {

    assertIsShaderJobFile(shaderJobFile);

    final String shaderJobFilePrefix = FilenameUtils.removeExtension(shaderJobFile.toString());

    final List<TranslationUnit> translationUnits = new ArrayList<>();

    for (String extension : Arrays.asList("vert", "frag", "comp")) {
      final Optional<TranslationUnit> maybeTu =
          ParseHelper.maybeParseShader(new File(shaderJobFilePrefix + "." + extension));
      maybeTu.ifPresent(translationUnits::add);
    }

    final File licenseFile = new File(shaderJobFilePrefix + ".license");

    return new GlslShaderJob(
        licenseFile.exists()
            ? Optional.of(FileUtils.readFileToString(licenseFile, Charset.defaultCharset()))
            : Optional.empty(),
        new PipelineInfo(shaderJobFile),
        translationUnits);
  }

  public void tryDeleteFile(File file) {
    FileUtils.deleteQuietly(file);
  }

  /**
   * Output a file alongside a shader job file.
   * This method should be avoided, but is useful for now.
   *
   * @param outputFileExtension E.g. ".prob"
   * @deprecated Should probably be avoided, but useful for now.
   */
  public void writeAdditionalInfo(
      File shaderJobFile,
      String outputFileExtension,
      String outputContents) throws FileNotFoundException {
    assertIsShaderJobFile(shaderJobFile);

    String outputFileNoExtension = FilenameUtils.removeExtension(shaderJobFile.toString());
    try (PrintStream stream = ps(new File(outputFileNoExtension + outputFileExtension))) {
      stream.println(outputContents);
    }
  }

  public void writeByteArrayToFile(File file, byte[] contents) throws IOException {
    FileUtils.writeByteArrayToFile(file, contents);
  }

  public void writeShaderJobFile(
      final ShaderJob shaderJob,
      final File outputShaderJobFile) throws FileNotFoundException {
    writeShaderJobFile(shaderJob, outputShaderJobFile, Optional.empty());
  }

  public void writeShaderJobFile(
      final ShaderJob shaderJob,
      final File outputShaderJobFile,
      final Optional<UniformValueSupplier> uniformValues) throws FileNotFoundException {

    assertIsShaderJobFile(outputShaderJobFile);

    String outputFileNoExtension = FilenameUtils.removeExtension(outputShaderJobFile.toString());

    for (TranslationUnit tu : shaderJob.getShaders()) {
      writeShader(
          tu,
          shaderJob.getLicense(),
          new File(outputFileNoExtension + "." + tu.getShaderKind().getFileExtension()),
          uniformValues
      );
    }

    //noinspection deprecation: OK for use inside this class.
    writeAdditionalInfo(
        outputShaderJobFile,
        ".json",
        shaderJob.getPipelineInfo().toString());
  }

  public void writeStringToFile(File file, String contents) throws IOException {
    FileUtils.writeStringToFile(file, contents, Charset.defaultCharset());
  }

  /**
   * Provides an in-memory representation of the image associated with a shader job result.
   * Assumes that an image file is present as part of the shader job result.
   * @param shaderJobResultFile The shader job for which a result image is to be processed.
   * @return In-memory representation of image.
   * @throws IOException on absence of an image file or failing to read the file.
   */
  public BufferedImage getBufferedImageFromShaderJobResultFile(File shaderJobResultFile)
      throws IOException {
    return ImageIO.read(getUnderlyingImageFileFromShaderJobResultFile(shaderJobResultFile));
  }

  private static void assertIsShaderJobFile(File shaderJobFile) {
    if (!shaderJobFile.getName().endsWith(".json")
        || shaderJobFile.getName().endsWith(".info.json")) {
      throw new IllegalArgumentException(
          "shaderJobFile: must be a .json file (and not .info.json):" + shaderJobFile);
    }
  }

  private static void assertIsShaderJobResultFile(File shaderJobResultFile) {
    if (!shaderJobResultFile.getName().endsWith(".info.json")) {
      throw new IllegalArgumentException(
          "shaderJobResultFile: must be a .info.json file" + shaderJobResultFile);
    }
  }

  private static File getParent(File file) {
    File resultDir = file.getParentFile();
    if (resultDir == null) {
      resultDir = new File(".");
    }
    return resultDir;
  }

  private static PrintStream ps(File file) throws FileNotFoundException {
    return new PrintStream(new FileOutputStream(file));
  }

  private static void writeShader(
      TranslationUnit tu,
      Optional<String> license,
      File outputFile,
      Optional<UniformValueSupplier> uniformValues
  ) throws FileNotFoundException {
    try (PrintStream stream = ps(outputFile)) {
      PrettyPrinterVisitor.emitShader(
          tu,
          license,
          stream,
          PrettyPrinterVisitor.DEFAULT_INDENTATION_WIDTH,
          PrettyPrinterVisitor.DEFAULT_NEWLINE_SUPPLIER,
          uniformValues
      );
    }
  }

  private void assertImagesExist(File shaderJobResultFile) throws FileNotFoundException {
    assertIsShaderJobFile(shaderJobResultFile);
    String fileNoExtension = FileHelper.removeEnd(shaderJobResultFile.toString(), ".info.json");
    File imageFile = new File(fileNoExtension + ".png");
    if (!isFile(imageFile)) {
      throw new FileNotFoundException(
          "Could not find image file "
              + imageFile
              + " for shader result file "
              + shaderJobResultFile);
    }
  }

  private void copyOrMoveShaderJobFileTo(
      File shaderJobFileSource,
      File shaderJobFileDest,
      boolean replaceExisting,
      boolean copy) throws IOException {
    // TODO: Should we only move/copy particular files?
    assertIsShaderJobFile(shaderJobFileSource);
    assertIsShaderJobFile(shaderJobFileDest);

    // Copy:
    //
    // x.json
    // x.frag
    // x.vert
    //
    // to:
    //
    // y.json
    // y.frag
    // y.vert

    // Calculate 'y'.
    String sourceNameNoExtension =
        FileHelper.removeEnd(shaderJobFileSource.getName(), ".json");
    String destNameNoExtension =
        FileHelper.removeEnd(shaderJobFileDest.getName(), ".json");

    File destDir = getParent(shaderJobFileDest);

    for (File file : getShaderJobFileRelatedFiles(shaderJobFileSource)) {
      // New name is "y" + ("x.blah" / "x")
      final String newName =
          destNameNoExtension
              + FileHelper.removeStart(file.getName(), sourceNameNoExtension);
      final File destFile = new File(destDir, newName);
      if (copy) {
        copyFile(file, destFile, replaceExisting);
      } else {
        moveFile(file, destFile, replaceExisting);
      }
    }
  }

  private void copyOrMoveShaderJobResultFileTo(
      File shaderResultFileSource,
      File shaderResultFileDest,
      boolean replaceExisting,
      boolean copy) throws IOException {
    // TODO: Should we only move/copy particular files?
    assertIsShaderJobResultFile(shaderResultFileSource);
    assertIsShaderJobResultFile(shaderResultFileDest);

    // Copy:
    //
    // x.info.json
    // x.png
    // x.txt
    //
    // to:
    //
    // y.info.json
    // y.png
    // y.txt

    // Calculate 'y'.
    String sourceNameNoExtension =
        FileHelper.removeEnd(shaderResultFileSource.getName(), ".info.json");
    String destNameNoExtension =
        FileHelper.removeEnd(shaderResultFileDest.getName(), ".info.json");

    File destDir = getParent(shaderResultFileDest);

    // For each x.*
    for (File file : getShaderResultFileRelatedFiles(shaderResultFileSource)) {

      // New name is "y" + ("x.blah" / "x")
      final String newName =
          destNameNoExtension
              + FileHelper.removeStart(file.getName(), sourceNameNoExtension);
      final File destFile = new File(destDir, newName);
      if (copy) {
        copyFile(file, destFile, replaceExisting);
      } else {
        moveFile(file, destFile, replaceExisting);
      }
    }
  }

  private String getMD5(File shaderJobFile) throws IOException {
    assertIsShaderJobFile(shaderJobFile);

    String fileNoExtension = FilenameUtils.removeExtension(shaderJobFile.toString());
    final File vertexShaderFile = new File(fileNoExtension + ".vert");
    final File fragmentShaderFile = new File(fileNoExtension + ".frag");
    final File computeShaderFile = new File(fileNoExtension + ".comp");

    if (!isFile(vertexShaderFile) && !isFile(fragmentShaderFile) && !isFile(computeShaderFile)) {
      throw new IllegalStateException("No frag, vert or comp shader found for " + shaderJobFile);
    }

    byte[] vertexData = isFile(vertexShaderFile)
        ? readFileToByteArray(vertexShaderFile)
        : new byte[0];
    byte[] fragmentData = isFile(fragmentShaderFile)
        ? readFileToByteArray(fragmentShaderFile)
        : new byte[0];
    byte[] computeData = isFile(computeShaderFile)
        ? readFileToByteArray(computeShaderFile)
        : new byte[0];
    //  This metadata is required in order to distinguish between shader jobs
    //  with identical shaders but different pipeline information.
    byte[] metaData = isFile(shaderJobFile)
        ? readFileToByteArray(shaderJobFile)
        : new byte[0];
    byte[] combinedData =
        new byte[vertexData.length + fragmentData.length + computeData.length + metaData.length ];
    System.arraycopy(
        vertexData,
        0,
        combinedData,
        0,
        vertexData.length);
    System.arraycopy(
        fragmentData,
        0,
        combinedData,
        vertexData.length,
        fragmentData.length);
    System.arraycopy(
        computeData,
        0,
        combinedData,
        vertexData.length + fragmentData.length,
        computeData.length);
    System.arraycopy(
        metaData,
        0,
        combinedData,
        vertexData.length + fragmentData.length + computeData.length,
        metaData.length);
    return DigestUtils.md5Hex(combinedData);
  }

  private List<Double> getPointsFromJson(JsonObject json, String key) {
    final List<Double> result = new ArrayList<>();
    final JsonArray points = json.get(key).getAsJsonArray();
    for (int i = 0; i < points.size(); i++) {
      result.add(points.get(i).getAsDouble());
    }
    return result;
  }

  private File[] getShaderJobFileRelatedFiles(File shaderJobFile) throws IOException {
    assertIsShaderJobFile(shaderJobFile);
    assertExists(shaderJobFile);

    String fileNoExtension =
        FilenameUtils.removeExtension(shaderJobFile.toString());

    File[] relatedFiles =
        Stream.of(".json", ".vert", ".frag", ".comp", ".primitives", ".prob", ".license")
            .map(ext -> new File(fileNoExtension + ext))
            .filter(this::isFile)
            .toArray(File[]::new);

    return relatedFiles;
  }

  private File[] getShaderResultFileRelatedFiles(File shaderResultFile) throws IOException {
    assertIsShaderJobResultFile(shaderResultFile);
    assertExists(shaderResultFile);

    String fileNoExtension =
        FileHelper.removeEnd(shaderResultFile.toString(), ".info.json");

    File[] relatedFiles =
        Stream.of(".info.json", ".txt", ".png")
            .map(ext -> new File(fileNoExtension + ext))
            .filter(this::isFile)
            .toArray(File[]::new);

    return relatedFiles;
  }

  private File getUnderlyingImageFileFromShaderJobResultFile(File shaderJobResultFile) {
    assertIsShaderJobResultFile(shaderJobResultFile);
    String shaderJobFileWithoutExtension =
        FileHelper.removeEnd(shaderJobResultFile.toString(), ".info.json");
    return new File(shaderJobFileWithoutExtension + ".png");
  }

  private boolean shaderIsValid(
      File shaderFile,
      boolean throwExceptionOnValidationError,
      boolean isVulkan)
      throws IOException, InterruptedException {
    return checkValidationResult(ToolHelper.runValidatorOnShader(ExecHelper.RedirectType.TO_BUFFER,
        shaderFile, isVulkan), shaderFile.getName(), throwExceptionOnValidationError);
  }

  private boolean shaderIsValidShaderTranslator(
      File shaderFile,
      ShadingLanguageVersion shadingLanguageVersion,
      boolean throwExceptionOnValidationError)
      throws IOException, InterruptedException {
    if (!ShaderTranslatorShadingLanguageVersionSupport.isVersionSupported(shadingLanguageVersion)) {
      // Shader translator does not support this shading language version, so just say that the
      // shader is valid.
      return true;
    }
    final ExecResult shaderTranslatorResult = ToolHelper.runShaderTranslatorOnShader(
        ExecHelper.RedirectType.TO_BUFFER,
        shaderFile,
        ShaderTranslatorShadingLanguageVersionSupport
            .getShaderTranslatorArgument(shadingLanguageVersion));
    return checkValidationResult(
        shaderTranslatorResult,
        shaderFile.getName(),
        throwExceptionOnValidationError);
  }

  private boolean checkValidationResult(ExecResult res,
                                        String filename, boolean throwExceptionOnValidationError) {
    if (res.res != 0) {
      LOGGER.warn("Shader {} failed to validate.  Validator stdout: " + res.stdout + ".  "
              + "Validator stderr: " + res.stderr,
          filename);
      if (throwExceptionOnValidationError) {
        throw new RuntimeException("Validation failed.");
      }
      return false;
    }
    return true;
  }

  /**
   * Runs a GraphicsFuzz Python driver script, from the python/drivers directory.
   * @param redirectType Determines where output is redirected to.
   * @param directory Working directory; set to null if current directory is fine.
   * @param driverName Name of the Python driver, with no extension.
   * @param driverArgs Arguments to be passed to the Python driver.
   * @return the result of executing the Python driver.
   * @throws IOException if something IO-related goes wrong.
   * @throws InterruptedException if something goes wrong running the driver command.
   */
  public ExecResult runPythonDriver(ExecHelper.RedirectType redirectType, File directory,
                                      String driverName, String... driverArgs) throws IOException,
      InterruptedException {
    final String[] execArgs = new String[driverArgs.length + 1];
    execArgs[0] = Paths.get(ToolPaths.getPythonDriversDir(), driverName).toString()
        + (System.getProperty("os.name").startsWith("Windows") ? ".bat" : "");
    System.arraycopy(driverArgs, 0, execArgs, 1, driverArgs.length);
    return new ExecHelper().exec(redirectType,
        directory, false,
        execArgs);
  }

  /**
   * Stores data about an image; right now its file and histogram.
   * Could be extended in due course with e.g. PSNR
   */
  private static final class ImageData {

    public final File imageFile;
    private final opencv_core.Mat imageMat;
    private final opencv_core.Mat histogram;

    public ImageData(File imageFile) throws FileNotFoundException {
      this.imageFile = imageFile;
      this.imageMat = opencv_imgcodecs.imread(imageFile.getAbsolutePath());
      opencv_imgproc.cvtColor(this.imageMat, this.imageMat, opencv_imgproc.COLOR_BGR2HSV);
      this.histogram = ImageUtil.getHistogram(imageFile.getAbsolutePath());
    }

    public ImageData(String imageFileName) throws FileNotFoundException {
      this(new File(imageFileName));
    }

    public void getImageDiffStats(
        ImageData other,
        JsonObject metrics) throws IOException {

      metrics.addProperty(
          "histogramDistance",
          ImageUtil.compareHistograms(this.histogram, other.histogram));

      metrics.addProperty(
          "psnr",
          opencv_core.PSNR(this.imageMat, other.imageMat));

      FuzzyImageComparison.MainResult fuzzyDiffResult;
      try {
        fuzzyDiffResult = FuzzyImageComparison.mainHelper(
            new String[] {imageFile.toString(), other.imageFile.toString()});
      } catch (ArgumentParserException exception) {
        throw new RuntimeException(exception);
      }

      JsonElement fuzzDiffResultJson = new Gson().toJsonTree(fuzzyDiffResult);
      metrics.add(FUZZY_DIFF_KEY, fuzzDiffResultJson);
    }

  }

}
