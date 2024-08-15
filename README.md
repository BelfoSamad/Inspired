* Google Play Link: https://play.google.com/store/apps/details?id=com.samadtch.inspired

> [!CAUTION]
> App is still in preview and Integration with Canva isn't valid yet until app is fully tested which will be done as soon as possible.

# Architecture

Inspired is built with Compose Multiplatform, a Hybrid App development framework to be able to build
with one source code the Android, IOS and Desktop versions. Currently, only the Android version is
available and all the remaining platforms will be supported soon.

The project is split into:

- Common: on `/commonMain` containing the UI and the logic except platform specific features like:
  Splash Screen, Palette API, Crashlytics...etc. Whenever there is a common implementation of these
  platform specific features, it will be implemented on common.
- Platform Specific (Android, IOS)

`/commonMain` communicates with Platform Specific code either via `expect/actual`, Dependency
Injection or through the Composable `App()` which is the entry of the app.

The app follows the Android's recommended Architecture (MVVM) and the packages and well defined

- Data Layer: DataSources (local, remote), Repositories.
- UI Layer: ViewModels, Screens and Navigation.

# To Do

- [ ] Push Folder update (Create/Delete/Update) into AssetDialog
- [ ] Move Assets
- [ ] Move Folders (Drag/Drop)

