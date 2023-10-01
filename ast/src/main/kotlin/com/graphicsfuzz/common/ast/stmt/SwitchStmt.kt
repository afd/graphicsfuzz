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

class SwitchStmt(private var expr: Expr, private var body: BlockStmt) : Stmt() {

  fun getExpr(): Expr = expr

  fun getBody(): BlockStmt = body

  override fun hasChild(candidateChild: IAstNode): Boolean = candidateChild === expr || candidateChild === body

  override fun replaceChild(child: IAstNode, newChild: IAstNode) {
    if (child === expr) {
      expr = newChild as Expr
    } else if (child === body) {
      body = newChild as BlockStmt
    } else {
      throw IllegalArgumentException()
    }
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitSwitchStmt(this);
  }

  override fun clone(): Stmt = SwitchStmt(expr.clone(), body.clone())

}
