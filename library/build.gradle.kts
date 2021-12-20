plugins {
  kotlin(Kotlin.JvmPluginId)
}

kotlin {
  explicitApi()
}

dependencies {
  implementation(Coroutines.Core)
}
