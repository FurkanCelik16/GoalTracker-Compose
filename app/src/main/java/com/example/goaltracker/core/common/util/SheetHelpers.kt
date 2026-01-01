package com.example.goaltracker.core.common.util

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.Velocity


fun Modifier.disableVerticalSwipe(): Modifier = this.composed {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                return available
            }
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return available
            }
        }
    }
    this.nestedScroll(nestedScrollConnection)
}

@Composable
fun SmartSheetBackHandler(
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    BackHandler(enabled = isKeyboardOpen) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }
}