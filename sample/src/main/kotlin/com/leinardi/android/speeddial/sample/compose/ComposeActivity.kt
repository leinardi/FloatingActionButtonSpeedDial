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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.leinardi.android.speeddial.sample.compose

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.leinardi.android.speeddial.compose.FabWithLabel
import com.leinardi.android.speeddial.compose.SpeedDial
import com.leinardi.android.speeddial.compose.SpeedDialOverlay
import com.leinardi.android.speeddial.compose.SpeedDialState
import com.leinardi.android.speeddial.sample.R
import com.leinardi.android.speeddial.sample.compose.component.TopAppBar
import com.leinardi.android.speeddial.sample.compose.theme.PurpleTheme
import com.leinardi.android.speeddial.sample.compose.theme.SampleTheme
import com.leinardi.android.speeddial.sample.interactor.GetVersionInteractor
import com.leinardi.android.speeddial.sample.interactor.ToggleNightModeInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Sample compose project
 */
class ComposeActivity : AppCompatActivity() {  // AppCompatActivity is needed to be able to toggle Day/Night programmatically
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SampleTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val context = LocalContext.current
    val speedDialState = rememberSaveable { mutableStateOf(SpeedDialState.Collapsed) }
    val overlayVisible = rememberSaveable { mutableStateOf(speedDialState.value.isExpanded()) }
    val reverseAnimationOnClose = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val speedDialVisible = rememberSaveable { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TopBar(scrollBehavior) },
        bottomBar = { BottomBar(speedDialVisible, scope, snackbarHostState, context, reverseAnimationOnClose) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                speedDialState,
                overlayVisible,
                speedDialVisible,
                reverseAnimationOnClose,
                scope,
                snackbarHostState,
            )
        },
    ) { scaffoldPadding ->
        ScaffoldContent(scaffoldPadding, overlayVisible, speedDialState)
    }
}

