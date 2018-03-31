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

package com.leinardi.fabspeeddial;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leinardi.fabspeeddial.SpeedDialView.OnOptionFabSelectedListener;

import static android.support.design.widget.FloatingActionButton.SIZE_AUTO;
import static android.support.design.widget.FloatingActionButton.SIZE_MINI;
import static android.support.design.widget.FloatingActionButton.SIZE_NORMAL;
import static com.leinardi.fabspeeddial.SpeedDialActionItem.NOT_SET;

/**
 * View that contains fab button and its label.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
final class FabWithLabelView extends LinearLayout {
    private static final String TAG = FabWithLabelView.class.getSimpleName();

    private TextView mLabel;
    private FloatingActionButton mFab;
    private CardView mLabelBackground;
    private boolean mIsLabelEnable;
    private SpeedDialActionItem mSpeedDialActionItem;
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

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        getFab().setVisibility(visibility);
        if (isLabelEnable()) {
            getLabelBackground().setVisibility(visibility);
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

    public SpeedDialActionItem getSpeedDialActionItem() {
        return mSpeedDialActionItem;
    }

    public void setSpeedDialActionItem(SpeedDialActionItem actionItem) {
        mSpeedDialActionItem = actionItem;
        setId(actionItem.getId());
        setFabLabel(actionItem.getLabel());
        setFabLabelClickable(getSpeedDialActionItem().isLabelClickable());

        int iconTintColor = actionItem.getFabImageTintColor();

        int iconResId = actionItem.getFabImageResource();
        if (iconResId != NOT_SET) {
            Drawable drawable = AppCompatResources.getDrawable(getContext(), iconResId);
            if (drawable != null && iconTintColor != NOT_SET) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), iconTintColor);
            }
            setFabIcon(drawable);
        }

        int fabBackgroundColor = actionItem.getFabBackgroundColor();
        if (fabBackgroundColor == NOT_SET) {
            fabBackgroundColor = UiUtils.getPrimaryColor(getContext());
        }
        setFabBackgroundColor(fabBackgroundColor);

        int labelColor = actionItem.getLabelColor();
        if (labelColor == NOT_SET) {
            labelColor = ResourcesCompat.getColor(getResources(), R.color.sd_label_text_color,
                    getContext().getTheme());
        }
        setLabelColor(labelColor);
        int labelBackgroundColor = actionItem.getLabelBackgroundColor();
        if (labelBackgroundColor == NOT_SET) {
            labelBackgroundColor = ResourcesCompat.getColor(getResources(), R.color.cardview_light_background,
                    getContext().getTheme());
        }
        setLabelBackgroundColor(labelBackgroundColor);
        if (actionItem.getFabSize() == SIZE_AUTO) {
            getFab().setSize(SIZE_MINI);
        } else {
            getFab().setSize(actionItem.getFabSize());
        }
        setFabSize(actionItem.getFabSize());
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
                    mOnOptionFabSelectedListener.onOptionFabSelected(getSpeedDialActionItem());
                }
            });
            getLabelBackground().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSpeedDialActionItem().isLabelClickable() && isLabelEnable()) {
                        mOnOptionFabSelectedListener.onOptionFabSelected(getSpeedDialActionItem());
                    }
                }
            });
        }
    }

    /**
     * Init custom attributes.
     *
     * @param context context.
     * @param attrs   attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        View rootView = inflate(context, R.layout.sd_fab_with_label_view, this);

        mFab = rootView.findViewById(R.id.fab);
        mLabel = rootView.findViewById(R.id.label);
        mLabelBackground = rootView.findViewById(R.id.label_container);

        setFabSize(SIZE_MINI);
        setOrientation(LinearLayout.HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);

        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.FabWithLabelView, 0, 0);

        try {
            @DrawableRes int src = attr.getResourceId(R.styleable.FabWithLabelView_srcCompat, NOT_SET);
            if (src == NOT_SET) {
                src = attr.getResourceId(R.styleable.FabWithLabelView_android_src, NOT_SET);
            }
            SpeedDialActionItem.Builder builder = new SpeedDialActionItem.Builder(getId(), src);
            String labelText = attr.getString(R.styleable.FabWithLabelView_fabLabel);
            builder.setLabel(labelText);
            @ColorInt int fabBackgroundColor = UiUtils.getPrimaryColor(context);
            fabBackgroundColor = attr.getColor(R.styleable.FabWithLabelView_fabBackgroundColor, fabBackgroundColor);
            builder.setFabBackgroundColor(fabBackgroundColor);
            @ColorInt int labelColor = NOT_SET;
            labelColor = attr.getColor(R.styleable.FabWithLabelView_fabLabelColor, labelColor);
            builder.setLabelColor(labelColor);
            @ColorInt int labelBackgroundColor = NOT_SET;
            labelBackgroundColor = attr.getColor(R.styleable.FabWithLabelView_fabLabelBackgroundColor,
                    labelBackgroundColor);
            builder.setLabelBackgroundColor(labelBackgroundColor);
            boolean labelClickable = attr.getBoolean(R.styleable.FabWithLabelView_fabLabelClickable, true);
            builder.setLabelClickable(labelClickable);
            setSpeedDialActionItem(builder.create());
        } catch (Exception e) {
            Log.e(TAG, "Failure setting FabWithLabelView icon", e);
        } finally {
            attr.recycle();
        }
    }

    private void setFabSize(@FloatingActionButton.Size int fabSize) {
        int normalFabSizePx = getContext().getResources().getDimensionPixelSize(R.dimen.sd_fab_normal_size);
        int miniFabSizePx = getContext().getResources().getDimensionPixelSize(R.dimen.sd_fab_mini_size);
        int fabSizePx = fabSize == SIZE_NORMAL ? normalFabSizePx : miniFabSizePx;
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                fabSizePx);
        layoutParams.gravity = Gravity.END;
        if (fabSize == SIZE_NORMAL) {
            int excessMargin = (normalFabSizePx - miniFabSizePx) / 2;
            layoutParams.setMargins(
                    layoutParams.leftMargin - excessMargin,
                    layoutParams.topMargin,
                    layoutParams.rightMargin - excessMargin,
                    layoutParams.bottomMargin
            );
        }
        setLayoutParams(layoutParams);
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

    private void setFabLabelClickable(boolean clickable) {
        getLabelBackground().setClickable(clickable);
        getLabelBackground().setFocusable(clickable);
        getLabelBackground().setEnabled(clickable);
    }

    /**
     * Sets fab color in floating action menu.
     *
     * @param color color to set.
     */
    private void setFabBackgroundColor(int color) {
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setLabelColor(int color) {
        mLabel.setTextColor(color);
    }

    private void setLabelBackgroundColor(int color) {
        mLabelBackground.setCardBackgroundColor(ColorStateList.valueOf(color));
    }
}

