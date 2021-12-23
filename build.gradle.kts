import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt

plugins {
  id(DetektLib.PluginId) version DetektLib.Version
  id(GradleVersions.PluginId) version GradleVersions.Version
  id(GrGit.PluginId) version GrGit.Version
  id(Shipkit.AutoVersion.PluginId) version Shipkit.AutoVersion.Version
  id(Shipkit.Changelog.PluginId) version Shipkit.Changelog.Version
  id(Shipkit.GithubRelease.PluginId) version Shipkit.GithubRelease.Version
}

buildscript {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  dependencies {
    classpath(Android.GradlePlugin)
    classpath(Kotlin.GradlePlugin)
    classpath(Kotlin.DokkaGradlePlugin)
    classpath(DetektLib.Plugin)
    classpath(GradleVersions.Plugin)
    classpath(MavenPublish.GradlePlugin)
  }
}

allprojects {
  repositories {
    mavenCentral()
    google()
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
  }
}

dependencies {
  detekt(DetektLib.Formatting)
  detekt(DetektLib.Cli)
}

tasks.withType<Detekt> {
  parallel = true
  config.setFrom(rootProject.file("detekt-config.yml"))
  setSource(files(projectDir))
  exclude(subprojects.map { "${it.buildDir.relativeTo(rootDir).path}/" })
  exclude("**/.gradle/**")
  reports {
    xml {
      enabled = true
      destination = file("build/reports/detekt/detekt-results.xml")
    }
    html.enabled = false
    txt.enabled = false
  }
}

tasks.register("check") {
  group = "Verification"
  description = "Allows to attach Detekt to the root project."
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate.version) && !isNonStable(currentVersion)
  }
}

fun isNonStable(version: String): Boolean {
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  return !regex.matches(version)
}
