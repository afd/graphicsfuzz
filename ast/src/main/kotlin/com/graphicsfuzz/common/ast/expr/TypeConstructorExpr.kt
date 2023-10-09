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

class TypeConstructorExpr(var typename: String, argsData: List<Expr>) : Expr() {

  private val args: MutableList<Expr> = argsData.toMutableList()

  constructor(typename: String, vararg arg: Expr): this(typename, arg.asList())

  init {
    checkNoTopLevelCommaExpression(argsData)
  }

  fun getArgs(): List<Expr> {
    return Collections.unmodifiableList(args)
  }

  fun getArg(index: Int): Expr {
    return args[index]
  }

  /**
   * Removes the argument at the given index and returns it.
   *
   * @param index The index at which an argument should be removed
   * @return The removed argument
   */
  fun removeArg(index: Int) = args.removeAt(index)

  /**
   * Reveals how many arguments there are.
   *
   * @return Number of arguments
   */
  fun getNumArgs(): Int = args.size

  override fun accept(visitor: IAstVisitor) {
    visitor.visitTypeConstructorExpr(this)
  }

  override fun clone(): TypeConstructorExpr = TypeConstructorExpr(typename, args.map(Expr::clone))

  /**
   * Inserts an argument at the given index, moving existing arguments down one place.
   *
   * @param index The index at which insertion should take place
   * @param arg The argument to be inserted
   */
  fun insertArg(index: Int, arg: Expr) {
    args.add(index, arg)
  }

  override fun hasChild(candidateChild: IAstNode): Boolean = args.contains(candidateChild)

  override fun getChild(index: Int): Expr {
    if (index !in 0..<args.size) {
      throw IndexOutOfBoundsException("TypeConstructorExpr has no child at index $index")
    }
    return args[index]
  }

  override fun setChild(index: Int, expr: Expr) {
    if (index !in 0..<args.size) {
      throw IndexOutOfBoundsException("TypeConstructorExpr has no child at index $index")
    }
    args[index] = expr
  }

  override fun getNumChildren(): Int = args.size

}
