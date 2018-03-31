# Yet Another Floating Action Button Menu

[![GitHub release](https://img.shields.io/github/release/leinardi/FloatingActionButtonSpeedDial.svg?style=plastic)](https://github.com/leinardi/FloatingActionButtonSpeedDial/releases)
[![Travis](https://img.shields.io/travis/leinardi/FloatingActionButtonSpeedDial/master.svg?style=plastic)](https://travis-ci.org/leinardi/FloatingActionButtonSpeedDial)
[![GitHub license](https://img.shields.io/github/license/leinardi/FloatingActionButtonSpeedDial.svg?style=plastic)](https://github.com/leinardi/FloatingActionButtonSpeedDial/blob/master/LICENSE) 

TBD

## Features
- [x] MinSdk 14
- [x] Highly customizable (label, icon, ripple, fab and label background colors) 
- [x] Same animations as in Inbox by Gmail
- [x] Option to have different icons for open/close state
- [x] Out-of-the box support for Snackbar behavior
- [x] Optional support for `RecyclerView` and `NestedScrollView`
- [x] Support for VectorDrawable
- [x] Easy to programmatically add, replace or remove items


## To Do
- [ ] Add label to main FAB (blocked by https://issuetracker.google.com/issues/77303906)
- [ ] Add FAB size option (blocked by https://issuetracker.google.com/issues/77303906)
- [ ] Clean up code 
- [ ] Add Javadoc
- [ ] Write tests
- [ ] Publish first release

## Disabling app `SnackbarBehavior`
```java
CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFabMenuView.getLayoutParams();
params.setBehavior(new FabMenuView.NoBehavior());
mFabMenuView.requestLayout();
```

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
