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
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SpeedDialOverlayLayout extends RelativeLayout {
    private static final String TAG = SpeedDialOverlayLayout.class.getSimpleName();
    private boolean mClickableOverlay;
    private int mAnimationDuration;

    public SpeedDialOverlayLayout(@NonNull Context context) {
        super(context);
    }

    public SpeedDialOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeedDialOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
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

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpeedDialOverlayLayout, 0, 0);
        int overlayColor = ResourcesCompat.getColor(getResources(), R.color.sd_overlay_color, context.getTheme());
        try {
            overlayColor = attr.getColor(R.styleable.SpeedDialOverlayLayout_android_background, overlayColor);
            mClickableOverlay = attr.getBoolean(R.styleable.SpeedDialOverlayLayout_clickable_overlay, true);
        } catch (Exception e) {
            Log.e(TAG, "Failure setting FabOverlayLayout attrs", e);
        } finally {
            attr.recycle();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(getResources().getDimension(R.dimen.sd_overlay_elevation));
        }
        setBackgroundColor(overlayColor);
        setVisibility(View.GONE);
        mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
    }
}
