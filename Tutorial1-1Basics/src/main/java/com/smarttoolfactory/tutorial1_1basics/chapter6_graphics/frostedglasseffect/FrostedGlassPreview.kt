@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview(
    name = "Frosted Glass Blur",
    showBackground = true,
    backgroundColor = 0xFF0B0B0B,
    widthDp = 411,
    heightDp = 891,
    apiLevel = 31
)
@Composable
private fun FrostedGlassBlurPreview() {
    FrostedGlassPreviewContent(
        initialMode = FrostedGlassImplementationMode.PlatformBlur
    )
}

@Preview(
    name = "Frosted Glass Bitmap Fallback",
    showBackground = true,
    backgroundColor = 0xFF0B0B0B,
    widthDp = 411,
    heightDp = 891,
    apiLevel = 30
)
@Composable
private fun FrostedGlassBitmapFallbackPreview() {
    FrostedGlassPreviewContent(
        initialMode = FrostedGlassImplementationMode.BitmapBlur
    )
}

@Preview(
    name = "Frosted Glass Styled Fallback",
    showBackground = true,
    backgroundColor = 0xFF0B0B0B,
    widthDp = 411,
    heightDp = 891,
    apiLevel = 30
)
@Composable
private fun FrostedGlassStyledFallbackPreview() {
    FrostedGlassPreviewContent(
        initialMode = FrostedGlassImplementationMode.BitmapBlur,
        initialGlassStyle = GlassSurfaceStyle.Convex
    )
}

@Preview(
    name = "Frosted Glass Concave Fallback",
    showBackground = true,
    backgroundColor = 0xFF0B0B0B,
    widthDp = 411,
    heightDp = 891,
    apiLevel = 30
)
@Composable
private fun FrostedGlassConcaveFallbackPreview() {
    FrostedGlassPreviewContent(
        initialMode = FrostedGlassImplementationMode.BitmapBlur,
        initialGlassStyle = GlassSurfaceStyle.Concave
    )
}

