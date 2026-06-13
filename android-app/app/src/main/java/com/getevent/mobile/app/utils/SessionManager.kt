package com.getevent.mobile.app.utils

import android.content.Context
import com.getevent.mobile.app.model.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object SessionManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()
    private var storage: SessionStorage? = null

    fun init(context: Context) {
        if (storage != null) return
        storage = SessionStorage(context.applicationContext)
        scope.launch {
            val savedToken = storage?.readToken()
            val savedUserJson = storage?.readUserJson()
            _token.value = savedToken
            _user.value = savedUserJson?.let { runCatching { gson.fromJson(it, User::class.java) }.getOrNull() }
        }
    }

    fun setSession(token: String, user: User) {
        _token.value = token
        _user.value = user
        scope.launch {
            storage?.save(token = token, userJson = gson.toJson(user))
        }
    }

    fun clear() {
        _token.value = null
        _user.value = null
        scope.launch { storage?.clear() }
    }
}
