# Floating Action Button Speed Dial

[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml.svg?style=plastic)](https://jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml)
[![GitHub release](https://img.shields.io/github/release/leinardi/FloatingActionButtonSpeedDial/all.svg?style=plastic)](https://github.com/leinardi/FloatingActionButtonSpeedDial/releases)
[![Travis](https://img.shields.io/travis/leinardi/FloatingActionButtonSpeedDial/master.svg?style=plastic)](https://travis-ci.org/leinardi/FloatingActionButtonSpeedDial)
[![GitHub license](https://img.shields.io/github/license/leinardi/FloatingActionButtonSpeedDial.svg?style=plastic)](https://github.com/leinardi/FloatingActionButtonSpeedDial/blob/master/LICENSE) 
[![Stars](https://img.shields.io/github/stars/leinardi/FloatingActionButtonSpeedDial.svg?style=social&label=Stars)](https://github.com/leinardi/FloatingActionButtonSpeedDial/stargazers) 

Android library providing an implementation of the Material Design Floating Action Button Speed Dial.

![Demo](/art/demo_1.gif)

## Features
- [x] MinSdk 15
- [x] Highly customizable (label, icon, ripple, fab and label background colors, themes support) 
- [x] Same animations as in Inbox by Gmail
- [x] Option to have different icons for open/close state
- [x] Optional overlay/touch guard layout
- [x] Support for bottom, left and right menu expansion (left and right have no labels)
- [x] Out-of-the box support for Snackbar behavior
- [x] Optional support for `RecyclerView` and `NestedScrollView` behavior
- [x] Support for VectorDrawable
- [x] Easy to use

## To Do
- [ ] Add label to main FAB (blocked by https://issuetracker.google.com/issues/77303906)
- [ ] Add FAB size option (blocked by https://issuetracker.google.com/issues/77303906)
- [ ] Clean up code 
- [ ] Add Javadoc
- [ ] Write tests
- [ ] Publish first release

## How to use
### Setup
The request to publish this library to Jcenter is currently pending, meanwhile it is possible to use this repository:
```
repositories {
    google()
    jcenter()
    maven { url 'https://dl.bintray.com/leinardi/android' }
}
```

Dependencies entry (latest version: [![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml.svg?style=flat)](https://jcenter.bintray.com/com/leinardi/android/speed-dial/maven-metadata.xml)):
```
implementation "com.leinardi.android:speed-dial:1.0-alpha02"
```
### Use
Add the `SpeedDialView` to your layout:

```xml
<com.leinardi.android.speeddial.SpeedDialView
    android:id="@+id/speedDial"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    app:srcCompat="@drawable/ic_add_white_24dp" />
```

Add the items to the `SpeedDialView`:

```java
SpeedDialView speedDialView = findViewById(R.id.speedDial);
speedDialView.addFabOptionItem(
        new SpeedDialActionItem.Builder(R.id.fab_link, R.drawable.ic_link_white_24dp)
                .create()
);
```
Add the click listeners:
```java
speedDialView.setMainFabOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (speedDialView.isFabMenuOpen()) {
            speedDialView.closeOptionsMenu();
        }
    }
});

speedDialView.setOptionFabSelectedListener(new SpeedDialView.OnOptionFabSelectedListener() {
    @Override
    public void onOptionFabSelected(SpeedDialActionItem speedDialActionItem) {
        switch (speedDialActionItem.getId()) {
            case R.id.fab_link:
                showToast("Link action clicked!");
                break;
            default:
                break;
        }
    }
});
```

A fully working example is available [here](/sample).

## Demo
### Video
https://www.youtube.com/watch?v=tWowiF5ElAg
### App
[![Get it on the Play Store](/art/playstore_getiton.png)](https://play.google.com/store/apps/details?id=com.leinardi.android.speeddial.sample)

## Screenshots
### API 27 and 16
<img src="/art/screenshot_api_27.png" width="360"/> <img src="/art/screenshot_api_16.png" width="360"/>

### Bottom and left expansion
<img src="/art/screenshot_api_27_top_fab_bottom_expansion.png" width="360"/> <img src="/art/screenshot_api_27_bottom_fab_left_expansion.png" width="360"/>


## Disabling app `SnackbarBehavior`
```java
CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) speedDialView.getLayoutParams();
params.setBehavior(new SpeedDialView.NoBehavior());
speedDialView.requestLayout();
```

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
