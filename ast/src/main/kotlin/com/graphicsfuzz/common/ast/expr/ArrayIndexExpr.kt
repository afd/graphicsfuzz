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

package com.graphicsfuzz.common.ast.expr

import com.graphicsfuzz.common.ast.IAstNode
import com.graphicsfuzz.common.ast.visitors.IAstVisitor

class ArrayIndexExpr(arrayData: Expr, var index: Expr) : Expr() {

  private var array: Expr = arrayData

  init {
    // Motivation for this exception:
    // vec2 v;
    // v[0]; // fine
    // v + vec2(0.0)[0]; // not fine - the following was probably intended:
    // (v + vec2(0.0))[0]; // fine
    if (array is BinaryExpr) {
      throw IllegalArgumentException("Array index into binary expression "
          + array.getText() + " not allowed.")
    }
  }

  fun getArray(): Expr = array

  override fun accept(visitor: IAstVisitor) {
    visitor.visitArrayIndexExpr(this)
  }

  override fun clone(): ArrayIndexExpr = ArrayIndexExpr(array.clone(), index.clone())

  override fun hasChild(candidateChild: IAstNode) = candidateChild === array || candidateChild === index

  override fun getChild(index: Int): Expr {
    if (index == 0) {
      return array
    }
    if (index == 1) {
      return this.index
    }
    throw IndexOutOfBoundsException("Index for ArrayIndexExpr must be 0 or 1")
  }

  override fun setChild(index: Int, expr: Expr) {
    if (index == 0) {
      array = expr
      return
    }
    if (index == 1) {
      this.index = expr
      return
    }
    throw IndexOutOfBoundsException("Index for ArrayIndexExpr must be 0 or 1")
  }

  override fun getNumChildren(): Int = 2

}
