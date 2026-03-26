package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview(
    name = "Glass Surface Playground Dark",
    showBackground = true,
    backgroundColor = 0xFF081120,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun GlassSurfacePlaygroundDarkPreview() {
    GlassSurfacePlayground(
        darkTheme = true
    )
}

@Preview(
    name = "Glass Surface Playground Light",
    showBackground = true,
    backgroundColor = 0xFFF5F7FB,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun GlassSurfacePlaygroundLightPreview() {
    GlassSurfacePlayground(
        darkTheme = false
    )
}

@Composable
private fun GlassSurfacePlayground(
    darkTheme: Boolean = true
) {
    var selectedAccentColor by remember { mutableStateOf(GlassSurfacePreviewAccent.Ocean) }
    var selectedStyle by remember { mutableStateOf(GlassSurfaceStyle.Flat) }
    var tintAlpha by remember { mutableFloatStateOf(0.14f) }
    var borderWidth by remember { mutableStateOf(1.dp) }
    var shadowElevation by remember { mutableStateOf(20.dp) }
    var selectedShape by remember { mutableStateOf(GlassSurfacePreviewShape.Soft) }
    var selectedTintTone by remember {
        mutableStateOf(GlassSurfacePreviewTintTone.Ice)
    }

    GlassSurfacePlaygroundContent(
        darkTheme = darkTheme,
        selectedAccentColor = selectedAccentColor,
        onAccentColorSelected = { selectedAccentColor = it },
        selectedStyle = selectedStyle,
        onStyleSelected = { selectedStyle = it },
        tintAlpha = tintAlpha,
        onTintAlphaSelected = { tintAlpha = it },
        borderWidth = borderWidth,
        onBorderWidthSelected = { borderWidth = it },
        shadowElevation = shadowElevation,
        onShadowElevationSelected = { shadowElevation = it },
        selectedShape = selectedShape,
        onShapeSelected = { selectedShape = it },
        selectedTintTone = selectedTintTone,
        onTintToneSelected = { selectedTintTone = it }
    )
}

@Composable
private fun GlassSurfacePlaygroundContent(
    darkTheme: Boolean,
    selectedAccentColor: GlassSurfacePreviewAccent,
    onAccentColorSelected: (GlassSurfacePreviewAccent) -> Unit,
    selectedStyle: GlassSurfaceStyle,
    onStyleSelected: (GlassSurfaceStyle) -> Unit,
    tintAlpha: Float,
    onTintAlphaSelected: (Float) -> Unit,
    borderWidth: Dp,
    onBorderWidthSelected: (Dp) -> Unit,
    shadowElevation: Dp,
    onShadowElevationSelected: (Dp) -> Unit,
    selectedShape: GlassSurfacePreviewShape,
    onShapeSelected: (GlassSurfacePreviewShape) -> Unit,
    selectedTintTone: GlassSurfacePreviewTintTone,
    onTintToneSelected: (GlassSurfacePreviewTintTone) -> Unit
) {
    val scrollState = rememberScrollState()
    val previewColors = glassSurfacePreviewColors(
        darkTheme = darkTheme,
        accent = selectedAccentColor
    )

    val tintColor = selectedTintTone.tintBaseColor(darkTheme).copy(alpha = tintAlpha)
    val borderColor = selectedTintTone.borderBaseColor(darkTheme)
        .copy(alpha = (tintAlpha + 0.08f).coerceAtMost(0.36f))
    val shadowColor = selectedTintTone.shadowBaseColor(darkTheme)
        .copy(alpha = if (darkTheme) 0.22f else 0.16f)
    val shape = selectedShape.shape

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                Brush.verticalGradient(
                    colors = previewColors.backgroundColors()
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassSurfaceBackgroundHero(
            darkTheme = darkTheme,
            accentColor = previewColors.accent,
            accentContainer = previewColors.accentContainer,
            accentContainerSubtle = previewColors.accentContainerSubtle
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .glassSurface(
                    shape = shape,
                    style = selectedStyle,
                    tint = tintColor,
                    borderColor = borderColor,
                    borderWidth = borderWidth,
                    shadowElevation = shadowElevation,
                    shadowColor = shadowColor
                )
                .clip(shape)
                .background(Color.Transparent)
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Glass Surface",
                        style = MaterialTheme.typography.headlineMedium,
                        color = previewColors.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = selectedStyle.previewLabel(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = previewColors.onSurfaceVariant
                    )
                    Text(
                        text = "${previewColors.modeLabel} preview  •  ${selectedAccentColor.displayLabel()} accent",
                        style = MaterialTheme.typography.labelLarge,
                        color = previewColors.onSurfaceMuted
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Shape: ${selectedShape.label}",
                        style = MaterialTheme.typography.labelLarge,
                        color = previewColors.onSurfaceVariant
                    )
                    if (selectedStyle == GlassSurfaceStyle.Flat) {
                        Text(
                            text = "${selectedTintTone.label} tint ${tintAlpha.formatPreview()}  Border ${borderWidth.value.toInt()}dp  Shadow ${shadowElevation.value.toInt()}dp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = previewColors.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Styled surfaces ignore flat tint/border/shadow parameters.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = previewColors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        GlassSurfaceControlGroup(
            title = "Accent color",
            colors = previewColors
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassSurfacePreviewAccent.entries.forEach { accent ->
                    GlassSurfaceControlChip(
                        label = accent.displayLabel(),
                        selected = selectedAccentColor == accent,
                        onClick = { onAccentColorSelected(accent) },
                        colors = previewColors,
                        leadingColor = accent.previewColor(darkTheme)
                    )
                }
            }
        }

        GlassSurfaceControlGroup(
            title = "Style",
            colors = previewColors
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassSurfaceStyle.entries.forEach { style ->
                    GlassSurfaceControlChip(
                        label = style.buttonTitle(),
                        selected = selectedStyle == style,
                        onClick = { onStyleSelected(style) },
                        colors = previewColors
                    )
                }
            }
        }

        GlassSurfaceControlGroup(
            title = "Shape",
            colors = previewColors
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassSurfacePreviewShape.entries.forEach { shapeOption ->
                    GlassSurfaceControlChip(
                        label = shapeOption.label,
                        selected = selectedShape == shapeOption,
                        onClick = { onShapeSelected(shapeOption) },
                        colors = previewColors
                    )
                }
            }
        }

        if (selectedStyle == GlassSurfaceStyle.Flat) {
            GlassSurfaceControlGroup(
                title = "Tint tone",
                colors = previewColors
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassSurfacePreviewTintTone.entries.forEach { tintTone ->
                        GlassSurfaceControlChip(
                            label = tintTone.label,
                            selected = selectedTintTone == tintTone,
                            onClick = { onTintToneSelected(tintTone) },
                            colors = previewColors,
                            leadingColor = tintTone.previewSwatch(darkTheme)
                        )
                    }
                }
            }

            GlassSurfaceControlGroup(
                title = "Tint alpha",
                colors = previewColors
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.08f, 0.14f, 0.2f, 0.28f).forEach { alpha ->
                        GlassSurfaceControlChip(
                            label = alpha.formatPreview(),
                            selected = tintAlpha == alpha,
                            onClick = { onTintAlphaSelected(alpha) },
                            colors = previewColors
                        )
                    }
                }
            }

            GlassSurfaceControlGroup(
                title = "Border width",
                colors = previewColors
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.dp, 1.dp, 2.dp, 3.dp).forEach { width ->
                        GlassSurfaceControlChip(
                            label = "${width.value.toInt()}dp",
                            selected = borderWidth == width,
                            onClick = { onBorderWidthSelected(width) },
                            colors = previewColors
                        )
                    }
                }
            }

            GlassSurfaceControlGroup(
                title = "Shadow",
                colors = previewColors
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.dp, 12.dp, 20.dp, 28.dp).forEach { elevation ->
                        GlassSurfaceControlChip(
                            label = "${elevation.value.toInt()}dp",
                            selected = shadowElevation == elevation,
                            onClick = { onShadowElevationSelected(elevation) },
                            colors = previewColors
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun GlassSurfaceBackgroundHero(
    darkTheme: Boolean,
    accentColor: Color,
    accentContainer: Color,
    accentContainerSubtle: Color
) {
    val backgroundBrush = Brush.radialGradient(
        colors = if (darkTheme) {
            listOf(
                accentColor.copy(alpha = 0.92f),
                accentContainer.copy(alpha = 0.96f),
                Color(0xFF0B1220)
            )
        } else {
            listOf(
                accentContainerSubtle,
                accentContainer,
                Color(0xFFEAF1FA)
            )
        }
    )
    val startOrbColor = if (darkTheme) {
        accentColor.copy(alpha = 0.22f)
    } else {
        accentColor.copy(alpha = 0.14f)
    }
    val endOrbColor = if (darkTheme) {
        Color(0x33FB7185)
    } else {
        Color(0x26F97316)
    }
    val stripePairs = listOf(
        accentColor to accentContainer,
        Color(0xFF8B5CF6) to Color(0xFFEC4899),
        Color(0xFF14B8A6) to Color(0xFF22C55E)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(backgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 18.dp)
                .size(132.dp)
                .clip(CircleShape)
                .background(startOrbColor)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 18.dp)
                .size(112.dp)
                .clip(CircleShape)
                .background(endOrbColor)
        )
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stripePairs.forEachIndexed { index, stripeColors ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (index == 1) 96.dp else 76.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    stripeColors.first,
                                    stripeColors.second
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun GlassSurfaceControlGroup(
    title: String,
    colors: GlassSurfacePreviewColors,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = colors.outline.copy(alpha = 0.36f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = colors.onSurface
        )
        content()
    }
}

@Composable
private fun GlassSurfaceControlChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: GlassSurfacePreviewColors,
    leadingColor: Color? = null
) {
    val backgroundColor = if (selected) {
        colors.accent.copy(alpha = 0.88f)
    } else {
        colors.surfaceHigh.copy(alpha = 0.72f)
    }
    val borderColor = if (selected) {
        Color.White.copy(alpha = 0.28f)
    } else {
        colors.outline.copy(alpha = 0.28f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (leadingColor != null) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(leadingColor)
                        .border(
                            width = 1.dp,
                            color = if (selected) {
                                Color.White.copy(alpha = 0.8f)
                            } else {
                                colors.outline.copy(alpha = 0.48f)
                            },
                            shape = CircleShape
                        )
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Color.White else colors.onSurface
            )
        }
    }
}

