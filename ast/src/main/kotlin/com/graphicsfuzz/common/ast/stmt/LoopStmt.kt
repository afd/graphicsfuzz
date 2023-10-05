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
import com.graphicsfuzz.common.ast.visitors.StandardVisitor;

abstract class LoopStmt(private var condition: Expr?, var body: Stmt) : Stmt() {

  /**
   * Reports whether a condition for the loop is present (it is not in e.g. "for(init; ; inc)"
   *
   * @return Whether condition is present.
   */
  abstract fun hasCondition(): Boolean

  open fun getCondition(): Expr? = condition

  fun setCondition(condition: Expr) {
    this.condition = condition
  }

  override fun replaceChild(child: IAstNode, newChild: IAstNode) {
    if (child === body) {
      body = newChild as Stmt
    } else if (child === condition) {
      setCondition(newChild as Expr)
    } else {
      throw IllegalArgumentException()
    }
  }

  override fun hasChild(candidateChild: IAstNode): Boolean =
    candidateChild === body || candidateChild === condition

  /**
   * Determines whether the loop's body contains any break or continue statements
   * that are not nested in inner loops.
   */
  fun containsDirectBreakOrContinueStmt(): Boolean = ContainsDirectBreakOrContinueStmt().check()

  private class FoundBreakOrContinueStmtException : RuntimeException() {

  }

  private inner class ContainsDirectBreakOrContinueStmt : StandardVisitor() {

    private var nestingDepth: Int = 0;

    fun check(): Boolean {
      return try {
        visit(body)
        false;
      } catch (exception: FoundBreakOrContinueStmtException) {
        true
      }
    }

    override fun visit(node: IAstNode) {
      if (node is LoopStmt) {
        nestingDepth++
      }
      super.visit(node);
      if (node is LoopStmt) {
        nestingDepth--
      }
    }

    override fun visitBreakStmt(breakStmt: BreakStmt) {
      if (nestingDepth == 0) {
        throw FoundBreakOrContinueStmtException()
      }
    }

    override fun visitContinueStmt(continueStmt: ContinueStmt) {
      if (nestingDepth == 0) {
        throw FoundBreakOrContinueStmtException()
      }
    }

  }

}
