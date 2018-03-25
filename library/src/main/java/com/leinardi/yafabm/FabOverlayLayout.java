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
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

@SuppressWarnings("unused")
@CoordinatorLayout.DefaultBehavior(ViewSnackbarBehavior.class)
public class FabOverlayLayout extends RelativeLayout {
    private static final String TAG = FabOverlayLayout.class.getSimpleName();
    @ColorInt
    private int mOverlayColor;
    private boolean mClickableOverlay;
    private int mAnimationDuration;

    public FabOverlayLayout(@NonNull Context context) {
        super(context);
    }

    public FabOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FabOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FabOverlayLayout, 0, 0);
        try {
            mOverlayColor = attr.getColor(R.styleable.FabOverlayLayout_background_color,
                    Color.parseColor("#aaffffff"));
            mClickableOverlay = attr.getBoolean(R.styleable.FabOverlayLayout_clickable_overlay, true);
        } catch (Exception e) {
            Log.e(TAG, "Failure setting FabOverlayLayout attrs", e);
        } finally {
            attr.recycle();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(getResources().getDimension(R.dimen.fab_overlay_elevation));
        }
        setBackgroundColor(mOverlayColor);
        setVisibility(View.GONE);
        mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    @ColorInt
    public int getOverlayColor() {
        return mOverlayColor;
    }

    public void setOverlayColor(@ColorInt int overlayColor) {
        setBackgroundColor(overlayColor);
        this.mOverlayColor = overlayColor;
    }

    public boolean hasClickableOverlay() {
        return mClickableOverlay;
    }

    public void setClickableOverlay(boolean clickableOverlay) {
        this.mClickableOverlay = clickableOverlay;
    }

    public void setAnimationDuration(int animationDuration) {
        this.mAnimationDuration = animationDuration;
    }

    public void show() {
        toggle(true);
    }

    public void show(boolean immediately) {
        toggle(true, immediately);
    }

    public void hide() {
        toggle(false);
    }

    public void hide(boolean immediately) {
        toggle(false, immediately);
    }

    public void toggle(boolean show) {
        toggle(show, false);
    }

    public void toggle(final boolean show, boolean immediately) {
        if (show) {
            if (immediately) {
                setVisibility(VISIBLE);
            } else {
                UiUtils.fadeInAnim(this);
            }
        } else {
            if (immediately) {
                setVisibility(GONE);
            } else {
                UiUtils.fadeOutAnim(this);
            }
        }
    }
}
