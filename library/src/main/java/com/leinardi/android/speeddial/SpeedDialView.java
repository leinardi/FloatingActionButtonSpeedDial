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

package com.leinardi.android.speeddial;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.leinardi.android.speeddial.SpeedDialActionItem.NOT_SET;
import static com.leinardi.android.speeddial.SpeedDialView.ExpansionMode.BOTTOM;
import static com.leinardi.android.speeddial.SpeedDialView.ExpansionMode.LEFT;
import static com.leinardi.android.speeddial.SpeedDialView.ExpansionMode.RIGHT;
import static com.leinardi.android.speeddial.SpeedDialView.ExpansionMode.TOP;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings({"unused", "WeakerAccess"})
@CoordinatorLayout.DefaultBehavior(SpeedDialView.SnackbarBehavior.class)
public class SpeedDialView extends LinearLayout {

    private static final String TAG = SpeedDialView.class.getSimpleName();
    private List<FabWithLabelView> mFabWithLabelViews = new ArrayList<>();
    private FloatingActionButton mMainFab;
    private boolean mIsFabMenuOpen = false;
    private Drawable mMainFabOpenDrawable = null;
    private Drawable mMainFabCloseDrawable = null;
    private OnOptionFabSelectedListener mOnOptionFabSelectedListener;
    private SpeedDialOverlayLayout mSpeedDialOverlayLayout;
    @ExpansionMode
    private int mExpansionMode;

    public SpeedDialView(Context context) {
        super(context);
        init(context, null);
    }

    public SpeedDialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeedDialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @ExpansionMode
    public int getExpansionMode() {
        return mExpansionMode;
    }

