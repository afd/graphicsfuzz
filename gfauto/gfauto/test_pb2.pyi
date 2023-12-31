"""
@generated by mypy-protobuf.  Do not edit manually!
isort:skip_file
Copyright 2019 The GraphicsFuzz Project Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""
import builtins
import collections.abc
import gfauto.common_pb2
import gfauto.device_pb2
import google.protobuf.descriptor
import google.protobuf.internal.containers
import google.protobuf.message
import sys

if sys.version_info >= (3, 8):
    import typing as typing_extensions
else:
    import typing_extensions

DESCRIPTOR: google.protobuf.descriptor.FileDescriptor

@typing_extensions.final
class Test(google.protobuf.message.Message):
    """A test directory (test_dir) contains a Test proto in "test.json", plus the reference and variant shader jobs.
    A Test proto contains all the information needed to execute a test on a specific device, plus the crash signature
    for detecting if the result is interesting (i.e. the bug reproduces).
    """

    DESCRIPTOR: google.protobuf.descriptor.Descriptor

    GLSL_FIELD_NUMBER: builtins.int
    SPIRV_FUZZ_FIELD_NUMBER: builtins.int
    CRASH_SIGNATURE_FIELD_NUMBER: builtins.int
    DEVICE_FIELD_NUMBER: builtins.int
    BINARIES_FIELD_NUMBER: builtins.int
    EXPECTED_STATUS_FIELD_NUMBER: builtins.int
    CRASH_REGEX_OVERRIDE_FIELD_NUMBER: builtins.int
    SKIP_VALIDATION_FIELD_NUMBER: builtins.int
    DERIVED_FROM_FIELD_NUMBER: builtins.int
    COMMON_SPIRV_ARGS_FIELD_NUMBER: builtins.int
    @property
    def glsl(self) -> global___TestGlsl: ...
    @property
    def spirv_fuzz(self) -> global___TestSpirvFuzz: ...
    crash_signature: builtins.str
    @property
    def device(self) -> gfauto.device_pb2.Device: ...
    @property
    def binaries(self) -> google.protobuf.internal.containers.RepeatedCompositeFieldContainer[gfauto.common_pb2.Binary]: ...
    expected_status: builtins.str
    """The expected status when running the test. E.g. CRASH, TOOL_CRASH, TIMEOUT. See fuzz.py."""
    crash_regex_override: builtins.str
    """If set, this Python regular expression will be used instead of the crash_signature. The regular expression
    will be matched against the log from running the test. E.g. ".*some_error_string.*"
    This is useful for nondeterministic bugs.
    """
    skip_validation: builtins.bool
    """If true, don't run spirv-val to validate SPIR-V at every stage. This can be useful if the validator rejects the
    SPIR-V you want to test, but you want to continue anyway e.g. because the validator is buggy or because you know
    the tool under test should handle the invalid SPIR-V.
    """
    derived_from: builtins.str
    """If set, this indicates the source of the test. Most often this will be set to a shader name from the shaders
    included with GraphicsFuzz that was used as the reference shader. For example, "colorgrid_modulo".
    """
    @property
    def common_spirv_args(self) -> google.protobuf.internal.containers.RepeatedScalarFieldContainer[builtins.str]:
        """These arguments will be passed to any tool that accepts common spirv-val arguments. Supported arguments include:
        --scalar-block-layout, --skip-block-layout, etc. Tools include: spirv-val, spirv-opt, spirv-fuzz, and spirv-reduce.
        This field is typically used to relax SPIR-V validation.
        """
    def __init__(
        self,
        *,
        glsl: global___TestGlsl | None = ...,
        spirv_fuzz: global___TestSpirvFuzz | None = ...,
        crash_signature: builtins.str = ...,
        device: gfauto.device_pb2.Device | None = ...,
        binaries: collections.abc.Iterable[gfauto.common_pb2.Binary] | None = ...,
        expected_status: builtins.str = ...,
        crash_regex_override: builtins.str = ...,
        skip_validation: builtins.bool = ...,
        derived_from: builtins.str = ...,
        common_spirv_args: collections.abc.Iterable[builtins.str] | None = ...,
    ) -> None: ...
    def HasField(self, field_name: typing_extensions.Literal["device", b"device", "glsl", b"glsl", "spirv_fuzz", b"spirv_fuzz", "test", b"test"]) -> builtins.bool: ...
    def ClearField(self, field_name: typing_extensions.Literal["binaries", b"binaries", "common_spirv_args", b"common_spirv_args", "crash_regex_override", b"crash_regex_override", "crash_signature", b"crash_signature", "derived_from", b"derived_from", "device", b"device", "expected_status", b"expected_status", "glsl", b"glsl", "skip_validation", b"skip_validation", "spirv_fuzz", b"spirv_fuzz", "test", b"test"]) -> None: ...
    def WhichOneof(self, oneof_group: typing_extensions.Literal["test", b"test"]) -> typing_extensions.Literal["glsl", "spirv_fuzz"] | None: ...

global___Test = Test

@typing_extensions.final
class TestGlsl(google.protobuf.message.Message):
    DESCRIPTOR: google.protobuf.descriptor.Descriptor

    SPIRV_OPT_ARGS_FIELD_NUMBER: builtins.int
    @property
    def spirv_opt_args(self) -> google.protobuf.internal.containers.RepeatedScalarFieldContainer[builtins.str]: ...
    def __init__(
        self,
        *,
        spirv_opt_args: collections.abc.Iterable[builtins.str] | None = ...,
    ) -> None: ...
    def ClearField(self, field_name: typing_extensions.Literal["spirv_opt_args", b"spirv_opt_args"]) -> None: ...

global___TestGlsl = TestGlsl

@typing_extensions.final
class TestSpirvFuzz(google.protobuf.message.Message):
    """Spirv-fuzz generated spirv test."""

    DESCRIPTOR: google.protobuf.descriptor.Descriptor

    SPIRV_OPT_ARGS_FIELD_NUMBER: builtins.int
    @property
    def spirv_opt_args(self) -> google.protobuf.internal.containers.RepeatedScalarFieldContainer[builtins.str]: ...
    def __init__(
        self,
        *,
        spirv_opt_args: collections.abc.Iterable[builtins.str] | None = ...,
    ) -> None: ...
    def ClearField(self, field_name: typing_extensions.Literal["spirv_opt_args", b"spirv_opt_args"]) -> None: ...

global___TestSpirvFuzz = TestSpirvFuzz