private enum class GlassSurfacePreviewShape(
    val label: String,
    val shape: Shape
) {
    Soft("Soft", RoundedCornerShape(28.dp)),
    Pill("Pill", RoundedCornerShape(percent = 50)),
    Sharp("Sharp", RoundedCornerShape(12.dp))
}

private enum class GlassSurfacePreviewAccent(
    private val label: String,
    private val darkAccent: Color,
    private val lightAccent: Color,
    private val darkContainer: Color,
    private val lightContainer: Color,
    private val darkContainerSubtle: Color,
    private val lightContainerSubtle: Color
) {
    Ocean(
        label = "Ocean",
        darkAccent = Color(0xFF60A5FA),
        lightAccent = Color(0xFF2563EB),
        darkContainer = Color(0xFF1D4ED8),
        lightContainer = Color(0xFF93C5FD),
        darkContainerSubtle = Color(0xFF172554),
        lightContainerSubtle = Color(0xFFDBEAFE)
    ),
    Mint(
        label = "Mint",
        darkAccent = Color(0xFF34D399),
        lightAccent = Color(0xFF059669),
        darkContainer = Color(0xFF047857),
        lightContainer = Color(0xFFA7F3D0),
        darkContainerSubtle = Color(0xFF052E2B),
        lightContainerSubtle = Color(0xFFD1FAE5)
    ),
    Amber(
        label = "Amber",
        darkAccent = Color(0xFFFBBF24),
        lightAccent = Color(0xFFD97706),
        darkContainer = Color(0xFFB45309),
        lightContainer = Color(0xFFFDE68A),
        darkContainerSubtle = Color(0xFF3B2306),
        lightContainerSubtle = Color(0xFFFEF3C7)
    ),
    Rose(
        label = "Rose",
        darkAccent = Color(0xFFFB7185),
        lightAccent = Color(0xFFDB2777),
        darkContainer = Color(0xFFBE185D),
        lightContainer = Color(0xFFF9A8D4),
        darkContainerSubtle = Color(0xFF4A044E),
        lightContainerSubtle = Color(0xFFFCE7F3)
    ),
    Violet(
        label = "Violet",
        darkAccent = Color(0xFFA78BFA),
        lightAccent = Color(0xFF7C3AED),
        darkContainer = Color(0xFF6D28D9),
        lightContainer = Color(0xFFD8B4FE),
        darkContainerSubtle = Color(0xFF2E1065),
        lightContainerSubtle = Color(0xFFF3E8FF)
    );

    fun displayLabel(): String = label

    fun accentColor(darkTheme: Boolean): Color {
        return if (darkTheme) darkAccent else lightAccent
    }

    fun accentContainer(darkTheme: Boolean): Color {
        return if (darkTheme) darkContainer else lightContainer
    }

    fun accentContainerSubtle(darkTheme: Boolean): Color {
        return if (darkTheme) darkContainerSubtle else lightContainerSubtle
    }

    fun previewColor(darkTheme: Boolean): Color {
        return accentColor(darkTheme)
    }
}

