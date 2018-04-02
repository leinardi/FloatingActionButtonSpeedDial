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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

class UiUtils {
    private static final int SHORT_ANIM_TIME = 200;
    private static final float ROTATION_ANGLE = 45.0F;

    private UiUtils() {
    }

    static int getPrimaryColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    static int getAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    static int dpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics));
    }

    static int pxToDp(float px) {
        return Math.round(px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Fade out animation.
     *
     * @param view view to animate.
     */
    public static void fadeOutAnim(final View view) {
        ViewCompat.animate(view).cancel();
        view.setAlpha(1F);
        view.setVisibility(VISIBLE);
        ViewCompat.animate(view)
                .alpha(0F)
                .withLayer()
                .setDuration(SHORT_ANIM_TIME)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(GONE);
                    }
                })
                .start();
    }

    /**
     * Fade out animation.
     *
     * @param view view to animate.
     */
    public static void fadeInAnim(final View view) {
        ViewCompat.animate(view).cancel();
        view.setAlpha(0);
        view.setVisibility(VISIBLE);
        ViewCompat.animate(view)
                .alpha(1F)
                .withLayer()
                .setDuration(SHORT_ANIM_TIME)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Closing animation.
     *
     * @param view view that starts that animation.
     */
    public static void shrinkAnim(final View view, final boolean removeView) {
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view)
                .alpha(0F)
                .withLayer()
                .setDuration(SHORT_ANIM_TIME)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (removeView) {
                            ViewGroup parent = (ViewGroup) view.getParent();
                            if (parent != null) {
                                parent.removeView(view);
                            }
                        } else {
                            view.setVisibility(GONE);
                        }
                    }
                })
                .start();
    }

    /**
     * Rotate a view of {@link #ROTATION_ANGLE} degrees.
     *
     * @param view    The view to rotate.
     * @param animate true to animate the rotation, false to be instant.
     * @see #rotateBackward(View, boolean)
     */
    public static void rotateForward(View view, boolean animate) {
        ViewCompat.animate(view)
                .rotation(ROTATION_ANGLE)
                .withLayer()
                .setDuration(animate ? SHORT_ANIM_TIME : 0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Rotate a view back to its default angle (0°).
     *
     * @param view    The view to rotate.
     * @param animate true to animate the rotation, false to be instant.
     * @see #rotateForward(View, boolean)
     */
    public static void rotateBackward(View view, boolean animate) {
        ViewCompat.animate(view)
                .rotation(0.0F)
                .withLayer()
                .setDuration(animate ? SHORT_ANIM_TIME : 0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    public static Drawable getRotateDrawable(Context context, @DrawableRes int drawableRes) {
        final Drawable drawable = AppCompatResources.getDrawable(context, drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap
                .Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        canvas.rotate(-ROTATION_ANGLE, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
