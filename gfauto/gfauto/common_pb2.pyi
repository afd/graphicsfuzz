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
class Binary(google.protobuf.message.Message):
    """Defines a binary (or really, any file).
    Note that Binary is used to:
     - Describe which version of a binary to find/download and use in settings.json (Settings proto) or test.json
       (Test proto) given the |name|, |tags|, and |version|. |path| is set to "".
     - Define a specific binary file in recipe.json (Recipe proto) or artifact.json (Artifact proto). |path| is a
       relative path to the binary, always using "/" as the directory separator.
    """

    DESCRIPTOR: google.protobuf.descriptor.Descriptor

    NAME_FIELD_NUMBER: builtins.int
    TAGS_FIELD_NUMBER: builtins.int
    PATH_FIELD_NUMBER: builtins.int
    VERSION_FIELD_NUMBER: builtins.int
    name: builtins.str
    """E.g. glslangValidator."""
    @property
    def tags(self) -> google.protobuf.internal.containers.RepeatedScalarFieldContainer[builtins.str]:
        """This should be a list of strings (tags) containing the platform and other details.
        E.g. "Linux", "x64", "Debug", "malloc", "msan".
        When being used to specify how to *find* a binary, the tags must be found in the binary definition.
        E.g. "Debug", "msan".
        Typically, the current platform will also need to be found (e.g. "Linux") before the Binary can be chosen for use.
        """
    path: builtins.str
    """When describing which version of a binary to find/download and use in settings.json (Settings proto) or test.json
    (Test proto), is set to the empty string "". However, you can provide the path of a binary for testing purposes, in
    which case you will most likely want to provide the absolute path to the binary.
    When defining a specific binary file in recipe.json (Recipe proto) or artifact.json (Artifact proto), gives the
    path (always using "/" as the directory separator) relative from the artifact.json/recipe.json file to the binary,
    after the ArchiveSet has been extracted.
    """
    version: builtins.str
    """This should typically be a hash of some kind."""
    def __init__(
        self,
        *,
        name: builtins.str = ...,
        tags: collections.abc.Iterable[builtins.str] | None = ...,
        path: builtins.str = ...,
        version: builtins.str = ...,
    ) -> None: ...
    def ClearField(self, field_name: typing_extensions.Literal["name", b"name", "path", b"path", "tags", b"tags", "version", b"version"]) -> None: ...

global___Binary = Binary

@typing_extensions.final
class Archive(google.protobuf.message.Message):
    DESCRIPTOR: google.protobuf.descriptor.Descriptor

    URL_FIELD_NUMBER: builtins.int
    OUTPUT_FILE_FIELD_NUMBER: builtins.int
    OUTPUT_DIRECTORY_FIELD_NUMBER: builtins.int
    url: builtins.str
    """The URL from which to download the archive."""
    output_file: builtins.str
    """The output location for the downloaded archive."""
    output_directory: builtins.str
    """The directory in which the archive will be extracted."""
    def __init__(
        self,
        *,
        url: builtins.str = ...,
        output_file: builtins.str = ...,
        output_directory: builtins.str = ...,
    ) -> None: ...
    def ClearField(self, field_name: typing_extensions.Literal["output_directory", b"output_directory", "output_file", b"output_file", "url", b"url"]) -> None: ...

global___Archive = Archive

@typing_extensions.final
class ArchiveSet(google.protobuf.message.Message):
    DESCRIPTOR: google.protobuf.descriptor.Descriptor

    ARCHIVES_FIELD_NUMBER: builtins.int
    BINARIES_FIELD_NUMBER: builtins.int
    @property
    def archives(self) -> google.protobuf.internal.containers.RepeatedCompositeFieldContainer[global___Archive]:
        """A list of Archives; all of these will be downloaded and extracted when used in a recipe.
        See RecipeDownloadAndExtractArchiveSet proto.
        """
    @property
    def binaries(self) -> google.protobuf.internal.containers.RepeatedCompositeFieldContainer[global___Binary]:
        """A list of Binaries that can be found in the artifact. See ArtifactMetadataExtractedArchiveSet proto."""
    def __init__(
        self,
        *,
        archives: collections.abc.Iterable[global___Archive] | None = ...,
        binaries: collections.abc.Iterable[global___Binary] | None = ...,
    ) -> None: ...
    def ClearField(self, field_name: typing_extensions.Literal["archives", b"archives", "binaries", b"binaries"]) -> None: ...

global___ArchiveSet = ArchiveSet