@Composable
private fun FrostedGlassPreviewContent(
    initialMode: FrostedGlassImplementationMode = FrostedGlassImplementationMode.BitmapBlur,
    initialGlassStyle: GlassSurfaceStyle = GlassSurfaceStyle.Flat
) {
    val previewColors = FrostedGlassPreviewColors
    val frostedGlassState = rememberFrostedGlassState()
    val glassShape = RoundedCornerShape(30.dp)
    val surfaceTint = previewColors.surface.copy(alpha = 0.34f)
    val supportsRenderEffect = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val listState = rememberLazyListState()
    val availableModes = if (supportsRenderEffect) {
        listOf(
            FrostedGlassImplementationMode.PlatformBlur,
            FrostedGlassImplementationMode.BitmapBlur
        )
    } else {
        listOf(
            FrostedGlassImplementationMode.BitmapBlur
        )
    }
    var selectedMode by remember(initialMode, supportsRenderEffect) {
        mutableStateOf(
            if (initialMode in availableModes) {
                initialMode
            } else {
                availableModes.first()
            }
        )
    }
    var selectedBlurRadius by remember {
        mutableStateOf(32.dp)
    }
    var selectedLegacyDownsampleFactor by remember {
        mutableStateOf(0.6f)
    }
    var selectedLegacyBlurStrength by remember {
        mutableStateOf(0.55f)
    }
    var selectedGlassStyle by remember(initialGlassStyle) {
        mutableStateOf(initialGlassStyle)
    }
    var showPreviewControlsSheet by remember { mutableStateOf(false) }
    val modeLabel = selectedMode.previewLabel(
        blurRadius = selectedBlurRadius,
        legacyDownsampleFactor = selectedLegacyDownsampleFactor,
        legacyBlurStrength = selectedLegacyBlurStrength
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(previewColors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .frostedGlassHost(
                    state = frostedGlassState,
                    blurRadius = selectedBlurRadius,
                    implementationMode = selectedMode,
                    legacyDownsampleFactor = selectedLegacyDownsampleFactor,
                    legacyBlurStrength = selectedLegacyBlurStrength
                )
        ) {
            FrostedGlassBackdrop(
                listState = listState,
                previewColors = previewColors
            )
        }
        val glassCardModifier = Modifier
            .align(Alignment.TopCenter)
            .padding(horizontal = 20.dp, vertical = 36.dp)
            .fillMaxWidth()
            .height(132.dp)
            .frostedGlassTarget(
                state = frostedGlassState,
                shape = glassShape
            )
            .glassSurface(
                shape = glassShape,
                style = selectedGlassStyle,
                tint = surfaceTint,
                borderColor = previewColors.outline.copy(alpha = 0.55f),
                shadowElevation = 0.dp,
                shadowColor = Color.Transparent
            )
            .clip(glassShape)

        Box(
            modifier = Modifier
                .then(glassCardModifier)
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Frosted Glass",
                    style = MaterialTheme.typography.titleLarge,
                    color = previewColors.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = modeLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = previewColors.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF60A5FA))
                    )
                    Text(
                        text = "Shared host/target modifier pair",
                        style = MaterialTheme.typography.labelLarge,
                        color = previewColors.onSurfaceMuted
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FrostedGlassPreviewControlsButton(
                onClick = { showPreviewControlsSheet = true }
            )
        }
        if (showPreviewControlsSheet) {
            FrostedGlassPreviewControlsTray(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                availableModes = availableModes,
                selectedMode = selectedMode,
                onModeSelected = { selectedMode = it },
                selectedStyle = selectedGlassStyle,
                onStyleSelected = { selectedGlassStyle = it },
                selectedRadius = selectedBlurRadius,
                onRadiusSelected = { selectedBlurRadius = it },
                selectedLegacyDownsampleFactor = selectedLegacyDownsampleFactor,
                onLegacyDownsampleFactorSelected = { selectedLegacyDownsampleFactor = it },
                selectedLegacyBlurStrength = selectedLegacyBlurStrength,
                onLegacyBlurStrengthSelected = { selectedLegacyBlurStrength = it },
                showBitmapControls = selectedMode == FrostedGlassImplementationMode.BitmapBlur,
                onClose = { showPreviewControlsSheet = false }
            )
        }
    }
}

