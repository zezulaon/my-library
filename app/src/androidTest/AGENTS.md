### Android Instrumented Tests

- UI tests should only use `AndroidComposeTestRule` along with `ComponentActivity`
- DI is provided via Koin and `KoinTestRule` and setup in `BaseInstrumentedTest`
- Robot classes are in files suffixed with `Robot.kt` and uses `AndroidComposeTestRule` to perform actions and verifications. For example see `BooksManagementRobot.kt`
- Tests are in files suffixed with `InstrumetedTest.kt` and uses Robot classes to perform actions and verifications. For example see `BooksManagementInstrumentedTest.kt`