    public void setExpansionMode(@ExpansionMode int expansionMode) {
        mExpansionMode = expansionMode;
        switch (expansionMode) {
            case TOP:
            case BOTTOM:
                setOrientation(VERTICAL);
                for (FabWithLabelView fabWithLabelView : mFabWithLabelViews) {
                    fabWithLabelView.setOrientation(HORIZONTAL);
                }
                break;
            case LEFT:
            case RIGHT:
                setOrientation(HORIZONTAL);
                for (FabWithLabelView fabWithLabelView : mFabWithLabelViews) {
                    fabWithLabelView.setOrientation(VERTICAL);
                }
                break;
        }

    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

    public void show() {
        show(null);
    }

    public void show(@Nullable final OnVisibilityChangedListener listener) {
        mMainFab.show(listener);
    }

    public void hide() {
        hide(null);
    }

    public void hide(@Nullable OnVisibilityChangedListener listener) {
        if (isFabMenuOpen()) {
            closeOptionsMenu();
        }
        mMainFab.hide();
    }

    public SpeedDialOverlayLayout getSpeedDialOverlayLayout() {
        return mSpeedDialOverlayLayout;
    }

    public void setSpeedDialOverlayLayout(SpeedDialOverlayLayout speedDialOverlayLayout) {
        mSpeedDialOverlayLayout = speedDialOverlayLayout;
        mSpeedDialOverlayLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOptionsMenu();
            }
        });
    }

    public void addAllFabOptionItem(Collection<SpeedDialActionItem> speedDialActionItemList) {
        for (SpeedDialActionItem speedDialActionItem : speedDialActionItemList) {
            addFabOptionItem(speedDialActionItem);
        }
    }

    public void addFabOptionItem(SpeedDialActionItem speedDialActionItem) {
        addFabOptionItem(speedDialActionItem, mFabWithLabelViews.size());
    }

    public void addFabOptionItem(SpeedDialActionItem speedDialActionItem, int position) {
        addFabOptionItem(speedDialActionItem, position, true);
    }

    public void addFabOptionItem(SpeedDialActionItem speedDialActionItem, int position, boolean animate) {
        FabWithLabelView oldView = findFabWithLabelViewById(speedDialActionItem.getId());
        if (oldView != null) {
            replaceFabOptionItem(oldView.getSpeedDialActionItem(), speedDialActionItem);
        } else {
            FabWithLabelView newView = createNewFabWithLabelView(speedDialActionItem);
            int layoutPosition = getLayoutPosition(position);
            addView(newView, layoutPosition);
            mFabWithLabelViews.add(position, newView);
            if (isFabMenuOpen()) {
                if (animate) {
                    showWithAnimationFabWithLabelView(newView, 0);
                }
            } else {
                newView.setVisibility(GONE);
            }
        }
    }

    private int getLayoutPosition(int position) {
        if (mExpansionMode == TOP || mExpansionMode == LEFT) {
            return mFabWithLabelViews.size() - position;
        } else {
            return position + 1;
        }
    }

    public boolean removeFabOptionItem(int position) {
        return removeFabOptionItem(mFabWithLabelViews.get(position).getSpeedDialActionItem());
    }

    public boolean removeFabOptionItem(SpeedDialActionItem speedDialActionItem) {
        return removeFabOptionItemById(speedDialActionItem.getId());
    }

    public boolean removeFabOptionItemById(@IdRes int idRes) {
        return removeFabOptionItem(findFabWithLabelViewById(idRes));
    }

    public boolean replaceFabOptionItem(SpeedDialActionItem newSpeedDialActionItem, int position) {
        return replaceFabOptionItem(mFabWithLabelViews.get(position).getSpeedDialActionItem(), newSpeedDialActionItem);
    }

    public boolean replaceFabOptionItem(SpeedDialActionItem oldSpeedDialActionItem, SpeedDialActionItem
            newSpeedDialActionItem) {
        FabWithLabelView oldView = findFabWithLabelViewById(oldSpeedDialActionItem.getId());
        if (oldView != null) {
            int index = mFabWithLabelViews.indexOf(oldView);
            if (index < 0) {
                return false;
            }
            removeFabOptionItem(findFabWithLabelViewById(newSpeedDialActionItem.getId()), null, false);
            removeFabOptionItem(findFabWithLabelViewById(oldSpeedDialActionItem.getId()), null, false);
            addFabOptionItem(newSpeedDialActionItem, index, false);
            return true;
        } else {
            return false;
        }
    }

    public void clearFabOptionItems() {
        Iterator<FabWithLabelView> it = mFabWithLabelViews.iterator();
        while (it.hasNext()) {
            FabWithLabelView fabWithLabelView = it.next();
            removeFabOptionItem(fabWithLabelView, it, true);
        }
    }

    /**
     * Set a listener that will be notified when a menu fab is selected.
     *
     * @param listener listener to set.
     */
    public void setOptionFabSelectedListener(final OnOptionFabSelectedListener listener) {
        mOnOptionFabSelectedListener = listener;
        for (int optionFabIndex = 0; optionFabIndex < mFabWithLabelViews.size(); optionFabIndex++) {
            final FabWithLabelView fabWithLabelView = mFabWithLabelViews.get(optionFabIndex);
            fabWithLabelView.setOptionFabSelectedListener(mOnOptionFabSelectedListener);
        }
    }

    /**
     * Set Main FloatingActionButton ClickMOnOptionFabSelectedListener.
     *
     * @param listener listener to set.
     */
    public void setMainFabOnClickListener(@Nullable final OnClickListener listener) {
        mMainFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!mIsFabMenuOpen && !mFabWithLabelViews.isEmpty()) {
                    openOptionsMenu();
                } else {
                    if (listener == null) {
                        closeOptionsMenu();
                    } else {
                        listener.onClick(view);
                    }
                }
            }
        });
    }

    /**
     * Opens options menu.
     */
    public void openOptionsMenu() {
        toggleOptionsMenu(true);
    }

    /**
     * Closes options menu.
     */
    public void closeOptionsMenu() {
        toggleOptionsMenu(false);
    }

    public void toggleOptionsMenu() {
        toggleOptionsMenu(!mIsFabMenuOpen);
    }

    /**
     * Return returns true if menu opened,false otherwise.
     */
    public boolean isFabMenuOpen() {
        return mIsFabMenuOpen;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        ArrayList<SpeedDialActionItem> speedDialActionItems = new ArrayList<>(mFabWithLabelViews.size());
        for (FabWithLabelView fabWithLabelView : mFabWithLabelViews) {
            speedDialActionItems.add(fabWithLabelView.getSpeedDialActionItem());
        }
        bundle.putParcelableArrayList(SpeedDialActionItem.class.getName(), speedDialActionItems);
        bundle.putBoolean("IsFabMenuOpen", mIsFabMenuOpen);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) { // implicit null check
            Bundle bundle = (Bundle) state;
            ArrayList<SpeedDialActionItem> speedDialActionItems = bundle.getParcelableArrayList(SpeedDialActionItem
                    .class.getName());
            if (speedDialActionItems != null && !speedDialActionItems.isEmpty()) {
                //                Collections.reverse(speedDialActionItems);
                addAllFabOptionItem(speedDialActionItems);
            }
            toggleOptionsMenu(bundle.getBoolean("IsFabMenuOpen", mIsFabMenuOpen));
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private boolean removeFabOptionItem(FabWithLabelView view, @Nullable Iterator<FabWithLabelView> it, boolean
            animate) {
        if (view != null) {
            if (it != null) {
                it.remove();
            } else {
                mFabWithLabelViews.remove(view);
            }

            if (isFabMenuOpen()) {
                if (mFabWithLabelViews.isEmpty()) {
                    closeOptionsMenu();
                }
                if (animate) {
                    UiUtils.shrinkAnim(view, true);
                } else {
                    removeView(view);
                }
            } else {
                removeView(view);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean removeFabOptionItem(FabWithLabelView view) {
        return removeFabOptionItem(view, null, true);
    }

    private void init(Context context, AttributeSet attrs) {
        mMainFab = createMainFab();
        addView(mMainFab);
        setExpansionMode(TOP);
        setClipChildren(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(getResources().getDimension(R.dimen.sd_close_elevation));
        }
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.SpeedDialView, 0, 0);
        try {
            @DrawableRes int openDrawableRes = attr.getResourceId(R.styleable.SpeedDialView_srcCompat, NOT_SET);
            if (openDrawableRes == NOT_SET) {
                openDrawableRes = attr.getResourceId(R.styleable.SpeedDialView_srcCompat, NOT_SET);
            }
            if (openDrawableRes != NOT_SET) {
                mMainFabOpenDrawable = AppCompatResources.getDrawable(getContext(), openDrawableRes);
            }
            int closeDrawableRes = attr.getResourceId(R.styleable.SpeedDialView_close_src, NOT_SET);
            if (openDrawableRes != NOT_SET) {
                mMainFabCloseDrawable = UiUtils.getRotateDrawable(context, closeDrawableRes);
            }
            //            int color = attr.getColor(
            //                    R.styleable.SpeedDialView_color,
            //                    UiUtils.getAccentColor(context));
            //            mMainFab.setBackgroundTintList(ColorStateList.valueOf(color));
        } catch (Exception e) {
            Log.e(TAG, "Failure setting FabWithLabelView icon", e);
        } finally {
            attr.recycle();
        }
        mMainFab.setImageDrawable(mMainFabOpenDrawable);
    }

    private FloatingActionButton createMainFab() {
        FloatingActionButton floatingActionButton = new FloatingActionButton(getContext());
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.END;
        int margin = UiUtils.dpToPx(getContext(), 4);
        layoutParams.setMargins(0, 0, margin, 0);
        floatingActionButton.setUseCompatPadding(true);
        floatingActionButton.setLayoutParams(layoutParams);
        floatingActionButton.setClickable(true);
        floatingActionButton.setFocusable(true);
        floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
        return floatingActionButton;
    }

    private FabWithLabelView createNewFabWithLabelView(SpeedDialActionItem speedDialActionItem) {
        FabWithLabelView newView;
        int theme = speedDialActionItem.getTheme();
        if (theme == NOT_SET) {
            newView = new FabWithLabelView(getContext());
        } else {
            newView = new FabWithLabelView(new ContextThemeWrapper(getContext(), theme), null, theme);
        }
        newView.setSpeedDialActionItem(speedDialActionItem);
        newView.setOrientation(getOrientation() == VERTICAL ? HORIZONTAL : VERTICAL);
        newView.setOptionFabSelectedListener(mOnOptionFabSelectedListener);
        return newView;
    }

    private void toggleOptionsMenu(boolean show) {
        visibilitySetup(show);
        mIsFabMenuOpen = show;
        if (show) {
            if (mMainFabCloseDrawable != null) {
                mMainFab.setImageDrawable(mMainFabCloseDrawable);
            }
            UiUtils.rotateForward(mMainFab);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.sd_open_elevation));
            } else {
                bringToFront();
            }
        } else {
            UiUtils.rotateBackward(mMainFab);
            if (mMainFabCloseDrawable != null) {
                mMainFab.setImageDrawable(mMainFabOpenDrawable);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.sd_close_elevation));
            }
        }
        showHideOverlay(show);
    }

    private void showHideOverlay(boolean show) {
        if (mSpeedDialOverlayLayout != null) {
            if (show) {
                mSpeedDialOverlayLayout.show();
            } else {
                mSpeedDialOverlayLayout.hide();
            }
        }
    }

    @Nullable
    private FabWithLabelView findFabWithLabelViewById(@IdRes int id) {
        for (FabWithLabelView fabWithLabelView : mFabWithLabelViews) {
            if (fabWithLabelView.getId() == id) {
                return fabWithLabelView;
            }
        }
        return null;
    }

    /**
     * Menu opening animation.
     *
     * @param view view that starts that animation.
     */
    private void enlargeAnim(View view, long startOffset) {
        view.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.sd_scale_fade_and_translate_in);
        anim.setStartOffset(startOffset);
        view.startAnimation(anim);
    }

    /**
     * Set menus visibility (visible or invisible).
     */
    private void visibilitySetup(boolean visible) {
        int size = mFabWithLabelViews.size();
        if (visible) {
            for (int i = 0; i < size; i++) {
                FabWithLabelView fabWithLabelView = mFabWithLabelViews.get(i);
                showWithAnimationFabWithLabelView(fabWithLabelView, i * 50);
            }
        } else {
            for (int i = 0; i < size; i++) {
                UiUtils.shrinkAnim(mFabWithLabelViews.get(i), false);
            }
        }
    }

    private void showWithAnimationFabWithLabelView(FabWithLabelView fabWithLabelView, int delay) {
        fabWithLabelView.setVisibility(View.VISIBLE);
        enlargeAnim(fabWithLabelView.getFab(), delay);
        if (fabWithLabelView.isLabelEnable()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.sd_fade_and_translate_in);
            animation.setStartOffset(delay);
            fabWithLabelView.getLabelBackground().startAnimation(animation);
        }
    }

    /**
     * Listener for handling events on option fab's.
     */
    public interface OnOptionFabSelectedListener {
        void onOptionFabSelected(SpeedDialActionItem speedDialActionItem);
    }

    /**
     * Behavior designed for use with {@link View} instances. Its main function
     * is to move {@link View} views so that any displayed {@link android.support.design.widget.Snackbar}s do
     * not cover them.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class SnackbarBehavior extends CoordinatorLayout.Behavior<View> {
        private static final boolean AUTO_HIDE_DEFAULT = true;

        private Rect mTmpRect;
        private OnVisibilityChangedListener mInternalAutoHideListener;
        private boolean mAutoHideEnabled;

        public SnackbarBehavior() {
            super();
            mAutoHideEnabled = AUTO_HIDE_DEFAULT;
        }

        public SnackbarBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs,
                    android.support.design.R.styleable.FloatingActionButton_Behavior_Layout);
            mAutoHideEnabled = a.getBoolean(
                    android.support.design.R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide,
                    AUTO_HIDE_DEFAULT);
            a.recycle();
        }

        private static boolean isBottomSheet(@NonNull View view) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof CoordinatorLayout.LayoutParams) {
                return ((CoordinatorLayout.LayoutParams) lp)
                        .getBehavior() instanceof BottomSheetBehavior;
            }
            return false;
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

        @VisibleForTesting
        void setInternalAutoHideListener(OnVisibilityChangedListener listener) {
            mInternalAutoHideListener = listener;
        }

        protected void show(View child) {
            if (child instanceof FloatingActionButton) {
                ((FloatingActionButton) child).show(mInternalAutoHideListener);
            } else if (child instanceof SpeedDialView) {
                ((SpeedDialView) child).show(mInternalAutoHideListener);
            } else {
                child.setVisibility(View.VISIBLE);
            }
        }

        protected void hide(View child) {
            if (child instanceof FloatingActionButton) {
                ((FloatingActionButton) child).hide(mInternalAutoHideListener);
            } else if (child instanceof SpeedDialView) {
                ((SpeedDialView) child).hide(mInternalAutoHideListener);
            } else {
                child.setVisibility(View.INVISIBLE);
            }
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
                hide(child);
            } else {
                show(child);
            }
            return true;
        }
    }

    /**
     * Behavior designed for use with {@link View} instances. Its main function
     * is to move {@link View} views so that any displayed {@link android.support.design.widget.Snackbar}s do
     * not cover them.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class ScrollingViewSnackbarBehavior extends SnackbarBehavior {
        public ScrollingViewSnackbarBehavior() {
        }

        public ScrollingViewSnackbarBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull
                View directTargetChild, @NonNull View target, int axes, int type) {
            return true;
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof RecyclerView || super.layoutDependsOn(parent, child, dependency);
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View
                target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                    type);
            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                hide(child);
            } else if (dyConsumed < 0) {
                show(child);
            }
        }
    }

    public static class NoBehavior extends CoordinatorLayout.Behavior<View> {
        public NoBehavior() {
        }

        public NoBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    @Retention(SOURCE)
    @IntDef({TOP, BOTTOM, LEFT, RIGHT})
    public @interface ExpansionMode {
        int TOP = 0;
        int BOTTOM = 1;
        int LEFT = 2;
        int RIGHT = 3;
    }
}