@Composable
private fun FrostedGlassBackdrop(
    listState: androidx.compose.foundation.lazy.LazyListState,
    previewColors: FrostedGlassPreviewPalette
) {
    val accentBrushes = listOf(
        Brush.linearGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF1D4ED8))
        ),
        Brush.linearGradient(
            colors = listOf(Color(0xFF3F3F46), Color(0xFFEA580C))
        ),
        Brush.linearGradient(
            colors = listOf(Color(0xFF164E63), Color(0xFF22C55E))
        ),
        Brush.linearGradient(
            colors = listOf(Color(0xFF581C87), Color(0xFFEC4899))
        )
    )
    val accentOverlayBrushes = listOf(
        Brush.linearGradient(
            colors = listOf(Color(0xAA0F172A), Color(0x881D4ED8))
        ),
        Brush.linearGradient(
            colors = listOf(Color(0x993F3F46), Color(0x88EA580C))
        ),
        Brush.linearGradient(
            colors = listOf(Color(0x99164E63), Color(0x8822C55E))
        ),
        Brush.linearGradient(
            colors = listOf(Color(0x99581C87), Color(0x88EC4899))
        )
    )
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF050816), Color(0xFF111827), previewColors.background)
    )
    val heroBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF091226), Color(0xFF152A4B), Color(0xFF08111F))
    )
    val orbColors = listOf(Color(0x3322D3EE), Color(0x33FB7185))
    val heroStripeBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0EA5E9), Color(0xFF6366F1), Color(0xFFEF4444))
    )

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(heroBrush)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xC40B1220),
                                    Color(0x6A18263E),
                                    Color(0xC40B1220)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 40.dp)
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(orbColors.first())
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 116.dp, end = 6.dp)
                        .size(126.dp)
                        .clip(CircleShape)
                        .background(orbColors.last())
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .height(104.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(heroStripeBrush)
                )
            }
        }

        item {
            Text(
                text = "Drag this preview in interactive mode",
                style = MaterialTheme.typography.titleMedium,
                color = previewColors.onSurface
            )
        }

        item {
            Text(
                text = "Change controls in the docked tray while the glass card stays visible over this lazy list.",
                style = MaterialTheme.typography.bodyMedium,
                color = previewColors.onSurfaceVariant
            )
        }

        items(
            items = List(12) { it },
            key = { it }
        ) { itemIndex ->
            val paletteIndex = itemIndex % accentBrushes.size
            val brush = accentBrushes[paletteIndex]
            val overlayBrush = accentOverlayBrushes[paletteIndex]

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (itemIndex % 2 == 0) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                }
            ) {
                Box(
                    modifier = Modifier
                        .width(if (itemIndex % 2 == 0) 230.dp else 190.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(brush)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(overlayBrush)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.18f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.28f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Preview Tile ${itemIndex + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = if (itemIndex % 2 == 0) {
                                "Wide gradient content moving under the glass."
                            } else {
                                "Narrow card variant for stronger contrast."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.82f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.18f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(240.dp))
        }
    }
}

@Composable
private fun FrostedGlassModeSelector(
    availableModes: List<FrostedGlassImplementationMode>,
    selectedMode: FrostedGlassImplementationMode,
    onModeSelected: (FrostedGlassImplementationMode) -> Unit
) {
    FrostedGlassSelectorContainer(
        title = "Blur mode"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableModes.forEach { mode ->
                FrostedGlassModeButton(
                    modifier = Modifier.weight(1f),
                    title = mode.buttonTitle(),
                    subtitle = mode.buttonSubtitle(),
                    selected = selectedMode == mode,
                    onClick = { onModeSelected(mode) }
                )
            }
        }
    }
}

@Composable
private fun FrostedGlassPreviewControlsButton(
    title: String = "Preview controls",
    subtitle: String = "Open docked tray",
    onClick: () -> Unit
) {
    FrostedGlassModeButton(
        modifier = Modifier.fillMaxWidth(),
        title = title,
        subtitle = subtitle,
        selected = false,
        onClick = onClick
    )
}

@Composable
private fun FrostedGlassPreviewControlsTray(
    modifier: Modifier = Modifier,
    availableModes: List<FrostedGlassImplementationMode>,
    selectedMode: FrostedGlassImplementationMode,
    onModeSelected: (FrostedGlassImplementationMode) -> Unit,
    selectedStyle: GlassSurfaceStyle,
    onStyleSelected: (GlassSurfaceStyle) -> Unit,
    selectedRadius: Dp,
    onRadiusSelected: (Dp) -> Unit,
    selectedLegacyDownsampleFactor: Float,
    onLegacyDownsampleFactorSelected: (Float) -> Unit,
    selectedLegacyBlurStrength: Float,
    onLegacyBlurStrengthSelected: (Float) -> Unit,
    showBitmapControls: Boolean,
    onClose: () -> Unit
) {
    val trayShape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp)
            .clip(trayShape)
            .background(FrostedGlassPreviewColors.surface.copy(alpha = 0.94f))
            .border(
                width = 1.dp,
                color = FrostedGlassPreviewColors.outline.copy(alpha = 0.42f),
                shape = trayShape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(42.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FrostedGlassPreviewColors.onSurface.copy(alpha = 0.18f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FrostedGlassModeSelector(
                availableModes = availableModes,
                selectedMode = selectedMode,
                onModeSelected = onModeSelected
            )

            FrostedGlassGlassStyleSelector(
                selectedStyle = selectedStyle,
                onStyleSelected = onStyleSelected
            )

            FrostedGlassBlurRadiusSelector(
                selectedRadius = selectedRadius,
                onRadiusSelected = onRadiusSelected
            )

            if (showBitmapControls) {
                FrostedGlassLegacyDownsampleSelector(
                    selectedFactor = selectedLegacyDownsampleFactor,
                    onFactorSelected = onLegacyDownsampleFactorSelected
                )

                FrostedGlassLegacyStrengthSelector(
                    selectedStrength = selectedLegacyBlurStrength,
                    onStrengthSelected = onLegacyBlurStrengthSelected
                )
            }
        }

        FrostedGlassPreviewControlsButton(
            title = "Close controls",
            subtitle = "Hide docked tray",
            onClick = onClose
        )
    }
}

