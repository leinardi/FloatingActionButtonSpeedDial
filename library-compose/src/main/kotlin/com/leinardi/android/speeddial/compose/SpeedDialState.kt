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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * Enum that represents possible SpeedDial states.
 */
@Suppress("EnumNaming")
enum class SpeedDialState {
    /**
     * State that means a component is collapsed
     */
    Collapsed,

    /**
     * State that means a component is expanded
     */
    Expanded;

    fun toggle(): SpeedDialState = if (this == Expanded) Collapsed else Expanded
    fun isExpanded() = this == Expanded
}

/**
 * Return corresponding SpeedDialState based on a Boolean representation
 *
 * @param value whether the SpeedDialState is Expanded or Collapsed
 */

val LocalSpeedDialState: ProvidableCompositionLocal<MutableState<SpeedDialState>> = compositionLocalOf { error("No SpeedDialState provided") }
