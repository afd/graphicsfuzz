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

package com.graphicsfuzz.common.ast.stmt

import com.graphicsfuzz.common.ast.IAstNode
import com.graphicsfuzz.common.ast.expr.Expr
import com.graphicsfuzz.common.ast.visitors.IAstVisitor

class ForStmt(private var init: Stmt, condition: Expr?, private var increment: Expr?, body: Stmt) : LoopStmt(condition, body) {

  override fun hasCondition(): Boolean = getCondition() != null

  /**
   * Reports whether a condition for the loop is present (it is not in e.g. "for(init; cond; )"
   *
   * @return Whether increment is present.
   */
  fun hasIncrement(): Boolean {
    return increment != null
  }

  fun getInit(): Stmt = init

  fun getIncrement(): Expr {
    if (increment != null) {
      return increment!!
    }
    throw UnsupportedOperationException()
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitForStmt(this)
  }

  override fun clone(): ForStmt = ForStmt(init.clone(),
        if (hasCondition()) getCondition()!!.clone() else null,
        if (hasIncrement()) increment!!.clone() else null,
        body.clone())

  override fun replaceChild(child: IAstNode, newChild: IAstNode) {
    if (child === init) {
      init = newChild as Stmt
    } else if (child === increment) {
      increment = newChild as Expr
    } else {
      super.replaceChild(child, newChild)
    }
  }

  override fun hasChild(candidateChild: IAstNode): Boolean =
    candidateChild === init
          || candidateChild === increment
          || super.hasChild(candidateChild)

}
