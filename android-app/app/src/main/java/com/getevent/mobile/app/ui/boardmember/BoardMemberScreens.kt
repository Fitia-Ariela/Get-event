package com.getevent.mobile.app.ui.boardmember

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.getevent.mobile.app.model.Reservation
import com.getevent.mobile.app.ui.AppViewModel
import com.getevent.mobile.app.ui.components.GetEventLargeTopBar
import com.getevent.mobile.app.ui.components.GetEventTextField
import com.getevent.mobile.app.ui.components.GetEventTopBar
import com.getevent.mobile.app.ui.components.ListLoadingSkeleton
import com.getevent.mobile.app.ui.components.RoleGrid
import com.getevent.mobile.app.ui.components.RoleMenuCard
import com.getevent.mobile.app.ui.components.RoleMenuEntry
import com.getevent.mobile.app.ui.student.components.ReservationStatusChip
import com.getevent.mobile.app.ui.theme.ButtonShape
import com.getevent.mobile.app.ui.theme.CardShape
import com.getevent.mobile.app.ui.theme.GetEventTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDashboardScreen(
    onScan: () -> Unit,
    onReservations: () -> Unit
) {
    Scaffold(
        topBar = {
            GetEventLargeTopBar(
                title = "Espace bureau",
                subtitle = "GetEvent"
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(RoleGrid),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Outils de validation",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                RoleMenuCard(
                    RoleMenuEntry(
                        title = "Scanner QR",
                        subtitle = "Valider les billets à l'entrée",
                        icon = Icons.Default.QrCodeScanner,
                        onClick = onScan,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            item {
                RoleMenuCard(
                    RoleMenuEntry(
                        title = "Liste des réservations",
                        subtitle = "Consulter et suivre les inscriptions",
                        icon = Icons.Default.ListAlt,
                        onClick = onReservations,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: AppViewModel,
    onBack: (() -> Unit)? = null,
    onOpenDashboard: (() -> Unit)? = null,
    onOpenReservations: (() -> Unit)? = null
) {
    val message by viewModel.message.collectAsState()
    val loading by viewModel.ticketLoading.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    QrScannerContent(
        message = message,
        loading = loading,
        snackbarHost = { SnackbarHost(snackbar) },
        onBack = onBack,
        onOpenDashboard = onOpenDashboard,
        onOpenReservations = onOpenReservations,
        onValidate = { id -> viewModel.validateTicket(id.toLongOrNull() ?: 0L) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerContent(
    message: String?,
    loading: Boolean,
    snackbarHost: @Composable () -> Unit,
    onValidate: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    onOpenDashboard: (() -> Unit)? = null,
    onOpenReservations: (() -> Unit)? = null
) {
    var ticketId by remember { mutableStateOf("") }
    val isSuccess = message?.contains("valid", ignoreCase = true) == true

    Scaffold(
        snackbarHost = snackbarHost,
        topBar = {
            GetEventTopBar(
                title = "Validation billet",
                onBack = onBack,
                actions = {
                    if (onOpenDashboard != null) {
                        IconButton(onClick = onOpenDashboard) {
                            Icon(Icons.Default.Dashboard, contentDescription = "Tableau de bord")
                        }
                    }
                    if (onOpenReservations != null) {
                        IconButton(onClick = onOpenReservations) {
                            Icon(Icons.Default.ListAlt, contentDescription = "Réservations")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(RoleGrid),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Surface(
                    modifier = Modifier.size(140.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    "Scanner ou saisir l'ID",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Collez l'identifiant du ticket scanné pour valider l'entrée.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape
                ) {
                    Column(
                        modifier = Modifier.padding(RoleGrid),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GetEventTextField(
                            value = ticketId,
                            onValueChange = { ticketId = it },
                            label = "ID du ticket"
                        )
                        Button(
                            onClick = { onValidate(ticketId) },
                            enabled = ticketId.isNotBlank() && !loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = ButtonShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Valider l'entrée", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = message != null) {
                    message?.let { msg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSuccess) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.errorContainer
                                }
                            )
                        ) {
                            Text(
                                msg,
                                modifier = Modifier.padding(16.dp),
                                color = if (isSuccess) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit = {},
    title: String = "Réservations"
) {
    val reservations by viewModel.reservations.collectAsState()
    val loading by viewModel.reservationsLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAllReservations() }

    ReservationListContent(
        title = title,
        reservations = reservations,
        loading = loading,
        onBack = onBack,
        onRefresh = { viewModel.loadAllReservations() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListContent(
    title: String,
    reservations: List<Reservation>,
    loading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            GetEventTopBar(title = title, onBack = onBack, onRefresh = onRefresh)
        }
    ) { padding ->
        when {
            loading && reservations.isEmpty() -> ListLoadingSkeleton()
            reservations.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucune réservation",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(RoleGrid),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservations, key = { it.idReservation }) { res ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = CardShape) {
                            ListItem(
                                headlineContent = {
                                    Text("Réservation #${res.idReservation}", fontWeight = FontWeight.Bold)
                                },
                                supportingContent = {
                                    Text(
                                        "Événement #${res.evenementId} · ${res.dateReservation}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingContent = { ReservationStatusChip(res.statut) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardDashboardPreview() {
    GetEventTheme {
        BoardDashboardScreen({}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun QrScannerPreview() {
    GetEventTheme {
        QrScannerContent(
            message = "Ticket validé",
            loading = false,
            snackbarHost = {},
            onValidate = {}
        )
    }
}
