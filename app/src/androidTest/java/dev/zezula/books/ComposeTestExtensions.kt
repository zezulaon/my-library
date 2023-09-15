package dev.zezula.books

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeContentTestRule

// Copyright 2022 Google LLC.
// SPDX-License-Identifier: Apache-2.0
//
// Helpers functions for waiting in tests. Taken from:
// https://medium.com/androiddevelopers/alternatives-to-idling-resources-in-compose-tests-8ae71f9fc473

@OptIn(ExperimentalTestApi::class)
fun ComposeContentTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 1_000L,
) {
    return this.waitUntilNodeCount(matcher, 1, timeoutMillis)
}
