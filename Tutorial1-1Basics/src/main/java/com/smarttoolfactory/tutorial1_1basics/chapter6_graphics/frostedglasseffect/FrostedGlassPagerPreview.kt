package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.frostedglasseffect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(
    name = "Frosted Glass Pager",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 412,
    heightDp = 892,
    apiLevel = 31
)
@Composable
private fun FrostedGlassPagerPreview() {
    FrostedGlassPagerPreviewContent()
}

@Composable
private fun FrostedGlassPagerPreviewContent() {
    val frostedGlassState = rememberSharedFrostedGlassState()
    val pagerState = rememberPagerState(pageCount = { FrostedGlassPagerPreviewPages.size })
    val pagerShape = RoundedCornerShape(28.dp)
    val sectionShape = RoundedCornerShape(26.dp)
    val colors = FrostedGlassPagerColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .sharedFrostedGlassHost(
                    state = frostedGlassState,
                    blurRadius = 16.dp,
                    implementationMode = FrostedGlassImplementationMode.Auto,
                    legacyDownsampleFactor = 0.65f,
                    legacyBlurStrength = 0.60f,
                    legacyBlurHostConfig = LegacyBlurHostConfig(
                        stableCaptureBounds = true
                    )
                )
        ) {
            FrostedGlassPagerBackdrop()
            FrostedGlassPagerHostText(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 418.dp)
            )
        }

        FrostedGlassPagerHeader(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            textPrimary = colors.textPrimary,
            textSecondary = colors.textSecondary,
            accent = colors.headerAccent,
            title = "Pager with frosted sections",
            description = "The root backdrop stays as the shared host, while each page keeps " +
                "just one small bottom-centered frosted panel inset by 16.dp."
        )

        FrostedGlassPagerCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 188.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .height(330.dp),
            frostedGlassState = frostedGlassState,
            pagerState = pagerState,
            pageShape = pagerShape,
            sectionShape = sectionShape,
            colors = colors
        )
    }
}

@Composable
private fun FrostedGlassPagerHostText(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "HOST TEXT",
            style = MaterialTheme.typography.displaySmall,
            color = Color(0xAA5A2EB8),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "This root text should soften when the bottom frost target passes over it.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xAA5E4590),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FrostedGlassPagerHeader(
    modifier: Modifier,
    textPrimary: Color,
    textSecondary: Color,
    accent: Color,
    title: String,
    description: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Editorial header",
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = textPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary
        )
    }
}

@Composable
private fun FrostedGlassPagerCard(
    modifier: Modifier,
    frostedGlassState: SharedFrostedGlassState,
    pagerState: PagerState,
    pageShape: RoundedCornerShape,
    sectionShape: RoundedCornerShape,
    colors: FrostedGlassPagerColors
) {
    Column(
        modifier = modifier
            .clip(pageShape),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(0.dp)
        ) { page ->
            FrostedGlassPagerPage(
                item = FrostedGlassPagerPreviewPages[page],
                page = page,
                frostedGlassState = frostedGlassState,
                pageShape = pageShape,
                sectionShape = sectionShape,
                colors = colors,
                textPrimary = colors.textPrimary,
                textSecondary = colors.textSecondary
            )
        }

        FrostedGlassPagerIndicators(
            currentPage = pagerState.currentPage,
            textSecondary = colors.textSecondary
        )
    }
}

@Composable
private fun FrostedGlassPagerPage(
    item: FrostedGlassPagerPreviewPage,
    page: Int,
    frostedGlassState: SharedFrostedGlassState,
    pageShape: RoundedCornerShape,
    sectionShape: RoundedCornerShape,
    colors: FrostedGlassPagerColors,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(pageShape)
            .border(
                width = 1.dp,
                color = colors.pageBorder,
                shape = pageShape
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall,
            color = textPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary
        )
        Text(
            text = item.caption,
            style = MaterialTheme.typography.labelLarge,
            color = colors.captionColor
        )

        Spacer(modifier = Modifier.weight(1f))

        FrostedGlassPageSection(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(FrostedGlassPagerSectionWidth)
                .height(120.dp)
                .padding(bottom = 16.dp),
            key = "page-section-$page",
            item = item,
            frostedGlassState = frostedGlassState,
            sectionShape = sectionShape,
            colors = colors
        )
    }
}

