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
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuInflater;
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
    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdapter;

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
        toolbarBottom.setContentInsetsAbsolute(0, 0);
        ActionMenuView actionMenuView = findViewById(R.id.amvMenu);
        if (actionMenuView != null) {
            actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
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
                        mSpeedDial.setMainFabAnimationRotateAngle(0);
                    } else if (id == R.id.action_rotation_angle_45) {
                        mSpeedDial.setMainFabAnimationRotateAngle(45);
                    } else if (id == R.id.action_rotation_angle_90) {
                        mSpeedDial.setMainFabAnimationRotateAngle(90);
                    } else if (id == R.id.action_rotation_angle_180) {
                        mSpeedDial.setMainFabAnimationRotateAngle(180);
                    } else if (id == R.id.action_main_fab_background_color_closed_primary) {
                        mSpeedDial.setMainFabClosedBackgroundColor(UiUtils.getPrimaryColor(BaseUseCaseActivity.this));
                    } else if (id == R.id.action_main_fab_background_color_closed_orange) {
                        mSpeedDial.setMainFabClosedBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_orange_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_closed_purple) {
                        mSpeedDial.setMainFabClosedBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_purple_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_closed_white) {
                        mSpeedDial.setMainFabClosedBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_white_1000, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_closed_none) {
                        mSpeedDial.setMainFabClosedBackgroundColor(0);
                    } else if (id == R.id.action_main_fab_background_color_opened_primary) {
                        mSpeedDial.setMainFabOpenedBackgroundColor(UiUtils.getPrimaryColor(BaseUseCaseActivity.this));
                    } else if (id == R.id.action_main_fab_background_color_opened_orange) {
                        mSpeedDial.setMainFabOpenedBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_orange_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_opened_purple) {
                        mSpeedDial.setMainFabOpenedBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_purple_500, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_opened_white) {
                        mSpeedDial.setMainFabOpenedBackgroundColor(
                                ResourcesCompat.getColor(getResources(), R.color.material_white_1000, getTheme()));
                    } else if (id == R.id.action_main_fab_background_color_opened_none) {
                        mSpeedDial.setMainFabOpenedBackgroundColor(0);
                    } else if (id == R.id.action_toggle_list) {
                        if (mRecyclerView.getAdapter() == null) {
                            mRecyclerView.setAdapter(mCustomAdapter);
                        } else {
                            mRecyclerView.setAdapter(null);
                        }
                    } else if (id == R.id.action_toggle_reverse_animation) {
                        mSpeedDial.setUseReverseAnimationOnClose(!mSpeedDial.getUseReverseAnimationOnClose());
                    }
                    return true;
                }
            });

            MenuInflater inflater = getMenuInflater();
            // use amvMenu here
            inflater.inflate(R.menu.menu_base_use_case, actionMenuView.getMenu());
        }
    }

    private void initRecyclerView() {
        initDataset();
        mRecyclerView = findViewById(R.id.recyclerView);
        if (mRecyclerView != null) {
            mCustomAdapter = new CustomAdapter(mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            mRecyclerView.setAdapter(mCustomAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    protected void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
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
