package com.getevent.mobile.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getevent.mobile.app.api.EventRequest
import com.getevent.mobile.app.api.EventDetailResponse
import com.getevent.mobile.app.api.LocationRequest
import com.getevent.mobile.app.api.RegisterRequest
import com.getevent.mobile.app.model.Event
import com.getevent.mobile.app.model.Location
import com.getevent.mobile.app.model.Reservation
import com.getevent.mobile.app.model.Role
import com.getevent.mobile.app.model.Ticket
import com.getevent.mobile.app.model.User
import com.getevent.mobile.app.repository.GetEventRepository
import com.getevent.mobile.app.utils.NetworkErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: GetEventRepository = GetEventRepository()
) : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    private val _ticket = MutableStateFlow<Ticket?>(null)
    val ticket: StateFlow<Ticket?> = _ticket.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    private val _stats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val stats: StateFlow<Map<String, Int>> = _stats.asStateFlow()

    private val _eventDetail = MutableStateFlow<EventDetailResponse?>(null)
    val eventDetail: StateFlow<EventDetailResponse?> = _eventDetail.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _eventsLoading = MutableStateFlow(false)
    val eventsLoading: StateFlow<Boolean> = _eventsLoading.asStateFlow()

    private val _eventDetailLoading = MutableStateFlow(false)
    val eventDetailLoading: StateFlow<Boolean> = _eventDetailLoading.asStateFlow()

    private val _reservationsLoading = MutableStateFlow(false)
    val reservationsLoading: StateFlow<Boolean> = _reservationsLoading.asStateFlow()

    private val _ticketLoading = MutableStateFlow(false)
    val ticketLoading: StateFlow<Boolean> = _ticketLoading.asStateFlow()

    private val _usersLoading = MutableStateFlow(false)
    val usersLoading: StateFlow<Boolean> = _usersLoading.asStateFlow()

    private val _locationsLoading = MutableStateFlow(false)
    val locationsLoading: StateFlow<Boolean> = _locationsLoading.asStateFlow()

    private val _statsLoading = MutableStateFlow(false)
    val statsLoading: StateFlow<Boolean> = _statsLoading.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun loadEvents() = viewModelScope.launch {
        _eventsLoading.value = true
        runCatching { repository.listEvents() }
            .onSuccess { _events.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _eventsLoading.value = false
    }

    fun loadEventDetail(eventId: Long) = viewModelScope.launch {
        _eventDetailLoading.value = true
        _eventDetail.value = null
        runCatching { repository.getEvent(eventId) }
            .onSuccess { _eventDetail.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _eventDetailLoading.value = false
    }

    fun reserve(eventId: Long) = viewModelScope.launch {
        runCatching { repository.reserve(eventId) }
            .onSuccess { _message.value = "Reservation created: ${it.idReservation}" }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
    }

    fun loadMyReservations() = viewModelScope.launch {
        _reservationsLoading.value = true
        runCatching { repository.myReservations() }
            .onSuccess { _reservations.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _reservationsLoading.value = false
    }

    fun loadAllReservations() = viewModelScope.launch {
        runCatching { repository.allReservations() }
            .onSuccess { _reservations.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
    }

    fun loadTicketByReservation(reservationId: Long) = viewModelScope.launch {
        _ticketLoading.value = true
        _ticket.value = null
        runCatching { repository.ticketByReservation(reservationId) }
            .onSuccess { _ticket.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _ticketLoading.value = false
    }

    fun validateTicket(ticketId: Long) = viewModelScope.launch {
        runCatching { repository.validateTicket(ticketId) }
            .onSuccess {
                _ticket.value = it
                _message.value = "Ticket validated"
            }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
    }

    fun loadUsers() = viewModelScope.launch {
        _usersLoading.value = true
        runCatching { repository.listUsers() }
            .onSuccess { _users.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _usersLoading.value = false
    }

    fun loadStats() = viewModelScope.launch {
        _statsLoading.value = true
        runCatching { repository.stats() }
            .onSuccess { _stats.value = it.parRole }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _statsLoading.value = false
    }

    fun loadLocations() = viewModelScope.launch {
        _locationsLoading.value = true
        runCatching { repository.listLocations() }
            .onSuccess { _locations.value = it }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
        _locationsLoading.value = false
    }

    fun createEvent(
        nom: String,
        date: String,
        lieuId: Long,
        description: String,
        estPrive: Boolean,
        tarif: Double
    ) = viewModelScope.launch {
        runCatching {
            repository.createEvent(EventRequest(nom, date, lieuId, description, estPrive, tarif))
        }.onSuccess {
            _message.value = "Event created"
            loadEvents()
        }.onFailure { _message.value = it.message }
    }

    fun createLocation(nom: String, longitude: Float, latitude: Float, capacite: Int) = viewModelScope.launch {
        runCatching { repository.createLocation(LocationRequest(nom, longitude, latitude, capacite)) }
            .onSuccess {
                _message.value = "Location created"
                loadLocations()
            }
            .onFailure { _message.value = NetworkErrors.userMessage(it) }
    }

    fun createUser(
        numeroInscription: String,
        nom: String,
        niveau: String,
        parcours: String,
        numeroTel: Long,
        email: String,
        nomFacebook: String,
        password: String,
        role: Role
    ) = viewModelScope.launch {
        runCatching {
            repository.createUser(
                RegisterRequest(
                    numeroInscription,
                    nom,
                    niveau,
                    parcours,
                    numeroTel,
                    email,
                    nomFacebook,
                    password,
                    role
                )
            )
        }.onSuccess {
            _message.value = "User created"
            loadUsers()
        }.onFailure { _message.value = it.message }
    }
}
