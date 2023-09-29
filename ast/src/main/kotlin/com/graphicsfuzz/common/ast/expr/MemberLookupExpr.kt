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
import com.graphicsfuzz.common.ast.visitors.IAstVisitor;
import java.util.Collections;

class MemberLookupExpr(structure: Expr, var member: String) : Expr() {

  var structure: Expr = structure
    set(value) {
      checkNoTopLevelCommaExpression(listOf(value))
      field = value;
    }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitMemberLookupExpr(this)
  }

  override fun clone(): MemberLookupExpr = MemberLookupExpr(structure.clone(), member)

  override fun hasChild(candidateChild: IAstNode) : Boolean = structure === candidateChild

  override fun getChild(index: Int): Expr {
    if (index == 0) {
      return structure;
    }
    throw IndexOutOfBoundsException("Index for MemberLookupExpr must be 0");
  }

  override fun setChild(index: Int, expr: Expr) {
    if (index == 0) {
      structure = expr
      return
    }
    throw IndexOutOfBoundsException("Index for MemberLookupExpr must be 0")
  }

  override fun getNumChildren(): Int = 1

}
