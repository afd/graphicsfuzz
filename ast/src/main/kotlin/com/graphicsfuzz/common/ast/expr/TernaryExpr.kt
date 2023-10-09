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

class TernaryExpr(private var test: Expr, private var thenExpr: Expr, private var elseExpr: Expr) : Expr() {

  init {
    // The 'test' and 'else' expressions are not allowed to be top-level instances of the comma
    // operator, but the 'then' expression is, e.g. 'a ? b, c : d' is legal.
    checkNoTopLevelCommaExpression(listOf(test, elseExpr))
  }

  fun getTest(): Expr = test

  fun getThenExpr(): Expr = thenExpr

  fun getElseExpr(): Expr = elseExpr

  override fun accept(visitor: IAstVisitor) {
    visitor.visitTernaryExpr(this)
  }

  override fun clone(): TernaryExpr = TernaryExpr(test.clone(), thenExpr.clone(), elseExpr.clone())

  override fun hasChild(candidateChild: IAstNode): Boolean =
      candidateChild === test || candidateChild === thenExpr || candidateChild === elseExpr

  override fun getChild(index: Int): Expr {
    if (index == 0) {
      return test
    }
    if (index == 1) {
      return thenExpr
    }
    if (index == 2) {
      return elseExpr
    }
    throw IndexOutOfBoundsException("Index for TernaryExpr must be 0, 1 or 2")
  }

  override fun setChild(index: Int, expr: Expr) {
    if (index == 0) {
      test = expr
      return
    }
    if (index == 1) {
      thenExpr = expr
      return
    }
    if (index == 2) {
      elseExpr = expr
      return
    }
    throw IndexOutOfBoundsException("Index for TernaryExpr must be 0, 1 or 2")
  }

  override fun getNumChildren(): Int = 3

}
