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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.FloatingActionButton;

import static android.support.design.widget.FloatingActionButton.SIZE_AUTO;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SpeedDialActionItem implements Parcelable {
    public static final int NOT_SET = Integer.MIN_VALUE;
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
    @IdRes
    private final int mId;
    private final String mLabel;
    @DrawableRes
    private final int mFabImageResource;
    @ColorInt
    private final int mFabImageTintColor;
    @ColorInt
    private final int mFabBackgroundColor;
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
        mFabImageTintColor = builder.mFabImageTintColor;
        mFabImageResource = builder.mImageResource;
        mFabBackgroundColor = builder.mFabBackgroundColor;
        mLabelColor = builder.mLabelColor;
        mLabelBackgroundColor = builder.mLabelBackgroundColor;
        mLabelClickable = builder.mLabelClickable;
        mFabSize = builder.mFabSize;
        mTheme = builder.mTheme;
    }

    protected SpeedDialActionItem(Parcel in) {
        this.mId = in.readInt();
        this.mLabel = in.readString();
        this.mFabImageResource = in.readInt();
        this.mFabImageTintColor = in.readInt();
        this.mFabBackgroundColor = in.readInt();
        this.mLabelColor = in.readInt();
        this.mLabelBackgroundColor = in.readInt();
        this.mLabelClickable = in.readByte() != 0;
        this.mFabSize = in.readInt();
        this.mTheme = in.readInt();
    }

    public int getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    @DrawableRes
    public int getFabImageResource() {
        return mFabImageResource;
    }

    @ColorInt
    public int getFabImageTintColor() {
        return mFabImageTintColor;
    }

    @ColorInt
    public int getFabBackgroundColor() {
        return mFabBackgroundColor;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mLabel);
        dest.writeInt(this.mFabImageResource);
        dest.writeInt(this.mFabImageTintColor);
        dest.writeInt(this.mFabBackgroundColor);
        dest.writeInt(this.mLabelColor);
        dest.writeInt(this.mLabelBackgroundColor);
        dest.writeByte(this.mLabelClickable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mFabSize);
        dest.writeInt(this.mTheme);
    }

    // Disabled due to https://issuetracker.google.com/issues/77303906
    @FloatingActionButton.Size
    /* public */ int getFabSize() {
        return mFabSize;
    }

    public static class Builder {
        @IdRes
        private final int mId;
        @DrawableRes
        private final int mImageResource;
        @ColorInt
        private int mFabImageTintColor = NOT_SET;
        private String mLabel;
        @ColorInt
        private int mFabBackgroundColor = NOT_SET;
        @ColorInt
        private int mLabelColor = NOT_SET;
        @ColorInt
        private int mLabelBackgroundColor = NOT_SET;
        private boolean mLabelClickable = true;
        @FloatingActionButton.Size
        private int mFabSize = SIZE_AUTO;
        @StyleRes
        private int mTheme = NOT_SET;

        public Builder(@IdRes int id, @DrawableRes int imageResource) {
            mId = id;
            mImageResource = imageResource;
        }

        public Builder setLabel(String label) {
            mLabel = label;
            return this;
        }

        public Builder setFabImageTintColor(int fabImageTintColor) {
            mFabImageTintColor = fabImageTintColor;
            return this;
        }

        public Builder setFabBackgroundColor(@ColorInt int fabBackgroundColor) {
            mFabBackgroundColor = fabBackgroundColor;
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

        // Disabled due to https://issuetracker.google.com/issues/77303906
        /* public */ Builder setFabSize(@FloatingActionButton.Size int fabSize) {
            mFabSize = fabSize;
            return this;
        }

    }
}