@Composable
private fun FrostedGlassGlassStyleSelector(
    selectedStyle: GlassSurfaceStyle,
    onStyleSelected: (GlassSurfaceStyle) -> Unit
) {
    val availableStyles = listOf(
        GlassSurfaceStyle.Flat,
        GlassSurfaceStyle.Convex,
        GlassSurfaceStyle.Concave
    )

    FrostedGlassSelectorContainer(
        title = "Glass surface"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableStyles.forEach { style ->
                FrostedGlassModeButton(
                    modifier = Modifier.weight(1f),
                    title = style.buttonTitle(),
                    subtitle = style.buttonSubtitle(),
                    selected = selectedStyle == style,
                    onClick = { onStyleSelected(style) }
                )
            }
        }
    }
}

@Composable
private fun FrostedGlassBlurRadiusSelector(
    selectedRadius: Dp,
    onRadiusSelected: (Dp) -> Unit
) {
    val availableRadii = listOf(16.dp, 24.dp, 32.dp, 40.dp, 48.dp)

    FrostedGlassSelectorContainer(
        title = "Blur radius"
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableRadii.forEach { radius ->
                FrostedGlassModeButton(
                    modifier = Modifier.width(86.dp),
                    title = radius.previewRadiusTitle(),
                    subtitle = radius.previewRadiusSubtitle(),
                    selected = selectedRadius == radius,
                    onClick = { onRadiusSelected(radius) }
                )
            }
        }
    }
}

@Composable
private fun FrostedGlassLegacyDownsampleSelector(
    selectedFactor: Float,
    onFactorSelected: (Float) -> Unit
) {
    val availableFactors = listOf(0.45f, 0.6f, 0.75f, 0.9f)

    FrostedGlassSelectorContainer(
        title = "Legacy downsample"
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableFactors.forEach { factor ->
                FrostedGlassModeButton(
                    modifier = Modifier.width(86.dp),
                    title = factor.previewFactorTitle(),
                    subtitle = factor.previewFactorSubtitle(),
                    selected = selectedFactor == factor,
                    onClick = { onFactorSelected(factor) }
                )
            }
        }
    }
}

@Composable
private fun FrostedGlassLegacyStrengthSelector(
    selectedStrength: Float,
    onStrengthSelected: (Float) -> Unit
) {
    val availableStrengths = listOf(0.35f, 0.55f, 0.75f, 0.95f)

    FrostedGlassSelectorContainer(
        title = "Legacy strength"
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableStrengths.forEach { strength ->
                FrostedGlassModeButton(
                    modifier = Modifier.width(86.dp),
                    title = strength.previewFactorTitle(),
                    subtitle = strength.previewStrengthSubtitle(),
                    selected = selectedStrength == strength,
                    onClick = { onStrengthSelected(strength) }
                )
            }
        }
    }
}

@Composable
private fun FrostedGlassSelectorContainer(
    title: String,
    content: @Composable () -> Unit
) {
    val containerShape = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(FrostedGlassPreviewColors.surface.copy(alpha = 0.92f))
            .border(
                width = 1.dp,
                color = FrostedGlassPreviewColors.outline.copy(alpha = 0.42f),
                shape = containerShape
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = FrostedGlassPreviewColors.onSurface
        )
        content()
    }
}

