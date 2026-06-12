package com.zyroplay.app.ui.modifier

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zyroplay.app.ui.theme.LocalZyroTheme

fun Modifier.tvFocusable(): Modifier = composed {
    val theme = LocalZyroTheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    this
        .focusable(interactionSource = interactionSource)
        .onFocusChanged { }
        .then(
            if (isFocused) {
                Modifier.border(2.dp, theme.secondary, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            } else {
                Modifier
            }
        )
}
