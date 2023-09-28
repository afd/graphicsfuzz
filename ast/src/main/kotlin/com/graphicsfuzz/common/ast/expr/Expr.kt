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

package com.graphicsfuzz.common.ast.expr;

import com.graphicsfuzz.common.ast.ChildDoesNotExistException;
import com.graphicsfuzz.common.ast.IAstNode;

abstract class Expr : IAstNode {

  override abstract fun clone(): Expr

  abstract fun getChild(index: Int): Expr

  abstract fun setChild(index: Int, expr: Expr)

  abstract fun getNumChildren(): Int

  override fun replaceChild(child: IAstNode, newChild: IAstNode) {
    if (child !is Expr || newChild !is Expr) {
      throw IllegalArgumentException()
    }
    for (i in 0..<getNumChildren()) {
      if (getChild(i) == child) {
        setChild(i, newChild)
        return;
      }
    }
    throw ChildDoesNotExistException(child, this)
  }

  override abstract fun hasChild(candidateChild: IAstNode): Boolean

  companion object {
    fun checkNoTopLevelCommaExpression(args: List<Expr>) {
      for (arg in args) {
        if (arg is BinaryExpr && arg.op == BinOp.COMMA) {
          throw IllegalArgumentException("Invalid use of comma expression.")
        }
      }
    }
  }

}