@Composable
private fun FrostedGlassModeButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(18.dp)
    val backgroundColor = if (selected) {
        Color(0xFF1D4ED8).copy(alpha = 0.88f)
    } else {
        FrostedGlassPreviewColors.surfaceHigh.copy(alpha = 0.72f)
    }
    val borderColor = if (selected) {
        Color.White.copy(alpha = 0.3f)
    } else {
        FrostedGlassPreviewColors.outline.copy(alpha = 0.28f)
    }
    val titleColor = if (selected) {
        Color.White
    } else {
        FrostedGlassPreviewColors.onSurface
    }
    val subtitleColor = if (selected) {
        Color.White.copy(alpha = 0.76f)
    } else {
        FrostedGlassPreviewColors.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .clip(buttonShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = buttonShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = titleColor
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = subtitleColor
        )
    }
}

private fun FrostedGlassImplementationMode.previewLabel(
    blurRadius: Dp,
    legacyDownsampleFactor: Float,
    legacyBlurStrength: Float
): String {
    return when (this) {
        FrostedGlassImplementationMode.Auto -> "Automatic mode"
        FrostedGlassImplementationMode.PlatformBlur -> {
            "RenderEffect blur (${blurRadius.previewInlineValue()})"
        }
        FrostedGlassImplementationMode.BitmapBlur -> {
            "Bitmap blur fallback (${blurRadius.previewInlineValue()}, " +
                "ds ${legacyDownsampleFactor.previewInlineValue()}, " +
                "str ${legacyBlurStrength.previewInlineValue()})"
        }
    }
}

private fun FrostedGlassImplementationMode.buttonTitle(): String {
    return when (this) {
        FrostedGlassImplementationMode.Auto -> "Auto"
        FrostedGlassImplementationMode.PlatformBlur -> "Render"
        FrostedGlassImplementationMode.BitmapBlur -> "Bitmap"
    }
}

private fun FrostedGlassImplementationMode.buttonSubtitle(): String {
    return when (this) {
        FrostedGlassImplementationMode.Auto -> "Runtime"
        FrostedGlassImplementationMode.PlatformBlur -> "API 12+"
        FrostedGlassImplementationMode.BitmapBlur -> "Legacy"
    }
}

private fun Float.previewInlineValue(): String {
    return String.format("%.2f", this)
}

private fun Dp.previewInlineValue(): String {
    return "${value.toInt()}dp"
}

private fun Float.previewFactorTitle(): String {
    return String.format("%.2f", this)
}

private fun Float.previewFactorSubtitle(): String {
    return when (this) {
        0.45f -> "softer sample"
        0.6f -> "default"
        0.75f -> "sharper"
        else -> "closest"
    }
}

private fun Float.previewStrengthSubtitle(): String {
    return when (this) {
        0.35f -> "subtle"
        0.55f -> "default"
        0.75f -> "strong"
        else -> "heaviest"
    }
}

private fun Dp.previewRadiusTitle(): String {
    return "${value.toInt()}dp"
}

private fun Dp.previewRadiusSubtitle(): String {
    return when (value.toInt()) {
        16 -> "tight"
        24 -> "light"
        32 -> "default"
        40 -> "soft"
        else -> "heavy"
    }
}

private fun GlassSurfaceStyle.buttonTitle(): String {
    return when (this) {
        GlassSurfaceStyle.Flat -> "Flat"
        GlassSurfaceStyle.Convex -> "Convex"
        GlassSurfaceStyle.Concave -> "Concave"
    }
}

private fun GlassSurfaceStyle.buttonSubtitle(): String {
    return when (this) {
        GlassSurfaceStyle.Flat -> "Plain"
        GlassSurfaceStyle.Convex -> "Raised"
        GlassSurfaceStyle.Concave -> "Recessed"
    }
}

private data class FrostedGlassPreviewPalette(
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val onSurfaceMuted: Color
)

private val FrostedGlassPreviewColors = FrostedGlassPreviewPalette(
    background = Color(0xFF0B1220),
    surface = Color(0xFF1A2232),
    surfaceHigh = Color(0xFF273244),
    outline = Color(0xFF64748B),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFCBD5E1),
    onSurfaceMuted = Color(0xFF94A3B8)
)
