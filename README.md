* Google Play Link: https://play.google.com/store/apps/details?id=com.samadtch.inspired
* App Video Presentation: https://www.youtube.com/watch?v=kjNMkMWH_wc

> [!TIP]
> The app is available on Google Play, you can test it right now!

> [!CAUTION]
> If the Authentication process doesn't redirect you back to the app directly (Android 13+), make
> sure to add the link as verified in the App's settings. Automatic verification is being worked at
> now.

> [!CAUTION]
> App is still in preview and Integration with Canva isn't valid yet untill app is fully tested which will be done as soon as possible.

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

# Security

The app uses OAuth to authenticate with **Canva** through **Connect API**. The process is secure and
generated tokens are saved in a secure Shared Preferences.

# Issues

Due to the time limit, some functionalities were not done in the best possible way and/or not
properly tested, here's a list of functionalities that could've been done better...

- When the UI State changes because of an action (deleting an asset (inspiration) or a folder,
  Creating a folder or an asset), the app refreshes the whole page instead of just updating the
  state of the affected part. This was the quickest way possible to update state after an action, a
  more sophisticated way will be added as soon as possible.
- For Authentication, due to the requirement for a valid redirect url. Starting of Android 13, the
  domain name has to be verified to be able to apply Deep Linking to re-open the app and send the
  Authorization Key. Right now, the verification has to be done manually through adding the link as
  trusted within the App's Settings. The automatic verification process is being worked on right now
  and I will update the README.md as soon as it is confirmed.
- More tests, cleanings and validation has to be done before the app is ready for the app is ready
  for Initial Production Release. If you find any issues, please create a new issue in the
  repository and it will be checked on as soon as possible.