@Composable
private fun FrostedGlassPageSection(
    modifier: Modifier,
    key: Any,
    item: FrostedGlassPagerPreviewPage,
    frostedGlassState: SharedFrostedGlassState,
    sectionShape: RoundedCornerShape,
    colors: FrostedGlassPagerColors
) {
    Box(
        modifier = modifier
            .sharedFrostedGlassTarget(
                state = frostedGlassState,
                key = key,
                shape = sectionShape
            )
            .border(
                width = 1.dp,
                color = colors.surfaceBorder,
                shape = sectionShape
            )
            .clip(sectionShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Frost Target",
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.panelNote,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun FrostedGlassPagerIndicators(
    currentPage: Int,
    textSecondary: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FrostedGlassPagerPreviewPages.forEachIndexed { index, item ->
            val selected = index == currentPage
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (selected) 28.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            Color(0xFF6E40E8)
                        } else {
                            Color.White.copy(alpha = 0.45f)
                        }
                    )
            )
            if (selected) {
                Text(
                    text = item.shortLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FrostedGlassPagerBackdrop() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val heroTop = size.height * 0.26f
                val heroHeight = size.height - heroTop + size.height * 0.12f
                val heroBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEDE3FF),
                        Color(0xFFC5A1FF),
                        Color(0xFF7B44F2),
                        Color(0xFF4A1C9E)
                    ),
                    start = Offset(size.width * 0.12f, heroTop),
                    end = Offset(size.width * 0.94f, size.height)
                )
                val glowBrush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.50f),
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.78f, size.height * 0.54f),
                    radius = size.minDimension * 0.54f
                )

                onDrawBehind {
                    drawRect(color = Color.White)
                    drawRoundRect(
                        brush = heroBrush,
                        topLeft = Offset(-size.width * 0.08f, heroTop),
                        size = Size(size.width * 1.16f, heroHeight),
                        cornerRadius = CornerRadius(
                            x = size.width * 0.24f,
                            y = size.width * 0.24f
                        )
                    )
                    drawOval(
                        brush = glowBrush,
                        topLeft = Offset(size.width * 0.38f, size.height * 0.30f),
                        size = Size(size.width * 0.84f, size.height * 0.42f)
                    )
                    drawOval(
                        color = Color.White.copy(alpha = 0.26f),
                        topLeft = Offset(size.width * 0.56f, size.height * 0.70f),
                        size = Size(size.width * 0.52f, size.height * 0.18f)
                    )
                    drawOval(
                        color = Color(0xFFD5B8FF).copy(alpha = 0.34f),
                        topLeft = Offset(size.width * 0.74f, size.height * 0.77f),
                        size = Size(size.width * 0.30f, size.height * 0.11f)
                    )
                    drawOval(
                        color = Color(0xFFFFE7FF).copy(alpha = 0.24f),
                        topLeft = Offset(size.width * 0.68f, size.height * 0.83f),
                        size = Size(size.width * 0.40f, size.height * 0.13f)
                    )
                }
            }
    )
}

private data class FrostedGlassPagerPreviewPage(
    val shortLabel: String,
    val title: String,
    val description: String,
    val caption: String,
    val mode: String,
    val focus: String,
    val panelNote: String
)

private val FrostedGlassPagerTextPrimary = Color(0xFF25124A)
private val FrostedGlassPagerTextSecondary = Color(0xFF715A9A)
private val FrostedGlassPagerHeaderAccent = Color(0xFF8C74B1)
private val FrostedGlassPagerCaptionColor = Color(0xFF8467B8)
private val FrostedGlassPagerSectionWidth = 260.dp

private data class FrostedGlassPagerColors(
    val pageBorder: Color = Color(0x66FFFFFF),
    val surfaceTint: Color = Color.White.copy(alpha = 0.22f),
    val surfaceBorder: Color = Color.White.copy(alpha = 0.52f),
    val textPrimary: Color = FrostedGlassPagerTextPrimary,
    val textSecondary: Color = FrostedGlassPagerTextSecondary,
    val captionColor: Color = FrostedGlassPagerCaptionColor,
    val headerAccent: Color = FrostedGlassPagerHeaderAccent
)

private val FrostedGlassPagerPreviewPages = listOf(
    FrostedGlassPagerPreviewPage(
        shortLabel = "Sibling",
        title = "Sibling layout",
        description = "The purple backdrop is captured by the root host while the pager " +
            "shell stays regular and only the inset note panel is frosted.",
        caption = "Blur only appears where the page registers an inset target.",
        mode = "Auto blur",
        focus = "Layering",
        panelNote = "Only this bottom-centered panel is frosted. The rest of the page stays " +
            "fully sharp."
    ),
    FrostedGlassPagerPreviewPage(
        shortLabel = "Header",
        title = "White header transition",
        description = "The header keeps a clean white field so the card can straddle " +
            "the break between editorial white space and the gradient body.",
        caption = "The shell provides contrast without turning the full page into glass.",
        mode = "Rounded rect",
        focus = "Contrast",
        panelNote = "This inset sits 16.dp from the bottom and stays centered so the layout " +
            "still reads mostly non-frosted."
    ),
    FrostedGlassPagerPreviewPage(
        shortLabel = "Ellipses",
        title = "Bottom-right ellipses",
        description = "The background draws overlapping ellipses in the lower right " +
            "to give the glass blur more depth and shape variation.",
        caption = "Those ellipses become more obvious inside the frosted panel.",
        mode = "Draw cache",
        focus = "Backdrop",
        panelNote = "The small frosted inset picks up the purple ellipses behind it without " +
            "covering the rest of the page."
    )
)
