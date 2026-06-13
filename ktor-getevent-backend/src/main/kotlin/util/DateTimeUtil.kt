package util

import java.time.Instant

object DateTimeUtil {
    fun nowIso(): String = Instant.now().toString()
}
