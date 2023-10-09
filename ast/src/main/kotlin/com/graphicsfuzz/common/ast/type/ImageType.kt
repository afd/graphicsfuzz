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

class ImageType private constructor() : BuiltinType() {

  // ImageType is in essence an enumeration.
  // No need to override .equals() and .hashCode()

  override fun toString(): String =
    when(this) {
      IMAGE1D -> "image1d"
      IMAGE2D -> "image2d"
      IMAGE3D -> "image3d"
      IMAGE2DRECT -> "image2drect"
      IMAGECUBE -> "imagecube"
      IMAGEBUFFER -> "imagebuffer"
      IMAGE1DARRAY -> "image1darray"
      IMAGE2DARRAY -> "image2darray"
      IMAGECUBEARRAY -> "imagecubearray"
      IMAGE2DMS -> "image2dms"
      IMAGE2DMSARRAY -> "image2dmsarray"
      IIMAGE1D -> "iimage1d"
      IIMAGE2D -> "iimage2d"
      IIMAGE3D -> "iimage3d"
      IIMAGE2DRECT -> "iimage2drect"
      IIMAGECUBE -> "iimagecube"
      IIMAGEBUFFER -> "iimagebuffer"
      IIMAGE1DARRAY -> "iimage1darray"
      IIMAGE2DARRAY -> "iimage2darray"
      IIMAGECUBEARRAY -> "iimagecubearray"
      IIMAGE2DMS -> "iimage2dms"
      IIMAGE2DMSARRAY -> "iimage2dmsarray"
      UIMAGE1D -> "uimage1d"
      UIMAGE2D -> "uimage2d"
      UIMAGE3D -> "uimage3d"
      UIMAGE2DRECT -> "uimage2drect"
      UIMAGECUBE -> "uimagecube"
      UIMAGEBUFFER -> "uimagebuffer"
      UIMAGE1DARRAY -> "uimage1darray"
      UIMAGE2DARRAY -> "uimage2darray"
      UIMAGECUBEARRAY -> "uimagecubearray"
      UIMAGE2DMS -> "uimage2dms"
      UIMAGE2DMSARRAY -> "uimage2dmsarray"
      else -> throw RuntimeException("Invalid type")
    }

  override fun hasCanonicalConstant(unused: Scope) = false

  override fun getCanonicalConstant(scope: Scope): Expr {
    // Sanity-check that there is indeed no canonical constant.
    assert(!hasCanonicalConstant(scope))
    throw RuntimeException("No canonical constant for $this")
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitImageType(this)
  }

  companion object {
    @JvmField val IMAGE1D: ImageType = ImageType()
    @JvmField val IMAGE2D: ImageType = ImageType()
    @JvmField val IMAGE3D: ImageType = ImageType()
    @JvmField val IMAGE2DRECT: ImageType = ImageType()
    @JvmField val IMAGECUBE: ImageType = ImageType()
    @JvmField val IMAGEBUFFER: ImageType = ImageType()
    @JvmField val IMAGE1DARRAY: ImageType = ImageType()
    @JvmField val IMAGE2DARRAY: ImageType = ImageType()
    @JvmField val IMAGECUBEARRAY: ImageType = ImageType()
    @JvmField val IMAGE2DMS: ImageType = ImageType()
    @JvmField val IMAGE2DMSARRAY: ImageType = ImageType()
    @JvmField val IIMAGE1D: ImageType = ImageType()
    @JvmField val IIMAGE2D: ImageType = ImageType()
    @JvmField val IIMAGE3D: ImageType = ImageType()
    @JvmField val IIMAGE2DRECT: ImageType = ImageType()
    @JvmField val IIMAGECUBE: ImageType = ImageType()
    @JvmField val IIMAGEBUFFER: ImageType = ImageType()
    @JvmField val IIMAGE1DARRAY: ImageType = ImageType()
    @JvmField val IIMAGE2DARRAY: ImageType = ImageType()
    @JvmField val IIMAGECUBEARRAY: ImageType = ImageType()
    @JvmField val IIMAGE2DMS: ImageType = ImageType()
    @JvmField val IIMAGE2DMSARRAY: ImageType = ImageType()
    @JvmField val UIMAGE1D: ImageType = ImageType()
    @JvmField val UIMAGE2D: ImageType = ImageType()
    @JvmField val UIMAGE3D: ImageType = ImageType()
    @JvmField val UIMAGE2DRECT: ImageType = ImageType()
    @JvmField val UIMAGECUBE: ImageType = ImageType()
    @JvmField val UIMAGEBUFFER: ImageType = ImageType()
    @JvmField val UIMAGE1DARRAY: ImageType = ImageType()
    @JvmField val UIMAGE2DARRAY: ImageType = ImageType()
    @JvmField val UIMAGECUBEARRAY: ImageType = ImageType()
    @JvmField val UIMAGE2DMS: ImageType = ImageType()
    @JvmField val UIMAGE2DMSARRAY: ImageType = ImageType()
  }

}
