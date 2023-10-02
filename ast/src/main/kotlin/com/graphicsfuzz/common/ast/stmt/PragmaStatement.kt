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

import com.graphicsfuzz.common.ast.decl.Declaration;
import com.graphicsfuzz.common.ast.visitors.IAstVisitor;

class PragmaStatement private constructor() : Declaration() {

  override fun accept(visitor: IAstVisitor) {
    visitor.visitPragmaStatement(this)
  }

  override fun clone(): PragmaStatement = this

  override fun getText(): String {
    return "#pragma " + (
      if (this === OPTIMIZE_ON) {
        "optimize(on)"
      } else if (this === OPTIMIZE_OFF) {
        "optimize(off)"
      } else if (this === DEBUG_ON) {
        "debug(on)"
      } else if (this === DEBUG_OFF) {
        "debug(off)"
      } else {
        assert(this === INVARIANT_ALL)
        "invariant(all)"
      }) + "\n"
  }

  companion object {
    @JvmField val OPTIMIZE_ON: PragmaStatement = PragmaStatement()
    @JvmField val OPTIMIZE_OFF: PragmaStatement = PragmaStatement()
    @JvmField val DEBUG_ON: PragmaStatement = PragmaStatement()
    @JvmField val DEBUG_OFF: PragmaStatement = PragmaStatement()
    @JvmField val INVARIANT_ALL: PragmaStatement = PragmaStatement()
  }

}
