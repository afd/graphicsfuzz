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

package com.graphicsfuzz.common.ast.type

import com.graphicsfuzz.common.ast.expr.Expr
import com.graphicsfuzz.common.ast.visitors.IAstVisitor
import com.graphicsfuzz.common.typing.Scope

class SamplerType private constructor() : BuiltinType() {

  override fun toString(): String =
    when(this) {
      SAMPLER1D -> "sampler1D"
      SAMPLER2D -> "sampler2D"
      SAMPLER2DRECT -> "sampler2DRect"
      SAMPLER3D -> "sampler3D"
      SAMPLERCUBE -> "samplerCube"
      SAMPLEREXTERNALOES -> "samplerExternalOES"
      SAMPLER1DSHADOW -> "sampler1DShadow"
      SAMPLER2DSHADOW -> "sampler2DShadow"
      SAMPLER2DRECTSHADOW -> "sampler2DRectShadow"
      SAMPLERCUBESHADOW -> "samplerCubeShadow"
      SAMPLER1DARRAY -> "sampler1DArray"
      SAMPLER2DARRAY -> "sampler2DArray"
      SAMPLER1DARRAYSHADOW -> "sampler1DArrayShadow"
      SAMPLER2DARRAYSHADOW -> "sampler2DArrayShadow"
      SAMPLERBUFFER -> "samplerBuffer"
      SAMPLERCUBEARRAY -> "samplerCubeArray"
      SAMPLERCUBEARRAYSHADOW -> "samplerCubeArrayShadow"
      ISAMPLER1D -> "isampler1D"
      ISAMPLER2D -> "isampler2D"
      ISAMPLER2DRECT -> "isampler2DRect"
      ISAMPLER3D -> "isampler3D"
      ISAMPLERCUBE -> "isamplerCube"
      ISAMPLER1DARRAY -> "isampler1DArray"
      ISAMPLER2DARRAY -> "isampler2DArray"
      ISAMPLERBUFFER -> "isamplerBuffer"
      ISAMPLERCUBEARRAY -> "isamplerCubeArray"
      USAMPLER1D -> "usampler1D"
      USAMPLER2D -> "usampler2D"
      USAMPLER2DRECT -> "usampler2DRect"
      USAMPLER3D -> "usampler3D"
      USAMPLERCUBE -> "usamplerCube"
      USAMPLER1DARRAY -> "usampler1DArray"
      USAMPLER2DARRAY -> "usampler2DArray"
      USAMPLERBUFFER -> "usamplerBuffer"
      USAMPLERCUBEARRAY -> "usamplerCubeArray"
      SAMPLER2DMS -> "sampler2DMS"
      ISAMPLER2DMS -> "isampler2DMS"
      USAMPLER2DMS -> "usampler2DMS"
      SAMPLER2DMSARRAY -> "sampler2DMSArray"
      ISAMPLER2DMSARRAY -> "isampler2DMSArray"
      USAMPLER2DMSARRAY -> "usampler2DMSArray"
      else -> throw RuntimeException("Invalid type")
    }

  override fun hasCanonicalConstant(unused: Scope) = false

  override fun getCanonicalConstant(scope: Scope): Expr {
    // Sanity-check that there is indeed no canonical constant.
    assert(!hasCanonicalConstant(scope))
    throw RuntimeException("No canonical constant for $this")
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitSamplerType(this)
  }

  companion object {
    @JvmField val SAMPLER1D: SamplerType = SamplerType()
    @JvmField val SAMPLER2D: SamplerType = SamplerType()
    @JvmField val SAMPLER2DRECT: SamplerType = SamplerType()
    @JvmField val SAMPLER3D: SamplerType = SamplerType()
    @JvmField val SAMPLERCUBE: SamplerType = SamplerType()
    @JvmField val SAMPLEREXTERNALOES: SamplerType = SamplerType()
    @JvmField val SAMPLER1DSHADOW: SamplerType = SamplerType()
    @JvmField val SAMPLER2DSHADOW: SamplerType = SamplerType()
    @JvmField val SAMPLER2DRECTSHADOW: SamplerType = SamplerType()
    @JvmField val SAMPLERCUBESHADOW: SamplerType = SamplerType()
    @JvmField val SAMPLER1DARRAY: SamplerType = SamplerType()
    @JvmField val SAMPLER2DARRAY: SamplerType = SamplerType()
    @JvmField val SAMPLER1DARRAYSHADOW: SamplerType = SamplerType()
    @JvmField val SAMPLER2DARRAYSHADOW: SamplerType = SamplerType()
    @JvmField val SAMPLERBUFFER: SamplerType = SamplerType()
    @JvmField val SAMPLERCUBEARRAY: SamplerType = SamplerType()
    @JvmField val SAMPLERCUBEARRAYSHADOW: SamplerType = SamplerType()
    @JvmField val ISAMPLER1D: SamplerType = SamplerType()
    @JvmField val ISAMPLER2D: SamplerType = SamplerType()
    @JvmField val ISAMPLER2DRECT: SamplerType = SamplerType()
    @JvmField val ISAMPLER3D: SamplerType = SamplerType()
    @JvmField val ISAMPLERCUBE: SamplerType = SamplerType()
    @JvmField val ISAMPLER1DARRAY: SamplerType = SamplerType()
    @JvmField val ISAMPLER2DARRAY: SamplerType = SamplerType()
    @JvmField val ISAMPLERBUFFER: SamplerType = SamplerType()
    @JvmField val ISAMPLERCUBEARRAY: SamplerType = SamplerType()
    @JvmField val USAMPLER1D: SamplerType = SamplerType()
    @JvmField val USAMPLER2D: SamplerType = SamplerType()
    @JvmField val USAMPLER2DRECT: SamplerType = SamplerType()
    @JvmField val USAMPLER3D: SamplerType = SamplerType()
    @JvmField val USAMPLERCUBE: SamplerType = SamplerType()
    @JvmField val USAMPLER1DARRAY: SamplerType = SamplerType()
    @JvmField val USAMPLER2DARRAY: SamplerType = SamplerType()
    @JvmField val USAMPLERBUFFER: SamplerType = SamplerType()
    @JvmField val USAMPLERCUBEARRAY: SamplerType = SamplerType()
    @JvmField val SAMPLER2DMS: SamplerType = SamplerType()
    @JvmField val ISAMPLER2DMS: SamplerType = SamplerType()
    @JvmField val USAMPLER2DMS: SamplerType = SamplerType()
    @JvmField val SAMPLER2DMSARRAY: SamplerType = SamplerType()
    @JvmField val ISAMPLER2DMSARRAY: SamplerType = SamplerType()
    @JvmField val USAMPLER2DMSARRAY: SamplerType = SamplerType()
  }
}
