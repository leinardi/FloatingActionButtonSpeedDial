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

@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)

package com.leinardi.android.speeddial.sample.compose

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        setContent {
            SampleTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    var speedDialState by rememberSaveable { mutableStateOf(SpeedDialState.Collapsed) }
    var speedDialVisible by rememberSaveable { mutableStateOf(true) }
    var reverseAnimationOnClose by rememberSaveable { mutableStateOf(false) }
    var overlayVisible: Boolean by rememberSaveable { mutableStateOf(speedDialState.isExpanded()) }
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Column {
                TopAppBar(
                    title = stringResource(R.string.app_name_compose),
                    subtitle = stringResource(R.string.app_version, GetVersionInteractor()(context.applicationContext)),
                )
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.primary,
                    elevation = AppBarDefaults.TopAppBarElevation,
                ) {
                    IconButton(
                        onClick = { speedDialVisible = !speedDialVisible },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_show_white_24dp),
                            contentDescription = stringResource(R.string.action_show_hide_fab),
                        )
                    }
                    IconButton(
                        onClick = { showSnackbar(scope, scaffoldState.snackbarHostState, context.getString(R.string.test_snackbar)) },
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
                            reverseAnimationOnClose = !reverseAnimationOnClose
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_animation_white_24dp),
                            contentDescription = stringResource(R.string.action_toggle_reverse_animation),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            var replaceActionVisible by rememberSaveable { mutableStateOf(false) }
            var deleteActionVisibility by rememberSaveable { mutableStateOf(false) }
            AnimatedVisibility(
                visible = speedDialVisible,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                SpeedDial(
                    state = speedDialState,
                    onFabClick = { expanded ->
                        overlayVisible = !expanded
                        speedDialState = SpeedDialState(!expanded)
                        if (expanded) {
                            showToast(context, context.getString(R.string.main_action_clicked))
                        }
                    },
                    fabOpenedContent = { Icon(Icons.Default.Edit, null) },
                    fabClosedContent = { Icon(Icons.Default.Add, null) },
                    fabAnimationRotateAngle = 90f,
                    labelContent = { Text("Close") },
                    reverseAnimationOnClose = reverseAnimationOnClose,
                ) {
                    item {
                        FabWithLabel(
                            onClick = { showToast(context, context.getString(R.string.no_action_clicked)) },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_link_white_24dp),
                                contentDescription = null,
                            )
                        }
                    }
                    item {
                        FabWithLabel(
                            onClick = { showToast(context, context.getString(R.string.label_custom_color) + " clicked!") },
                            labelContent = {
                                Text(
                                    text = stringResource(R.string.label_custom_color),
                                    color = Color(0XFFFFFFFF),
                                )
                            },
                            labelBackgroundColor = Color(0XFF4285F4),
                            fabBackgroundColor = Color(0XFFFFFFFF),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_custom_color),
                                contentDescription = null,
                                tint = Color(0XFF4285F4),
                            )
                        }
                    }
                    item {
                        FabWithLabel(
                            onClick = {
                                showSnackbar(
                                    scope = scope,
                                    snackbarHostState = scaffoldState.snackbarHostState,
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
                            labelBackgroundColor = Color.Transparent,
                            labelContainerElevation = 0.dp,
                            fabBackgroundColor = Color(0XFF4CAF50),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_white_24dp),
                                contentDescription = null,
                                tint = MaterialTheme.colors.onPrimary,
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
                                fabBackgroundColor = Color(0XFFFF9800),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_replace_white_24dp),
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onPrimary,
                                )
                            }
                        }
                    }
                    if (deleteActionVisibility) {
                        item {
                            FabWithLabel(
                                onClick = { deleteActionVisibility = false },
                                labelContent = { Text(stringResource(R.string.label_remove_action)) },
                                fabBackgroundColor = MaterialTheme.colors.secondary,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete_white_24dp),
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onPrimary,
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
                                    tint = MaterialTheme.colors.onSecondary,
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { scaffoldPadding ->
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            )
                        }
                    }
                }
            }
            SpeedDialOverlay(
                visible = overlayVisible,
                onClick = {
                    overlayVisible = false
                    speedDialState = speedDialState.toggle()
                },
            )
        }
    }
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
fun PreviewMainContent() {
    SampleTheme {
        MainContent()
    }
}
