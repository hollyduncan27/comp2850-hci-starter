```kotlin
package utils

import io.ktor.http.HttpStatusCode
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Simple CSV logger for HCI evaluation metrics.
 * Thread-safe, appends to data/metrics.csv.
 *
 * Privacy: Ensure session_id is anonymous (no PII).
 */
data class LogEntry(
    val sessionId: String,
    val requestId: String,
    val taskCode: String,
    val step: String,
    val outcome: String,
    val durationMs: Long,
    val statusCode: Int,
    val jsMode: String,
)

object Logger {
    private val out =
        File("data/metrics.csv").apply {
            parentFile?.mkdirs()
            if (!exists()) writeText("ts_iso,session_id,request_id,task_code,step,outcome,ms,http_status,js_mode\n")
        }

    @Synchronized
    fun write(entry: LogEntry) {
        val ts = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        out.appendText(
            "$ts,${entry.sessionId},${entry.requestId},${entry.taskCode},${entry.step}," +
                "${entry.outcome},${entry.durationMs},${entry.statusCode},${entry.jsMode}\n",
        )
    }

    fun validationError(
        sessionId: String,
        requestId: String,
        taskCode: String,
        outcome: String,
        jsMode: String,
    ) {
        write(
            LogEntry(
                sessionId = sessionId,
                requestId = requestId,
                taskCode = taskCode,
                step = "validation_error",
                outcome = outcome,
                durationMs = 0,
                statusCode = HttpStatusCode.BadRequest.value,
                jsMode = jsMode,
            )
        )
    }

    fun success(
        sessionId: String,
        requestId: String,
        taskCode: String,
        durationMs: Long,
        jsMode: String,
    ) {
        write(
            LogEntry(
                sessionId = sessionId,
                requestId = requestId,
                taskCode = taskCode,
                step = "success",
                outcome = "",
                durationMs = durationMs,
                statusCode = HttpStatusCode.OK.value,
                jsMode = jsMode,
            )
        )
    }
}
