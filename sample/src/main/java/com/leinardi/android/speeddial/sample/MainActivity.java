/*
 * Copyright 2018 Roberto Leinardi.
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

package com.leinardi.android.speeddial.sample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.leinardi.android.speeddial.sample.usecases.BaseUseCaseActivity;
import com.leinardi.android.speeddial.sample.usecases.UseCasesActivity;

/**
 * Sample project
 */
@SuppressWarnings("PMD") // sample project with long methods
public class MainActivity extends BaseUseCaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ADD_ACTION_POSITION = 4;
    private SpeedDialView mSpeedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSpeedDial(savedInstanceState == null);

    }

    private void initSpeedDial(boolean addActionItems) {
        mSpeedDialView = findViewById(R.id.speedDial);

        if (addActionItems) {
            mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_no_label, R.drawable
                    .ic_link_white_24dp)
                    .create());

            Drawable drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_custom_color);
            mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_custom_color, drawable)
                    .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.material_white_1000,
                            getTheme()))
                    .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.inbox_primary, getTheme()))
                    .setLabel(R.string.label_custom_color)
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.inbox_primary,
                            getTheme()))
                    .create());

            mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_long_label, R.drawable
                    .ic_lorem_ipsum)
                    .setLabel("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                            "incididunt ut labore et dolore magna aliqua.")
                    .create());

            drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_add_white_24dp);
            mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_action, drawable)
                    .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.material_green_500,
                            getTheme()))
                    .setLabel(R.string.label_add_action)
                    .setLabelBackgroundColor(Color.TRANSPARENT)
                    .create());

            mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_custom_theme, R.drawable
                    .ic_theme_white_24dp)
                    .setLabel(getString(R.string.label_custom_theme))
                    .setTheme(R.style.AppTheme_Purple)
                    .create());

        }

        //Set main action clicklistener.
        mSpeedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                showToast("Main action clicked!");
                return false; // True to keep the Speed Dial open
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                Log.d(TAG, "Speed dial toggle state changed. Open = " + isOpen);
            }
        });

        //Set option fabs clicklisteners.
        mSpeedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_no_label:
                        showToast("No label action clicked!\nClosing with animation");
                        mSpeedDialView.close(); // To close the Speed Dial with animation
                        return true; // false will close it without animation
                    case R.id.fab_long_label:
                        showSnackbar(actionItem.getLabel(MainActivity.this) + " clicked!");
                        break;
                    case R.id.fab_custom_color:
                        showToast(actionItem.getLabel(MainActivity.this) + " clicked!\nClosing without animation.");
                        return false; // closes without animation (same as mSpeedDialView.close(false); return false;)
                    case R.id.fab_custom_theme:
                        showToast(actionItem.getLabel(MainActivity.this) + " clicked!");
                        break;
                    case R.id.fab_add_action:
                        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_replace_action,
                                R.drawable.ic_replace_white_24dp)
                                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color
                                                .material_orange_500,
                                        getTheme()))
                                .setLabel(getString(R.string.label_replace_action))
                                .create(), ADD_ACTION_POSITION);
                        break;
                    case R.id.fab_replace_action:
                        mSpeedDialView.replaceActionItem(new SpeedDialActionItem.Builder(R.id
                                .fab_remove_action,
                                R.drawable.ic_delete_white_24dp)
                                .setLabel(getString(R.string.label_remove_action))
                                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.inbox_accent,
                                        getTheme()))
                                .create(), ADD_ACTION_POSITION);
                        break;
                    case R.id.fab_remove_action:
                        mSpeedDialView.removeActionItemById(R.id.fab_remove_action);
                        break;
                    default:
                        break;
                }
                return true; // To keep the Speed Dial open
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //Closes menu if its opened.
        if (mSpeedDialView.isOpen()) {
            mSpeedDialView.close();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_use_cases) {
            startActivity(new Intent(MainActivity.this, UseCasesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }
}
