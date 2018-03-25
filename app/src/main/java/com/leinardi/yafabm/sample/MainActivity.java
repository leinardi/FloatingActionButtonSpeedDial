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

package com.leinardi.yafabm.sample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.leinardi.yafabm.FabMenuView;
import com.leinardi.yafabm.FabOptionItem;
import com.leinardi.yafabm.FabOverlayLayout;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FabMenuView mFabMenuView;
    private View mView;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        initToolbar();

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();
        initRecyclerView(savedInstanceState);

        initFabActionMenu();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(com.leinardi.yafabm.sample.R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFabActionMenu() {
        mFabMenuView = findViewById(com.leinardi.yafabm.sample.R.id.fab_l);

        FabOverlayLayout fabOverlayLayout = findViewById(R.id.overlay);

        FabOptionItem fabOptionItem = new FabOptionItem.Builder(R.id.fab_link, R.drawable.ic_link_white_24dp)
                .create();
        mFabMenuView.addFabOptionItem(fabOptionItem);

        fabOptionItem = new FabOptionItem.Builder(R.id.fab_add, R.drawable.ic_add_alarm_white_24dp)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green_fab, getTheme()))
                .setLabel(getString(R.string.fab_1))
                .create();
        mFabMenuView.addFabOptionItem(fabOptionItem);

        fabOptionItem = new FabOptionItem.Builder(R.id.fab_remove, R.drawable.ic_camera_alt_white_24dp)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_dark, getTheme()))
                .setLabelClickable(false)
                .setLabel("Long string that I should ellipsize bla bla blabla bla blabla bla bla")
                .create();
        mFabMenuView.addFabOptionItem(fabOptionItem);

        fabOptionItem = new FabOptionItem.Builder(R.id.fab_1, R.drawable.ic_add_alarm_white_24dp)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, getTheme()))
                .setLabel(getString(R.string.fab_1))
                .create();

        mFabMenuView.setFabOverlayLayout(fabOverlayLayout);

        mFabMenuView.addFabOptionItem(fabOptionItem);

        //Set main fab clicklistener.
        mFabMenuView.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Main fab clicked!", Toast.LENGTH_SHORT).show();
                if (mFabMenuView.isFabMenuOpen()) {
                    mFabMenuView.closeOptionsMenu();
                }
            }
        });

        //Set option fabs clicklisteners.
        mFabMenuView.setOptionFabSelectedListener(new FabMenuView.OnOptionFabSelectedListener() {
            @Override
            public void onOptionFabSelected(FabOptionItem fabOptionItem) {
                switch (fabOptionItem.getId()) {
                    case R.id.fab_add:
                        Snackbar.make(mFabMenuView, fabOptionItem.getLabel() + " clicked!", Snackbar.LENGTH_LONG)
                                .show();
                        //                                Toast.makeText(
                        //                                        getApplicationContext(),
                        //                                        fabOptionItem.getTitle() + " clicked!",
                        //                                        Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.fab_remove:
                        final Snackbar mySnackbar = Snackbar.make(mFabMenuView,
                                fabOptionItem.getLabel() + " clicked!", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mySnackbar.dismiss();
                            }
                        });
                        mySnackbar.show();
                        break;
                    case R.id.fab_link:
                        //                                Snackbar.make(mFabMenuView, fabOptionItem.getTitle() + "
                        // clicked!", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),
                                fabOptionItem.getLabel() + "clicked!",
                                Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        });

        mView = findViewById(R.id.button);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FAB pressed");
                //                        Snackbar.make(view, "Hi, welcome to my app!", Snackbar.LENGTH_LONG).show();
                FabOptionItem fabOptionItem = new FabOptionItem.Builder(((int) System.currentTimeMillis()), R.drawable
                        .ic_add_alarm_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_light,
                                getTheme()))
                        .setLabel("poba")
                        .create();
                mFabMenuView.replaceFabOptionItem(3, fabOptionItem);
            }
        });

        mFabMenuView.attachToRecyclerView(mRecyclerView);

    }

    private void initRecyclerView(Bundle savedInstanceState) {
        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(MainActivity.this);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new CustomAdapter(mDataset);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        //        mLinearLayoutRadioButton = (RadioButton) findViewById(R.id.linear_layout_rb);
        //        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        //            }
        //        });

        //        mGridLayoutRadioButton = (RadioButton) findViewById(R.id.grid_layout_rb);
        //        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
        //            }
        //        });
        //
        //        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(MainActivity.this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(MainActivity.this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(MainActivity.this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.leinardi.yafabm.sample.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Closes menu if its opened.
        if (mFabMenuView.isFabMenuOpen()) {
            mFabMenuView.closeOptionsMenu();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.leinardi.yafabm.sample.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
