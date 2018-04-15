# Change Log

## [1.0-alpha05] - 2018-04-15
- `Drawable`s are not parcelables so is not possible to restore them when the view is recreated 
  for example after an orientation change. If possible always use the `DrawableRes`.
- Fixed Expansion mode and rotate angle not persisted on orientation change
- Fixed inverted behavior for `SpeedDialView.setMainFabOpenBackgroundColor` 
  and `SpeedDialView.setMainFabCloseBackgroundColor`
- Added `SpeedDialView.getActionItems()`
- `SpeedDialView.OnChangeListener.onMainActionSelected()` now returns true to keep the Speed Dial open, false to close it

## [1.0-alpha04] - 2018-04-14
- android support library 27.1.1
- lowered minSdk from 15 to 14
- several API changes:
    - renamed `FabWithLabelView.setOptionFabSelectedListener()` to `FabWithLabelView.setOnActionSelectedListener()`
    - renamed `FabWithLabelView.isLabelEnable()` to `FabWithLabelView.isLabelEnabled()`
    - added `SpeedDialActionItem.Builder(@IdRes int id, @Nullable Drawable d)`
    - added `SpeedDialView.setOnChangeListener(OnChangeListener l)`
    - removed `SpeedDialView.setMainFabOnClickListener(OnClickListener l)`
    - renamed `OnOptionFabSelectedListener` to `OnActionSelectedListener`
    - renamed `SpeedDialView.setOptionFabSelectedListener()` to `SpeedDialView.setOnActionSelectedListener()`
    - renamed `SpeedDialView.addAllFabOptionItem()` to `SpeedDialView.addAllActionItems()`
    - renamed `SpeedDialView.addFabOptionItem()` to `SpeedDialView.addActionItem()`
    - renamed `SpeedDialView.replaceFabOptionItem()` to `SpeedDialView.replaceActionItem()`
    - renamed `SpeedDialView.removeFabOptionItemById()` to `SpeedDialView.removeActionItemById()`
    - renamed `SpeedDialView.removeFabOptionItem()` to `SpeedDialView.removeActionItem()`
    - renamed `SpeedDialView.isFabMenuOpen()` to `SpeedDialView.isOpen()`
    - renamed `SpeedDialView.closeOptionsMenu()` to `SpeedDialView.close()`
    - renamed `SpeedDialView.openOptionsMenu()` to `SpeedDialView.open()`
    - renamed `SpeedDialView.toggleOptionsMenu()` to `SpeedDialView.toggle()`
    - removed attributes `android:src`
    - renamed attributes `srcCompat` to `sdMainFabOpenSrc`
    - renamed attributes `sdFabCloseSrc` to `sdMainFabCloseSrc`
    - removed attributes `sdFabRotateOnToggle`
    - added attributes `sdMainFabCloseRotateAngle`
 - minor fixes

## [1.0-alpha03] - 2018-04-02
- fixed #4: FAB icons rotate only once
- renamed attribute close_src to sdFabCloseSrc
- added attributes `sdFabRotateOnToggle` and `sdExpansionMode`
- fixed various minor UI issues

## [1.0-alpha02] - 2018-04-01
- first public release
