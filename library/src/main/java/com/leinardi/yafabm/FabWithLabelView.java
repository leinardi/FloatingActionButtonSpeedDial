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
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leinardi.yafabm.FabMenuView.OnOptionFabSelectedListener;

/**
 * View that contains fab button and its label.
 */
//@SuppressWarnings("unused")
final class FabWithLabelView extends LinearLayout {
    private static final String TAG = FabWithLabelView.class.getSimpleName();

    private static final int FAB_MINI_SIZE_DP = 56;
    private TextView mLabel;
    private FloatingActionButton mFab;
    private CardView mLabelBackground;
    private boolean mIsLabelEnable;
    private FabOptionItem mFabOptionItem;
    private OnOptionFabSelectedListener mOnOptionFabSelectedListener;

    public FabWithLabelView(Context context) {
        super(context);
        init(context, null);
    }

    public FabWithLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FabWithLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Init custom attributes.
     *
     * @param context context.
     * @param attrs   attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        View rootView = inflate(context, R.layout.fab_with_label_view, this);

        mFab = rootView.findViewById(R.id.fab);
        mLabel = rootView.findViewById(R.id.label);
        mLabelBackground = rootView.findViewById(R.id.label_background);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                UiUtils.dpToPx(context, FAB_MINI_SIZE_DP));
        layoutParams.gravity = Gravity.END;
        setLayoutParams(layoutParams);
        setOrientation(LinearLayout.HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);

        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.FabWithLabelView, 0, 0);
        try {
            String labelText = attr.getString(R.styleable.FabWithLabelView_fab_label);
            mLabel.setText(labelText);

            int fabColor = UiUtils.getPrimaryColor(context);
            mFab.setBackgroundTintList(ColorStateList.valueOf(fabColor));
        } catch (Exception e) {
            Log.e(TAG, "Failure setting FabWithLabelView icon", e);
        } finally {
            attr.recycle();
        }
    }

    public void setFabOptionItem(FabOptionItem fabOptionItem) {
        mFabOptionItem = fabOptionItem;
        setId(fabOptionItem.getId());
        setFabLabel(fabOptionItem.getLabel());
        VectorDrawableCompat mDrawable = VectorDrawableCompat.create(getResources(), fabOptionItem
                .getFabImageResource(), getContext().getTheme());
        setFabIcon(mDrawable);
        int fabBackgroundColor = fabOptionItem.getFabBackgroundColor();
        if (fabBackgroundColor != FabOptionItem.COLOR_NOT_SET) {
            setFabColor(fabBackgroundColor);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        getFab().setVisibility(visibility);
        if (isLabelEnable()) {
            getLabelBackground().setVisibility(visibility);
        }
    }

    /**
     * Sets fab drawable.
     *
     * @param mDrawable drawable to set.
     */
    private void setFabIcon(Drawable mDrawable) {
        mFab.setImageDrawable(mDrawable);
    }

    /**
     * Sets fab label․
     *
     * @param sequence label to set.
     */
    private void setFabLabel(CharSequence sequence) {
        if (!TextUtils.isEmpty(sequence)) {
            mLabel.setText(sequence);
            mIsLabelEnable = true;
        } else {
            setLabelEnable(false);
            mIsLabelEnable = false;
        }
    }

    /**
     * Return true if button has label, false otherwise.
     */
    public boolean isLabelEnable() {
        return mIsLabelEnable;
    }

    /**
     * Enables or disables label of button.
     */
    private void setLabelEnable(boolean enable) {
        mLabelBackground.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets fab color in floating action menu.
     *
     * @param color color to set.
     */
    private void setFabColor(int color) {
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    /**
     * Return This method returns fab labels background card.
     */
    public CardView getLabelBackground() {
        return mLabelBackground;
    }

    /**
     * Return This method returns fab in action menu.
     */
    public FloatingActionButton getFab() {
        return mFab;
    }

    public FabOptionItem getFabOptionItem() {
        return mFabOptionItem;
    }

    /**
     * Set a listener that will be notified when a menu fab is selected.
     *
     * @param onOptionFabSelectedListener listener to set.
     */
    public void setOptionFabSelectedListener(final OnOptionFabSelectedListener onOptionFabSelectedListener) {
        mOnOptionFabSelectedListener = onOptionFabSelectedListener;
        if (mOnOptionFabSelectedListener != null) {
            getFab().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnOptionFabSelectedListener.onOptionFabSelected(getFabOptionItem());
                }
            });
            getLabelBackground().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getFabOptionItem().isLabelClickable() && isLabelEnable()) {
                        mOnOptionFabSelectedListener.onOptionFabSelected(getFabOptionItem());
                    }
                }
            });
        }
    }
}