private data class GlassSurfacePreviewColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val onSurfaceMuted: Color,
    val modeLabel: String,
    val accent: Color,
    val accentContainer: Color,
    val accentContainerSubtle: Color
) {
    fun backgroundColors(): List<Color> {
        return listOf(backgroundTop, accentContainerSubtle.copy(alpha = 0.98f), backgroundBottom)
    }
}

private enum class GlassSurfacePreviewTintTone(
    val label: String,
    private val darkTint: Color,
    private val lightTint: Color,
    private val darkBorder: Color,
    private val lightBorder: Color,
    private val darkShadow: Color,
    private val lightShadow: Color
) {
    Ice(
        label = "Ice",
        darkTint = Color(0xFFDFF4FF),
        lightTint = Color(0xFF9FD8FF),
        darkBorder = Color.White,
        lightBorder = Color(0xFF1D4ED8),
        darkShadow = Color(0xFF020617),
        lightShadow = Color(0xFF0F172A)
    ),
    Neutral(
        label = "Neutral",
        darkTint = Color.White,
        lightTint = Color.White,
        darkBorder = Color.White,
        lightBorder = Color(0xFF475569),
        darkShadow = Color.Black,
        lightShadow = Color(0xFF111827)
    ),
    Mint(
        label = "Mint",
        darkTint = Color(0xFFD5FEE8),
        lightTint = Color(0xFF6EE7B7),
        darkBorder = Color(0xFFA7F3D0),
        lightBorder = Color(0xFF047857),
        darkShadow = Color(0xFF052E25),
        lightShadow = Color(0xFF064E3B)
    ),
    Rose(
        label = "Rose",
        darkTint = Color(0xFFFFE4EE),
        lightTint = Color(0xFFF9A8D4),
        darkBorder = Color(0xFFF9A8D4),
        lightBorder = Color(0xFFBE185D),
        darkShadow = Color(0xFF4A044E),
        lightShadow = Color(0xFF831843)
    ),
    Amber(
        label = "Amber",
        darkTint = Color(0xFFFFF2C7),
        lightTint = Color(0xFFFDE68A),
        darkBorder = Color(0xFFFDE68A),
        lightBorder = Color(0xFFB45309),
        darkShadow = Color(0xFF422006),
        lightShadow = Color(0xFF78350F)
    );

    fun tintBaseColor(darkTheme: Boolean): Color {
        return if (darkTheme) darkTint else lightTint
    }

    fun borderBaseColor(darkTheme: Boolean): Color {
        return if (darkTheme) darkBorder else lightBorder
    }

    fun shadowBaseColor(darkTheme: Boolean): Color {
        return if (darkTheme) darkShadow else lightShadow
    }

    fun previewSwatch(darkTheme: Boolean): Color {
        return if (darkTheme) {
            darkTint.copy(alpha = 0.9f)
        } else {
            lightBorder.copy(alpha = 0.9f)
        }
    }
}

