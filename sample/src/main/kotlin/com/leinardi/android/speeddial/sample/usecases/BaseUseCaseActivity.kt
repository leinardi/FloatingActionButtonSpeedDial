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

package com.leinardi.android.speeddial.sample.usecases

import android.content.res.Configuration
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.leinardi.android.speeddial.UiUtils
import com.leinardi.android.speeddial.sample.CustomAdapter
import com.leinardi.android.speeddial.sample.R

abstract class BaseUseCaseActivity : AppCompatActivity() {
    private val coordinatorLayout by lazy { findViewById<CoordinatorLayout>(R.id.coordinatorLayout) }
    protected val speedDialView: SpeedDialView by lazy { findViewById(R.id.speedDial) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private var dataset = emptyArray<String>()
    private var toast: Toast? = null
    private var snackbar: Snackbar? = null
    private var customAdapter: CustomAdapter? = null

    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        initToolbar()
        initRecyclerView()
    }

    @Suppress("LongMethod", "ComplexMethod", "MagicNumber")
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            subtitle = getString(R.string.app_version, "TODO")
        }
        setSupportActionBar(toolbar)

        val toolbarBottom = findViewById<Toolbar>(R.id.toolbar_bottom)
        toolbarBottom.setContentInsetsAbsolute(0, 0)
        val actionMenuView = findViewById<ActionMenuView>(R.id.amvMenu)
        if (actionMenuView != null) {
            actionMenuView.setOnMenuItemClickListener { item ->
                val id = item.itemId

                when (id) {
                    R.id.action_show -> if (speedDialView.visibility == View.VISIBLE) {
                        speedDialView.hide()
                    } else {
                        speedDialView.show()
                    }
                    R.id.action_snack -> showSnackbar("Test snackbar")
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
                    R.id.action_toggle_day_night -> {
                        val nightModeFlats = resources.configuration.uiMode and
                                Configuration.UI_MODE_NIGHT_MASK
                        if (nightModeFlats == Configuration.UI_MODE_NIGHT_NO) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    }
                    R.id.action_main_fab_background_color_closed_primary ->
                        speedDialView.mainFabClosedBackgroundColor = UiUtils.getPrimaryColor(
                            this@BaseUseCaseActivity,
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
                        speedDialView.mainFabOpenedBackgroundColor = UiUtils.getPrimaryColor(
                            this@BaseUseCaseActivity,
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

    protected fun showToast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
        checkNotNull(toast).show()
    }

    protected fun showSnackbar(text: String) {
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
        private const val DATASET_COUNT = 60
    }
}
