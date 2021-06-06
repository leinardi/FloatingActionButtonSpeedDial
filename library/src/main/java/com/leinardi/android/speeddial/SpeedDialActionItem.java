/*
 * Copyright 2021 Roberto Leinardi.
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
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StringDef;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_AUTO;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SpeedDialActionItem implements Parcelable {
    public static final int RESOURCE_NOT_SET = Integer.MIN_VALUE;

    @StringDef({TYPE_NORMAL, TYPE_FILL})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FabType { };
    public static final String TYPE_NORMAL = "normal";
    public static final String TYPE_FILL = "fill";

    @IdRes
    private final int mId;
    @Nullable
    private final String mLabel;
    @StringRes
    private final int mLabelRes;
    @Nullable
    private final String mContentDescription;
    @StringRes
    private final int mContentDescriptionRes;
    @DrawableRes
    private final int mFabImageResource;
    @Nullable
    private final Drawable mFabImageDrawable;
    @ColorInt
    private final int mFabImageTintColor;
    private final boolean mFabImageTint;
    private final String mFabType;
    @ColorInt
    private final int mFabBackgroundColor;
    @Px
    private final float mFabElevation;
    @ColorInt
    private final int mLabelColor;
    @ColorInt
    private final int mLabelBackgroundColor;
    private final boolean mLabelClickable;
    @FloatingActionButton.Size
    private final int mFabSize;
    @StyleRes
    private final int mTheme;

    private SpeedDialActionItem(Builder builder) {
        mId = builder.mId;
        mLabel = builder.mLabel;
        mLabelRes = builder.mLabelRes;
        mContentDescription = builder.mContentDescription;
        mContentDescriptionRes = builder.mContentDescriptionRes;
        mFabImageTintColor = builder.mFabImageTintColor;
        mFabImageTint = builder.mFabImageTint;
        mFabType = builder.mFabType;
        mFabImageResource = builder.mFabImageResource;
        mFabImageDrawable = builder.mFabImageDrawable;
        mFabBackgroundColor = builder.mFabBackgroundColor;
        mFabElevation = builder.mFabElevation;
        mLabelColor = builder.mLabelColor;
        mLabelBackgroundColor = builder.mLabelBackgroundColor;
        mLabelClickable = builder.mLabelClickable;
        mFabSize = builder.mFabSize;
        mTheme = builder.mTheme;
    }

    public int getId() {
        return mId;
    }

    @Nullable
    public String getLabel(Context context) {
        if (mLabel != null) {
            return mLabel;
        } else if (mLabelRes != RESOURCE_NOT_SET) {
            return context.getString(mLabelRes);
        } else {
            return null;
        }
    }

    @Nullable
    public String getContentDescription(Context context) {
        if (mContentDescription != null) {
            return mContentDescription;
        } else if (mContentDescriptionRes != RESOURCE_NOT_SET) {
            return context.getString(mContentDescriptionRes);
        } else {
            return null;
        }
    }

    /**
     * Gets the current Drawable, or null if no Drawable has been assigned.
     *
     * @param context A context to retrieve the Drawable from (needed for SpeedDialActionItem.Builder(int, int).
     * @return the speed dial item drawable, or null if no drawable has been assigned.
     */
    @Nullable
    public Drawable getFabImageDrawable(Context context) {
        if (mFabImageDrawable != null) {
            return mFabImageDrawable;
        } else if (mFabImageResource != RESOURCE_NOT_SET) {
            return AppCompatResources.getDrawable(context, mFabImageResource);
        } else {
            return null;
        }
    }

    @ColorInt
    public int getFabImageTintColor() {
        return mFabImageTintColor;
    }

    public boolean getFabImageTint() {
        return mFabImageTint;
    }

    @FabType
    public String getFabType() {
        return mFabType;
    }

    @ColorInt
    public int getFabBackgroundColor() {
        return mFabBackgroundColor;
    }

    @Px
    public float getFabElevation() {
        return mFabElevation;
    }

    @ColorInt
    public int getLabelColor() {
        return mLabelColor;
    }

    public int getLabelBackgroundColor() {
        return mLabelBackgroundColor;
    }

    public boolean isLabelClickable() {
        return mLabelClickable;
    }

    @StyleRes
    public int getTheme() {
        return mTheme;
    }

    public FabWithLabelView createFabWithLabelView(Context context) {
        FabWithLabelView fabWithLabelView;
        int theme = getTheme();
        if (theme == RESOURCE_NOT_SET) {
            fabWithLabelView = new FabWithLabelView(context);
        } else {
            fabWithLabelView = new FabWithLabelView(new ContextThemeWrapper(context, theme), null, theme);
        }
        fabWithLabelView.setSpeedDialActionItem(this);
        return fabWithLabelView;
    }

    @FloatingActionButton.Size
    public int getFabSize() {
        return mFabSize;
    }

    public static class Builder {
        @IdRes
        private final int mId;
        @DrawableRes
        private final int mFabImageResource;
        @Nullable
        private Drawable mFabImageDrawable;
        @ColorInt
        private int mFabImageTintColor = RESOURCE_NOT_SET;
        private boolean mFabImageTint = true;

        private String mFabType = TYPE_NORMAL;
        @Nullable
        private String mLabel;
        @StringRes
        private int mLabelRes = RESOURCE_NOT_SET;
        @Nullable
        private String mContentDescription;
        @StringRes
        private int mContentDescriptionRes = RESOURCE_NOT_SET;
        @ColorInt
        private int mFabBackgroundColor = RESOURCE_NOT_SET;
        @Px
        private float mFabElevation = 0f;
        @ColorInt
        private int mLabelColor = RESOURCE_NOT_SET;
        @ColorInt
        private int mLabelBackgroundColor = RESOURCE_NOT_SET;
        private boolean mLabelClickable = true;
        @FloatingActionButton.Size
        private int mFabSize = SIZE_AUTO;
        @StyleRes
        private int mTheme = RESOURCE_NOT_SET;

        /**
         * Creates a builder for a speed dial action item that uses a {@link DrawableRes} as icon.
         *
         * @param id               the identifier for this action item. The identifier must be unique to the instance
         *                         of {@link SpeedDialView}. The identifier should be a positive number.
         * @param fabImageResource resId the resource identifier of the drawable
         */
        public Builder(@IdRes int id, @DrawableRes int fabImageResource) {
            mId = id;
            mFabImageResource = fabImageResource;
            mFabImageDrawable = null;
        }

        /**
         * Creates a builder for a speed dial action item that uses a {@link Drawable} as icon.
         * <p class="note">{@link Drawable} are not parcelables so is not possible to restore them when the view is
         * recreated for example after an orientation change. If possible always use the {@link #Builder(int, int)}</p>
         *
         * @param id       the identifier for this action item. The identifier must be unique to the instance
         *                 of {@link SpeedDialView}. The identifier should be a positive number.
         * @param drawable the Drawable to set, or null to clear the content
         */
        public Builder(@IdRes int id, @Nullable Drawable drawable) {
            mId = id;
            mFabImageDrawable = drawable;
            mFabImageResource = RESOURCE_NOT_SET;
        }

        /**
         * Creates a builder for a speed dial action item that uses a {@link SpeedDialActionItem} instance to
         * initialize the default values.
         *
         * @param speedDialActionItem it will be used for the default values of the builder.
         */
        public Builder(SpeedDialActionItem speedDialActionItem) {
            mId = speedDialActionItem.mId;
            mLabel = speedDialActionItem.mLabel;
            mLabelRes = speedDialActionItem.mLabelRes;
            mContentDescription = speedDialActionItem.mContentDescription;
            mContentDescriptionRes = speedDialActionItem.mContentDescriptionRes;
            mFabImageResource = speedDialActionItem.mFabImageResource;
            mFabImageDrawable = speedDialActionItem.mFabImageDrawable;
            mFabImageTintColor = speedDialActionItem.mFabImageTintColor;
            mFabImageTint = speedDialActionItem.mFabImageTint;
            mFabType = speedDialActionItem.mFabType;
            mFabBackgroundColor = speedDialActionItem.mFabBackgroundColor;
            mFabElevation = speedDialActionItem.mFabElevation;
            mLabelColor = speedDialActionItem.mLabelColor;
            mLabelBackgroundColor = speedDialActionItem.mLabelBackgroundColor;
            mLabelClickable = speedDialActionItem.mLabelClickable;
            mFabSize = speedDialActionItem.mFabSize;
            mTheme = speedDialActionItem.mTheme;
        }

        public Builder setLabel(@Nullable String label) {
            mLabel = label;
            if (mContentDescription == null || mContentDescriptionRes == RESOURCE_NOT_SET) {
                mContentDescription = label;
            }
            return this;
        }

        public Builder setLabel(@StringRes int labelRes) {
            mLabelRes = labelRes;
            if (mContentDescription == null || mContentDescriptionRes == RESOURCE_NOT_SET) {
                mContentDescriptionRes = labelRes;
            }
            return this;
        }

        public Builder setContentDescription(@Nullable String contentDescription) {
            mContentDescription = contentDescription;
            return this;
        }

        public Builder setContentDescription(@StringRes int contentDescriptionRes) {
            mContentDescriptionRes = contentDescriptionRes;
            return this;
        }

        public Builder setFabImageTintColor(@Nullable @ColorInt Integer fabImageTintColor) {
            if (fabImageTintColor == null) {
                mFabImageTint = false;
            } else {
                mFabImageTint = true;
                mFabImageTintColor = fabImageTintColor;
            }
            return this;
        }

        /**
         * set SpeedDialActionItem size.
         * SpeedDialActionItem.TYPE_NORMAL Use normal Fab.
         * SpeedDialActionItem.TYPE_FILL Set Floating Action Button image to fill the button.
         */
        public Builder setFabType(@FabType String fabType) {
            mFabType = fabType;
            return this;
        }

        public Builder setFabBackgroundColor(@ColorInt int fabBackgroundColor) {
            mFabBackgroundColor = fabBackgroundColor;
            return this;
        }

        public Builder setFabElevation(@Px float elevation) {
            mFabElevation = elevation;
            return this;
        }

        public Builder setLabelColor(@ColorInt int labelColor) {
            mLabelColor = labelColor;
            return this;
        }

        public Builder setLabelBackgroundColor(@ColorInt int labelBackgroundColor) {
            mLabelBackgroundColor = labelBackgroundColor;
            return this;
        }

        public Builder setLabelClickable(boolean labelClickable) {
            mLabelClickable = labelClickable;
            return this;
        }

        public Builder setTheme(int mTheme) {
            this.mTheme = mTheme;
            return this;
        }

        public SpeedDialActionItem create() {
            return new SpeedDialActionItem(this);
        }

        public Builder setFabSize(@FloatingActionButton.Size int fabSize) {
            mFabSize = fabSize;
            return this;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mLabel);
        dest.writeInt(this.mLabelRes);
        dest.writeString(this.mContentDescription);
        dest.writeInt(this.mContentDescriptionRes);
        dest.writeInt(this.mFabImageResource);
        dest.writeInt(this.mFabImageTintColor);
        dest.writeByte(this.mFabImageTint ? (byte) 1 : (byte) 0);
        dest.writeString(this.mFabType);
        dest.writeInt(this.mFabBackgroundColor);
        dest.writeFloat(this.mFabElevation);
        dest.writeInt(this.mLabelColor);
        dest.writeInt(this.mLabelBackgroundColor);
        dest.writeByte(this.mLabelClickable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mFabSize);
        dest.writeInt(this.mTheme);
    }

    protected SpeedDialActionItem(Parcel in) {
        this.mId = in.readInt();
        this.mLabel = in.readString();
        this.mLabelRes = in.readInt();
        this.mContentDescription = in.readString();
        this.mContentDescriptionRes = in.readInt();
        this.mFabImageResource = in.readInt();
        this.mFabImageDrawable = null;
        this.mFabImageTintColor = in.readInt();
        this.mFabImageTint = in.readByte() != 0;
        this.mFabType = in.readString();
        this.mFabBackgroundColor = in.readInt();
        this.mFabElevation = in.readFloat();
        this.mLabelColor = in.readInt();
        this.mLabelBackgroundColor = in.readInt();
        this.mLabelClickable = in.readByte() != 0;
        this.mFabSize = in.readInt();
        this.mTheme = in.readInt();
    }

    public static final Creator<SpeedDialActionItem> CREATOR = new Creator<SpeedDialActionItem>() {
        @Override
        public SpeedDialActionItem createFromParcel(Parcel source) {
            return new SpeedDialActionItem(source);
        }

        @Override
        public SpeedDialActionItem[] newArray(int size) {
            return new SpeedDialActionItem[size];
        }
    };
}
