# Android Instrumented Tests (`app/src/androidTest`)

## Overview

* UI tests use Jetpack Compose (`createAndroidComposeRule`) with Robot pattern
* DI is provided via Koin and `KoinTestRule`
* When a test fails:
    * Compose semantics tree is logged - `PrintSemanticsOnFailureRule`
    * A screenshot is captured - `ScreenshotOnFailureRule`

---

### Example tasks

Run tests on the managed device:

```bash
./gradlew :app:mediumPhoneApi35StandardDebugAndroidTest
```

### Robot pattern
Robot pattern is used to make writing and maintaining UI tests easier. UI tests focused on WHAT the user does on the screen, Robot classes
encapsulate HOW those actions are performed.
