# AndroidTemplate

Template for Android projects, it includes:

## Dependencies provided via `buildSrc`
 Common Android dependencies are already defined in the `buildSrc` directory. \
 For convenience of version bumping, the [Gradle Versions](https://github.com/ben-manes/gradle-versions-plugin) plugin is added. \
 To check for any version updates, run: 
 ```
    gradle dependencyUpdates
 ``` 
 
### Some of the defined dependencies:
  - Multiple AndroidX libraries
  - Compose + Accompanist
  - SqlDelight
  - Koin
  - Detekt
  - Firebase tools
  - Timber
  - Kotest
  
## Multi-module support
 Automatic module configuration provided by [auto-module](https://github.com/pablisco/auto-module) plugin. \
 The `CommonAndroidPlugin` defined in the project takes care of tedious task of setting new android module.
 Example `build.gradle.kts` file, making use of this plugin:
 ```kotlin  
  plugins {
    id(Android.LibraryPluginId)
    kotlin(Kotlin.AndroidPluginId)
    id("common-android-plugin")
  }
  
  dependencies {
    implementation(Kotlin.StdLib)
    implementation(Material.Core)  
    implementation(AndroidX.AppCompat)
    debugImplementation(Debug.FoQA)
  }
```
## Debug / QA tools
[FoQA](https://github.com/DroidsOnRoids/FoQA) as a QA tools container. It includes:
- [Chucker](https://github.com/ChuckerTeam/chucker)
- Runtime font scaling
- Database and SharedPrefs debugging tools

And more, all available in the Hyperion menu. 

## Basic CI configuration
Project has basic Github Actions CI setup created. It will run unit tests and lint on any raised PR.

## Out of the box Compose support
Template is ready for starting the development using Jetpack Compose. \
In case this is not needed, remove the lines marked by `// FIXME remove if not using compose` comments.

## Working on a stable version of Android Studio
As this template is aimed at development using latest tools and Jetpack Compose, it is not compatible
with stable version of AS. If you want to work on it, please remove Compose support as described in the
previous paragraph and revert Android Gradle Plugin to latest stable version (in this moments it is 4.1.1):
 - Change AGP version in `Dependencies.kt`.
 - Change AGP version in `build.gradle.kts` of `buildSrc` directory.
 - Run `gradle wrapper` task.
 - Sync and rebuild the project. 

## License

    Copyright 2021 Bogusz Pawłowski

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
