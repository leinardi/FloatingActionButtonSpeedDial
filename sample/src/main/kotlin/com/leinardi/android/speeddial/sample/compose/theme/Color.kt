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

package com.leinardi.android.speeddial.sample.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SampleLightColors = lightColors(
    primary = Color(0XFF4285F4),
    primaryVariant = Color(0XFF3367D6),
    secondary = Color(0XFFDB4437),
    secondaryVariant = Color(0XFFC62828),
    background = Color(0XFFFFFFFF),
    surface = Color(0XFFFFFFFF),
    error = Color(0XFFE53935),
    onPrimary = Color(0XFFFFFFFF),
    onSecondary = Color(0XFFFFFFFF),
    onBackground = Color(0XFF191C1B),
    onSurface = Color(0XFF191C1B),
    onError = Color(0XFFFFFFFF),
)
val SampleDarkColors = darkColors(
    primary = Color(0xFFCE93D8),
    primaryVariant = Color(0xFF8E24AA),
    secondary = Color(0xFFA5D6A7),
    secondaryVariant = Color(0xFF4CAF50),
    background = Color(0xFF212121),
    surface = Color(0xFF424242),
    error = Color(0xFFEF9A9A),
    onPrimary = Color(0xFF212121),
    onSecondary = Color(0xFF212121),
    onBackground = Color(0XFFFFFFFF),
    onSurface = Color(0XFFFFFFFF),
    onError = Color(0xFF212121),
)

val Colors.textPrimary: Color
    @Composable
    get() = MaterialTheme.colors.onSurface.copy(alpha = 0.87f)

val Colors.textSecondary: Color
    @Composable
    get() = MaterialTheme.colors.onSurface.copy(alpha = 0.60f)
