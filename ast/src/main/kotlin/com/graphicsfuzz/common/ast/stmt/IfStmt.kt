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

package com.graphicsfuzz.common.ast.stmt;

import com.graphicsfuzz.common.ast.IAstNode;
import com.graphicsfuzz.common.ast.expr.Expr;
import com.graphicsfuzz.common.ast.visitors.IAstVisitor;

class IfStmt(var condition: Expr, var thenStmt: Stmt, private var elseStmt: Stmt?) : Stmt() {

  fun hasElseStmt(): Boolean = elseStmt !== null

  fun getElseStmt(): Stmt {
    require(hasElseStmt())
    return elseStmt as Stmt
  }

  fun setElseStmt(elseStmt: Stmt) {
    this.elseStmt = elseStmt
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitIfStmt(this)
  }

  override fun replaceChild(child: IAstNode, newChild: IAstNode) {
    if (child === condition && newChild is Expr) {
      condition = newChild
      return
    }
    if (child === thenStmt && newChild is Stmt) {
      thenStmt = newChild
      return
    }
    if (child === elseStmt && newChild is Stmt) {
      elseStmt = newChild
      return
    }
    throw IllegalArgumentException();
  }

  override fun hasChild(candidateChild: IAstNode): Boolean {
    return condition === candidateChild
          || thenStmt === candidateChild
          || elseStmt === candidateChild;
  }

  override fun clone(): IfStmt = IfStmt(condition.clone(), thenStmt.clone(),
        if (elseStmt === null) null else (elseStmt as Stmt).clone())

}
