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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipBorder
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun FabWithLabel(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    labelContent: @Composable (() -> Unit)? = null,
    labelColors: ChipColors = AssistChipDefaults.elevatedAssistChipColors(),
    labelMaxWidth: Dp = 160.dp,
    labelElevation: ChipElevation? = AssistChipDefaults.elevatedAssistChipElevation(),
    labelBorder: ChipBorder = AssistChipDefaults.assistChipBorder(),
    fabShape: Shape = FloatingActionButtonDefaults.shape,
    fabElevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    fabSize: Dp = SpeedDialMiniFabSize,
    fabContainerColor: Color = MaterialTheme.colorScheme.surface,
    fabContentColor: Color = contentColorFor(fabContainerColor),
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
            AssistChip(
                onClick = onClick,
                label = labelContent,
                modifier = Modifier.widthIn(max = labelMaxWidth),
                colors = labelColors,
                elevation = labelElevation,
                border = labelBorder,
            )
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(fabSize),
            shape = fabShape,
            containerColor = fabContainerColor,
            contentColor = fabContentColor,
            elevation = fabElevation,
            content = fabContent,
        )
    }
}