private fun GlassSurfaceStyle.buttonTitle(): String {
    return when (this) {
        GlassSurfaceStyle.Flat -> "Flat"
        GlassSurfaceStyle.Convex -> "Convex"
        GlassSurfaceStyle.Concave -> "Concave"
    }
}

private fun GlassSurfaceStyle.previewLabel(): String {
    return when (this) {
        GlassSurfaceStyle.Flat -> "Flat surface with parameterized tint, border, and shadow"
        GlassSurfaceStyle.Convex -> "Raised styled glass treatment"
        GlassSurfaceStyle.Concave -> "Recessed styled glass treatment"
    }
}

private fun glassSurfacePreviewColors(
    darkTheme: Boolean,
    accent: GlassSurfacePreviewAccent
): GlassSurfacePreviewColors {
    return if (darkTheme) {
        GlassSurfacePreviewColors(
            backgroundTop = Color(0xFF050816),
            backgroundBottom = Color(0xFF081120),
            surface = Color(0xFF172033),
            surfaceHigh = Color(0xFF243044),
            outline = Color(0xFF64748B),
            onSurface = Color(0xFFF8FAFC),
            onSurfaceVariant = Color(0xFFCBD5E1),
            onSurfaceMuted = Color(0xFF94A3B8),
            modeLabel = "Dark",
            accent = accent.accentColor(true),
            accentContainer = accent.accentContainer(true),
            accentContainerSubtle = accent.accentContainerSubtle(true)
        )
    } else {
        GlassSurfacePreviewColors(
            backgroundTop = Color(0xFFF8FBFF),
            backgroundBottom = Color(0xFFEAF1FA),
            surface = Color(0xFFF8FAFC),
            surfaceHigh = Color(0xFFE2E8F0),
            outline = Color(0xFF94A3B8),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF334155),
            onSurfaceMuted = Color(0xFF64748B),
            modeLabel = "Light",
            accent = accent.accentColor(false),
            accentContainer = accent.accentContainer(false),
            accentContainerSubtle = accent.accentContainerSubtle(false)
        )
    }
}

private fun Float.formatPreview(): String {
    return String.format("%.2f", this)
}
