# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: gfauto/settings.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import symbol_database as _symbol_database
from google.protobuf.internal import builder as _builder
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from gfauto import common_pb2 as gfauto_dot_common__pb2
from gfauto import device_pb2 as gfauto_dot_device__pb2


DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\x15gfauto/settings.proto\x12\x06gfauto\x1a\x13gfauto/common.proto\x1a\x13gfauto/device.proto\"\xbe\x05\n\x08Settings\x12\'\n\x0b\x64\x65vice_list\x18\x01 \x01(\x0b\x32\x12.gfauto.DeviceList\x12\'\n\x0f\x63ustom_binaries\x18\x02 \x03(\x0b\x32\x0e.gfauto.Binary\x12!\n\x19maximum_duplicate_crashes\x18\x03 \x01(\r\x12\x1d\n\x15maximum_fuzz_failures\x18\x04 \x01(\r\x12\x1b\n\x13reduce_tool_crashes\x18\x05 \x01(\x08\x12\x16\n\x0ereduce_crashes\x18\x06 \x01(\x08\x12\x19\n\x11reduce_bad_images\x18\x07 \x01(\x08\x12.\n\x16latest_binary_versions\x18\x08 \x03(\x0b\x32\x0e.gfauto.Binary\x12)\n!extra_graphics_fuzz_generate_args\x18\t \x03(\t\x12\'\n\x1f\x65xtra_graphics_fuzz_reduce_args\x18\n \x03(\t\x12#\n\x1bonly_reduce_signature_regex\x18\x0b \x01(\t\x12\x10\n\x08_comment\x18\x0c \x01(\t\x12&\n\x1e\x65xtra_spirv_fuzz_generate_args\x18\r \x03(\t\x12$\n\x1c\x65xtra_spirv_fuzz_shrink_args\x18\x0e \x03(\t\x12\x1f\n\x17\x65xtra_spirv_reduce_args\x18\x0f \x03(\t\x12\x19\n\x11\x63ommon_spirv_args\x18\x10 \x03(\t\x12\'\n\x1flegacy_graphics_fuzz_vulkan_arg\x18\x11 \x01(\x08\x12)\n!skip_semantics_changing_reduction\x18\x12 \x01(\x08\x12\x18\n\x10spirv_opt_just_o\x18\x13 \x01(\x08\x12\x1b\n\x13keep_reduction_work\x18\x14 \x01(\x08\x62\x06proto3')

_globals = globals()
_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, _globals)
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'gfauto.settings_pb2', _globals)
if _descriptor._USE_C_DESCRIPTORS == False:

  DESCRIPTOR._options = None
  _globals['_SETTINGS']._serialized_start=76
  _globals['_SETTINGS']._serialized_end=778
# @@protoc_insertion_point(module_scope)
