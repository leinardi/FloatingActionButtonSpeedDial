package com.github.ag.floatingactionmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused,RestrictedApi")
public class FabWithOptions extends LinearLayout {

    private List<FabWithTitleView> mFabWithTitles = new ArrayList<>();
    private FloatingActionButton mMainFab;
    private MenuBuilder mFabMenu = new MenuBuilder(getContext());
    private boolean enableMiniFabs = false;
    private Context context;
    private OptionsFabLayout mOptionsFabLayout;
    private int mainFabOpenDrawable;
    private int mainFabCloseDrawable;


    /**
     * Constructors
     */
    public FabWithOptions(Context context) {
        this(context, null);
    }

    public FabWithOptions(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context) {
        setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        View rootView = inflate(context, R.layout.fab_options, this);
        mMainFab = (FloatingActionButton) rootView.findViewById(R.id.mega_fab);
        setOrientation(VERTICAL);
        setClipChildren(false);
    }

    /**
     * Init custom attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OptionsFabLayout, 0, 0);
        setupMainFab(context, a);
        setupMiniFabs(context, a);
        a.recycle();
    }

    /**
     * Set Main FloatingActionButton parameters.
     */
    private void setupMainFab(Context context, TypedArray a) {
        mainFabOpenDrawable = a.getResourceId(R.styleable.OptionsFabLayout_open_src, -1);
        mainFabCloseDrawable = a.getResourceId(R.styleable.OptionsFabLayout_close_src, -1);

        int color = a.getColor(
                R.styleable.OptionsFabLayout_color,
                ContextCompat.getColor(context, R.color.colorAccent));

        mMainFab.setBackgroundTintList(ColorStateList.valueOf(color));

        if (mainFabOpenDrawable != -1) {
            mMainFab.setImageResource(mainFabOpenDrawable);
        }
        //Set margins on Main FloatingActionButton.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MarginLayoutParams params = (MarginLayoutParams) mMainFab.getLayoutParams();
            params.setMargins(0, 0, pxToDp(context, 4), 0);
            if (Build.VERSION.SDK_INT >= 17)
                params.setMarginEnd(pxToDp(context, 4));
            mMainFab.setLayoutParams(params);
        }
    }

    /**
     * Set mini fab's titles,drawables,colors.
     */
    private void setupMiniFabs(Context context, TypedArray a) {
        MenuInflater inflater = new MenuInflater(context);

        int miniFabsColor = a.getColor(
                R.styleable.OptionsFabLayout_options_color,
                ContextCompat.getColor(context, R.color.colorPrimary));

        int menuId = a.getResourceId(R.styleable.OptionsFabLayout_options_menu, -1);
        if (menuId != -1) {
            inflater.inflate(menuId, mFabMenu);
            for (int i = 0; i < mFabMenu.size(); i++) {
                FabWithTitleView mFabWithTitleView = new FabWithTitleView(context);

                setupMarginsForMiniFab(context, mFabWithTitleView);

                mFabWithTitleView.setMiniFabTitle(mFabMenu.getItem(i).getTitle());
                mFabWithTitleView.setFabIcon(mFabMenu.getItem(i).getIcon());
                mFabWithTitleView.setMiniFabColor(miniFabsColor);

                addView(mFabWithTitleView, 0);
                mFabWithTitles.add(mFabWithTitleView);
            }
            setMiniFabsEnable(false);
        }
    }

    /**
     * Set margins for mini fab's.
     */
    private void setupMarginsForMiniFab(Context context, FabWithTitleView FabWithTitleView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MarginLayoutParams miniFabParams = (MarginLayoutParams) FabWithTitleView.getFab().getLayoutParams();
            miniFabParams.setMargins(0, 0, pxToDp(context, 12), 0);
            if (Build.VERSION.SDK_INT >= 17)
                miniFabParams.setMarginEnd(pxToDp(context, 12));
            FabWithTitleView.getFab().setLayoutParams(miniFabParams);
        }
    }

    /**
     * Converts px to dp.
     */
    public static int pxToDp(Context context, int pixel) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                pixel,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Set mini fab's state (enabled or disabled).
     */
    public void setMiniFabsEnable(boolean enable) {
        for (FabWithTitleView mFabWithlabel : mFabWithTitles) {
            mFabWithlabel.getCard().setVisibility(enable ? View.VISIBLE : View.GONE);
            mFabWithlabel.getFab().setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Set Main FloatingActionButton ClickListener.
     *
     * @param listener listener to set.
     * @param layout   layout that contains FloatingActionButton.
     */
    public void setMainFabOnClickListener(final OnClickListener listener, final OptionsFabLayout layout) {

        mOptionsFabLayout = layout;
        mOptionsFabLayout.mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOptionsMenu();
            }
        });

        mMainFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Show menu when ripple ends.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!enableMiniFabs) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                layout.setElevation(pxToDp(context, 5));
                            } else {
                                layout.bringToFront();
                            }
                            visibilitySetup(View.VISIBLE);
                            enableMiniFabs = true;

                            if (mainFabCloseDrawable != -1) {
                                mMainFab.setImageResource(mainFabCloseDrawable);
                            }
                        } else {
                            listener.onClick(view);
                        }
                    }
                }, 50);

            }
        });
    }

    /**
     * @return returns all mini fab's with title.
     */
    public List<FabWithTitleView> getMiniFabs() {
        return mFabWithTitles;
    }

    MenuItem getMenuItem(int index) {
        return mFabMenu.getItem(index);
    }

    /**
     * Closes options menu.
     */
    void closeOptionsMenu() {
        enableMiniFabs = false;
        visibilitySetup(View.GONE);

        if (mainFabOpenDrawable != -1) {
            mMainFab.setImageResource(mainFabOpenDrawable);
        }
    }

    /**
     * @return returns true if menu opened,false otherwise.
     */
    boolean isMiniFabsOpened() {
        return enableMiniFabs;
    }

    /**
     * Closing animation.
     *
     * @param myView view that starts that animation.
     */
    private void shrinkAnim(final View myView) {
        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        anim.setDuration(130);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (myView instanceof FabWithTitleView) {
                    ((FabWithTitleView) myView).getCard().setVisibility(View.GONE);
                    ((FabWithTitleView) myView).getFab().setVisibility(View.GONE);
                }
                myView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        myView.startAnimation(anim);
    }

    /**
     * Menu opening animation.
     *
     * @param myView view that starts that animation.
     */
    private void enlargeAnim(View myView) {
        myView.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.enlarge);
        myView.startAnimation(anim);
    }

    /**
     * Set menus visibility (visible or invisible).
     */
    private void visibilitySetup(int visible) {

        if (visible == View.VISIBLE) {
            Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            anim.setDuration(50);
            mOptionsFabLayout.mRootView.setVisibility(visible);
            mOptionsFabLayout.mRootView.startAnimation(anim);
        }
        if (visible == View.VISIBLE)
            for (int i = 0; i < mFabWithTitles.size(); i++) {
                mFabWithTitles.get(i).setVisibility(View.VISIBLE);

                final int finalI = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enlargeAnim(mFabWithTitles.get(finalI).getFab());
                    }
                }, i * 15);
                if (mFabWithTitles.get(finalI).getTitleEnable())
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFabWithTitles.get(finalI).getCard().setVisibility(View.VISIBLE);
                            mFabWithTitles.get(finalI)
                                    .getCard()
                                    .startAnimation(
                                            AnimationUtils.loadAnimation(context, R.anim.fade_and_translate));
                        }
                    }, mFabWithTitles.size() * 18);
            }
        else {
            shrinkAnim(mOptionsFabLayout.mRootView);
            for (int i = 0; i < mFabWithTitles.size(); i++) {
                shrinkAnim(mFabWithTitles.get(i));
            }

        }
    }
}
