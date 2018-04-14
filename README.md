# Floating Action Button Speed Dial

[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml.svg?style=plastic)](https://jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml)
[![Travis](https://img.shields.io/travis/leinardi/FloatingActionButtonSpeedDial/master.svg?style=plastic)](https://travis-ci.org/leinardi/FloatingActionButtonSpeedDial)
[![GitHub license](https://img.shields.io/github/license/leinardi/FloatingActionButtonSpeedDial.svg?style=plastic)](https://github.com/leinardi/FloatingActionButtonSpeedDial/blob/master/LICENSE) 
[![Waffle.io - Columns and their card count](https://badge.waffle.io/leinardi/FloatingActionButtonSpeedDial.svg?columns=all&style=plastic)](https://waffle.io/leinardi/FloatingActionButtonSpeedDial) 
[![Stars](https://img.shields.io/github/stars/leinardi/FloatingActionButtonSpeedDial.svg?style=social&label=Stars)](https://github.com/leinardi/FloatingActionButtonSpeedDial/stargazers) 


<img src="/art/demo_1.gif" width="290" align="right" hspace="0" />

Android library providing an implementation of the [Material Design Floating Action Button Speed Dial](https://material.io/guidelines/components/buttons-floating-action-button.html#buttons-floating-action-button-transitions).

## Features
- [x] MinSdk 14
- [x] Highly customizable (label, icon, ripple, fab and label background colors, themes support) 
- [x] Same animations as in [Inbox by Gmail](https://play.google.com/store/apps/details?id=com.google.android.apps.inbox)
- [x] Option to have different icons for open/close state
- [x] Optional overlay/touch guard layout
- [x] Support for bottom, left and right menu expansion (left and right have no labels)
- [x] Out-of-the box support for Snackbar behavior
- [x] Optional support for `RecyclerView` and `NestedScrollView` behavior
- [x] Support for VectorDrawable
- [x] Easy to use

## Development status
Check the [Waffle.io](https://waffle.io/leinardi/FloatingActionButtonSpeedDial) board.

## How to use
### Gradle setup
#### Official releases
The library is available on Jcenter so no additonal repository is required.

Dependencies entry (latest version on Jcenter [![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml.svg?style=flat)](https://jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml)):
```
implementation "com.leinardi.android:speed-dial:1.0-alpha04"
```
#### Snapshots (development branch)
You can use JitPack to test the latest `master` (remember that `master` is the development branch and can be unstable or completely broken).

Add the JitPack repository to your build file:
```
maven { url 'https://jitpack.io' }
```

Add the dependency
```
implementation 'com.github.leinardi:FloatingActionButtonSpeedDial:master-SNAPSHOT'
```

### Basic use
Add the `SpeedDialView` to your layout:

```xml
<com.leinardi.android.speeddial.SpeedDialView
    android:id="@+id/speedDial"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    app:sdMainFabOpenSrc="@drawable/ic_add_white_24dp" />
```

Add the items to the `SpeedDialView`:

```java
SpeedDialView speedDialView = findViewById(R.id.speedDial);
speedDialView.addActionItem(
        new SpeedDialActionItem.Builder(R.id.fab_link, R.drawable.ic_link_white_24dp)
                .create()
);
```
Add the click listeners:
```java
speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
    @Override
    public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
        switch (speedDialActionItem.getId()) {
            case R.id.fab_link:
                showToast("Link action clicked!");
                return false; // true to keep the Speed Dial open
            default:
                return false;
        }
    }
});
```

### Optional steps
#### Add the main action click listener
```java
speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
    @Override
    public void onMainActionSelected() {
        // Call your main action here and than close the menu
        if (mSpeedDialView.isOpen()) {
            mSpeedDialView.close();
        }
    }

    @Override
    public void onToggleChanged(boolean isOpen) {
        Log.d(TAG, "Speed dial toggle state changed. Open = " + isOpen);
    }
});
```

#### Customizing the items
The `SpeedDialActionItem.Builder` provides several setters to customize the aspect of one item:

```java
mSpeedDialView.addActionItem(
        new SpeedDialActionItem.Builder(R.id.fab_custom_color, R.drawable.ic_custom_color)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.material_white_1000, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.inbox_primary, getTheme()))
                .setLabel(getString(R.string.label_custom_color))
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.inbox_primary, getTheme()))
                .setLabelClickable(false)
                .create()
);
```
Is is also possible to specify a theme to easily change the FAB background and ripple effect color:

```java
mSpeedDialView.addActionItem(
        new SpeedDialActionItem.Builder(R.id.fab_custom_theme, R.drawable.ic_theme_white_24dp)
                .setLabel(getString(R.string.label_custom_theme))
                .setTheme(R.style.AppTheme_Purple)
                .create());
```
```xml
<style name="AppTheme.Purple" parent="AppTheme">
    <item name="colorPrimary">@color/material_purple_500</item>
    <item name="colorPrimaryDark">@color/material_purple_700</item>
    <item name="colorAccent">@color/material_purple_a700</item>
    <item name="colorControlHighlight">@color/material_purple_200</item>
</style>
```

#### Adding an overlay/touch guard when the menu is open (like Inbox by Gmail)
You simply need to add the `SpeedDialOverlayLayout` to your layout:

```xml
<com.leinardi.android.speeddial.SpeedDialOverlayLayout
    android:id="@+id/overlay"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
and then provide the instance of that layout to the `SpeedDialView`:

```java
SpeedDialOverlayLayout overlayLayout = findViewById(R.id.overlay);
mSpeedDialView.setSpeedDialOverlayLayout(overlayLayout);
```

#### Hiding the FAB when scrolling a `RecyclerView` or a `NestedScrollView`
Just apply the `ScrollingViewSnackbarBehavior` to the `SpeedDialView`. This can be done via XML using
the convenience string resource `@string/speeddial_scrolling_view_snackbar_behavior`:

```xml
<com.leinardi.android.speeddial.SpeedDialView
    android:id="@+id/speedDial"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_behavior="@string/speeddial_scrolling_view_snackbar_behavior" />
```

Or programmatically:
```java
CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) speedDialView.getLayoutParams();
params.setBehavior(new SpeedDialView.ScrollingViewSnackbarBehavior());
speedDialView.requestLayout();
```

NB: for the behaviors to work, `SpeedDialView` needs to be a direct child of `CoordinatorLayout`

#### Disabling `SnackbarBehavior`
Since the `SnackbarBehavior` is enabled by default and, afaik, it is not possible to remove a Behavior, simply use apply the `SpeedDialView.NoBehavior` instead:
```java
CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) speedDialView.getLayoutParams();
params.setBehavior(new SpeedDialView.NoBehavior());
speedDialView.requestLayout();
```

### Sample project
A fully working example is available [here](/sample).

## Demo
### Video
https://www.youtube.com/watch?v=tWowiF5ElAg
### Sample app
[![Get it on the Play Store](/art/playstore_getiton.png)](https://play.google.com/store/apps/details?id=com.leinardi.android.speeddial.sample)

## Screenshots
### API 27, API 16, bottom and left expansion
<img src="/art/screenshot_api_27.png" width="215"/> <img src="/art/screenshot_api_16.png" width="215"/> <img src="/art/screenshot_api_27_top_fab_bottom_expansion.png" width="215"/> <img src="/art/screenshot_api_27_bottom_fab_left_expansion.png" width="215"/>

## FAQ
### How can I change the maximum length of the label?
You can set a different value for the max length of the label overriding `sd_label_max_width`:
```
<dimen name="sd_label_max_width">240dp</dimen>
```
### How can I create a new resource ID, required by the `SpeedDialActionItem.Builder`?
It can be done in XML using the `<item type="id" />`:
```
<resources>
    <item name="fab_action1" type="id" />
    <item name="fab_action2" type="id" />
    <item name="fab_action3" type="id" />
    <item name="fab_action4" type="id" />
</resources>
```
More info [here](https://developer.android.com/guide/topics/resources/more-resources.html#Id).
### How can I change the color of the overlay / touch guard layout?
The color of the `SpeedDialOverlayLayout` can be changed simply using the `android:background` attribute or, programmatically, using the equivalent setter like any other view.

## Changelog
See the [CHANGELOG.md](/CHANGELOG.md)

## Credits
This project is based on [floating-action-menu by ArthurGhazaryan](https://github.com/ArthurGhazaryan/floating-action-menu).

## Licenses
```
Copyright 2018 Roberto Leinardi.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
```
