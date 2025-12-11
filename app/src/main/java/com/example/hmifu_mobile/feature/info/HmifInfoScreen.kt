package com.example.hmifu_mobile.feature.info

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * HMIF division data.
 */
data class Division(
    val name: String,
    val description: String,
    val head: String,
    val icon: ImageVector = Icons.Default.Groups,
    val color: Color = HmifBlue
)

/**
 * HMIF Info Screen - Premium 2025 Design
 *
 * Features:
 * - Gradient hero section
 * - Glassmorphic cards
 * - Division cards with colors
 * - Contact section
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HmifInfoScreen(
    onNavigateBack: () -> Unit
) {
    val divisions = listOf(
        Division(
            name = "Badan Pengurus Harian (BPH)",
            description = "Core leadership team managing daily operations",
            head = "President & Vice President",
            color = HmifBlue
        ),
        Division(
            name = "Akademik",
            description = "Academic programs, workshops, and study groups",
            head = "Head of Academic Affairs",
            color = HmifOrange
        ),
        Division(
            name = "Riset & Pengembangan",
            description = "Research projects and tech development",
            head = "Head of R&D",
            color = HmifPurple
        ),
        Division(
            name = "Hubungan Masyarakat",
            description = "Public relations and external partnerships",
            head = "Head of Public Relations",
            color = GradientEnd
        ),
        Division(
            name = "Sumber Daya Manusia",
            description = "Member development and internal affairs",
            head = "Head of HR",
            color = HmifBlue
        ),
        Division(
            name = "Media & Komunikasi",
            description = "Social media, documentation, and content",
            head = "Head of Media",
            color = HmifOrange
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = HmifBlue
                        )
                        Text(
                            text = "About HMIF",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(HmifTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
        ) {
            // Hero section
            item {
                StaggeredAnimatedItem(index = 0) {
                    HeroSection()
                }
            }

            // Vision & Mission
            item {
                StaggeredAnimatedItem(index = 1) {
                    VisionMissionSection()
                }
            }

            // Divisions header
            item {
                StaggeredAnimatedItem(index = 2) {
                    Text(
                        text = "Our Divisions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Division cards
            itemsIndexed(divisions) { index, division ->
                StaggeredAnimatedItem(index = index + 3) {
                    DivisionCard(division = division)
                }
            }

            // Contact section
            item {
                StaggeredAnimatedItem(index = divisions.size + 3) {
                    ContactSection()
                }
            }

            item {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// HERO SECTION
// ════════════════════════════════════════════════════════════════

@Composable
private fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.xxl))
            .background(
                Brush.linearGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .padding(HmifTheme.spacing.xl)
    ) {
        Column {
            Text(
                text = "🎓 HMIF UKRIDA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))
            Text(
                text = "Himpunan Mahasiswa Informatika is the official student organization " +
                        "for Informatics students at Ukrida. We foster academic excellence, " +
                        "professional development, and community.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// VISION & MISSION
// ════════════════════════════════════════════════════════════════

@Composable
private fun VisionMissionSection() {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)) {
            // Vision
            Column {
                Text(
                    text = "🎯 Vision",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = HmifBlue
                )
                Spacer(modifier = Modifier.height(HmifTheme.spacing.xs))
                Text(
                    text = "To be the leading student organization that empowers Informatics students " +
                            "to excel academically and professionally.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Mission
            Column {
                Text(
                    text = "🚀 Mission",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = HmifOrange
                )
                Spacer(modifier = Modifier.height(HmifTheme.spacing.xs))
                Text(
                    text = "• Provide quality academic support and resources\n" +
                            "• Organize events that enhance technical and soft skills\n" +
                            "• Build a strong network within the tech community\n" +
                            "• Foster innovation and creativity among members",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// DIVISION CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun DivisionCard(division: Division) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                    .background(division.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = division.icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = division.color
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = division.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = division.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = division.head,
                    style = MaterialTheme.typography.labelSmall,
                    color = division.color
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// CONTACT SECTION
// ════════════════════════════════════════════════════════════════

@Composable
private fun ContactSection() {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)) {
            Text(
                text = "📬 Contact Us",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = HmifBlue
                )
                Text(
                    text = "hmif@ukrida.ac.id",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = HmifPurple
                )
                Text(
                    text = "Instagram: @hmif_ukrida",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
