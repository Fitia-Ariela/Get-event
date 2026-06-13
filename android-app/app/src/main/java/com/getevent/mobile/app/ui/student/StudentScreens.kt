package com.getevent.mobile.app.ui.student

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.getevent.mobile.app.model.Event
import com.getevent.mobile.app.model.Reservation
import com.getevent.mobile.app.model.Ticket
import com.getevent.mobile.app.model.User
import com.getevent.mobile.app.ui.AppViewModel
import com.getevent.mobile.app.ui.student.components.EmptyEventsState
import com.getevent.mobile.app.ui.student.components.EventCardSkeleton
import com.getevent.mobile.app.ui.student.components.GlassOverlay
import com.getevent.mobile.app.ui.student.components.InfoRow
import com.getevent.mobile.app.ui.student.components.PrivacyBadge
import com.getevent.mobile.app.ui.student.components.ReservationStatusChip
import com.getevent.mobile.app.ui.student.components.ShimmerBox
import com.getevent.mobile.app.ui.student.components.TicketValidityChip
import com.getevent.mobile.app.ui.student.components.eventCoverBrush
import com.getevent.mobile.app.ui.theme.ButtonShape
import com.getevent.mobile.app.ui.theme.CardShape
import com.getevent.mobile.app.ui.theme.GetEventTheme
import com.getevent.mobile.app.utils.generateQrImageBitmap

private val Grid = 16.dp

