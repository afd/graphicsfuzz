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
import java.util.Collections

class FunctionCallExpr(var callee: String, argsData: List<Expr>) : Expr() {

  private val args: MutableList<Expr> = argsData.toMutableList()

  constructor(callee: String, vararg arg: Expr): this(callee, arg.asList())

  init {
    checkNoTopLevelCommaExpression(argsData)
  }

  fun getArgs(): List<Expr> {
    return Collections.unmodifiableList(args)
  }

  fun getNumArgs(): Int = args.size

  fun getArg(index: Int): Expr = args[index]

  fun setArg(index: Int, expr: Expr) {
    args[index] = expr
  }

  fun removeArg(index: Int) {
    args.removeAt(index)
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitFunctionCallExpr(this)
  }

  override fun clone(): FunctionCallExpr = FunctionCallExpr(callee,
        args.map(Expr::clone))

  override fun hasChild(candidateChild: IAstNode): Boolean = args.contains(candidateChild)

  override fun getChild(index: Int): Expr {
    if (index < 0 || index >= getNumArgs()) {
      throw IndexOutOfBoundsException("FunctionCallExpr has no child at index $index")
    }
    return getArg(index)
  }

  override fun setChild(index: Int, expr: Expr) {
    if (index < 0 || index >= getNumArgs()) {
      throw IndexOutOfBoundsException("FunctionCallExpr has no child at index $index")
    }
    setArg(index, expr)
  }

  override fun getNumChildren(): Int = getNumArgs()

}
