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

package com.leinardi.android.speeddial.sample

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ActionMenuView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.leinardi.android.speeddial.UiUtils
import com.leinardi.android.speeddial.sample.interactor.ToggleNightModeInteractor

/**
 * Sample project
 */
@Suppress("LongMethod", "ComplexMethod", "MagicNumber")  // sample project with long methods
class ViewActivity : AppCompatActivity() {
    private val toggleNightModeInteractor = ToggleNightModeInteractor()
    private val coordinatorLayout by lazy { findViewById<CoordinatorLayout>(R.id.coordinatorLayout) }
    private val speedDialView: SpeedDialView by lazy { findViewById(R.id.speedDial) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private var dataset = emptyArray<String>()
    private var toast: Toast? = null
    private var snackbar: Snackbar? = null
    private var customAdapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }
        initToolbar()
        initRecyclerView()
        initSpeedDial(savedInstanceState == null)
    }

    private fun initSpeedDial(addActionItems: Boolean) {
        if (addActionItems) {
            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.fab_no_label,
                    R.drawable.ic_link_white_24dp,
                )
                    .create(),
            )

            var drawable = AppCompatResources.getDrawable(this, R.drawable.ic_custom_color)
            val customBackgroundColor = Color.parseColor("#90CAF9")
            val fabWithLabelView = speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_custom_color, drawable)
                    .setFabImageTintColor(Color.WHITE)
                    .setLabel(R.string.label_custom_color)
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(customBackgroundColor)
                    .create(),
            )
            fabWithLabelView?.let {
                it.speedDialActionItem = it.speedDialActionItemBuilder
                    .setFabBackgroundColor(customBackgroundColor)
                    .create()
            }

            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.fab_long_label,
                    R.drawable.ic_lorem_ipsum,
                )
                    .setFabSize(FloatingActionButton.SIZE_NORMAL)
                    .setLabel(getString(R.string.lorem_ipsum))
                    .create(),
            )

            drawable = AppCompatResources.getDrawable(this, R.drawable.ic_add_white_24dp)
            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_add_action, drawable)
                    .setFabBackgroundColor(UiUtils.getAttributeColor(this, R.attr.colorSecondaryContainer))
                    .setFabImageTintColor(UiUtils.getAttributeColor(this, R.attr.colorOnSecondaryContainer))
                    .setLabel(R.string.label_add_action)
                    .setLabelBackgroundColor(Color.TRANSPARENT)
                    .create(),
            )

            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.fab_custom_theme,
                    R.drawable.ic_theme_white_24dp,
                )
                    .setLabel(getString(R.string.label_custom_theme))
                    .setTheme(R.style.Theme_MyApp_Purple)
                    .create(),
            )
        }

        // Set main action clicklistener.
        speedDialView.setOnChangeListener(
            object : SpeedDialView.OnChangeListener {
                override fun onMainActionSelected(): Boolean {
                    showToast(getString(R.string.main_action_clicked))
                    return false  // True to keep the Speed Dial open
                }

                override fun onToggleChanged(isOpen: Boolean) {
                    Log.d(TAG, "Speed dial toggle state changed. Open = $isOpen")
                }
            },
        )

        // Set option fabs clicklisteners.
        speedDialView.setOnActionSelectedListener(
            SpeedDialView.OnActionSelectedListener { actionItem ->
                when (actionItem.id) {
                    R.id.fab_no_label -> {
                        showToast(getString(R.string.no_action_clicked))
                        speedDialView.close()  // To close the Speed Dial with animation
                        return@OnActionSelectedListener true  // false will close it without animation
                    }
                    R.id.fab_long_label -> showSnackbar(actionItem.getLabel(this) + " clicked!")
                    R.id.fab_custom_color -> {
                        showToast(actionItem.getLabel(this) + " clicked!\nClosing without animation.")
                        // closes without animation (same as speedDialView.close(false); return false;)
                        return@OnActionSelectedListener false
                    }
                    R.id.fab_custom_theme -> showToast(actionItem.getLabel(this) + " clicked!")
                    R.id.fab_add_action -> speedDialView.addActionItem(
                        SpeedDialActionItem.Builder(
                            R.id.fab_replace_action,
                            R.drawable.ic_replace_white_24dp,
                        )
                            .setFabBackgroundColor(UiUtils.getAttributeColor(this, R.attr.colorTertiaryContainer))
                            .setFabImageTintColor(UiUtils.getAttributeColor(this, R.attr.colorOnTertiaryContainer))
                            .setLabel(getString(R.string.label_replace_action))
                            .setLabelBackgroundColor(UiUtils.getAttributeColor(this, R.attr.colorTertiaryContainer))
                            .setLabelColor(UiUtils.getAttributeColor(this, R.attr.colorOnTertiaryContainer))
                            .create(),
                        ADD_ACTION_POSITION,
                    )
                    R.id.fab_replace_action -> speedDialView.replaceActionItem(
                        SpeedDialActionItem.Builder(
                            R.id
                                .fab_remove_action,
                            R.drawable.ic_delete_white_24dp,
                        )
                            .setFabBackgroundColor(UiUtils.getAttributeColor(this, R.attr.colorErrorContainer))
                            .setFabImageTintColor(UiUtils.getAttributeColor(this, R.attr.colorOnErrorContainer))
                            .setLabel(getString(R.string.label_remove_action))
                            .setLabelBackgroundColor(UiUtils.getAttributeColor(this, R.attr.colorErrorContainer))
                            .setLabelColor(UiUtils.getAttributeColor(this, R.attr.colorOnErrorContainer))
                            .create(),
                        ADD_ACTION_POSITION,
                    )
                    R.id.fab_remove_action -> speedDialView.removeActionItemById(R.id.fab_remove_action)
                }
                true  // To keep the Speed Dial open
            },
        )
    }

    override fun onBackPressed() {
        // Closes menu if its opened.
        if (speedDialView.isOpen) {
            speedDialView.close()
        } else {
            super.onBackPressed()
        }
    }

    @Suppress("LongMethod", "ComplexMethod", "MagicNumber")
    private fun initToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toolbarBottom = findViewById<MaterialToolbar>(R.id.toolbar_bottom)
        toolbarBottom.setContentInsetsAbsolute(0, 0)
        val actionMenuView = findViewById<ActionMenuView>(R.id.amvMenu)
        if (actionMenuView != null) {
            actionMenuView.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_show -> if (speedDialView.visibility == View.VISIBLE) {
                        speedDialView.hide()
                    } else {
                        speedDialView.show()
                    }
                    R.id.action_snack -> showSnackbar(getString(R.string.test_snackbar))
                    R.id.action_add_item ->
                        speedDialView.addActionItem(
                            SpeedDialActionItem.Builder(
                                SystemClock.uptimeMillis().toInt(),
                                R.drawable.ic_pencil_alt_white_24dp,
                            ).create(),
                        )
                    R.id.action_remove_item -> {
                        val size = speedDialView.actionItems.size
                        if (size > 0) {
                            speedDialView.removeActionItem(size - 1)
                        }
                    }
                    R.id.action_expansion_mode_top ->
                        speedDialView.expansionMode = SpeedDialView.ExpansionMode.TOP
                    R.id.action_expansion_mode_left ->
                        speedDialView.expansionMode = SpeedDialView.ExpansionMode.LEFT
                    R.id.action_expansion_mode_bottom ->
                        speedDialView.expansionMode = SpeedDialView.ExpansionMode.BOTTOM
                    R.id.action_expansion_mode_right ->
                        speedDialView.expansionMode = SpeedDialView.ExpansionMode.RIGHT
                    R.id.action_rotation_angle_0 -> speedDialView.mainFabAnimationRotateAngle = 0f
                    R.id.action_rotation_angle_45 -> speedDialView.mainFabAnimationRotateAngle = 45f
                    R.id.action_rotation_angle_90 -> speedDialView.mainFabAnimationRotateAngle = 90f
                    R.id.action_rotation_angle_180 -> speedDialView.mainFabAnimationRotateAngle = 180f
                    R.id.action_toggle_day_night -> toggleNightModeInteractor(this)
                    R.id.action_main_fab_background_color_closed_primary ->
                        speedDialView.mainFabClosedBackgroundColor = UiUtils.getAttributeColor(
                            this,
                            R.attr.colorSurface,
                        )
                    R.id.action_main_fab_background_color_closed_orange ->
                        speedDialView.mainFabClosedBackgroundColor = ResourcesCompat.getColor(
                            resources,
                            R.color.material_orange_500,
                            theme,
                        )
                    R.id.action_main_fab_background_color_closed_purple ->
                        speedDialView.mainFabClosedBackgroundColor = ResourcesCompat.getColor(
                            resources,
                            R.color.material_purple_500,
                            theme,
                        )
                    R.id.action_main_fab_background_color_closed_white ->
                        speedDialView.mainFabClosedBackgroundColor = ResourcesCompat.getColor(
                            resources,
                            R.color.material_white_1000,
                            theme,
                        )
                    R.id.action_main_fab_background_color_closed_none ->
                        speedDialView.mainFabClosedBackgroundColor = 0
                    R.id.action_main_fab_background_color_opened_primary ->
                        speedDialView.mainFabOpenedBackgroundColor = UiUtils.getAttributeColor(
                            this,
                            R.attr.colorSurface,
                        )
                    R.id.action_main_fab_background_color_opened_orange ->
                        speedDialView.mainFabOpenedBackgroundColor = ResourcesCompat.getColor(
                            resources,
                            R.color.material_orange_500,
                            theme,
                        )
                    R.id.action_main_fab_background_color_opened_purple ->
                        speedDialView.mainFabOpenedBackgroundColor = ResourcesCompat.getColor(
                            resources,
                            R.color.material_purple_500,
                            theme,
                        )
                    R.id.action_main_fab_background_color_opened_white ->
                        speedDialView.mainFabOpenedBackgroundColor = ResourcesCompat.getColor(
                            resources,
                            R.color.material_white_1000,
                            theme,
                        )
                    R.id.action_main_fab_background_color_opened_none ->
                        speedDialView.mainFabOpenedBackgroundColor = 0
                    R.id.action_toggle_list -> if (recyclerView.adapter == null) {
                        recyclerView.adapter = customAdapter
                    } else {
                        recyclerView.adapter = null
                    }
                    R.id.action_toggle_reverse_animation ->
                        speedDialView.useReverseAnimationOnClose = !speedDialView.useReverseAnimationOnClose
                }
                true
            }

            val inflater = menuInflater
            // use amvMenu here
            inflater.inflate(R.menu.menu_base_use_case, actionMenuView.menu)
        }
    }

    private fun initRecyclerView() {
        initDataset()
        customAdapter = CustomAdapter(dataset)
        // Set CustomAdapter as the adapter for RecyclerView.
        recyclerView.adapter = customAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
    }

    private fun showToast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
        checkNotNull(toast).show()
    }

    private fun showSnackbar(text: String) {
        snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
        checkNotNull(snackbar).apply {
            setAction("Close") { dismiss() }
            show()
        }
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private fun initDataset() {
        dataset = Array(DATASET_COUNT) { "This is element #$it" }
    }

    companion object {
        private const val ADD_ACTION_POSITION = 4
        private const val DATASET_COUNT = 60
        private val TAG = ViewActivity::class.java.simpleName
    }
}
