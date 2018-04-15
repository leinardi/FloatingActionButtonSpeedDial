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

package com.leinardi.android.speeddial.sample.usecases;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.leinardi.android.speeddial.UiUtils;
import com.leinardi.android.speeddial.sample.BuildConfig;
import com.leinardi.android.speeddial.sample.CustomAdapter;
import com.leinardi.android.speeddial.sample.R;

public abstract class BaseUseCaseActivity extends AppCompatActivity {
    private static final int DATASET_COUNT = 60;
    private String[] mDataset;
    private CoordinatorLayout mCoordinatorLayout;
    private Toast mToast;
    private Snackbar mSnackbar;
    private SpeedDialView mSpeedDial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        initToolbar();
        initRecyclerView();
        mSpeedDial = findViewById(R.id.speedDial);
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(getString(R.string.app_version, BuildConfig.VERSION_NAME));
        setSupportActionBar(toolbar);

        Toolbar toolbarBottom = findViewById(R.id.toolbar_bottom);
        if (toolbarBottom != null) {
            toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    //noinspection SimplifiableIfStatement
                    if (id == R.id.action_show) {
                        if (mSpeedDial.getVisibility() == View.VISIBLE) {
                            mSpeedDial.hide();
                        } else {
                            mSpeedDial.show();
                        }
                    } else if (id == R.id.action_snack) {
                        showSnackbar("Test snackbar");
                    } else if (id == R.id.action_add_item) {
                        mSpeedDial.addActionItem(new SpeedDialActionItem.Builder((int) SystemClock.uptimeMillis(),
                                R.drawable.ic_pencil_alt_white_24dp).create());
                    } else if (id == R.id.action_remove_item) {
                        int size = mSpeedDial.getActionItems().size();
                        if (size > 0) {
                            mSpeedDial.removeActionItem(size - 1);
                        }
                    } else if (id == R.id.action_expansion_mode_top) {
                        mSpeedDial.setExpansionMode(SpeedDialView.ExpansionMode.TOP);
                    } else if (id == R.id.action_expansion_mode_left) {
                        mSpeedDial.setExpansionMode(SpeedDialView.ExpansionMode.LEFT);
                    } else if (id == R.id.action_expansion_mode_bottom) {
                        mSpeedDial.setExpansionMode(SpeedDialView.ExpansionMode.BOTTOM);
                    } else if (id == R.id.action_expansion_mode_right) {
                        mSpeedDial.setExpansionMode(SpeedDialView.ExpansionMode.RIGHT);
                    } else if (id == R.id.action_rotation_angle_0) {
                        mSpeedDial.setMainFabCloseRotateAngle(0);
                    } else if (id == R.id.action_rotation_angle_45) {
                        mSpeedDial.setMainFabCloseRotateAngle(45);
                    } else if (id == R.id.action_rotation_angle_90) {
                        mSpeedDial.setMainFabCloseRotateAngle(90);
                    } else if (id == R.id.action_rotation_angle_180) {
                        mSpeedDial.setMainFabCloseRotateAngle(180);
                    } else if (id == R.id.action_main_fab_background_color_open_primary) {
                        mSpeedDial.setMainFabOpenBackgroundColor(UiUtils.getPrimaryColor(BaseUseCaseActivity.this));
                    } else if (id == R.id.action_main_fab_background_color_open_orange) {
                        mSpeedDial.setMainFabOpenBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_orange_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_open_purple) {
                        mSpeedDial.setMainFabOpenBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_purple_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_open_white) {
                        mSpeedDial.setMainFabOpenBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_white_1000, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_open_none) {
                        mSpeedDial.setMainFabOpenBackgroundColor(0);
                    } else if (id == R.id.action_main_fab_background_color_close_primary) {
                        mSpeedDial.setMainFabCloseBackgroundColor(UiUtils.getPrimaryColor(BaseUseCaseActivity.this));
                    } else if (id == R.id.action_main_fab_background_color_close_orange) {
                        mSpeedDial.setMainFabCloseBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_orange_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_close_purple) {
                        mSpeedDial.setMainFabCloseBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_purple_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_close_white) {
                        mSpeedDial.setMainFabCloseBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_white_1000, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_close_none) {
                        mSpeedDial.setMainFabCloseBackgroundColor(0);
                    }
                    return true;
                }
            });
            toolbarBottom.inflateMenu(R.menu.menu_use_cases);
        }
    }

    private void initRecyclerView() {
        initDataset();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            CustomAdapter adapter = new CustomAdapter(mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    protected void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void showSnackbar(String text) {
        mSnackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_SHORT);
        mSnackbar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnackbar.dismiss();
            }
        });
        mSnackbar.show();
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }
}
