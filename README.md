# Picsum Photo App

## Functionality
The app's functionality includes:
1. Fetch a list of images from picsum photos api (https://picsum.photos/) and show them in `RecyclerView` using `StaggeredGrid Layout Manager`.
2. User can view the list of images using smooth infinite scroll using paging api.
3. When an image is selected from `RecyclerView` it will load the full-screen image with pinch zoom in/out feature.
4. User can download the image from full-screen view.
5. User can share the image from full-screen view.
6. The image list is cached into local DB, so the list of images are available offline.
7. The app supports `SwipeRefreshLayout` to refresh `RecyclerView` content from remote source.

## Architecture
The app uses clean architecture with `MVVM(Model View View Model)` design pattern. 
MVVM provides better separation of concern, easier testing, Live data & lifecycle awareness, etc.

### UI
The UI consists of two screen
1. `MainActivity.kt` - Initial screen. Shows a list of images.
2. `FullScreenActivity.kt` - Shows full-screen view of the image with additional options.

### Model
Model is generated from `JSON` data into a Kotlin data class.
In addition, entity class has been added for room database.

### ViewModel

`MainViewModel.kt`

Used for fetching picsum images & update flow using paging data soruce.

`FullScreenViewModel.kt`

Used for downloading the image into a bitmap & save it into internal storage.


### Dependency Injection
The app uses `Dagger-hilt` as a dependency injection library.

The `ApplicationModule.kt` class provides  `Singleton` reference for `Retrofit`, `OkHttpClient`, `Repository` etc.

### Network
The network layer is composed of Repository, ApiService.
`PicsumApi` - Is an interface containing the suspend functions for retrofit API call.

`ImageListRepository` - Holds the definition of the remote/local repository call.

## Building

You can open the project in Android studio and press run.
Android Studio version used to build the project: Arctic fox 2020.3.1

Gradle plugin used in the project will require `Java 11.0` to run.

you can set the gradle jdk in `Preferences->Build Tools->Gradle->Gradle JDK`

## Tech Stack
1. [Android appcompat](https://developer.android.com/jetpack/androidx/releases/appcompat), [KTX](https://developer.android.com/kotlin/ktx), [Constraint layout](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout), [Material Support](https://material.io/develop/android/docs/getting-started).
2.  [Android View Binding](https://developer.android.com/topic/libraries/view-binding)
3. [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection.
4. [Retrofit](https://square.github.io/retrofit/) for REST API communication.
5. [Coroutine](https://developer.android.com/kotlin/coroutines) for Network call.
6. [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle), [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
7.  Kotlin [Flow](https://developer.android.com/kotlin/flow)
8. [Room](https://developer.android.com/jetpack/androidx/releases/room) for local database.
9. [Glide](https://github.com/bumptech/glide) for image loading.
10. [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) with [RemoteMediator](https://developer.android.com/topic/libraries/architecture/paging/v3-network-db) for smooth infinite scroll.
11. [Custom fileprovider](https://developer.android.com/training/secure-file-sharing/setup-sharing) for writing & reading files into internal storage.
12. [Swipe Refresh Layout](https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout) for pull-to-refresh `RecyclerView`.
13. [Mockito](https://developer.android.com/training/testing/local-tests) & [Junit](https://developer.android.com/training/testing/local-tests) for Unit testing.
14. [Robolectric](http://robolectric.org/) for Instrumentation testing.
15. [Truth](https://truth.dev/) for Assertion in testing.
16. [Photo View](https://github.com/Baseflow/PhotoView) for zoom in/out image.

## Testing

Unit and integration testing has been added for `MainViewModel` , `FullScreenViewModel`, `ImageListRepository` & `ImageListItemSerializableTest`.

### `MainViewModelTest.kt`

Test the viewmodel of the app using `CoroutineRule`.

The test cases comprise of testing different states like Loading, Success, Error with fake data for testing image list response, cache response.

### `FullScreenViewModelTest.kt`

Test Image donwload returns file uri & URL to bitmap conversion.

### `ListingRepository.kt`

Test the Repository of the app using `Robolectric`.

The test comprises of testing the functionality of Image Room Database like Insertion, Remove, Get saved response etc.

[Mock Webserver](https://github.com/square/okhttp/tree/master/mockwebserver) is used to test the Network api response in case of successful data, empty, failed case.


## Screenshots

Infinite Scroll, Pinch Zoom             |  Image Download                   |   Image share
:------------------------:|:------------------------:|:------------------------
![](images/image_scroll.gif)    |  ![](images/image_download.gif)  |   ![](images/image_share.gif)

