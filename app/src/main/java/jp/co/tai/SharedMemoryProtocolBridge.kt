package jp.co.tai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun SharedMemoryProtocolBridge(
    onScreenChanged: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        UiSharedMemory.init()
    }

    LaunchedEffect("ui_protocol_loop") {
        var last = -1

        while (true) {
            val value = UiSharedMemory.getScreen()

            if (value != last) {
                last = value
                onScreenChanged(value)
            }

            delay(500)
        }
    }
}