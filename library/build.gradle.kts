import com.vanniktech.maven.publish.SonatypeHost

plugins {
  kotlin(Kotlin.JvmPluginId)
  id(MavenPublish.PluginId)
}

kotlin {
  explicitApi()

  target {
    compilations.all {
      kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.time.ExperimentalTime"
      }
    }
  }
}

dependencies {
  implementation(Coroutines.Core)

  testImplementation(Kotest.RunnerJunit5)
  testImplementation(Kotest.Assertions)
  testImplementation(CoroutineTest.Turbine)
}

plugins.withId("com.vanniktech.maven.publish") {
  mavenPublish {
    sonatypeHost = SonatypeHost.S01
    releaseSigningEnabled = true
  }
}
