package com.creativedrewy.nativ.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.*

@Composable
fun BackHandler(
    backDispatcher: OnBackPressedDispatcher,
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    // Safely update the current onBack lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }

    SideEffect {
        backCallback.isEnabled = enabled
    }

    // If backDispatcher changes, dispose and reset the effect
    DisposableEffect(backDispatcher) {
        backDispatcher.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}