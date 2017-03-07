package com.github.ag.floatingactionmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

@SuppressWarnings("unused")
public class OptionsFabLayout extends RelativeLayout {

    private FabWithOptions mFabWithOptions;
    FrameLayout mRootView;
    int bgColor;
    private Context context;

    /**
     * Constructors.
     */
    public OptionsFabLayout(Context context) {
        this(context, null);
    }

    public OptionsFabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }


    private void init(Context context) {
        this.context = context;
        mRootView = new FrameLayout(context);
        mRootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mRootView);
    }

    /**
     * Init custom attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OptionsFabLayout, 0, 0);

        bgColor = a.getColor(
                R.styleable.OptionsFabLayout_background_color,
                ContextCompat.getColor(context, R.color.colorWhiteBackground));

        mRootView.setBackgroundColor(bgColor);
        mRootView.setVisibility(View.INVISIBLE);

        mFabWithOptions = new FabWithOptions(context, attrs);
        addView(mFabWithOptions);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFabWithOptions.getLayoutParams();
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        mFabWithOptions.setLayoutParams(params);

        a.recycle();
    }

    /**
     * Sets Main FloatingActionButton ClickListener.
     *
     * @param listener listener to set.
     */
    public void setMainFabOnClickListener(OnClickListener listener) {
        mFabWithOptions.setMainFabOnClickListener(listener, this);
    }

    /**
     * Closes fab menu.
     */
    public void closeOptionsMenu() {
        mFabWithOptions.closeOptionsMenu();
    }

    /**
     * @return returns true if menu opened,false otherwise.
     */
    public boolean isOptionsMenuOpened() {
        return mFabWithOptions.isMiniFabsOpened();
    }

    /**
     * Sets mini fab's colors.
     *
     * @param colors colors to set.
     */
    public void setMiniFabsColors(int... colors) {
        for (int i = 0; i < mFabWithOptions.getMiniFabs().size(); i++) {
            mFabWithOptions.getMiniFabs()
                    .get(i)
                    .getFab()
                    .setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(context, colors[i])));
        }
    }

    /**
     * Set a listener that will be notified when a menu fab is selected.
     *
     * @param listener listener to set.
     */
    public void setMiniFabSelectedListener(final OnMiniFabSelectedListener listener) {
        for (int miniFabIndex = 0; miniFabIndex < mFabWithOptions.getMiniFabs().size(); miniFabIndex++) {
            final int finalMiniFabIndex = miniFabIndex;
            mFabWithOptions.getMiniFabs().get(miniFabIndex).getFab().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMiniFabSelected(mFabWithOptions.getMenuItem(finalMiniFabIndex));
                }
            });
            mFabWithOptions.getMiniFabs().get(miniFabIndex).getCard().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onMiniFabSelected(mFabWithOptions.getMenuItem(finalMiniFabIndex));
                }

            });
            mFabWithOptions.getMiniFabs().get(miniFabIndex).getCard().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        ((CardView) v).setCardBackgroundColor(Color.parseColor("#ebebeb"));
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        ((CardView) v).setCardBackgroundColor(
                                ContextCompat.getColor(context, R.color.cardview_light_background));
                    return false;
                }
            });
        }
    }

    /**
     * Listener for handling events on mini fab's.
     */
    public static abstract class OnMiniFabSelectedListener {
        public abstract void onMiniFabSelected(MenuItem fabItem);
    }

}
