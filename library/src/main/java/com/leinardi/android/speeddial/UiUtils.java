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
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UiUtils {

    private UiUtils() {
    }

    public static int getPrimaryColor(Context context) {
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

    public static int getAccentColor(Context context) {
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

    public static int dpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics));
    }

    public static int pxToDp(float px) {
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
                .setDuration(view.getContext().getResources().getInteger(R.integer.sd_close_animation_duration))
                .setInterpolator(new FastOutSlowInInterpolator())
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
                .setDuration(view.getContext().getResources().getInteger(R.integer.sd_open_animation_duration))
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    /**
     * SpeedDial opening animation.
     *
     * @param view        view that starts that animation.
     * @param startOffset a delay in time to start the animation
     */
    public static void enlargeAnim(View view, long startOffset) {
        ViewCompat.animate(view).cancel();
        view.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.sd_scale_fade_and_translate_in);
        anim.setStartOffset(startOffset);
        view.startAnimation(anim);
    }

    /**
     * SpeedDial closing animation.
     *
     * @param view        view that starts that animation.
     * @param startOffset a delay in time to start the animation
     */
    public static void shrinkAnim(final View view, long startOffset) {
        ViewCompat.animate(view).cancel();
        view.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.sd_scale_fade_and_translate_out);
        anim.setStartOffset(startOffset);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(anim);
    }

    /**
     * Closing animation.
     *
     * @param view       view that starts that animation.
     * @param removeView true to remove the view when the animation is over, false otherwise.
     */
    public static void shrinkAnim(final View view, final boolean removeView) {
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view)
                .alpha(0F)
                .withLayer()
                .setDuration(view.getContext().getResources().getInteger(R.integer.sd_close_animation_duration))
                .setInterpolator(new FastOutSlowInInterpolator())
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
     * Rotate a view of the specified degrees.
     *
     * @param view    The view to rotate.
     * @param animate true to animate the rotation, false to be instant.
     * @see #rotateBackward(View, boolean)
     */
    public static void rotateForward(View view, float angle, boolean animate) {
        ViewCompat.animate(view)
                .rotation(angle)
                .withLayer()
                .setDuration(animate ?
                        view.getContext().getResources().getInteger(R.integer.sd_rotate_animation_duration) : 0)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    /**
     * Rotate a view back to its default angle (0°).
     *
     * @param view    The view to rotate.
     * @param animate true to animate the rotation, false to be instant.
     * @see #rotateForward(View, float, boolean)
     */
    public static void rotateBackward(View view, boolean animate) {
        ViewCompat.animate(view)
                .rotation(0.0F)
                .withLayer()
                .setDuration(animate ?
                        view.getContext().getResources().getInteger(R.integer.sd_rotate_animation_duration) : 0)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    public static Drawable getRotateDrawable(final Drawable drawable, final float angle) {
        if (angle == 0) {
            return drawable;
        }
        final Drawable[] drawables = {drawable};
        return new LayerDrawable(drawables) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, drawable.getBounds().width() / 2, drawable.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }

    /**
     * Creates a {@link Bitmap} from a {@link Drawable}.
     */
    @Nullable
    public static Bitmap getBitmapFromDrawable(@Nullable Drawable drawable) {
        if (drawable == null) {
            return null;
        } else {
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                // Single color bitmap will be created of 1x1 pixel
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap
                        .Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * Creates a {@link Drawable} from a {@link Bitmap}.
     */
    @Nullable
    public static Drawable getDrawableFromBitmap(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        } else {
            return new BitmapDrawable(bitmap);
        }
    }

    /**
     * Perform a tap of {@link ViewConfiguration#getTapTimeout()} milliseconds on the view.
     *
     * @param view the view you want to tap
     */
    public static void performTap(final View view) {
        view.setPressed(true);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setPressed(false);
                view.performClick();
            }
        }, ViewConfiguration.getTapTimeout());
    }
}
