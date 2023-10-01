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

import com.graphicsfuzz.common.ast.ChildDoesNotExistException;
import com.graphicsfuzz.common.ast.IAstNode
import com.graphicsfuzz.common.ast.visitors.IAstVisitor
import java.util.Collections;

/**
 * Constructor creates a block statement from the given list of statements.
 *
 * @param stmts Initial statements for the block
 * @param introducesNewScope Determines whether the block introduces a new lexical scope.  This
 *                           should be false in the case of a function body, as this has the same
 *                           lexical scope as the function parameters.
 */
class BlockStmt(stmts: List<Stmt>,
                // A procedure body, for body or while body
                // does not introduce a new scope: the procedure
                // header, for header or while header does this
                private var introducesNewScope: Boolean) : Stmt() {

  private var stmts: MutableList<Stmt> = stmts.toMutableList()

  fun getStmts(): List<Stmt> = Collections.unmodifiableList(stmts)

  fun setStmts(stmts: List<Stmt>) {
    this.stmts = stmts.toMutableList()
  }

  fun getNumStmts(): Int = stmts.size

  /**
   * Requires the block to be non-empty.
   * Yields the final statement in the block.
   *
   * @return The final statement of the block.
   */
  fun getLastStmt(): Stmt {
    require(stmts.isNotEmpty()) {
      "Attempt to get last statement of empty block."
    }
    return stmts[stmts.size - 1]
  }

  fun introducesNewScope(): Boolean = introducesNewScope

  fun setIntroducesNewScope(introducesNewScope: Boolean) {
    this.introducesNewScope = introducesNewScope
  }

  fun insertStmt(index: Int, stmt: Stmt) {
    stmts.add(index, stmt)
  }

  fun setStmt(index: Int, stmt: Stmt) {
    stmts[index] = stmt
  }

  fun getStmt(index: Int): Stmt = stmts[index]

  fun removeStmt(index: Int) {
    stmts.removeAt(index)
  }

  /**
   * Removes the given child statement from the block, throwing an exception if not present.
   *
   * @param child Statement to be removed
   */
  fun removeStmt(child: Stmt) {
    if (!stmts.contains(child)) {
      throw IllegalArgumentException("Block does not contain given statement.")
    }
    stmts.remove(child)
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitBlockStmt(this)
  }

  override fun replaceChild(child: IAstNode, newChild: IAstNode) {
    if (!hasChild(child)) {
      throw ChildDoesNotExistException(child, this)
    }
    if (newChild !is Stmt) {
      throw IllegalArgumentException(
          "Attempt to replace child of block statement with a non-statement: "
              + newChild)
    }
    for (i in 0..<stmts.size) {
      if (child === getStmt(i)) {
        setStmt(i, newChild)
        return;
      }
    }
    throw IllegalArgumentException("Should be unreachable.")
  }

  override fun hasChild(candidateChild: IAstNode): Boolean = stmts.contains(candidateChild)

  /**
   * Inserts the second statement right after the first statement, which must appear in the block.
   *
   * @param originalStmt A statement that must be present in the block
   * @param insertedStmt A statement to be inserted right after originalStmt
   */
  fun insertAfter(originalStmt: Stmt, insertedStmt: Stmt) {
    for (i in 0..<stmts.size) {
      if (getStmt(i) == originalStmt) {
        insertStmt(i + 1, insertedStmt)
        return
      }
    }
    throw IllegalArgumentException("Should be unreachable.")
  }

  /**
   * Inserts the second statement right before the first statement, which must appear in the block.
   *
   * @param originalStmt A statement that must be present in the block
   * @param insertedStmt A statement to be inserted right before originalStmt
   */
  fun insertBefore(originalStmt: Stmt, insertedStmt: Stmt) {
    for (i in 0..<stmts.size) {
      if (getStmt(i) === originalStmt) {
        insertStmt(i, insertedStmt)
        return
      }
    }
    throw IllegalArgumentException("Should be unreachable.")
  }

  /**
   * Adds the given statement to the end of the block.
   *
   * @param stmt A statement to be added to the block
   */
  fun addStmt(stmt: Stmt) {
    stmts.add(stmt)
  }

  override fun clone(): BlockStmt = BlockStmt(stmts.map(Stmt::clone), introducesNewScope)

}
