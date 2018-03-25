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

package com.leinardi.yafabm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.view.View.VISIBLE;

/**
 * Behavior designed for use with {@link View} instances. Its main function
 * is to move {@link View} views so that any displayed {@link android.support.design.widget.Snackbar}s do
 * not cover them.
 */
@SuppressWarnings("unused")
public class ViewSnackbarBehavior extends CoordinatorLayout.Behavior<View> {
    private static final boolean AUTO_HIDE_DEFAULT = true;

    private Rect mTmpRect;
    private OnVisibilityChangedListener mInternalAutoHideListener;
    private boolean mAutoHideEnabled;

    public ViewSnackbarBehavior() {
        super();
        mAutoHideEnabled = AUTO_HIDE_DEFAULT;
    }

    public ViewSnackbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                android.support.design.R.styleable.FloatingActionButton_Behavior_Layout);
        mAutoHideEnabled = a.getBoolean(
                android.support.design.R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide,
                AUTO_HIDE_DEFAULT);
        a.recycle();
    }

    /**
     * Sets whether the associated View automatically hides when there is
     * not enough space to be displayed. This works with {@link AppBarLayout}
     * and {@link BottomSheetBehavior}.
     *
     * @param autoHide true to enable automatic hiding
     * @attr ref android.support.design.R.styleable#FloatingActionButton_Behavior_Layout_behavior_autoHide
     */
    public void setAutoHideEnabled(boolean autoHide) {
        mAutoHideEnabled = autoHide;
    }

    /**
     * Returns whether the associated View automatically hides when there is
     * not enough space to be displayed.
     *
     * @return true if enabled
     * @attr ref android.support.design.R.styleable#FloatingActionButton_Behavior_Layout_behavior_autoHide
     */
    public boolean isAutoHideEnabled() {
        return mAutoHideEnabled;
    }

    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams lp) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            // If the developer hasn't set dodgeInsetEdges, lets set it to BOTTOM so that
            // we dodge any Snackbars
            lp.dodgeInsetEdges = Gravity.BOTTOM;
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        if (dependency instanceof AppBarLayout) {
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the VIEW is anchored to the AppBarLayout
            updateFabVisibilityForAppBarLayout(parent, (AppBarLayout) dependency, child);
        } else if (isBottomSheet(dependency)) {
            updateFabVisibilityForBottomSheet(dependency, child);
        }
        return false;
    }

    private static boolean isBottomSheet(@NonNull View view) {
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof CoordinatorLayout.LayoutParams) {
            return ((CoordinatorLayout.LayoutParams) lp)
                    .getBehavior() instanceof BottomSheetBehavior;
        }
        return false;
    }

    @VisibleForTesting
    void setInternalAutoHideListener(OnVisibilityChangedListener listener) {
        mInternalAutoHideListener = listener;
    }

    private boolean shouldUpdateVisibility(View dependency, View child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (!mAutoHideEnabled) {
            return false;
        }

        if (lp.getAnchorId() != dependency.getId()) {
            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the VIEW
            return false;
        }

        //noinspection RedundantIfStatement
        if (child.getVisibility() != VISIBLE) {
            // The view isn't set to be visible so skip changing its visibility
            return false;
        }

        return true;
    }

    private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout parent,
                                                       AppBarLayout appBarLayout, View child) {
        if (!shouldUpdateVisibility(appBarLayout, child)) {
            return false;
        }

        if (mTmpRect == null) {
            mTmpRect = new Rect();
        }

        // First, let's get the visible rect of the dependency
        final Rect rect = mTmpRect;
        ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);

        if (rect.bottom <= getMinimumHeightForVisibleOverlappingContent(appBarLayout)) {
            // If the anchor's bottom is below the seam, we'll animate our VIEW out
            //            child.hide(mInternalAutoHideListener);
            child.setVisibility(View.GONE);
        } else {
            // Else, we'll animate our VIEW back in
            //            child.show(mInternalAutoHideListener);
            child.setVisibility(View.VISIBLE);
        }
        return true;
    }

    private int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
        int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
        if (minHeight != 0) {
            return minHeight * 2;
        } else {
            int childCount = appBarLayout.getChildCount();
            return childCount >= 1 ? ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) * 2 : 0;
        }
    }

    private boolean updateFabVisibilityForBottomSheet(View bottomSheet,
                                                      View child) {
        if (!shouldUpdateVisibility(bottomSheet, child)) {
            return false;
        }
        CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (bottomSheet.getTop() < child.getHeight() / 2 + lp.topMargin) {
            //            child.hide(mInternalAutoHideListener);
            child.setVisibility(View.GONE);
        } else {
            //            child.show(mInternalAutoHideListener);
            child.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child,
                                 int layoutDirection) {
        // First, let's make sure that the visibility of the VIEW is consistent
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, count = dependencies.size(); i < count; i++) {
            final View dependency = dependencies.get(i);
            if (dependency instanceof AppBarLayout) {
                if (updateFabVisibilityForAppBarLayout(
                        parent, (AppBarLayout) dependency, child)) {
                    break;
                }
            } else if (isBottomSheet(dependency)) {
                if (updateFabVisibilityForBottomSheet(dependency, child)) {
                    break;
                }
            }
        }
        // Now let the CoordinatorLayout lay out the VIEW
        parent.onLayoutChild(child, layoutDirection);
        return true;
    }
}
