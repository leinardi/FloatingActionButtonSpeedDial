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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

public class FabOptionItem implements Parcelable {
    public static final int COLOR_NOT_SET = Integer.MIN_VALUE;
    @IdRes
    private final int mId;
    private final String mLabel;
    @DrawableRes
    private final int mFabImageResource;
    @ColorInt
    private final int mFabBackgroundColor;
    @ColorInt
    private final int mLabelColor;
    @ColorInt
    private final int mLabelBackgroundColor;
    private final boolean mLabelClickable;

    private FabOptionItem(Builder builder) {
        mId = builder.mId;
        mLabel = builder.mLabel;
        mFabImageResource = builder.mImageResource;
        mFabBackgroundColor = builder.mFabBackgroundColor;
        mLabelColor = builder.mLabelColor;
        mLabelBackgroundColor = builder.mLabelBackgroundColor;
        mLabelClickable = builder.mLabelClickable;
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

    public static class Builder {
        private String mLabel;
        @IdRes
        private int mId;
        @DrawableRes
        private int mImageResource;
        @ColorInt
        private int mFabBackgroundColor = COLOR_NOT_SET;
        @ColorInt
        private int mLabelColor = COLOR_NOT_SET;
        @ColorInt
        private int mLabelBackgroundColor = COLOR_NOT_SET;
        private boolean mLabelClickable = true;

        public Builder(@IdRes int id, @DrawableRes int imageResource) {
            mId = id;
            mImageResource = imageResource;
        }

        public Builder setLabel(String label) {
            mLabel = label;
            return this;
        }

        public Builder setImageResource(int imageResource) {
            mImageResource = imageResource;
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

        public FabOptionItem create() {
            return new FabOptionItem(this);
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
        dest.writeInt(this.mFabImageResource);
        dest.writeInt(this.mFabBackgroundColor);
        dest.writeInt(this.mLabelColor);
        dest.writeInt(this.mLabelBackgroundColor);
        dest.writeByte(this.mLabelClickable ? (byte) 1 : (byte) 0);
    }

    protected FabOptionItem(Parcel in) {
        this.mId = in.readInt();
        this.mLabel = in.readString();
        this.mFabImageResource = in.readInt();
        this.mFabBackgroundColor = in.readInt();
        this.mLabelColor = in.readInt();
        this.mLabelBackgroundColor = in.readInt();
        this.mLabelClickable = in.readByte() != 0;
    }

    public static final Parcelable.Creator<FabOptionItem> CREATOR = new Parcelable.Creator<FabOptionItem>() {
        @Override
        public FabOptionItem createFromParcel(Parcel source) {
            return new FabOptionItem(source);
        }

        @Override
        public FabOptionItem[] newArray(int size) {
            return new FabOptionItem[size];
        }
    };
}
