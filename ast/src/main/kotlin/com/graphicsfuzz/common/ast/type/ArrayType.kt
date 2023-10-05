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

import com.graphicsfuzz.common.ast.decl.ArrayInfo
import com.graphicsfuzz.common.ast.expr.ArrayConstructorExpr
import com.graphicsfuzz.common.ast.expr.Expr
import com.graphicsfuzz.common.ast.visitors.IAstVisitor
import com.graphicsfuzz.common.typing.Scope
import java.util.Objects

class ArrayType(val baseType: Type, val arrayInfo: ArrayInfo) : UnqualifiedType() {

  init {
    if (arrayInfo.getDimensionality() != 1) {
      throw IllegalArgumentException("Array types should be 1-dimensional: multi-dimensional "
          + "arrays are typed via arrays with arrays as their base type.")
    }
    if (baseType is QualifiedType) {
      throw IllegalArgumentException("Qualifiers should be applied to an array type, not to "
          + "the array's base type.")
    }
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitArrayType(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other !is ArrayType) {
      return false
    }
    if (this.baseType != other.baseType) {
      return false
    }
    assert(this.arrayInfo.getDimensionality() == 1)
    assert(other.arrayInfo.getDimensionality() == 1)
    if (this.arrayInfo.hasConstantSize(0) != other.arrayInfo.hasConstantSize(0)) {
      return false
    }
    if (this.arrayInfo.hasConstantSize(0)
        && !this.arrayInfo.getConstantSize(0)
        .equals(other.arrayInfo.getConstantSize(0))) {
      return false
    }
    return true
  }

  override fun hashCode(): Int = Objects.hash(baseType,
      if(arrayInfo.hasConstantSize(0))
        arrayInfo.getConstantSize(0)
        else arrayInfo.hasConstantSize(0))

  override fun clone(): ArrayType = ArrayType(baseType.clone(), arrayInfo.clone())

  override fun hasCanonicalConstant(scope: Scope) =
    baseType.hasCanonicalConstant(scope) && arrayInfo.hasConstantSize(0)

  override fun getCanonicalConstant(scope: Scope): Expr =
    ArrayConstructorExpr(this.clone(), (0..<arrayInfo.getConstantSize(0)).map{baseType.getCanonicalConstant(scope).clone()})

}
