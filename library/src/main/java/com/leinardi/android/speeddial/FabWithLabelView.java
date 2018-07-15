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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leinardi.android.speeddial.SpeedDialView.OnActionSelectedListener;

import static android.support.design.widget.FloatingActionButton.SIZE_AUTO;
import static android.support.design.widget.FloatingActionButton.SIZE_MINI;
import static android.support.design.widget.FloatingActionButton.SIZE_NORMAL;
import static com.leinardi.android.speeddial.SpeedDialActionItem.RESOURCE_NOT_SET;

/**
 * View that contains fab button and its label.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FabWithLabelView extends LinearLayout {
    private static final String TAG = FabWithLabelView.class.getSimpleName();

    private TextView mLabelTextView;
    private FloatingActionButton mFab;
    private CardView mLabelCardView;
    private boolean mIsLabelEnabled;
    @Nullable
    private SpeedDialActionItem mSpeedDialActionItem;
    @Nullable
    private OnActionSelectedListener mOnActionSelectedListener;
    @FloatingActionButton.Size
    private int mCurrentFabSize;
    private float mLabelCardViewElevation;
    @Nullable
    private Drawable mLabelCardViewBackground;

    public FabWithLabelView(Context context) {
        super(context);
        init(context, null);
    }

    public FabWithLabelView(Context context, @Nullable AttributeSet attrs) {
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
        if (isLabelEnabled()) {
            getLabelBackground().setVisibility(visibility);
        }
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        setFabSize(mCurrentFabSize);
        if (orientation == VERTICAL) {
            setLabelEnabled(false);
        } else {
            setLabel(mLabelTextView.getText().toString());
        }
    }

    /**
     * Return true if button has label, false otherwise.
     */
    public boolean isLabelEnabled() {
        return mIsLabelEnabled;
    }

    /**
     * Enables or disables label of button.
     */
    private void setLabelEnabled(boolean enabled) {
        mIsLabelEnabled = enabled;
        mLabelCardView.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    /**
     * Return This method returns fab labels background card.
     */
    public CardView getLabelBackground() {
        return mLabelCardView;
    }

    /**
     * Return This method returns fab in action menu.
     */
    public FloatingActionButton getFab() {
        return mFab;
    }

    @Nullable
    public SpeedDialActionItem getSpeedDialActionItem() {
        return mSpeedDialActionItem;
    }

    public void setSpeedDialActionItem(SpeedDialActionItem actionItem) {
        mSpeedDialActionItem = actionItem;
        setId(actionItem.getId());
        setLabel(actionItem.getLabel(getContext()));
        SpeedDialActionItem speedDialActionItem = getSpeedDialActionItem();
        setLabelClickable(speedDialActionItem != null && speedDialActionItem.isLabelClickable());

        int iconTintColor = actionItem.getFabImageTintColor();

        Drawable drawable = actionItem.getFabImageDrawable(getContext());
        if (drawable != null && iconTintColor != RESOURCE_NOT_SET) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), iconTintColor);
        }
        setFabIcon(drawable);

        int fabBackgroundColor = actionItem.getFabBackgroundColor();
        if (fabBackgroundColor == RESOURCE_NOT_SET) {
            fabBackgroundColor = UiUtils.getPrimaryColor(getContext());
        }
        setFabBackgroundColor(fabBackgroundColor);

        int labelColor = actionItem.getLabelColor();
        if (labelColor == RESOURCE_NOT_SET) {
            labelColor = ResourcesCompat.getColor(getResources(), R.color.sd_label_text_color,
                    getContext().getTheme());
        }
        setLabelColor(labelColor);
        int labelBackgroundColor = actionItem.getLabelBackgroundColor();
        if (labelBackgroundColor == RESOURCE_NOT_SET) {
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
     * @param listener listener to set.
     */
    public void setOnActionSelectedListener(@Nullable OnActionSelectedListener listener) {
        mOnActionSelectedListener = listener;
        if (mOnActionSelectedListener != null) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpeedDialActionItem speedDialActionItem = getSpeedDialActionItem();
                    if (mOnActionSelectedListener != null
                            && speedDialActionItem != null) {
                        if (speedDialActionItem.isLabelClickable()) {
                            UiUtils.performTap(getLabelBackground());
                        } else {
                            UiUtils.performTap(getFab());
                        }
                    }
                }
            });
            getFab().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpeedDialActionItem speedDialActionItem = getSpeedDialActionItem();
                    if (mOnActionSelectedListener != null
                            && speedDialActionItem != null) {
                        mOnActionSelectedListener.onActionSelected(speedDialActionItem);
                    }
                }
            });

            getLabelBackground().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpeedDialActionItem speedDialActionItem = getSpeedDialActionItem();
                    if (mOnActionSelectedListener != null
                            && speedDialActionItem != null
                            && speedDialActionItem.isLabelClickable()) {
                        mOnActionSelectedListener.onActionSelected(speedDialActionItem);
                    }
                }
            });
        } else {
            getFab().setOnClickListener(null);
            getLabelBackground().setOnClickListener(null);
        }

    }

    /**
     * Init custom attributes.
     *
     * @param context context.
     * @param attrs   attributes.
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        View rootView = inflate(context, R.layout.sd_fab_with_label_view, this);

        mFab = rootView.findViewById(R.id.fab);
        mLabelTextView = rootView.findViewById(R.id.label);
        mLabelCardView = rootView.findViewById(R.id.label_container);

        setFabSize(SIZE_MINI);
        setOrientation(LinearLayout.HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);

        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.FabWithLabelView, 0, 0);

        try {
            @DrawableRes int src = attr.getResourceId(R.styleable.FabWithLabelView_srcCompat, RESOURCE_NOT_SET);
            if (src == RESOURCE_NOT_SET) {
                src = attr.getResourceId(R.styleable.FabWithLabelView_android_src, RESOURCE_NOT_SET);
            }
            SpeedDialActionItem.Builder builder = new SpeedDialActionItem.Builder(getId(), src);
            String labelText = attr.getString(R.styleable.FabWithLabelView_fabLabel);
            builder.setLabel(labelText);
            @ColorInt int fabBackgroundColor = UiUtils.getPrimaryColor(context);
            fabBackgroundColor = attr.getColor(R.styleable.FabWithLabelView_fabBackgroundColor, fabBackgroundColor);
            builder.setFabBackgroundColor(fabBackgroundColor);
            @ColorInt int labelColor = RESOURCE_NOT_SET;
            labelColor = attr.getColor(R.styleable.FabWithLabelView_fabLabelColor, labelColor);
            builder.setLabelColor(labelColor);
            @ColorInt int labelBackgroundColor = RESOURCE_NOT_SET;
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
        int fabSideMarginPx = getContext().getResources().getDimensionPixelSize(R.dimen.sd_fab_side_margin);
        int fabSizePx = fabSize == SIZE_NORMAL ? normalFabSizePx : miniFabSizePx;
        LayoutParams rootLayoutParams;
        LayoutParams fabLayoutParams = (LayoutParams) mFab.getLayoutParams();
        if (getOrientation() == HORIZONTAL) {
            rootLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, fabSizePx);
            rootLayoutParams.gravity = Gravity.END;

            if (fabSize == SIZE_NORMAL) {
                int excessMargin = (normalFabSizePx - miniFabSizePx) / 2;
                fabLayoutParams.setMargins(fabSideMarginPx - excessMargin, 0, fabSideMarginPx - excessMargin, 0);
            } else {
                fabLayoutParams.setMargins(fabSideMarginPx, 0, fabSideMarginPx, 0);

            }
        } else {
            rootLayoutParams = new LayoutParams(fabSizePx, ViewGroup.LayoutParams.WRAP_CONTENT);
            rootLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            fabLayoutParams.setMargins(0, 0, 0, 0);
        }

        setLayoutParams(rootLayoutParams);
        mFab.setLayoutParams(fabLayoutParams);
        mCurrentFabSize = fabSize;
    }

    /**
     * Sets fab drawable.
     *
     * @param mDrawable drawable to set.
     */
    private void setFabIcon(@Nullable Drawable mDrawable) {
        mFab.setImageDrawable(mDrawable);
    }

    /**
     * Sets fab label․
     *
     * @param sequence label to set.
     */
    private void setLabel(@Nullable CharSequence sequence) {
        if (!TextUtils.isEmpty(sequence)) {
            mLabelTextView.setText(sequence);
            setLabelEnabled(getOrientation() == HORIZONTAL);
        } else {
            setLabelEnabled(false);
        }
    }

    private void setLabelClickable(boolean clickable) {
        getLabelBackground().setClickable(clickable);
        getLabelBackground().setFocusable(clickable);
        getLabelBackground().setEnabled(clickable);
    }

    /**
     * Sets fab color in floating action menu.
     *
     * @param color color to set.
     */
    private void setFabBackgroundColor(@ColorInt int color) {
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setLabelColor(@ColorInt int color) {
        mLabelTextView.setTextColor(color);
    }

    private void setLabelBackgroundColor(@ColorInt int color) {
        if (color == Color.TRANSPARENT) {
            mLabelCardView.setCardBackgroundColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mLabelCardViewElevation = mLabelCardView.getElevation();
                mLabelCardView.setElevation(0);
            } else {
                mLabelCardView.setBackgroundColor(Color.TRANSPARENT);
                mLabelCardViewBackground = mLabelCardView.getBackground();
            }
        } else {
            mLabelCardView.setCardBackgroundColor(ColorStateList.valueOf(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mLabelCardViewElevation != 0) {
                    mLabelCardView.setElevation(mLabelCardViewElevation);
                    mLabelCardViewElevation = 0;
                }
            } else {
                if (mLabelCardViewBackground != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mLabelCardView.setBackground(mLabelCardViewBackground);
                    } else {
                        mLabelCardView.setBackgroundDrawable(mLabelCardViewBackground);
                    }
                    mLabelCardViewBackground = null;
                }
            }
        }
    }
}

