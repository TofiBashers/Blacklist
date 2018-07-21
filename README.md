 # Blacklist

[![Build Status](https://travis-ci.org/TofiBashers/Blacklist.svg?branch=master)](https://travis-ci.org/TofiBashers/Blacklist)

This is new version of old application for api 4.0-4.3.

Code features:
  * [Kotlin](https://kotlinlang.org/)  
  * [Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html) + [RxJava 2](https://github.com/ReactiveX/RxJava) based architecture:  
     * [Room](https://developer.android.com/topic/libraries/architecture/room.html) as ORM   
     * RxJava2 used for data providing and buisness-logic (data + domain layers)  
     * UI-layer uses [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html) + [LiveData](https://developer.android.com/topic/libraries/architecture/livedata.html) for saving and modifying view state, reactive subscribers in ViewModel used for buisness-logic  
     * [Dagger 2](https://google.github.io/dagger/) for DI  
  * Also:  
     * [Butterknife](http://jakewharton.github.io/butterknife/)  
     * [Calligraphy](https://github.com/chrisjenx/Calligraphy)  
     * [joda-time-android](https://github.com/dlew/joda-time-android)  
     * [libphonenumber](https://github.com/googlei18n/libphonenumber)
     * Unit tests with [Mockito 2](http://site.mockito.org/) & [Robolectric](http://robolectric.org/)
     * Android test of Room Migration
     * Android codestyle with [Ribot styleguide](https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md) (except m-notation)
