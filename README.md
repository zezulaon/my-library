
My Library
==================
**work in progress** 🚧

**My Library** is an Android app for managing books and shelves built entirely with [Kotlin](https://kotlinlang.org/) and [Jetpack Compose](https://developer.android.com/jetpack/compose).

Some parts of the app were inspired by the [Now in Android](https://developer.android.com/series/now-in-android)
Android showcase app.

# Download
<a href="https://play.google.com/store/apps/details?id=dev.zezula.books"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="70"></a>

# Features

Detail     | Shelves   | Book List | Reviews
:--------:| :-----: |:-----:|:----:
![screen_detail](https://user-images.githubusercontent.com/1711411/212752299-0c021750-a5d1-46ae-b7d6-8504bd0f8d0b.png)  |  ![screen_drawer](https://user-images.githubusercontent.com/1711411/212752466-3d7a1540-d854-49f1-b2fe-30e6147ef781.png) | ![screen_home](https://user-images.githubusercontent.com/1711411/212752481-7bd4edf5-04a3-49b7-918e-f76fe67223d4.png) | ![screen_review](https://user-images.githubusercontent.com/1711411/212753098-494854e4-a426-4c9e-a0ef-9c26665038e7.png)

# Components/libraries/services used

* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [Jetpack Navigation](https://developer.android.com/jetpack/compose/navigation)
* [Koin for DI](https://insert-koin.io/)
* [ML Kit](https://developers.google.com/ml-kit/vision/barcode-scanning) (Barcode scanning)
* [Camera X](https://developer.android.com/training/camerax)
* [Firebase](https://firebase.google.com/) (Firestore, Auth, Crashlytics)
* [GoodReads Review API](https://www.goodreads.com/api) (Books data and reviews)

#### UI
The screens and UI components were designed using [Material 3 guidelines](https://m3.material.io/) and build in [Jetpack Compose](https://developer.android.com/jetpack/compose) as a _single Activity_ app. Screen UI state is managed and provided by Android _ViewModel_ (which also handles events coming from UI or Data layer).

#### Android Room
Android **Room DB** is used as a _single source of truth_ for the user data. All data coming from the network are first stored in the DB and then exposed to ViewModel via _Flow_ or as a result of a _suspend functions_.

#### Fireabase
**Firebase Firestore** is used to back up and sync a user's data. Currently the Firestore is used only for simple Add/Remove queries. (Complex search/join quires are handled by Room DB).
**Firebase Auth** is also used to sign user in the app. (Either via Google or as an Anonymous user).

#### Architecture
*My Library* tries to follow latest Android architecture recommendation and separates code into several layers: **UI Layer** (Compose and ViewModels), **Domain Layer**, **Data Layer** (Repositores and Data Sources). (Though use of **Domain Layer** is perhaps redundant - because of the small portion of business logic, the _Use Cases_ are mostly just delegating call to _Repositories_).

# SetUp
To compile and run the app, you will have to provide:
* [google-service.json file](https://firebase.google.com/docs/android/setup) (With Firestore/Auth/Crashlytics enabled)
* [Firebase Web Client ID](https://firebase.google.com/docs/auth/android/google-signin) (For user Sign In)
* [GoodReads API Key](https://www.goodreads.com/api) (For fetching books and reviews)

These keys go to the app's <code>gradle.properties</code>:
~~~~
myLibrary.goodreadsApiKey=GOODREADS_API_KEY
myLibrary.firebaseClientId=FIREBASE_CLIENT_ID
~~~~

# License
**My Library** is distributed under the terms of the Apache License (Version 2.0).

~~~~
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
~~~~





