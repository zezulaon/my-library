# Android Instrumented Tests (`app/src/androidTest`)

## Overview

* Instrumentation runner: `dev.zezula.books.InstrumentationTestRunner`
* UI tests use Jetpack Compose (`createAndroidComposeRule`)
* DI is provided via Koin and `KoinTestRule`
* On test failure:

    * Logs the full Compose semantics tree
    * Captures a screenshot via AndroidX Test Storage Service

Runner config (in `app/build.gradle.kts`):

---

## Dependency Injection (Koin)

Rule: `dev.zezula.books.core.rules.KoinTestRule`

```kotlin
@get:Rule(order = 0)
val koinTestRule = KoinTestRule(
    modules = listOf(appModule, flavoredAppModule, appInstrumentedTestModule),
)
```

* Starts Koin with the instrumentation `applicationContext`
* Loads provided modules before each test and unloads them afterward

---

## Failure Rules

Both rules live in `dev.zezula.books.core.rules` and reuse the same `ComposeTestRule`:

```kotlin
@get:Rule(order = 1)
val composeTestRule = createAndroidComposeRule<MyLibraryMainActivity>()

@get:Rule(order = 2)
val printSemanticsOnFailureRule = PrintSemanticsOnFailureRule(composeTestRule)

@get:Rule(order = 3)
val screenshotOnFailureRule = ScreenshotOnFailureRule(composeTestRule)
```

### `PrintSemanticsOnFailureRule`

* On failure, logs the **unmerged** Compose semantics tree.
* Logcat tag: `**** Failed Test Semantics`
  → Check Logcat for this tag when debugging broken tests.

### `ScreenshotOnFailureRule`

* On failure, captures `composeTestRule.onRoot()` as a bitmap and calls
  `writeToTestStorage("<class>_<method>")`.
* When using Gradle Managed Devices, screenshots are collected under:

```text
app/build/outputs/managed_device_android_test_additional_output/
```

(organized per test task and device; ideal for CI artifacts).

---

## Gradle Managed Devices (GMD)

Configured in `app/build.gradle.kts`:

Flavors: `standard`, `bookdiary`, `gb` (with `debug` build type).

### Example tasks

Run tests on the managed device:

```bash
# standardDebug variant
./gradlew :app:mediumPhoneApi35StandardDebugAndroidTest

# bookdiaryDebug variant
./gradlew :app:mediumPhoneApi35BookdiaryDebugAndroidTest

# gbDebug variant
./gradlew :app:mediumPhoneApi35GbDebugAndroidTest
```
