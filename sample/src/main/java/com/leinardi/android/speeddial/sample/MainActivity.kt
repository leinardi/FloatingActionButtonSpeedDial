/*
 * Copyright 2019 Roberto Leinardi.
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

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.leinardi.android.speeddial.sample.usecases.BaseUseCaseActivity
import com.leinardi.android.speeddial.sample.usecases.UseCasesActivity

/**
 * Sample project
 */
@Suppress("LongMethod", "ComplexMethod", "MagicNumber") // sample project with long methods
class MainActivity : BaseUseCaseActivity() {
    override val layoutRes = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSpeedDial(savedInstanceState == null)
    }

    private fun initSpeedDial(addActionItems: Boolean) {
        if (addActionItems) {
            speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id.fab_no_label, R.drawable
                    .ic_link_white_24dp)
                    .create())

            var drawable = AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_custom_color)
            val fabWithLabelView = speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id
                    .fab_custom_color, drawable)
                    .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.inbox_primary, theme))
                    .setLabel(R.string.label_custom_color)
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.inbox_primary,
                            theme))
                    .create())
            fabWithLabelView?.apply {
                speedDialActionItem = speedDialActionItemBuilder
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.material_white_1000, theme))
                        .create()
            }

            speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id.fab_long_label, R.drawable
                    .ic_lorem_ipsum)
                    .setLabel("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                            "incididunt ut labore et dolore magna aliqua.")
                    .create())

            drawable = AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_add_white_24dp)
            speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id.fab_add_action, drawable)
                    .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.material_green_500, theme))
                    .setLabel(R.string.label_add_action)
                    .setLabelBackgroundColor(Color.TRANSPARENT)
                    .create())

            speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id.fab_custom_theme, R.drawable
                    .ic_theme_white_24dp)
                    .setLabel(getString(R.string.label_custom_theme))
                    .setTheme(R.style.AppTheme_Purple)
                    .create())
        }

        // Set main action clicklistener.
        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                showToast("Main action clicked!")
                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
                Log.d(TAG, "Speed dial toggle state changed. Open = $isOpen")
            }
        })

        // Set option fabs clicklisteners.
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_no_label -> {
                    showToast("No label action clicked!\nClosing with animation")
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_long_label -> showSnackbar(actionItem.getLabel(this@MainActivity) + " clicked!")
                R.id.fab_custom_color -> {
                    showToast(actionItem.getLabel(this@MainActivity) + " clicked!\nClosing without animation.")
                    // closes without animation (same as speedDialView.close(false); return false;)
                    return@OnActionSelectedListener false
                }
                R.id.fab_custom_theme -> showToast(actionItem.getLabel(this@MainActivity) + " clicked!")
                R.id.fab_add_action -> speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id.fab_replace_action,
                        R.drawable.ic_replace_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color
                                .material_orange_500,
                                theme))
                        .setLabel(getString(R.string.label_replace_action))
                        .create(), ADD_ACTION_POSITION)
                R.id.fab_replace_action -> speedDialView.replaceActionItem(SpeedDialActionItem.Builder(R.id
                        .fab_remove_action,
                        R.drawable.ic_delete_white_24dp)
                        .setLabel(getString(R.string.label_remove_action))
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.inbox_accent,
                                theme))
                        .create(), ADD_ACTION_POSITION)
                R.id.fab_remove_action -> speedDialView.removeActionItemById(R.id.fab_remove_action)
            }
            true // To keep the Speed Dial open
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        // Closes menu if its opened.
        if (speedDialView.isOpen) {
            speedDialView.close()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_use_cases) {
            startActivity(Intent(this@MainActivity, UseCasesActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val ADD_ACTION_POSITION = 4
    }
}
