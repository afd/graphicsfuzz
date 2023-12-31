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

class BinaryExpr(var lhs: Expr, var rhs: Expr, val op: BinOp) : Expr() {

  init {
    if (op != BinOp.COMMA) {
      checkNoTopLevelCommaExpression(listOf(lhs, rhs))
    }
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitBinaryExpr(this)
  }

  override fun clone() : BinaryExpr = BinaryExpr(lhs.clone(), rhs.clone(), op)

  override fun hasChild(candidateChild: IAstNode): Boolean = lhs === candidateChild || rhs === candidateChild

  override fun getChild(index: Int): Expr {
    if (index == 0) {
      return lhs
    }
    if (index == 1) {
      return rhs
    }
    throw IndexOutOfBoundsException("Index for BinaryExpr must be 0 or 1")
  }

  override fun setChild(index: Int, expr: Expr) {
    if (index == 0) {
      lhs = expr
      return
    }
    if (index == 1) {
      rhs = expr
      return
    }
    throw IndexOutOfBoundsException("Index for BinaryExpr must be 0 or 1")
  }

  override fun getNumChildren(): Int = 2

}