// ─── 1. Discovery Home ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    viewModel: AppViewModel,
    onEventClick: (Long) -> Unit,
    onMyReservations: () -> Unit,
    onProfile: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val loading by viewModel.eventsLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("GetEvent", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text("Découvrir", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onMyReservations,
                icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                text = { Text("Mes billets", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = ButtonShape
            )
        }
    ) { padding ->
        when {
            loading && events.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = Grid),
                    verticalArrangement = Arrangement.spacedBy(Grid)
                ) {
                    EventCardSkeleton()
                    EventCardSkeleton()
                    EventCardSkeleton()
                }
            }
            !loading && events.isEmpty() -> {
                EmptyEventsState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(start = Grid, end = Grid, top = Grid, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(Grid)
                ) {
                    items(events, key = { it.idEvenement }) { event ->
                        DiscoveryEventCard(
                            event = event,
                            onClick = { onEventClick(event.idEvenement) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscoveryEventCard(event: Event, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(eventCoverBrush(event.idEvenement))
            ) {
                PrivacyBadge(
                    isPrivate = event.estPrive,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Grid)
                )
                GlassOverlay(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Grid)
                ) {
                    Text(
                        event.nomEvenement,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.padding(Grid),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    event.dateEvenement,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (event.estPrive) {
                    Text(
                        "· ${event.tarif} Ar",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

// ─── 2. Event Details ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: AppViewModel,
    eventId: Long,
    onReserve: (Long) -> Unit,
    onBack: () -> Unit = {}
) {
    val detail by viewModel.eventDetail.collectAsState()
    val loading by viewModel.eventDetailLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(eventId) {
        if (eventId != 0L) viewModel.loadEventDetail(eventId)
    }
    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Détails") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onReserve(eventId) },
                    enabled = !loading && eventId != 0L,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Grid)
                        .height(56.dp),
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Réserver maintenant", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        if (loading && detail == null) {
            EventDetailLoadingPlaceholder(
                modifier = Modifier.padding(padding).padding(Grid)
            )
        } else {
            val d = detail
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(
                                eventCoverBrush(d?.event?.idEvenement ?: eventId)
                            )
                    ) {
                        PrivacyBadge(
                            isPrivate = d?.event?.estPrive == true,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(Grid)
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier.padding(Grid),
                        verticalArrangement = Arrangement.spacedBy(Grid)
                    ) {
                        Text(
                            d?.event?.nomEvenement ?: "Événement",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            d?.event?.description.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ElevatedCard(shape = CardShape) {
                            Column(modifier = Modifier.padding(Grid)) {
                                InfoRow(
                                    icon = {
                                        Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    label = "Date & heure",
                                    value = d?.event?.dateEvenement ?: "—"
                                )
                                InfoRow(
                                    icon = {
                                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.secondary)
                                    },
                                    label = "Lieu",
                                    value = d?.lieu?.nom ?: "À confirmer"
                                )
                                if (d?.lieu != null) {
                                    InfoRow(
                                        icon = {
                                            Icon(Icons.Default.Badge, null, tint = MaterialTheme.colorScheme.tertiary)
                                        },
                                        label = "Capacité",
                                        value = "${d.lieu.capacite} places"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 3. My Reservations ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    viewModel: AppViewModel,
    onTicket: (Long) -> Unit,
    onBack: () -> Unit = {}
) {
    val reservations by viewModel.reservations.collectAsState()
    val loading by viewModel.reservationsLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMyReservations() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes réservations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        if (loading && reservations.isEmpty()) {
            ReservationsLoadingPlaceholder(
                modifier = Modifier.padding(padding).padding(Grid)
            )
        } else if (reservations.isEmpty()) {
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
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(Grid),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservations, key = { it.idReservation }) { reservation ->
                    ReservationListItem(reservation = reservation, onClick = { onTicket(reservation.idReservation) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationListItem(reservation: Reservation, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape
    ) {
        ListItem(
            headlineContent = {
                Text("Réservation #${reservation.idReservation}", fontWeight = FontWeight.Bold)
            },
            supportingContent = {
                Text(
                    "Événement #${reservation.evenementId} · ${reservation.dateReservation}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReservationStatusChip(reservation.statut)
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketQrScreen(
    viewModel: AppViewModel,
    reservationId: Long,
    onBack: () -> Unit = {}
) {
    val ticket by viewModel.ticket.collectAsState()
    val loading by viewModel.ticketLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(reservationId) {
        if (reservationId != 0L) viewModel.loadTicketByReservation(reservationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billet numérique", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Grid),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                ticket != null -> TicketQrContent(ticket = ticket!!, reservationId = reservationId)
                else -> Text(message ?: "Billet introuvable", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ─── 5. Student Profile ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User?, onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        if (user == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Session non active", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Grid),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Grid)
            ) {
                Surface(
                    modifier = Modifier.size(112.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            user.nom.take(1).uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(user.nom, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(8.dp))

                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = CardShape) {
                    ProfileInfoItem(Icons.Default.School, "Formation", user.parcours)
                    ProfileInfoItem(Icons.Default.TrendingUp, "Niveau", user.niveau)
                    ProfileInfoItem(Icons.Default.Badge, "Rôle", user.role.name)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(Grid)) {
                        Text("N° inscription", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.numeroInscription, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    ListItem(
        headlineContent = { Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        supportingContent = { Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
        leadingContent = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                Icon(icon, null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    )
}

@Composable
private fun EventDetailLoadingPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Grid)) {
        ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 240.dp)
        ShimmerBox(height = 18.dp)
        ShimmerBox(height = 18.dp)
        ShimmerBox(height = 18.dp)
        ShimmerBox(height = 18.dp)
    }
}

@Composable
private fun ReservationsLoadingPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ShimmerBox(height = 72.dp)
        ShimmerBox(height = 72.dp)
        ShimmerBox(height = 72.dp)
        ShimmerBox(height = 72.dp)
    }
}

@Composable
private fun TicketQrContent(ticket: Ticket, reservationId: Long) {
    val qrContent = ticket.urlCode.ifBlank { "GETEVENT-TICKET-${ticket.idTicket}" }
    val qrBitmap = remember(qrContent) { generateQrImageBitmap(qrContent, 480) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Grid)
    ) {
        ElevatedCard(
            shape = CardShape,
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Billet #${ticket.idTicket}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "QR Code billet",
                    modifier = Modifier
                        .size(260.dp)
                        .clip(CardShape),
                    contentScale = ContentScale.Fit
                )
                Text(
                    "Réservation #$reservationId",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF5C6B7A)
                )
            }
        }
        TicketValidityChip(isValid = !ticket.estUtilise)
    }
}

// ─── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DiscoveryHomePreview() {
    GetEventTheme {
        DiscoveryEventCard(
            event = Event(
                idEvenement = 1,
                nomEvenement = "Gala de fin d'année",
                dateEvenement = "2026-06-15T18:00:00",
                lieuId = 1,
                description = "Soirée festive",
                estPrive = false,
                tarif = 0.0
            ),
            onClick = {}
        )
    }
}
