package com.zyroplay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.BuildConfig
import com.zyroplay.app.model.NavDestination
import com.zyroplay.app.ui.navigation.NavItem
import com.zyroplay.app.ui.navigation.mainNavItems
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroSurface
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.modifier.tvFocusable
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun ZyroSidebar(
    currentRoute: String,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalZyroTheme.current
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(ZyroSurface)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZyroLogo(size = 56)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Zyro",
            style = MaterialTheme.typography.titleLarge,
            color = ZyroTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "play",
            style = MaterialTheme.typography.titleMedium,
            color = theme.secondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        mainNavItems.forEach { item ->
            SidebarItem(
                item = item,
                theme = theme,
                selected = currentRoute == item.destination.route,
                onClick = { onNavigate(item.destination) }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelMedium,
            color = ZyroTextMuted
        )
    }
}

@Composable
private fun SidebarItem(
    item: NavItem,
    theme: com.zyroplay.app.ui.theme.ZyroThemeColors,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .width(188.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (selected) {
                    Modifier.background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(theme.primary.copy(alpha = 0.4f), theme.secondary.copy(alpha = 0.15f))
                        )
                    )
                } else {
                    Modifier
                }
            )
            .tvFocusable()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.destination.label,
            tint = if (selected) Color.White else ZyroTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.destination.label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) ZyroTextPrimary else ZyroTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (selected) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(theme.secondary)
            )
        }
    }
}
