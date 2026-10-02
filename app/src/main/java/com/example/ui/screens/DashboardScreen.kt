package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GlassTintPalettes
import com.example.model.WidgetCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetItem
import com.example.model.WidgetSizeType
import com.example.physics.LiquidPhysicsState
import com.example.repository.WidgetCatalogRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.WidgetRenderer
import com.example.ui.editor.WidgetCustomizationSheet
import com.example.utils.HapticHelper
import com.example.widgets.AppWidgetPinHelper
import com.example.widgets.GlassCalendarWidgetProvider
import com.example.widgets.GlassClockWidgetProvider
import com.example.widgets.GlassLiquidBatteryWidgetProvider
import com.example.widgets.GlassQuickLauncherWidgetProvider
import com.example.widgets.GlassScreenTimeWidgetProvider
import com.example.widgets.GlassSystemStatsWidgetProvider
import com.example.widgets.GlassWidgetFlowTicker
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.GlassmorphicPresetId
import com.example.ui.theme.ThemeManager

enum class DashboardTab(val title: String) {
    ALL("All 138"),
    LIQUID("🧪 Liquid Physics"),
    POPULAR("🌟 Popular"),
    FAVORITES("❤️ Saved")
}

@Composable
fun DashboardScreen(
    physicsState: LiquidPhysicsState,
    onOpenLiquidLab: () -> Unit
) {
    val context = LocalContext.current
    val currentPreset by ThemeManager.currentPreset.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(DashboardTab.ALL) }
    var selectedCategory by remember { mutableStateOf<WidgetCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var activeEditingWidget by remember { mutableStateOf<WidgetItem?>(null) }
    val widgetCustomizations = remember { mutableStateMapOf<String, WidgetCustomization>() }
    val favoriteWidgetIds = remember { mutableStateMapOf<String, Boolean>() }

    var pinnedWidgetInfo by remember { mutableStateOf<Pair<WidgetItem, WidgetCustomization>?>(null) }

    val liveSystemData = com.example.utils.rememberLiveSystemData().value

    // Filter widgets
    val displayedWidgets = remember(selectedTab, selectedCategory, searchQuery, favoriteWidgetIds.size) {
        val baseList = when (selectedTab) {
            DashboardTab.ALL -> WidgetCatalogRepository.allWidgets
            DashboardTab.LIQUID -> WidgetCatalogRepository.getLiquidWidgets()
            DashboardTab.POPULAR -> WidgetCatalogRepository.getPopularWidgets()
            DashboardTab.FAVORITES -> WidgetCatalogRepository.allWidgets.filter { favoriteWidgetIds[it.id] == true }
        }

        val categoryFiltered = if (selectedCategory != null && selectedTab == DashboardTab.ALL) {
            baseList.filter { it.category == selectedCategory }
        } else {
            baseList
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            categoryFiltered.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.category.title.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        } else {
            categoryFiltered
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(currentPreset.backgroundGradient))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("dashboard_screen")
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. App Header & Title
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "GlassVibe App Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "GlassVibe",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "138 Premium & Liquid Widgets",
                                    color = currentPreset.accentGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Liquid Lab Action Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(currentPreset.liquidPrimaryColor.copy(alpha = 0.35f), currentPreset.accentGlow.copy(alpha = 0.25f))
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(listOf(currentPreset.accentGlow, currentPreset.specularRimColor)),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    HapticHelper.click(context)
                                    onOpenLiquidLab()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("open_lab_header_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🧪 Lab", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 2. Hero Interactive Liquid Physics Banner
            item(span = { GridItemSpan(2) }) {
                HeroLiquidShowcaseBanner(
                    physicsState = physicsState,
                    liveSystemData = liveSystemData,
                    onOpenLab = onOpenLiquidLab
                )
            }

            // 2.5 Glassmorphism Theme Presets Selector Bar
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GLASSMORPHISM PRESET",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentPreset.name,
                            color = currentPreset.accentGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeManager.presets.forEach { preset ->
                            val isSelected = currentPreset.id == preset.id
                            val iconEmoji = when (preset.id) {
                                GlassmorphicPresetId.FROSTED_MIDNIGHT -> "🌙"
                                GlassmorphicPresetId.OCEAN_DEPTH -> "🌊"
                                GlassmorphicPresetId.ELECTRIC_NEON -> "⚡"
                                GlassmorphicPresetId.CYBER_SUNSET -> "🌅"
                                GlassmorphicPresetId.EMERALD_AURORA -> "✨"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    preset.liquidPrimaryColor.copy(alpha = 0.50f),
                                                    preset.accentGlow.copy(alpha = 0.35f)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(0xFF1E293B).copy(alpha = 0.6f),
                                                    Color(0xFF0F172A).copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) preset.specularRimColor else Color(0xFF334155),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        HapticHelper.click(context)
                                        ThemeManager.setPreset(preset.id)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .testTag("theme_preset_${preset.id.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(iconEmoji, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = preset.name,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Search Bar
            item(span = { GridItemSpan(2) }) {
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("widget_search_field"),
                        placeholder = {
                            Text("Search 138+ widgets across 22 categories...", color = Color(0xFF64748B), fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.8f),
                            unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.6f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }

            // 4. Filter Tabs Row
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DashboardTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B).copy(alpha = 0.6f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedTab = tab
                                    selectedCategory = null
                                    HapticHelper.click(context)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("tab_${tab.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.title,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 5. 22 Categories Chip Row (shown when in ALL tab)
            if (selectedTab == DashboardTab.ALL) {
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // "All Categories" chip
                            val isAllSelected = selectedCategory == null
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isAllSelected) Color(0xFF334155) else Color(0xFF1E293B).copy(alpha = 0.4f))
                                    .border(1.dp, if (isAllSelected) Color(0xFF64748B) else Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedCategory = null
                                        HapticHelper.tick(context)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("All 22 Categories", color = if (isAllSelected) Color.White else Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            WidgetCategory.values().forEach { category ->
                                val isSelected = selectedCategory == category
                                val count = WidgetCatalogRepository.getWidgetsByCategory(category).size
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF0284C7).copy(alpha = 0.35f) else Color(0xFF1E293B).copy(alpha = 0.4f))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedCategory = category
                                        HapticHelper.tick(context)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("category_chip_${category.name.lowercase()}")
                                ) {
                                    Text(
                                        text = "${category.title} ($count)",
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Section Info & Results Count
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory != null) "${selectedCategory!!.title} Widgets" else "${displayedWidgets.size} Widgets Available",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("100% Free & Unlocked", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // 7. Widget Grid Items
            if (displayedWidgets.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Explore, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No widgets found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Try searching another keyword or clearing filters", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(
                    items = displayedWidgets,
                    key = { it.id },
                    span = { item ->
                        if (item.sizeType == WidgetSizeType.MEDIUM_4x2 ||
                            item.sizeType == WidgetSizeType.LARGE_4x4 ||
                            item.sizeType == WidgetSizeType.WIDE_4x1
                        ) {
                            GridItemSpan(2)
                        } else {
                            GridItemSpan(1)
                        }
                    }
                ) { widget ->
                    val custom = widgetCustomizations[widget.id] ?: widget.defaultCustomization
                    val isFav = favoriteWidgetIds[widget.id] == true

                    WidgetGridCard(
                        widget = widget,
                        customization = custom,
                        physicsState = physicsState,
                        liveSystemData = liveSystemData,
                        isFavorite = isFav,
                        onOpenEditor = {
                            activeEditingWidget = widget
                            HapticHelper.click(context)
                        },
                        onToggleFavorite = {
                            favoriteWidgetIds[widget.id] = !isFav
                            HapticHelper.tick(context)
                        },
                        onPinDirect = {
                            pinnedWidgetInfo = widget to custom
                            val isDirectRequested = AppWidgetPinHelper.pinWidgetToHomeScreen(context, widget, custom)
                            if (isDirectRequested) {
                                Toast.makeText(context, "Adding \"${widget.title}\" to Home Screen...", Toast.LENGTH_SHORT).show()
                            }
                            HapticHelper.splash(context)
                        }
                    )
                }
            }
        }

        // Customization Bottom Sheet Dialog
        if (activeEditingWidget != null) {
            val widget = activeEditingWidget!!
            val custom = widgetCustomizations[widget.id] ?: widget.defaultCustomization
            val isFav = favoriteWidgetIds[widget.id] == true

            WidgetCustomizationSheet(
                widget = widget,
                customization = custom,
                physicsState = physicsState,
                onCustomizationChange = { updated ->
                    widgetCustomizations[widget.id] = updated
                    GlassWidgetFlowTicker.updateAllWidgets(context)
                },
                onDismiss = { activeEditingWidget = null },
                isFavorite = isFav,
                onToggleFavorite = {
                    favoriteWidgetIds[widget.id] = !isFav
                },
                onPinWidget = { pinnedItem, pinnedCustom ->
                    pinnedWidgetInfo = pinnedItem to pinnedCustom
                    val isDirectRequested = AppWidgetPinHelper.pinWidgetToHomeScreen(context, pinnedItem, pinnedCustom)
                    if (isDirectRequested) {
                        Toast.makeText(context, "Adding \"${pinnedItem.title}\" to Home Screen...", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Pinned to Home Screen Confirmation & Guide Dialog
        if (pinnedWidgetInfo != null) {
            val (pinnedItem, pinnedCustom) = pinnedWidgetInfo!!
            AlertDialog(
                onDismissRequest = { pinnedWidgetInfo = null },
                containerColor = Color(0xFF0F172A),
                icon = {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(36.dp))
                },
                title = {
                    Text("Add to Home Screen", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Column {
                        Text(
                            "“${pinnedItem.title}” is ready for your Android Home Screen with live system data updates.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("QUICK SETUP TIPS:", color = Color(0xFF38BDF8), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• If your launcher displayed a prompt, tap 'Add' to place it directly.", color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Or long-press empty space on your home screen → Tap 'Widgets' → Pick 'GlassVibe'.", color = Color(0xFF94A3B8), fontSize = 12.sp, lineHeight = 16.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { pinnedWidgetInfo = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Got It", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// HERO INTERACTIVE LIQUID SHOWCASE BANNER
// -------------------------------------------------------------
@Composable
private fun HeroLiquidShowcaseBanner(
    physicsState: LiquidPhysicsState,
    liveSystemData: com.example.utils.RealSystemDataState,
    onOpenLab: () -> Unit
) {
    val context = LocalContext.current
    val heroCustomization = remember {
        WidgetCustomization(
            tintIndex = 4, // Cyber Cyan
            glassAlpha = 0.20f,
            borderOpacity = 0.70f,
            borderWidthDp = 1.8f,
            cornerRadiusDp = 22,
            manualLiquidLevel = 0.65f,
            useSensorPhysics = true
        )
    }

    val heroWidget = remember {
        WidgetItem(
            id = "hero_liquid_showcase",
            title = "Liquid Physics Showcase",
            category = WidgetCategory.LIQUID,
            description = "Sensor-driven glass fluid that sloshes when tilting your device",
            sizeType = WidgetSizeType.MEDIUM_4x2,
            isLiquid = true
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_liquid_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF0284C7).copy(alpha = 0.35f),
                            Color(0xFF1E1B4B).copy(alpha = 0.5f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(1.5.dp, Color(0xFF0284C7).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧪", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "HARDWARE SENSOR PHYSICS",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = if (physicsState.isUpsideDown) "INVERTED 180°" else "${physicsState.tiltAngleDeg.toInt()}° TILT",
                        color = if (physicsState.isUpsideDown) Color(0xFFFF5252) else Color(0xFF34D399),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Real Gyroscope Liquid Dynamics",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
                Text(
                    text = "Physically tilt or invert your phone to see the liquid slosh, splash, and flip with gravity.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Live Preview Box inside Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.35f)
                        .heightIn(min = 120.dp, max = 165.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            HapticHelper.splash(context)
                        }
                ) {
                    WidgetRenderer(
                        widget = heroWidget,
                        customization = heroCustomization,
                        physicsState = physicsState,
                        isInteractive = true,
                        realSystemData = liveSystemData
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Touch to splash • Tilt to slosh",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Button(
                        onClick = {
                            HapticHelper.click(context)
                            onOpenLab()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Open Full Lab", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// WIDGET GRID CARD
// -------------------------------------------------------------
@Composable
private fun WidgetGridCard(
    widget: WidgetItem,
    customization: WidgetCustomization,
    physicsState: LiquidPhysicsState,
    liveSystemData: com.example.utils.RealSystemDataState,
    isFavorite: Boolean,
    onOpenEditor: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPinDirect: () -> Unit
) {
    val cardBoxModifier = when (widget.sizeType) {
        WidgetSizeType.SMALL_2x2 -> Modifier.fillMaxWidth().aspectRatio(1.02f).heightIn(min = 145.dp, max = 195.dp)
        WidgetSizeType.MEDIUM_4x2 -> Modifier.fillMaxWidth().aspectRatio(2.15f).heightIn(min = 135.dp, max = 220.dp)
        WidgetSizeType.LARGE_4x4 -> Modifier.fillMaxWidth().aspectRatio(1.12f).heightIn(min = 270.dp, max = 390.dp)
        WidgetSizeType.WIDE_4x1 -> Modifier.fillMaxWidth().aspectRatio(3.5f).heightIn(min = 85.dp, max = 130.dp)
        WidgetSizeType.PILL_2x1 -> Modifier.fillMaxWidth().aspectRatio(1.95f).heightIn(min = 80.dp, max = 115.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Live Widget Render Box with responsive aspect ratio
            Box(
                modifier = cardBoxModifier
                    .clip(RoundedCornerShape(customization.cornerRadiusDp.dp))
                    .clickable(onClick = onOpenEditor)
            ) {
                WidgetRenderer(
                    widget = widget,
                    customization = customization,
                    physicsState = physicsState,
                    isInteractive = false,
                    realSystemData = liveSystemData
                )

                // Quick Action Bar on Top Right (Pin, Bookmark, Edit)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Quick Pin to Home Screen
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0284C7).copy(alpha = 0.9f))
                            .clickable(onClick = onPinDirect),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddHome,
                            contentDescription = "Add to Home Screen",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Bookmark Favorite
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(onClick = onToggleFavorite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isFavorite) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Customize
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(onClick = onOpenEditor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Edit",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Metadata below card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = widget.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = widget.sizeType.label.substringBefore(" "),
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
