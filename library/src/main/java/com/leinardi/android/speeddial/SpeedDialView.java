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
import android.support.v7.widget.CardView;
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
public class SpeedDialView extends LinearLayout implements CoordinatorLayout.AttachedBehavior {
    private static final String TAG = SpeedDialView.class.getSimpleName();
    private List<FabWithLabelView> mFabWithLabelViews = new ArrayList<>();
    private FloatingActionButton mMainFab;
    private boolean mIsOpen = false;
    @Nullable
    private Drawable mMainFabOpenDrawable = null;
    @Nullable
    private Drawable mMainFabCloseDrawable = null;
    @Nullable
    private SpeedDialOverlayLayout mOverlayLayout;
    @ExpansionMode
    private int mExpansionMode = TOP;
    private boolean mRotateOnToggle = true;
    @Nullable
    private OnChangeListener mOnChangeListener;
    @Nullable
    private OnActionSelectedListener mOnActionSelectedListener;
    private OnActionSelectedListener mOnActionSelectedProxyListener = new OnActionSelectedListener() {
        @Override
        public boolean onActionSelected(SpeedDialActionItem actionItem) {
            if (mOnActionSelectedListener != null) {
                boolean consumed = mOnActionSelectedListener.onActionSelected(actionItem);
                if (!consumed) {
                    close();
                }
                return consumed;
            } else {
                return false;
            }
        }
    };

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
        if (isOpen()) {
            close();
            // Workaround for mMainFab.hide() breaking the rotate anim
            ViewCompat.animate(mMainFab).rotation(0).setDuration(0).start();
        }
        mMainFab.hide(listener);
    }

    @Nullable
    public SpeedDialOverlayLayout getOverlayLayout() {
        return mOverlayLayout;
    }

    /**
     * Add the overlay/touch guard view to appear together with the speed dial menu.
     *
     * @param overlayLayout The view to add.
     */
    public void setOverlayLayout(@Nullable SpeedDialOverlayLayout overlayLayout) {
        if (overlayLayout != null) {
            overlayLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    close();
                }
            });
        } else if (mOverlayLayout != null) {
            setOnClickListener(null);
        }
        mOverlayLayout = overlayLayout;
    }

    /**
     * Appends all of the {@link SpeedDialActionItem} to the end of the list, in the order that they are returned by
     * the specified
     * collection's Iterator.
     *
     * @param actionItemCollection collection containing {@link SpeedDialActionItem} to be added to this list
     */
    public void addAllActionItems(Collection<SpeedDialActionItem> actionItemCollection) {
        for (SpeedDialActionItem speedDialActionItem : actionItemCollection) {
            addActionItem(speedDialActionItem);
        }
    }

    /**
     * Appends the specified {@link SpeedDialActionItem} to the end of this list.
     *
     * @param speedDialActionItem {@link SpeedDialActionItem} to be appended to this list
     */
    public void addActionItem(SpeedDialActionItem speedDialActionItem) {
        addActionItem(speedDialActionItem, mFabWithLabelViews.size());
    }

    /**
     * Inserts the specified {@link SpeedDialActionItem} at the specified position in this list. Shifts the element
     * currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
     *
     * @param actionItem {@link SpeedDialActionItem} to be appended to this list
     * @param position   index at which the specified element is to be inserted
     */
    public void addActionItem(SpeedDialActionItem actionItem, int position) {
        addActionItem(actionItem, position, true);
    }

    /**
     * Inserts the specified {@link SpeedDialActionItem} at the specified position in this list. Shifts the element
     * currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
     *
     * @param actionItem {@link SpeedDialActionItem} to be appended to this list
     * @param position   index at which the specified element is to be inserted
     * @param animate    true to animate the insertion, false to insert instantly
     */
    public void addActionItem(SpeedDialActionItem actionItem, int position, boolean animate) {
        FabWithLabelView oldView = findFabWithLabelViewById(actionItem.getId());
        if (oldView != null) {
            replaceActionItem(oldView.getSpeedDialActionItem(), actionItem);
        } else {
            FabWithLabelView newView = createNewFabWithLabelView(actionItem);
            int layoutPosition = getLayoutPosition(position);
            addView(newView, layoutPosition);
            mFabWithLabelViews.add(position, newView);
            if (isOpen()) {
                if (animate) {
                    showWithAnimationFabWithLabelView(newView, 0);
                }
            } else {
                newView.setVisibility(GONE);
            }
        }
    }

    /**
     * Removes the {@link SpeedDialActionItem} at the specified position in this list. Shifts any subsequent elements
     * to the left (subtracts one from their indices).
     *
     * @param position the index of the {@link SpeedDialActionItem} to be removed
     * @return the {@link SpeedDialActionItem} that was removed from the list
     */
    @Nullable
    public SpeedDialActionItem removeActionItem(int position) {
        SpeedDialActionItem speedDialActionItem = mFabWithLabelViews.get(position).getSpeedDialActionItem();
        removeActionItem(speedDialActionItem);
        return speedDialActionItem;
    }

    /**
     * Removes the specified {@link SpeedDialActionItem} from this list, if it is present. If the list does not
     * contain the element, it is unchanged.
     * <p>
     * Returns true if this list contained the specified element (or equivalently, if this list changed
     * as a result of the call).
     *
     * @param actionItem {@link SpeedDialActionItem} to be removed from this list, if present
     * @return true if this list contained the specified element
     */
    public boolean removeActionItem(@Nullable SpeedDialActionItem actionItem) {
        return actionItem != null && removeActionItemById(actionItem.getId()) != null;
    }

    /**
     * Finds and removes the first {@link SpeedDialActionItem} with the given ID, if it is present. If the list does not
     * contain the element, it is unchanged.
     *
     * @param idRes the ID to search for
     * @return the {@link SpeedDialActionItem} that was removed from the list, or null otherwise
     */
    @Nullable
    public SpeedDialActionItem removeActionItemById(@IdRes int idRes) {
        return removeActionItem(findFabWithLabelViewById(idRes));
    }

    /**
     * Replace the {@link SpeedDialActionItem} at the specified position in this list with the one provided as
     * parameter.
     *
     * @param newActionItem {@link SpeedDialActionItem} to use for the replacement
     * @param position      the index of the {@link SpeedDialActionItem} to be replaced
     * @return true if this list contained the specified element
     */
    public boolean replaceActionItem(SpeedDialActionItem newActionItem, int position) {
        return replaceActionItem(mFabWithLabelViews.get(position).getSpeedDialActionItem(),
                newActionItem);
    }

    /**
     * Replace an already added {@link SpeedDialActionItem} with the one provided as parameter.
     *
     * @param oldSpeedDialActionItem the old {@link SpeedDialActionItem} to remove
     * @param newSpeedDialActionItem the new {@link SpeedDialActionItem} to add
     * @return true if this list contained the specified element
     */
    public boolean replaceActionItem(@Nullable SpeedDialActionItem oldSpeedDialActionItem,
                                     SpeedDialActionItem newSpeedDialActionItem) {
        if (oldSpeedDialActionItem == null) {
            return false;
        } else {
            FabWithLabelView oldView = findFabWithLabelViewById(oldSpeedDialActionItem.getId());
            if (oldView != null) {
                int index = mFabWithLabelViews.indexOf(oldView);
                if (index < 0) {
                    return false;
                }
                removeActionItem(findFabWithLabelViewById(newSpeedDialActionItem.getId()), null, false);
                removeActionItem(findFabWithLabelViewById(oldSpeedDialActionItem.getId()), null, false);
                addActionItem(newSpeedDialActionItem, index, false);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Removes all of the {@link SpeedDialActionItem} from this list.
     */
    public void clearActionItems() {
        Iterator<FabWithLabelView> it = mFabWithLabelViews.iterator();
        while (it.hasNext()) {
            FabWithLabelView fabWithLabelView = it.next();
            removeActionItem(fabWithLabelView, it, true);
        }
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new SnackbarBehavior();
    }

    /**
     * Set a listener that will be notified when a menu fab is selected.
     *
     * @param listener listener to set.
     */
    public void setOnActionSelectedListener(@Nullable OnActionSelectedListener listener) {
        mOnActionSelectedListener = listener;

        for (int index = 0; index < mFabWithLabelViews.size(); index++) {
            final FabWithLabelView fabWithLabelView = mFabWithLabelViews.get(index);
            fabWithLabelView.setOnActionSelectedListener(mOnActionSelectedProxyListener);
        }
    }

    /**
     * Set Main FloatingActionButton ClickMOnOptionFabSelectedListener.
     *
     * @param onChangeListener listener to set.
     */
    public void setOnChangeListener(@Nullable final OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    /**
     * Opens speed dial menu.
     */
    public void open() {
        toggle(true);
    }

    /**
     * Closes speed dial menu.
     */
    public void close() {
        toggle(false);
    }

    /**
     * Toggles speed dial menu.
     */
    public void toggle() {
        toggle(!mIsOpen);
    }

    /**
     * Return returns true if speed dial menu is open,false otherwise.
     */
    public boolean isOpen() {
        return mIsOpen;
    }

    public boolean isRotateOnToggle() {
        return mRotateOnToggle;
    }

    public void setRotateOnToggle(boolean rotateOnToggle) {
        mRotateOnToggle = rotateOnToggle;
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
        bundle.putBoolean("IsOpen", mIsOpen);
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
                addAllActionItems(speedDialActionItems);
            }
            toggle(bundle.getBoolean("IsOpen", mIsOpen));
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private int getLayoutPosition(int position) {
        if (mExpansionMode == TOP || mExpansionMode == LEFT) {
            return mFabWithLabelViews.size() - position;
        } else {
            return position + 1;
        }
    }

    @Nullable
    private SpeedDialActionItem removeActionItem(@Nullable FabWithLabelView view,
                                                 @Nullable Iterator<FabWithLabelView> it,
                                                 boolean animate) {
        if (view != null) {
            SpeedDialActionItem speedDialActionItem = view.getSpeedDialActionItem();
            if (it != null) {
                it.remove();
            } else {
                mFabWithLabelViews.remove(view);
            }

            if (isOpen()) {
                if (mFabWithLabelViews.isEmpty()) {
                    close();
                }
                if (animate) {
                    UiUtils.shrinkAnim(view, true);
                } else {
                    removeView(view);
                }
            } else {
                removeView(view);
            }
            return speedDialActionItem;
        } else {
            return null;
        }
    }

    @Nullable
    private SpeedDialActionItem removeActionItem(@Nullable FabWithLabelView view) {
        return removeActionItem(view, null, true);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mMainFab = createMainFab();
        addView(mMainFab);
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
            int closeDrawableRes = attr.getResourceId(R.styleable.SpeedDialView_sdFabCloseSrc, NOT_SET);
            if (closeDrawableRes != NOT_SET) {
                final Drawable drawable = AppCompatResources.getDrawable(context, closeDrawableRes);
                mMainFabCloseDrawable = UiUtils.getRotateDrawable(drawable, -UiUtils.ROTATION_ANGLE);
            }
            mExpansionMode = attr.getInt(R.styleable.SpeedDialView_sdExpansionMode, mExpansionMode);
            mRotateOnToggle = attr.getBoolean(R.styleable.SpeedDialView_sdFabRotateOnToggle, mRotateOnToggle);
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
        setExpansionMode(mExpansionMode);
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
        floatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!mIsOpen && !mFabWithLabelViews.isEmpty()) {
                    open();
                } else {
                    if (mOnChangeListener == null) {
                        close();
                    } else {
                        mOnChangeListener.onMainActionSelected();
                    }
                }
            }
        });
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
        newView.setOnActionSelectedListener(mOnActionSelectedProxyListener);
        return newView;
    }

    private void toggle(boolean show) {
        if (mIsOpen == show) {
            return;
        }
        mIsOpen = show;
        visibilitySetup(show);
        if (show) {
            if (mMainFabCloseDrawable != null) {
                mMainFab.setImageDrawable(mMainFabCloseDrawable);
            }
            UiUtils.rotateForward(mMainFab, mRotateOnToggle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.sd_open_elevation));
            } else {
                bringToFront();
            }
        } else {
            UiUtils.rotateBackward(mMainFab, mRotateOnToggle);
            if (mMainFabCloseDrawable != null) {
                mMainFab.setImageDrawable(mMainFabOpenDrawable);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.sd_close_elevation));
            }
        }
        showHideOverlay(show);
        if (mOnChangeListener != null) {
            mOnChangeListener.onToggleChanged(show);
        }
    }

    private void showHideOverlay(boolean show) {
        if (mOverlayLayout != null) {
            if (show) {
                mOverlayLayout.show();
            } else {
                mOverlayLayout.hide();
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
     * SpeedDial opening animation.
     *
     * @param view view that starts that animation.
     */
    private void enlargeAnim(View view, long startOffset) {
        ViewCompat.animate(view).cancel();
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
        ViewCompat.animate(fabWithLabelView).cancel();
        fabWithLabelView.setAlpha(1);
        fabWithLabelView.setVisibility(View.VISIBLE);
        enlargeAnim(fabWithLabelView.getFab(), delay);
        if (fabWithLabelView.isLabelEnable()) {
            CardView labelBackground = fabWithLabelView.getLabelBackground();
            ViewCompat.animate(labelBackground).cancel();
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.sd_fade_and_translate_in);
            animation.setStartOffset(delay);
            labelBackground.startAnimation(animation);
        }
    }

    /**
     * Listener for handling events on option fab's.
     */
    public interface OnChangeListener {
        /**
         * Called when the main action has been clicked.
         */
        void onMainActionSelected();

        /**
         * Called when the toggle state of the speed dial menu changes (eg. it is opened or closed).
         *
         * @param isOpen true if the speed dial is open, false otherwise.
         */
        void onToggleChanged(boolean isOpen);
    }

    /**
     * Listener for handling events on option fab's.
     */
    public interface OnActionSelectedListener {
        /**
         * Called when a speed dial action has been clicked.
         *
         * @param actionItem the {@link SpeedDialActionItem} that was selected.
         * @return true if the callback consumed the click, false otherwise.
         */
        boolean onActionSelected(SpeedDialActionItem actionItem);
    }

    @Retention(SOURCE)
    @IntDef({TOP, BOTTOM, LEFT, RIGHT})
    public @interface ExpansionMode {
        int TOP = 0;
        int BOTTOM = 1;
        int LEFT = 2;
        int RIGHT = 3;
    }

    /**
     * Behavior designed for use with {@link View} instances. Its main function
     * is to move {@link View} views so that any displayed {@link android.support.design.widget.Snackbar}s do
     * not cover them.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class SnackbarBehavior extends CoordinatorLayout.Behavior<View> {
        private static final boolean AUTO_HIDE_DEFAULT = true;

        @Nullable
        private Rect mTmpRect;
        @Nullable
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
}
