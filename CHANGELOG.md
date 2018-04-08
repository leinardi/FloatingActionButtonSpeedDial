# Change Log

## [1.0-alpha04] - 2018-04-08
- android support library 27.1.1
- several API changes:
    - added `SpeedDialActionItem.Builder(@IdRes int id, @Nullable Drawable d)`
    - added `SpeedDialView.setOnSpeedDialChangeListener(OnSpeedDialChangeListener l)`
    - removed `SpeedDialView.setMainFabOnClickListener(OnClickListener l)`
    - renamed `OnOptionFabSelectedListener` to `OnSpeedDialClickListener`
    - renamed `SpeedDialView.setOptionFabSelectedListener()` to `SpeedDialView.setOnSpeedDialActionSelectedListener()`
    - renamed `SpeedDialView.addAllFabOptionItem()` to `SpeedDialView.addAllSpeedDialActionItems()`
    - renamed `SpeedDialView.addFabOptionItem()` to `SpeedDialView.addSpeedDialActionItem()`
    - renamed `SpeedDialView.replaceFabOptionItem()` to `SpeedDialView.replaceSpeedDialActionItem()`
    - renamed `SpeedDialView.removeFabOptionItemById()` to `SpeedDialView.removeSpeedDialActionItemById()`
    - renamed `SpeedDialView.removeFabOptionItem()` to `SpeedDialView.removeSpeedDialActionItem()`
    - renamed `SpeedDialView.isFabMenuOpen()` to `SpeedDialView.isSpeedDialOpen()`
    - renamed `SpeedDialView.closeOptionsMenu()` to `SpeedDialView.closeSpeedDial()`
    - renamed `SpeedDialView.openOptionsMenu()` to `SpeedDialView.openSpeedDial()`
    - renamed `SpeedDialView.toggleOptionsMenu()` to `SpeedDialView.toggleSpeedDial()`
    - renamed `FabWithLabelView.setOptionFabSelectedListener()` to `FabWithLabelView.setOnSpeedDialActionSelectedListener()`
  

## [1.0-alpha03] - 2018-04-02
- fixed #4: FAB icons rotate only once
- renamed attribute close_src to sdFabCloseSrc
- added attributes `sdFabRotateOnToggle` and `sdExpansionMode`
- fixed various minor UI issues

## [1.0-alpha02] - 2018-04-01
- first public release
