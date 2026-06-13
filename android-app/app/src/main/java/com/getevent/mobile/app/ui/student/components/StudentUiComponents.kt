package com.getevent.mobile.app.ui.student.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.getevent.mobile.app.model.ReservationStatus
import com.getevent.mobile.app.ui.theme.CardShape
import com.getevent.mobile.app.ui.theme.CoralAccent
import com.getevent.mobile.app.ui.theme.IndigoDeep
import com.getevent.mobile.app.ui.theme.IndigoLight
import com.getevent.mobile.app.ui.theme.TealAccent
import com.getevent.mobile.app.ui.theme.TealLight

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 16.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(CardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
    )
}

@Composable
fun EventCardSkeleton() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 160.dp)
            ShimmerBox(height = 20.dp)
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.6f), height = 14.dp)
        }
    }
}

@Composable
fun EmptyEventsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Aucun événement pour le moment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Revenez plus tard ou tirez pour actualiser.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun eventCoverBrush(seed: Long): Brush {
    val palettes = listOf(
        listOf(IndigoDeep, IndigoLight),
        listOf(TealAccent, TealLight),
        listOf(CoralAccent, IndigoLight),
        listOf(IndigoDeep, TealAccent)
    )
    val colors = palettes[(seed % palettes.size).toInt()]
    return Brush.linearGradient(colors)
}

@Composable
fun PrivacyBadge(isPrivate: Boolean, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = {},
        enabled = false,
        modifier = modifier,
        label = {
            Text(
                if (isPrivate) "Privé" else "Public",
                style = MaterialTheme.typography.labelLarge
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = if (isPrivate) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            disabledLabelColor = if (isPrivate) {
                MaterialTheme.colorScheme.onTertiaryContainer
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            }
        )
    )
}

@Composable
fun ReservationStatusChip(status: ReservationStatus) {
    val (label, container, labelColor) = when (status) {
        ReservationStatus.CONFIRMED -> Triple(
            "Confirmée",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        ReservationStatus.PENDING -> Triple(
            "En attente",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        ReservationStatus.CANCELLED -> Triple(
            "Annulée",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = container,
            disabledLabelColor = labelColor
        )
    )
}

@Composable
fun GlassOverlay(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = CardShape,
        color = Color.White.copy(alpha = 0.72f),
        tonalElevation = 4.dp,
        shadowElevation = 0.dp
    ) {
        content()
    }
}

@Composable
fun InfoRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon()
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TicketValidityChip(isValid: Boolean) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                if (isValid) "Billet valide" else "Billet utilisé",
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = if (isValid) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            disabledLabelColor = if (isValid) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    )
}
