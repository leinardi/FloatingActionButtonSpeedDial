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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leinardi.android.speeddial.SpeedDialView.OnActionSelectedListener;

import java.lang.annotation.Retention;

import static com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_AUTO;
import static com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_MINI;
import static com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_NORMAL;
import static com.leinardi.android.speeddial.SpeedDialActionItem.RESOURCE_NOT_SET;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * View that contains fab button and its label.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FabWithLabelView extends ConstraintLayout {
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
    @Orientation
    private int mOrientation = Orientation.HORIZONTAL;

    @Retention(SOURCE)
    @IntDef({Orientation.HORIZONTAL, Orientation.VERTICAL})
    public @interface Orientation {
        int HORIZONTAL = 0;
        int VERTICAL = 1;
    }

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

    @SuppressLint("RestrictedApi")
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        getFab().setVisibility(visibility);
        if (isLabelEnabled()) {
            getLabelBackground().setVisibility(visibility);
        }
    }

    public void setOrientation(@Orientation int orientation) {
        mOrientation = orientation;
        setFabSize(mCurrentFabSize);
        setLabel(mLabelTextView.getText().toString());
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
    public void setLabelEnabled(boolean enabled) {
        mIsLabelEnabled = enabled && !TextUtils.isEmpty(mLabelTextView.getText());
        mLabelCardView.setVisibility(mIsLabelEnabled ? View.VISIBLE : View.GONE);
    }

    /**
     * Returns FAB labels background card.
     */
    public CardView getLabelBackground() {
        return mLabelCardView;
    }

    /**
     * Returns FAB label text view.
     */
    public TextView getLabelTextView() {
        return mLabelTextView;
    }

    /**
     * Returns the {@link FloatingActionButton}.
     */
    public FloatingActionButton getFab() {
        return mFab;
    }

    public SpeedDialActionItem getSpeedDialActionItem() {
        if (mSpeedDialActionItem == null) {
            throw new IllegalStateException("SpeedDialActionItem not set yet!");
        }
        return mSpeedDialActionItem;
    }

    /**
     * Returns an instance of the {@link SpeedDialActionItem.Builder} initialized with the current instance of the
     * {@link SpeedDialActionItem} to make it easier to modify the current Action Item settings.
     */
    public SpeedDialActionItem.Builder getSpeedDialActionItemBuilder() {
        return new SpeedDialActionItem.Builder(getSpeedDialActionItem());
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

        mFab = rootView.findViewById(R.id.sd_fab);
        mLabelTextView = rootView.findViewById(R.id.sd_label);
        mLabelCardView = rootView.findViewById(R.id.sd_label_container);

        setFabSize(SIZE_MINI);
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
        if (mOrientation == Orientation.HORIZONTAL) {
            rootLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, fabSizePx);
            LayoutParams cardParams = getHorizontalLabelLayoutParams(mFab);
            mLabelCardView.setLayoutParams(cardParams);
            int cardViewId = mLabelCardView.getId();
            fabLayoutParams.endToEnd = LayoutParams.PARENT_ID;
            fabLayoutParams.startToEnd = cardViewId;
            fabLayoutParams.topToTop = LayoutParams.PARENT_ID;
            fabLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
            if (fabSize == SIZE_NORMAL) {
                int excessMargin = (normalFabSizePx - miniFabSizePx) / 2;
                fabLayoutParams.setMargins(fabSideMarginPx - excessMargin, 0, fabSideMarginPx - excessMargin, 0);
            } else {
                fabLayoutParams.setMargins(fabSideMarginPx, 0, fabSideMarginPx, 0);
            }
        } else {
            fabLayoutParams.setMargins(0, 0, 0, 0);
            rootLayoutParams = new LayoutParams(fabSizePx, ViewGroup.LayoutParams.WRAP_CONTENT);
            int cardViewId = mLabelCardView.getId();
            LayoutParams cardParams = getVerticalLabelLayoutParams(mFab);
            mLabelCardView.setLayoutParams(cardParams);
            fabLayoutParams.endToEnd = LayoutParams.PARENT_ID;
            fabLayoutParams.startToStart = LayoutParams.PARENT_ID;
            fabLayoutParams.topToTop = LayoutParams.PARENT_ID;
            fabLayoutParams.bottomToTop = cardViewId;
        }
        setLayoutParams(rootLayoutParams);
        mFab.setLayoutParams(fabLayoutParams);
        mCurrentFabSize = fabSize;
    }

    private static LayoutParams getVerticalLabelLayoutParams(View mainFabView) {
        int fabId = mainFabView.getId();
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.startToStart = fabId;
        layoutParams.endToEnd = fabId;
        layoutParams.topToBottom = fabId;
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        return layoutParams;
    }

    private static LayoutParams getHorizontalLabelLayoutParams(View mainFabView) {
        int fabId = mainFabView.getId();
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.startToStart = LayoutParams.PARENT_ID;
        layoutParams.endToStart = fabId;
        layoutParams.topToTop = LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        return layoutParams;
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
            setLabelEnabled(true);
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

