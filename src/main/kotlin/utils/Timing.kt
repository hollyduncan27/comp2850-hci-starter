package utils

import io.ktor.server.application.*
import io.ktor.util.*

/**
 * Timing helper for HCI evaluation.
 * Wraps a block of code, measures duration, logs result.
 */

// AttributeKey for storing request start time
val RequestStartTimeKey = AttributeKey<Long>("RequestStartTime")

// AttributeKey for request ID
val RequestIdKey = AttributeKey<String>("RequestId")

/**
 * Extension function to time a block of code and log the result.
 *
 * Usage:
 *   call.timed(taskCode = "T1_filter", jsMode = "on") {
 *       // ... validation, processing, etc.
 *       // If validation fails, throw exception or return early
 *   }
 *
 * Automatically logs success or captures exceptions.
 */
suspend fun ApplicationCall.timed(
    taskCode: String,
    jsMode: String,
    block: suspend ApplicationCall.() -> Unit
) {
    val start = System.currentTimeMillis()
    call.attributes.put(RequestStartTimeKey, start)

    val session = request.cookies["sid"] ?: "anon"
    val reqId = attributes.getOrNull(RequestIdKey) ?: newReqId()

    try {
        block()
        val duration = System.currentTimeMillis() - start
        Logger.success(session, reqId, taskCode, duration, jsMode)
    } catch (e: Exception) {
        val duration = System.currentTimeMillis() - start
        Logger.write(session, reqId, taskCode, "server_error", e.message ?: "unknown", duration, 500, jsMode)
        throw e
    }
}

/**
 * Helper to detect JavaScript mode from HTMX header.
 */
fun ApplicationCall.isHtmx(): Boolean =
    request.headers["HX-Request"]?.equals("true", ignoreCase = true) == true

fun ApplicationCall.jsMode(): String =
    if (isHtmx()) "on" else "off"

/**
 * Generate a unique request ID.
 */
private var requestCounter = 0
fun newReqId(): String = "r${String.format("%04d", ++requestCounter)}"