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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.leinardi.android.speeddial.compose.FabWithLabel
import com.leinardi.android.speeddial.compose.SpeedDial
import com.leinardi.android.speeddial.compose.SpeedDialOverlay
import com.leinardi.android.speeddial.compose.SpeedDialState
import com.leinardi.android.speeddial.sample.R
import com.leinardi.android.speeddial.sample.compose.component.TopAppBar
import com.leinardi.android.speeddial.sample.compose.theme.PurpleTheme
import com.leinardi.android.speeddial.sample.compose.theme.SampleTheme

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
    var overlayVisibility: Boolean by rememberSaveable { mutableStateOf(speedDialState == SpeedDialState.Expanded) }
    var replaceActionVisibility by rememberSaveable { mutableStateOf(false) }
    var deleteActionVisibility by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = stringResource(R.string.app_name_compose),
                    subtitle = "vTODO",  // TODO
                )
            }
        },
        floatingActionButton = {
            SpeedDial(
                state = speedDialState,
                onFabClick = { expanded ->
                    overlayVisibility = !expanded
                    speedDialState = SpeedDialState(!expanded)
                },
                fabOpenedContent = { Icon(Icons.Default.Edit, null) },
                fabClosedContent = { Icon(Icons.Default.Add, null) },
                fabAnimationRotateAngle = 90f,
                reverseAnimationOnClose = false,
            ) {
                item {
                    FabWithLabel(
                        onClick = {},
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_link_white_24dp),
                            contentDescription = null,
                        )
                    }
                }
                item {
                    FabWithLabel(
                        onClick = {},
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
                        onClick = {},
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
                            painter = painterResource(R.drawable.ic_custom_color),
                            contentDescription = null,
                        )
                    }
                }
                item {
                    FabWithLabel(
                        onClick = { replaceActionVisibility = true },
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
                if (replaceActionVisibility) {
                    item {
                        FabWithLabel(
                            onClick = {
                                replaceActionVisibility = false
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
                            onClick = { },
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
                            modifier = Modifier.clickable { },
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
                visible = overlayVisibility,
                onClick = {
                    overlayVisibility = false
                    speedDialState = speedDialState.toggle()
                },
            )
        }
    }
}
