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
package com.graphicsfuzz.common.ast.type

import java.util.*

class LayoutQualifierSequence(contents: List<LayoutQualifier>) : TypeQualifier("layout") {
    private val contents: MutableList<LayoutQualifier>

    init {
        this.contents = contents.toMutableList()
    }

    constructor(vararg contents: LayoutQualifier) : this(contents.asList())

    val layoutQualifiers: List<LayoutQualifier>
        get() = Collections.unmodifiableList(contents)

    override fun toString(): String {
        return (super.toString() + "("
                + contents
            .map { obj: LayoutQualifier -> obj.toString() }
            .reduce { item1: String, item2: String -> "$item1, $item2" } + ")")
    }
}
