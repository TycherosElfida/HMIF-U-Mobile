package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.ui.theme.CategoryAcademic
import com.example.hmifu_mobile.ui.theme.CategoryCareer
import com.example.hmifu_mobile.ui.theme.CategoryCompetition
import com.example.hmifu_mobile.ui.theme.CategoryEvent
import com.example.hmifu_mobile.ui.theme.CategoryInfo
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Animated Filter Chip Component
 *
 * An interactive filter chip with:
 * - Spring animation on selection
 * - Animated checkmark for selected state
 * - Color-coded by category
 *
 * @param text Chip label
 * @param selected Whether the chip is selected
 * @param onClick Click handler
 * @param color Chip accent color
 * @param modifier Modifier for the chip
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = HmifBlue,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            selected -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "filter_chip_scale"
    )

    val backgroundColor = if (selected) color else Color.Transparent
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderColor = if (selected) color else MaterialTheme.colorScheme.outline

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.full))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(HmifTheme.cornerRadius.full)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = HmifTheme.spacing.md, vertical = HmifTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Selected",
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * Skill Chip Component
 *
 * A removable chip for displaying skills/tags.
 */
@Composable
fun SkillChip(
    text: String,
    onRemove: (() -> Unit)? = null,
    color: Color = HmifBlue,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "skill_chip_scale"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )

        if (onRemove != null) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.2f))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onRemove
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove $text",
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Category Chip Component
 *
 * Pre-styled chips for event/announcement categories.
 */
@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    val color = when (category.lowercase()) {
        "event", "acara" -> CategoryEvent
        "academic", "akademik" -> CategoryAcademic
        "competition", "kompetisi" -> CategoryCompetition
        "career", "karier" -> CategoryCareer
        else -> CategoryInfo
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Filter Chips Row Component
 *
 * A horizontally scrollable row of filter chips.
 */
@Composable
fun FilterChipsRow(
    chips: List<String>,
    selectedChip: String?,
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        chips.forEach { chip ->
            FilterChip(
                text = chip,
                selected = chip == selectedChip,
                onClick = { onChipSelected(chip) }
            )
        }
    }
}

/**
 * Skills Grid Component
 *
 * A flow layout for displaying multiple skill chips.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsGrid(
    skills: List<String>,
    onRemove: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        skills.forEach { skill ->
            SkillChip(
                text = skill,
                onRemove = if (onRemove != null) {
                    { onRemove(skill) }
                } else null
            )
        }
    }
}
