/*
 * Copyright 2022 Roberto Leinardi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leinardi.android.speeddial.compose

import androidx.compose.foundation.lazy.LazyScopeMarker
import androidx.compose.runtime.Composable

/**
 * Receiver scope which is used by [SpeedDial].
 */
@LazyScopeMarker
class SpeedDialScope {
    private val _intervals = mutableListOf<SpeedDialIntervalContent>()
    internal val intervals: List<SpeedDialIntervalContent> = _intervals

    /**
     * Adds a single item.
     *
     * @param content the content of the item
     */
    fun item(
        content: @Composable SpeedDialItemScope.() -> Unit,
    ) {
        _intervals.add(0, SpeedDialIntervalContent(item = { content() }))
    }
}

internal data class SpeedDialIntervalContent(
    val item: @Composable SpeedDialItemScope.() -> Unit,
)
