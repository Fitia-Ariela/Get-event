package com.getevent.mobile.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getevent.mobile.app.api.RegisterRequest
import com.getevent.mobile.app.model.Role
import com.getevent.mobile.app.repository.GetEventRepository
import com.getevent.mobile.app.utils.NetworkErrors
import com.getevent.mobile.app.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: GetEventRepository = GetEventRepository()
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _role = MutableStateFlow<Role?>(null)
    val role: StateFlow<Role?> = _role.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            runCatching {
                repository.login(email, password)
            }.onSuccess { resp ->
                SessionManager.setSession(resp.token, resp.user)
                _role.value = resp.user.role
            }.onFailure { ex ->
                _error.value = NetworkErrors.userMessage(ex)
            }
            _loading.value = false
        }
    }

    fun register(
        numeroInscription: String,
        nom: String,
        niveau: String,
        parcours: String,
        numeroTel: Long,
        email: String,
        nomFacebook: String,
        password: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            runCatching {
                repository.register(
                    RegisterRequest(
                        numeroInscription = numeroInscription,
                        nom = nom,
                        niveau = niveau,
                        parcours = parcours,
                        numeroTel = numeroTel,
                        email = email,
                        nomFacebook = nomFacebook,
                        password = password,
                        role = Role.STUDENT
                    )
                )
            }.onSuccess { resp ->
                SessionManager.setSession(resp.token, resp.user)
                _role.value = resp.user.role
            }.onFailure { ex ->
                _error.value = NetworkErrors.userMessage(ex)
            }
            _loading.value = false
        }
    }

    fun consumeRole() {
        _role.value = null
    }
}
