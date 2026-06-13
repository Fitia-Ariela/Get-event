package com.getevent.mobile.app.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrors {
    fun userMessage(throwable: Throwable): String {
        val root = throwable.cause ?: throwable
        return when (root) {
            is ConnectException,
            is SocketTimeoutException,
            is UnknownHostException -> backendUnreachableMessage()

            is HttpException -> httpMessage(root)

            else -> {
                val msg = root.message.orEmpty()
                when {
                    msg.contains("failed to connect", ignoreCase = true) ||
                        msg.contains("ECONNREFUSED", ignoreCase = true) ||
                        msg.contains("Unable to resolve host", ignoreCase = true) ->
                        backendUnreachableMessage()

                    msg.isNotBlank() -> msg
                    else -> "Une erreur réseau est survenue."
                }
            }
        }
    }

    private fun backendUnreachableMessage(): String =
        "Serveur inaccessible. Démarrez le backend Ktor sur le port 8080 " +
            "(dossier ktor-getevent-backend : .\\gradlew.bat run) puis réessayez."

    private fun httpMessage(ex: HttpException): String {
        val body = runCatching { ex.response()?.errorBody()?.string() }.getOrNull()
        parseServerError(body)?.let { return it }
        return when (ex.code()) {
            401 -> "Session expirée ou identifiants incorrects."
            403 -> "Accès refusé pour votre rôle."
            404 -> "Ressource introuvable."
            in 500..599 -> "Erreur serveur (${ex.code()}). Réessayez plus tard."
            else -> "Erreur HTTP ${ex.code()}."
        }
    }

    private fun parseServerError(body: String?): String? {
        if (body.isNullOrBlank()) return null
        return runCatching {
            val json = Gson().fromJson(body, JsonObject::class.java)
            json.get("error")?.asString ?: json.get("message")?.asString
        }.getOrNull()
    }
}
