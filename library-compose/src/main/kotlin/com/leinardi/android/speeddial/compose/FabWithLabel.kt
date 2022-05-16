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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.FloatingActionButtonElevation
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun FabWithLabel(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    labelContent: @Composable (() -> Unit)? = null,
    labelBackgroundColor: Color = MaterialTheme.colors.surface,
    labelMaxWidth: Dp = 160.dp,
    labelContainerElevation: Dp = 2.dp,
    fabShape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    fabElevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    fabSize: Dp = SpeedDialMiniFabSize,
    fabBackgroundColor: Color = MaterialTheme.colors.primary,
    fabContentColor: Color = contentColorFor(fabBackgroundColor),
    fabContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(end = (SpeedDialFabSize - fabSize) / 2)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy((SpeedDialFabSize - fabSize) / 2 + 16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (labelContent != null) {
            Card(
                modifier = Modifier.widthIn(max = labelMaxWidth),
                onClick = onClick,
                backgroundColor = labelBackgroundColor,
                elevation = labelContainerElevation,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    propagateMinConstraints = true,
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.subtitle2) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = labelContent,
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(fabSize),
            shape = fabShape,
            backgroundColor = fabBackgroundColor,
            contentColor = fabContentColor,
            elevation = fabElevation,
        ) {
            fabContent()
        }
    }
}
