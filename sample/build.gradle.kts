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

  implementation(Material.Core)

  implementation(AndroidX.AppCompat)
  implementation(AndroidX.ConstraintLayout)
  implementation(AndroidX.ComposeActivity)

  implementation(Compose.Runtime) // FIXME remove if not using compose
  implementation(Compose.Ui) // FIXME remove if not using compose
  implementation(Compose.Foundation) // FIXME remove if not using compose
  implementation(Compose.FoundationLayout) // FIXME remove if not using compose
  implementation(Compose.Material) // FIXME remove if not using compose

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
