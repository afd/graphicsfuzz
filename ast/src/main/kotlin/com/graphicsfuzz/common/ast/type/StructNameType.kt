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

class StructNameType(var name: String) : UnqualifiedType() {

  override fun accept(visitor: IAstVisitor) {
    visitor.visitStructNameType(this)
  }

  override fun clone(): StructNameType = StructNameType(name)

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other !is StructNameType) {
      return false
    }
    return name == other.name
  }

  override fun hashCode(): Int = name.hashCode()

  override fun toString(): String = name

  override fun hasCanonicalConstant(scope: Scope ): Boolean =
    if (scope.lookupStructName(name) === null)
      throw RuntimeException("Attempt to check whether a struct has a canonical constant when"
          + " the struct is not in scope.")
      else scope.lookupStructName(name).hasCanonicalConstant(scope)

  override fun getCanonicalConstant(scope: Scope): Expr =
    scope.lookupStructName(name).getCanonicalConstant(scope)

}
