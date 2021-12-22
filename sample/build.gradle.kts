import org.ajoberstar.grgit.Grgit

plugins {
  id(Android.ApplicationPluginId)
  kotlin(Kotlin.AndroidPluginId)
  id("common-android-plugin")
}

val commitsCount = Grgit.open(mapOf("dir" to rootDir)).log().size

android {
  defaultConfig {
    versionCode = commitsCount
    versionName = "0.0.1"
  }
}

dependencies {
  implementation(Kotlin.StdLib)
  implementation(project(autoModules.library))

  implementation(Material.Core)

  implementation(AndroidX.AppCompat)
  implementation(AndroidX.ConstraintLayout)
  implementation(AndroidX.ComposeActivity)

  implementation(Compose.Ui)
  implementation(Compose.Foundation)
  implementation(Compose.FoundationLayout)
  implementation(Compose.Material)

  implementation(Coroutines.Core)

  implementation(AndroidX.Activity)

  debugImplementation(Debug.LeakCanary)
  debugImplementation(Debug.FoQA)
}
