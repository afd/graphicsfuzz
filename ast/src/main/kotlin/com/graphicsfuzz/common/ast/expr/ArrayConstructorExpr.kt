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

import com.graphicsfuzz.common.ast.IAstNode;
import com.graphicsfuzz.common.ast.type.ArrayType;
import com.graphicsfuzz.common.ast.visitors.IAstVisitor;
import java.util.Collections;

class ArrayConstructorExpr(val arrayType: ArrayType, argsData: List<Expr>) : Expr() {

  private val args: MutableList<Expr> = argsData.toMutableList()

  init {
    assert(arrayType.getArrayInfo().getDimensionality() == 1)
  }

  fun getArgs(): List<Expr> {
    return Collections.unmodifiableList(args)
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitArrayConstructorExpr(this);
  }

  override fun clone(): ArrayConstructorExpr = ArrayConstructorExpr(
    arrayType.clone(),
    args.map(Expr::clone))

  override fun hasChild(candidateChild: IAstNode): Boolean = args.contains(candidateChild)

  override fun getChild(index: Int): Expr {
    checkBounds(index)
    return args[index]
  }

  override fun setChild(index: Int, expr: Expr) {
    checkBounds(index)
    args[index] = expr
  }

  override fun getNumChildren(): Int = args.size

  private fun checkBounds(index: Int) {
    if (!(index in 0..<getNumChildren())) {
      throw IndexOutOfBoundsException("No child at index " + index)
    }
  }

}
