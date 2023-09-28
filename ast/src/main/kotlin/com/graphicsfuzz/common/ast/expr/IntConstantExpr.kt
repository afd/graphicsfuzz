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

import com.graphicsfuzz.common.ast.visitors.IAstVisitor;

class IntConstantExpr(val value: String) : ConstantExpr() {

  init {
    assert(!text.contains("u"))
  }

  override fun accept(visitor: IAstVisitor) {
    visitor.visitIntConstantExpr(this);
  }

  override fun clone(): IntConstantExpr = IntConstantExpr(value);

  fun getNumericValue(): Int {
    if (isOctal()) {
      return value.toInt(8)
    }
    if (isHex()) {
      return value.substring("0x".length).toInt(16)
    }
    return value.toInt()
  }

  private fun isOctal(): Boolean = value.startsWith("0")
      && value.length > 1 && !isHex()

  private fun isHex(): Boolean = value.startsWith("0x")

}
