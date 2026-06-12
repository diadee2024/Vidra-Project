package com.zyroplay.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyroplay.app.ui.components.ZyroGradientBackground
import com.zyroplay.app.ui.components.ZyroLogo
import com.zyroplay.app.ui.theme.ZyroCyan
import com.zyroplay.app.ui.theme.ZyroTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var startAnimation by remember { mutableFloatStateOf(0f) }
    val alpha by animateFloatAsState(
        targetValue = startAnimation,
        animationSpec = tween(1200),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = 1f
        delay(2500)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ZyroGradientBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ZyroLogo(size = 100)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Zyro",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp
            )
            Text(
                text = "play",
                style = MaterialTheme.typography.displayLarge,
                color = ZyroCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lecteur IPTV Premium",
                style = MaterialTheme.typography.bodyLarge,
                color = ZyroTextSecondary
            )
        }
    }
}