@Composable
private fun TopBar(scrollBehavior: TopAppBarScrollBehavior) {
    val context = LocalContext.current
    TopAppBar(
        title = stringResource(R.string.app_name_compose),
        subtitle = stringResource(R.string.app_version, GetVersionInteractor()(context.applicationContext)),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun BottomBar(
    speedDialVisible: MutableState<Boolean>,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context,
    reverseAnimationOnClose: MutableState<Boolean>,
) {
    BottomAppBar {
        IconButton(
            onClick = { speedDialVisible.value = !speedDialVisible.value },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_show_white_24dp),
                contentDescription = stringResource(R.string.action_show_hide_fab),
            )
        }
        IconButton(
            onClick = { showSnackbar(scope, snackbarHostState, context.getString(R.string.test_snackbar)) },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_snack_white_24dp),
                contentDescription = stringResource(R.string.action_show_snackbar),
            )
        }
        IconButton(
            onClick = {
                ToggleNightModeInteractor()(context)
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_day_night_white_24dp),
                contentDescription = stringResource(R.string.action_toggle_day_night),
            )
        }
        IconButton(
            onClick = {
                reverseAnimationOnClose.value = !reverseAnimationOnClose.value
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_animation_white_24dp),
                contentDescription = stringResource(R.string.action_toggle_reverse_animation),
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FloatingActionButton(
    speedDialState: MutableState<SpeedDialState>,
    overlayVisible: MutableState<Boolean>,
    speedDialVisible: MutableState<Boolean>,
    reverseAnimationOnClose: MutableState<Boolean>,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    BackHandler(enabled = speedDialState.value.isExpanded()) {
        closeSpeedDial(overlayVisible, speedDialState)
    }
    var replaceActionVisible by rememberSaveable { mutableStateOf(false) }
    var deleteActionVisibility by rememberSaveable { mutableStateOf(false) }
    AnimatedVisibility(
        visible = speedDialVisible.value,
        enter = scaleIn(),
        exit = scaleOut(),
    ) {
        val context = LocalContext.current
        SpeedDial(
            state = speedDialState.value,
            onFabClick = { expanded ->
                closeSpeedDial(overlayVisible, speedDialState)
                if (expanded) {
                    showToast(context, context.getString(R.string.main_action_clicked))
                }
            },
            fabOpenedContent = { Icon(Icons.Default.Edit, null) },
            fabClosedContent = { Icon(Icons.Default.Add, null) },
            fabAnimationRotateAngle = 90f,
            labelContent = { Text("Close") },
            reverseAnimationOnClose = reverseAnimationOnClose.value,
        ) {
            item {
                FabWithLabel(
                    onClick = {
                        closeSpeedDial(overlayVisible, speedDialState)
                        showToast(context, context.getString(R.string.no_action_clicked))
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_link_white_24dp),
                        contentDescription = null,
                    )
                }
            }
            item {
                FabWithLabel(
                    onClick = {
                        showToast(context, context.getString(R.string.label_custom_color) + " clicked!")
                        closeSpeedDial(overlayVisible, speedDialState)
                    },
                    labelContent = {
                        Text(
                            text = stringResource(R.string.label_custom_color),
                            color = Color(0XFFFFFFFF),
                        )
                    },
                    labelColors = AssistChipDefaults.elevatedAssistChipColors(containerColor = Color(0XFF90CAF9)),
                    fabContainerColor = Color.White,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_custom_color),
                        contentDescription = null,
                        tint = Color(0XFF90CAF9),
                    )
                }
            }
            item {
                FabWithLabel(
                    onClick = {
                        showSnackbar(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = context.getString(R.string.lorem_ipsum) + " clicked!",
                        )
                    },
                    labelContent = {
                        Text(
                            text = stringResource(R.string.lorem_ipsum),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    fabSize = 56.dp,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lorem_ipsum),
                        contentDescription = null,
                    )
                }
            }
            item {
                FabWithLabel(
                    onClick = { replaceActionVisible = true },
                    labelContent = { Text(stringResource(R.string.label_add_action)) },
                    labelColors = AssistChipDefaults.assistChipColors(),
                    labelElevation = AssistChipDefaults.assistChipElevation(),
                    labelBorder = AssistChipDefaults.assistChipBorder(Color.Transparent, Color.Transparent, 0.dp),
                    fabContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_white_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            if (replaceActionVisible) {
                item {
                    FabWithLabel(
                        onClick = {
                            replaceActionVisible = false
                            deleteActionVisibility = true
                        },
                        labelContent = { Text(stringResource(R.string.label_replace_action)) },
                        labelColors = AssistChipDefaults.elevatedAssistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                        fabContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_replace_white_24dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
            if (deleteActionVisibility) {
                item {
                    FabWithLabel(
                        onClick = { deleteActionVisibility = false },
                        labelContent = { Text(stringResource(R.string.label_remove_action)) },
                        labelColors = AssistChipDefaults.elevatedAssistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                        fabContainerColor = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete_white_24dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
            item {
                PurpleTheme {
                    FabWithLabel(
                        onClick = { showToast(context, context.getString(R.string.label_custom_theme) + " clicked!") },
                        labelContent = { Text(stringResource(R.string.label_custom_theme)) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_theme_white_24dp),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScaffoldContent(
    scaffoldPadding: PaddingValues,
    overlayVisible: MutableState<Boolean>,
    speedDialState: MutableState<SpeedDialState>,
) {
    Box {
        LazyColumn(
            modifier = Modifier.padding(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = scaffoldPadding.calculateBottomPadding(),
            ),
        ) {
            repeat(60) { index ->
                item {
                    Surface(
                        modifier = Modifier.clickable {
                            Timber.d("Element $index  clicked.")
                        },
                    ) {
                        Text(
                            text = "This is element #$index",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .alpha(ContentAlpha.medium)
                                .fillMaxWidth()
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
        SpeedDialOverlay(
            visible = overlayVisible.value,
            onClick = {
                closeSpeedDial(overlayVisible, speedDialState)
            },
        )
    }
}

private fun closeSpeedDial(
    overlayVisible: MutableState<Boolean>,
    speedDialState: MutableState<SpeedDialState>,
) {
    speedDialState.value = speedDialState.value.toggle()
    overlayVisible.value = speedDialState.value.isExpanded()
}

private fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

private fun showSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
) {
    scope.launch {
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short,
        )
    }
}

@Preview
@Composable
private fun PreviewMainContent() {
    SampleTheme {
        MainContent()
    }
}
