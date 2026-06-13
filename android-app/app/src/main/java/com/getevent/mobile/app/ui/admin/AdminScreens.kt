package com.getevent.mobile.app.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.getevent.mobile.app.ui.AppViewModel
import com.getevent.mobile.app.ui.components.FormSection
import com.getevent.mobile.app.ui.components.GetEventLargeTopBar
import com.getevent.mobile.app.ui.components.GetEventTextField
import com.getevent.mobile.app.ui.components.GetEventTopBar
import com.getevent.mobile.app.ui.components.ListLoadingSkeleton
import com.getevent.mobile.app.ui.components.RoleGrid
import com.getevent.mobile.app.ui.components.RoleMenuCard
import com.getevent.mobile.app.ui.components.RoleMenuEntry
import com.getevent.mobile.app.ui.theme.ButtonShape
import com.getevent.mobile.app.ui.theme.CardShape
import com.getevent.mobile.app.ui.theme.GetEventTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onEventCrud: () -> Unit,
    onLocationCrud: () -> Unit,
    onUsers: () -> Unit,
    onReservations: () -> Unit,
    onStats: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val menuEntries = listOf(
        RoleMenuEntry("Événements", "CRUD événements campus", Icons.Default.Event, onEventCrud, colors.primaryContainer),
        RoleMenuEntry("Lieux", "Salles et capacités", Icons.Default.Place, onLocationCrud, colors.secondaryContainer),
        RoleMenuEntry("Utilisateurs", "Comptes et rôles", Icons.Default.People, onUsers, colors.tertiaryContainer),
        RoleMenuEntry("Réservations", "Toutes les réservations", Icons.Default.ConfirmationNumber, onReservations, colors.surfaceVariant),
        RoleMenuEntry("Statistiques", "Répartition par rôle", Icons.Default.BarChart, onStats, colors.primaryContainer.copy(alpha = 0.5f))
    )

    Scaffold(
        topBar = {
            GetEventLargeTopBar(
                title = "Administration",
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
                    "Gestion du système",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(menuEntries, key = { it.title }) { entry ->
                RoleMenuCard(entry = entry)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudEventScreen(viewModel: AppViewModel, onBack: () -> Unit = {}) {
    var showForm by remember { mutableStateOf(false) }
    val events by viewModel.events.collectAsState()
    val loading by viewModel.eventsLoading.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadEvents() }
    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { GetEventTopBar("Gestion événements", onBack = onBack, onRefresh = { viewModel.loadEvents() }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showForm = !showForm },
                icon = { Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null) },
                text = { Text(if (showForm) "Fermer" else "Nouvel événement") },
                shape = ButtonShape
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AnimatedVisibility(visible = showForm) {
                    EventForm(viewModel) { showForm = false }
                }
            }
            if (loading && events.isEmpty()) {
                item { ListLoadingSkeleton() }
            } else {
                items(events, key = { it.idEvenement }) { event ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = RoleGrid), shape = CardShape) {
                        ListItem(
                            headlineContent = { Text(event.nomEvenement, fontWeight = FontWeight.SemiBold) },
                            supportingContent = {
                                Text(
                                    "${event.dateEvenement} · ${if (event.estPrive) "Privé" else "Public"}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventForm(viewModel: AppViewModel, onCreated: () -> Unit) {
    var nom by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var lieuId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estPrive by remember { mutableStateOf(false) }
    var tarif by remember { mutableStateOf("0") }

    FormSection(title = "Nouvel événement") {
        GetEventTextField(nom, { nom = it }, "Nom de l'événement")
        GetEventTextField(date, { date = it }, "Date (ISO)")
        GetEventTextField(lieuId, { lieuId = it }, "ID du lieu")
        GetEventTextField(description, { description = it }, "Description", singleLine = false)
        GetEventTextField(tarif, { tarif = it }, "Tarif")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Événement privé")
            Switch(checked = estPrive, onCheckedChange = { estPrive = it })
        }
        Button(
            onClick = {
                viewModel.createEvent(
                    nom, date, lieuId.toLongOrNull() ?: 0L, description, estPrive, tarif.toDoubleOrNull() ?: 0.0
                )
                onCreated()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Enregistrer", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudLocationScreen(viewModel: AppViewModel, onBack: () -> Unit = {}) {
    var showForm by remember { mutableStateOf(false) }
    val locations by viewModel.locations.collectAsState()
    val loading by viewModel.locationsLoading.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadLocations() }
    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { GetEventTopBar("Gestion lieux", onBack = onBack, onRefresh = { viewModel.loadLocations() }) },
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { showForm = !showForm },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AnimatedVisibility(visible = showForm) {
                    LocationForm(viewModel) { showForm = false }
                }
            }
            if (loading && locations.isEmpty()) {
                item { ListLoadingSkeleton() }
            } else if (!loading && locations.isEmpty()) {
                item {
                    Text(
                        "Aucun lieu. Ajoutez-en un avec le bouton +.",
                        modifier = Modifier.padding(horizontal = RoleGrid),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(locations, key = { it.id }) { loc ->
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = RoleGrid), shape = CardShape) {
                    ListItem(
                        headlineContent = { Text(loc.nom, fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Capacité: ${loc.capacite} places") },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationForm(viewModel: AppViewModel, onCreated: () -> Unit) {
    var nom by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var cap by remember { mutableStateOf("") }

    FormSection(title = "Nouveau lieu") {
        GetEventTextField(nom, { nom = it }, "Nom du lieu")
        GetEventTextField(longitude, { longitude = it }, "Longitude")
        GetEventTextField(latitude, { latitude = it }, "Latitude")
        GetEventTextField(cap, { cap = it }, "Capacité")
        Button(
            onClick = {
                viewModel.createLocation(
                    nom,
                    longitude.toFloatOrNull() ?: 0f,
                    latitude.toFloatOrNull() ?: 0f,
                    cap.toIntOrNull() ?: 0
                )
                onCreated()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = ButtonShape
        ) {
            Text("Ajouter le lieu", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(viewModel: AppViewModel, onBack: () -> Unit = {}) {
    val users by viewModel.users.collectAsState()
    val loading by viewModel.usersLoading.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadUsers() }
    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { GetEventTopBar("Utilisateurs", onBack = onBack, onRefresh = { viewModel.loadUsers() }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(RoleGrid),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                loading && users.isEmpty() -> item { ListLoadingSkeleton() }
                !loading && users.isEmpty() -> item {
                    Text(
                        "Aucun utilisateur trouvé.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> items(users, key = { it.id }) { user ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = CardShape) {
                        ListItem(
                            headlineContent = { Text(user.nom, fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            trailingContent = {
                                Badge { Text(user.role.name, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: AppViewModel, onBack: () -> Unit = {}) {
    val stats by viewModel.stats.collectAsState()
    val loading by viewModel.statsLoading.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadStats() }
    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { GetEventTopBar("Statistiques", onBack = onBack, onRefresh = { viewModel.loadStats() }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(RoleGrid),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (loading && stats.isEmpty()) {
                item { ListLoadingSkeleton(count = 3) }
            } else if (!loading && stats.isEmpty()) {
                item {
                    Text(
                        "Aucune statistique disponible.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(stats.entries.toList()) { (role, count) ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = CardShape) {
                        Row(
                            Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(role, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                count.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
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
private fun AdminDashboardPreview() {
    GetEventTheme {
        AdminDashboardScreen({}, {}, {}, {}, {})
    }
}
