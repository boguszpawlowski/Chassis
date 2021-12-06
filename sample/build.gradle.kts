import org.ajoberstar.grgit.Grgit

plugins {
  id(Android.ApplicationPluginId)
  kotlin(Kotlin.AndroidPluginId)
  id("common-android-plugin")
  id(SqlDelight.PluginId)
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
  implementation(SqlDelight.AndroidDriver)

  implementation(KotlinXSerialization.Core)
  implementation(KotlinXSerialization.Json)

  implementation(Koin.Core)

  implementation(AndroidX.Activity)
  implementation(AndroidX.Startup)
  implementation(AndroidX.Lifecycle)

  implementation(platform(Firebase.Bom))

  debugImplementation(Debug.LeakCanary)
  debugImplementation(Debug.FoQA)
}
