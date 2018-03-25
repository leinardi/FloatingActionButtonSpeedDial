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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@CoordinatorLayout.DefaultBehavior(ViewSnackbarBehavior.class)
public class FabMenuView extends LinearLayout {

    private static final String TAG = FabMenuView.class.getSimpleName();
    private List<FabWithLabelView> mFabWithLabelViews = new ArrayList<>();
    private FloatingActionButton mMainFab;
    private boolean mIsFabMenuOpen = false;
    private int mMainFabOpenDrawable;
    private int mMainFabCloseDrawable;
    private OnOptionFabSelectedListener mOnOptionFabSelectedListener;
    private FabOverlayLayout mFabOverlayLayout;

    public FabMenuView(Context context) {
        super(context);
        init(context, null);
    }

    public FabMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FabMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mMainFab = new FloatingActionButton(getContext());
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.END;
        int margin = UiUtils.dpToPx(getContext(), 16);
        layoutParams.setMargins(margin, margin, margin, margin);
        mMainFab.setLayoutParams(layoutParams);
        mMainFab.setClickable(true);
        mMainFab.setFocusable(true);
        mMainFab.setSize(FloatingActionButton.SIZE_NORMAL);
        addView(mMainFab);

        setOrientation(VERTICAL);
        setClipChildren(false);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FabMenuView, 0, 0);
        try {
            mMainFabOpenDrawable = attr.getResourceId(R.styleable.FabMenuView_open_src, -1);
            mMainFabCloseDrawable = attr.getResourceId(R.styleable.FabMenuView_close_src, -1);

            int color = attr.getColor(
                    R.styleable.FabMenuView_color,
                    UiUtils.getAccentColor(context));

            mMainFab.setBackgroundTintList(ColorStateList.valueOf(color));
        } catch (Exception e) {
            Log.e(TAG, "Failure setting FabWithLabelView icon", e);
        } finally {
            attr.recycle();
        }

        if (mMainFabOpenDrawable != -1) {
            mMainFab.setImageResource(mMainFabOpenDrawable);
        }
        //Set margins on Main FloatingActionButton.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MarginLayoutParams params = (MarginLayoutParams) mMainFab.getLayoutParams();
            params.setMargins(0, 0, UiUtils.dpToPx(context, 4), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(UiUtils.dpToPx(context, 4));
            }
            mMainFab.setLayoutParams(params);
        }
    }

    public void attachToRecyclerView(@NonNull RecyclerView rv) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && mMainFab.isShown()) {
                    if (isFabMenuOpen()) {
                        closeOptionsMenu();
                    }
                    mMainFab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mMainFab.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMainFab.show();
                        }
                    }, 1000);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        ArrayList<FabOptionItem> fabOptionItems = new ArrayList<>(mFabWithLabelViews.size());
        for (FabWithLabelView fabWithLabelView : mFabWithLabelViews) {
            fabOptionItems.add(fabWithLabelView.getFabOptionItem());
        }
        bundle.putParcelableArrayList(FabOptionItem.class.getName(), fabOptionItems);
        bundle.putBoolean("IsFabMenuOpen", mIsFabMenuOpen);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) { // implicit null check
            Bundle bundle = (Bundle) state;
            ArrayList<FabOptionItem> fabOptionItems = bundle.getParcelableArrayList(FabOptionItem.class.getName());
            if (fabOptionItems != null && !fabOptionItems.isEmpty()) {
                Collections.reverse(fabOptionItems);
                addAllFabOptionItem(fabOptionItems);
            }
            toggleOptionsMenu(bundle.getBoolean("IsFabMenuOpen", mIsFabMenuOpen));
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    public FabOverlayLayout getFabOverlayLayout() {
        return mFabOverlayLayout;
    }

    public void setFabOverlayLayout(FabOverlayLayout fabOverlayLayout) {
        mFabOverlayLayout = fabOverlayLayout;
        mFabOverlayLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOptionsMenu();
            }
        });
    }

    public void addAllFabOptionItem(Collection<FabOptionItem> fabOptionItemList) {
        for (FabOptionItem fabOptionItem : fabOptionItemList) {
            addFabOptionItem(fabOptionItem);
        }
    }

    public void addFabOptionItem(FabOptionItem fabOptionItem) {
        removeFabOptionItem(fabOptionItem);
        FabWithLabelView newView = createNewFabWithLabelView(fabOptionItem);
        addView(newView, 0);
        mFabWithLabelViews.add(0, newView);
        if (isFabMenuOpen()) {
            showWithAnimationFabWithLabelVIew(newView, 0);
        }
    }

    public boolean removeFabOptionItem(int position) {
        return removeFabOptionItem(mFabWithLabelViews.get(position).getFabOptionItem());
    }

    public boolean removeFabOptionItem(FabOptionItem fabOptionItem) {
        FabWithLabelView oldView = getFabWithLabelViewById(fabOptionItem.getId());
        if (oldView != null) {
            removeView(oldView);
            mFabWithLabelViews.remove(oldView);
            return true;
        } else {
            return false;
        }
    }

    public boolean replaceFabOptionItem(int position, FabOptionItem newFabOptionItem) {
        return replaceFabOptionItem(mFabWithLabelViews.get(position).getFabOptionItem(), newFabOptionItem);
    }

    public boolean replaceFabOptionItem(FabOptionItem oldFabOptionItem, FabOptionItem newFabOptionItem) {
        FabWithLabelView oldView = getFabWithLabelViewById(oldFabOptionItem.getId());
        if (oldView != null) {
            int index = mFabWithLabelViews.indexOf(oldView);
            removeFabOptionItem(oldFabOptionItem);
            FabWithLabelView newView = createNewFabWithLabelView(newFabOptionItem);
            mFabWithLabelViews.add(index, newView);
            addView(newView, index);
            return true;
        } else {
            return false;
        }
    }

    private FabWithLabelView createNewFabWithLabelView(FabOptionItem fabOptionItem) {
        FabWithLabelView newView = new FabWithLabelView(getContext());
        setupMarginsForOptionFab(newView);
        newView.setFabOptionItem(fabOptionItem);
        newView.getLabelBackground().setVisibility(isFabMenuOpen() ? View.VISIBLE : View.GONE);
        newView.getFab().setVisibility(isFabMenuOpen() ? View.VISIBLE : View.GONE);
        newView.setOptionFabSelectedListener(mOnOptionFabSelectedListener);
        return newView;
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
     * Set margins for option fab's.
     */
    private void setupMarginsForOptionFab(FabWithLabelView fabWithLabelView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MarginLayoutParams optionFabParams = (MarginLayoutParams) fabWithLabelView.getFab().getLayoutParams();
            optionFabParams.setMargins(0, 0, UiUtils.dpToPx(getContext(), 12), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                optionFabParams.setMarginEnd(UiUtils.dpToPx(getContext(), 12));
            }
            fabWithLabelView.getFab().setLayoutParams(optionFabParams);
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

    private void toggleOptionsMenu(boolean show) {
        visibilitySetup(show);
        mIsFabMenuOpen = show;
        if (show) {
            if (mMainFabCloseDrawable != -1) {
                mMainFab.setImageResource(mMainFabCloseDrawable);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.fab_menu_open_elevation));
            } else {
                bringToFront();
            }
            mFabOverlayLayout.show();
        } else {
            if (mMainFabOpenDrawable != -1) {
                mMainFab.setImageResource(mMainFabOpenDrawable);
            }
            mFabOverlayLayout.hide();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.fab_menu_close_elevation));
            } else {
                bringToFront();
            }
        }
    }

    /**
     * Return returns true if menu opened,false otherwise.
     */
    public boolean isFabMenuOpen() {
        return mIsFabMenuOpen;
    }

    @Nullable
    private FabWithLabelView getFabWithLabelViewById(@IdRes int id) {
        for (FabWithLabelView fabWithLabelView : mFabWithLabelViews) {
            if (fabWithLabelView.getId() == id) {
                return fabWithLabelView;
            }
        }
        return null;
    }

    /**
     * Closing animation.
     *
     * @param view view that starts that animation.
     */
    private void shrinkAnim(final View view) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(anim);
    }

    /**
     * Menu opening animation.
     *
     * @param view view that starts that animation.
     */
    private void enlargeAnim(View view, long startOffset) {
        view.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.enlarge);
        anim.setStartOffset(startOffset);
        view.startAnimation(anim);
    }

    /**
     * Set menus visibility (visible or invisible).
     */
    private void visibilitySetup(boolean visible) {
        //        mFabMenuView.toggle(visible, false, new OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //
        //            }
        //        });

        int size = mFabWithLabelViews.size();
        if (visible) {
            for (int i = 0; i < size; i++) {
                FabWithLabelView fabWithLabelView = mFabWithLabelViews.get(i);
                showWithAnimationFabWithLabelVIew(fabWithLabelView, (size - i) * 50);
            }
        } else {
            for (int i = 0; i < size; i++) {
                shrinkAnim(mFabWithLabelViews.get(i));
            }
        }
    }

    private void showWithAnimationFabWithLabelVIew(FabWithLabelView fabWithLabelView, int delay) {
        fabWithLabelView.setVisibility(View.VISIBLE);
        enlargeAnim(fabWithLabelView.getFab(), delay);
        if (fabWithLabelView.isLabelEnable()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_and_translate);
            animation.setStartOffset(delay);
            fabWithLabelView.getLabelBackground().startAnimation(animation);
        }
    }

    /**
     * Listener for handling events on option fab's.
     */
    public interface OnOptionFabSelectedListener {
        void onOptionFabSelected(FabOptionItem fabOptionItem);
    }
}
