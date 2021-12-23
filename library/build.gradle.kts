import com.vanniktech.maven.publish.SonatypeHost

plugins {
  kotlin(Kotlin.JvmPluginId)
  id(MavenPublish.PluginId)
}

kotlin {
  explicitApi()
}

dependencies {
  implementation(Coroutines.Core)
}

plugins.withId("com.vanniktech.maven.publish") {
  mavenPublish {
    sonatypeHost = SonatypeHost.S01
    releaseSigningEnabled = true
  }
}
