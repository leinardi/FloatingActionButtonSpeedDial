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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.FloatingActionButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@Composable
fun SpeedDial(
    state: SpeedDialState,
    onFabClick: (expanded: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    // expansionMode: ExpansionMode = ExpansionMode.Top,
    fabShape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    fabElevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    fabAnimationRotateAngle: Float = 45f,
    fabClosedContent: @Composable () -> Unit = { Icon(Icons.Default.Add, null) },
    fabClosedBackgroundColor: Color = MaterialTheme.colors.secondary,
    fabClosedContentColor: Color = contentColorFor(fabClosedBackgroundColor),
    fabOpenedContent: @Composable () -> Unit = { Icon(Icons.Default.Close, null) },
    fabOpenedBackgroundColor: Color = MaterialTheme.colors.secondary,
    fabOpenedContentColor: Color = contentColorFor(fabOpenedBackgroundColor),
    reverseAnimationOnClose: Boolean = false,
    contentAnimationDelayInMillis: Int = 20,
    content: SpeedDialScope.() -> Unit = {},
) {
    val scope = SpeedDialScope().apply(content)
    val itemScope = SpeedDialItemScope()
    val transition: Transition<SpeedDialState> = updateTransition(targetState = state, label = "SpeedDialStateTransition")
    val rotation: Float by transition.animateFloat(label = "SpeedDialStateRotation") { speedDialState ->
        if (speedDialState == SpeedDialState.Expanded) fabAnimationRotateAngle else 0f
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
    ) {
        // Action items
        repeat(scope.intervals.size) { index ->
            val animationInSpecIntOffset: FiniteAnimationSpec<IntOffset> =
                tween(delayMillis = contentAnimationDelayInMillis * (scope.intervals.size - index))
            val animationInSpecFloat: FiniteAnimationSpec<Float> = tween(delayMillis = contentAnimationDelayInMillis * (scope.intervals.size - index))
            val animationOutSpecIntOffset: FiniteAnimationSpec<IntOffset> = tween(delayMillis = contentAnimationDelayInMillis * index)
            val animationOutSpecFloat: FiniteAnimationSpec<Float> = tween(delayMillis = contentAnimationDelayInMillis * index)
            AnimatedVisibility(
                visible = state == SpeedDialState.Expanded,
                enter = fadeIn(animationInSpecFloat) + slideInVertically(animationInSpecIntOffset) { it / 2 },
                exit = if (reverseAnimationOnClose) {
                    fadeOut(animationOutSpecFloat) + slideOutVertically(animationOutSpecIntOffset) { it / 2 }
                } else {
                    fadeOut()
                },
            ) {
                scope.intervals[index].item(itemScope)
            }
        }

        // Main FAB
        val fabModifier = Modifier
            .padding(top = 8.dp)
            .rotate(rotation)

        when (state) {
            SpeedDialState.Expanded -> FloatingActionButton(
                onClick = { onFabClick(true) },
                modifier = fabModifier,
                shape = fabShape,
                backgroundColor = fabOpenedBackgroundColor,
                contentColor = fabOpenedContentColor,
                elevation = fabElevation,
                content = {
                    Box(modifier = Modifier.rotate(-fabAnimationRotateAngle)) {
                        fabOpenedContent()
                    }
                },
            )
            SpeedDialState.Collapsed -> FloatingActionButton(
                onClick = { onFabClick(false) },
                modifier = fabModifier,
                shape = fabShape,
                backgroundColor = fabClosedBackgroundColor,
                contentColor = fabClosedContentColor,
                elevation = fabElevation,
                content = fabClosedContent,
            )
        }
    }
}
