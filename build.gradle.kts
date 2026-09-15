// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}
tasks.register("testGame") {
  group = "verification"
  description = "Runs fast game rule, flow, and stress unit tests."
  dependsOn(":app:testDebugUnitTest")
}

tasks.register("testAll") {
  group = "verification"
  description = "Builds the debug APK and runs the important automated tests."
  dependsOn(":app:assembleDebug", ":app:testDebugUnitTest")
}

