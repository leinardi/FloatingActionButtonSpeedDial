package com.github.ag.floatingactionmenu;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View that contains mini fab button and its title.
 */
@SuppressWarnings("unused")
final class FabWithTitleView extends LinearLayout {

    private TextView mTitle;
    private FloatingActionButton mMiniFab;
    private CardView mTitleBg;
    private boolean isTitleEnable;


    /**
     * Constructors.
     */
    public FabWithTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FabWithTitleView(Context context) {
        this(context, null);
    }


    /**
     * Init custom attributes.
     *
     * @param context context.
     * @param attrs   attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FabWithTitleView, 0, 0);

        String titleText = a.getString(R.styleable.FabWithTitleView_fab_title);
        mTitle.setText(titleText);

        int fabColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mMiniFab.setBackgroundTintList(ColorStateList.valueOf(fabColor));
        a.recycle();
    }

    private void init(Context context) {
        View rootView = inflate(context, R.layout.fab_with_label, this);

        mMiniFab = (FloatingActionButton) rootView.findViewById(R.id.mini_fab);
        mTitle = (TextView) rootView.findViewById(R.id.title_text);
        mTitleBg = (CardView) rootView.findViewById(R.id.title_card);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                FabWithOptions.pxToDp(context, 56));
        layoutParams.gravity = Gravity.END;
        setLayoutParams(layoutParams);
        setOrientation(LinearLayout.HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);
    }

    /**
     * Sets mini fab drawable.
     *
     * @param mDrawable drawable to set.
     */
    void setFabIcon(Drawable mDrawable) {
        mMiniFab.setImageDrawable(mDrawable);
    }

    /**
     * Sets mini fab title․
     *
     * @param sequence title to set.
     */
    void setMiniFabTitle(CharSequence sequence) {
        if (!TextUtils.isEmpty(sequence)) {
            mTitle.setText(sequence);
            isTitleEnable = true;
        } else {
            setTitleEnable(false);
            isTitleEnable = false;
        }
    }

    /**
     * @return true if button has title,false otherwise.
     */
    boolean getTitleEnable() {
        return isTitleEnable;
    }

    /**
     * Enables or disables title of button.
     */
    void setTitleEnable(boolean enable) {
        mTitleBg.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Sets mini fab color in floating action menu.
     *
     * @param color color to set.
     */
    void setMiniFabColor(int color) {
        mMiniFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    /**
     * @return This method returns mini fab titles background card.
     */
    CardView getCard() {
        return mTitleBg;
    }

    /**
     * @return This method returns mini fab in action menu.
     */
    FloatingActionButton getFab() {
        return mMiniFab;
    }

}

