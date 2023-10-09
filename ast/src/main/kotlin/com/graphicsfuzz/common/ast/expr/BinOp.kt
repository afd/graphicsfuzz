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

enum class BinOp : Op {
  COMMA,
  MOD,
  MUL,
  DIV,
  ADD,
  SUB,
  BAND,
  BOR,
  BXOR,
  LAND,
  LOR,
  LXOR,
  SHL,
  SHR,
  LT,
  GT,
  LE,
  GE,
  EQ,
  NE,
  ASSIGN,
  MUL_ASSIGN,
  DIV_ASSIGN,
  MOD_ASSIGN,
  ADD_ASSIGN,
  SUB_ASSIGN,
  BAND_ASSIGN,
  BOR_ASSIGN,
  BXOR_ASSIGN,
  SHL_ASSIGN,
  SHR_ASSIGN;

  override fun getText(): String = when (this) {
      COMMA -> ","
      MOD -> "%"
      MUL -> "*"
      DIV -> "/"
      ADD -> "+"
      SUB -> "-"
      BAND -> "&"
      BOR -> "|"
      BXOR -> "^"
      LAND -> "&&"
      LOR -> "||"
      LXOR -> "^^"
      SHL -> "<<"
      SHR -> ">>"
      LT -> "<"
      GT -> ">"
      LE -> "<="
      GE -> ">="
      EQ -> "=="
      NE -> "!="
      ASSIGN -> "="
      MUL_ASSIGN -> "*="
      DIV_ASSIGN -> "/="
      MOD_ASSIGN -> "%="
      ADD_ASSIGN -> "+="
      SUB_ASSIGN -> "-="
      BAND_ASSIGN -> "&="
      BOR_ASSIGN -> "|="
      BXOR_ASSIGN -> "^="
      SHL_ASSIGN -> "<<="
      SHR_ASSIGN -> ">>="
    }

  override fun isSideEffecting(): Boolean = when(this) {
      ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN, ADD_ASSIGN, SUB_ASSIGN, BAND_ASSIGN, BOR_ASSIGN, BXOR_ASSIGN, SHL_ASSIGN, SHR_ASSIGN -> true
      else -> false
  }

}
