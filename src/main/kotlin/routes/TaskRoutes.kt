package routes

import data.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.pebbletemplates.pebble.PebbleEngine
import java.io.StringWriter

/**
 * NOTE FOR NON-INTELLIJ IDEs (VSCode, Eclipse, etc.):
 * IntelliJ IDEA automatically adds imports as you type. If using a different IDE,
 * you may need to manually add imports. The commented imports below show what you'll need
 * for future weeks. Uncomment them as needed when following the lab instructions.
 *
 * When using IntelliJ: You can ignore the commented imports below - your IDE will handle them.
 */

// Week 7+ imports (inline edit, toggle completion):
// import model.Task               // When Task becomes separate model class
// import model.ValidationResult   // For validation errors
// import renderTemplate            // Extension function from Main.kt
// import isHtmxRequest             // Extension function from Main.kt

// Week 8+ imports (pagination, search, URL encoding):
// import io.ktor.http.encodeURLParameter  // For query parameter encoding
// import utils.Page                       // Pagination helper class

// Week 9+ imports (metrics logging, instrumentation):
// import utils.jsMode              // Detect JS mode (htmx/nojs)
// import utils.logValidationError  // Log validation failures
// import utils.timed               // Measure request timing

// Note: Solution repo uses storage.TaskStore instead of data.TaskRepository
// You may refactor to this in Week 10 for production readiness

/**
 * Week 6 Lab 1: Simple task routes with HTMX progressive enhancement.
 *
 * **Teaching approach**: Start simple, evolve incrementally
 * - Week 6: Basic CRUD with Int IDs
 * - Week 7: Add toggle, inline edit
 * - Week 8: Add pagination, search
 */

fun Route.taskRoutes() {
    val pebble =
        PebbleEngine
            .Builder()
            .loader(
                io.pebbletemplates.pebble.loader.ClasspathLoader().apply {
                    prefix = "templates/"
                },
            ).build()

    /**
     * Helper: Check if request is from HTMX
     */
    fun ApplicationCall.isHtmx(): Boolean = request.headers["HX-Request"]?.equals("true", ignoreCase = true) == true

    /**
     * GET /tasks - List all tasks
     * Returns full page (no HTMX differentiation in Week 6)
     */
    get("/tasks") {
    val query = call.request.queryParameters["q"].orEmpty()
    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
    val data = repo.search(query = query, page = page, size = 10)

    // Add session info for footer
    val sessionId = call.sessions.get<UserSession>()?.id ?: "guest"
    val isHtmx = call.request.headers["HX-Request"]?.equals("true", ignoreCase = true) == true

    val model = mapOf(
        "title" to "Tasks",
        "page" to data,
        "query" to query,
        "sessionId" to sessionId,
        "isHtmx" to isHtmx
    )
    call.respondHtml(PebbleRender.render("tasks/index.peb", model))
}

    /**
     * POST /tasks - Add new task
     * Dual-mode: HTMX fragment or PRG redirect
     */
    post("/tasks") {
    val reqId = newReqId()
    call.attributes.put(RequestIdKey, reqId)

    val session = call.request.cookies["sid"] ?: "anon"
    val jsMode = call.jsMode()

    call.timed(taskCode = "T3_add", jsMode = jsMode) {
        val title = call.receiveParameters()["title"].orEmpty().trim()

        // Validation
        if (title.isBlank()) {
            Logger.validationError(session, reqId, "T3_add", "blank_title", 0, jsMode)
            if (call.isHtmx()) {
                val status = """<div id="status" hx-swap-oob="true">Title is required.</div>"""
                return@timed call.respondText(status, ContentType.Text.Html, HttpStatusCode.BadRequest)
            } else {
                return@timed call.respondRedirect("/tasks?error=title")
            }
        }

        if (title.length > 200) {
            Logger.validationError(session, reqId, "T3_add", "max_length", 0, jsMode)
            if (call.isHtmx()) {
                val status = """<div id="status" hx-swap-oob="true">Title too long (max 200 chars).</div>"""
                return@timed call.respondText(status, ContentType.Text.Html, HttpStatusCode.BadRequest)
            } else {
                return@timed call.respondRedirect("/tasks?error=title&msg=too_long")
            }
        }

        // Success path
        val task = repo.add(title)
        if (call.isHtmx()) {
            val item = PebbleRender.render("tasks/_item.peb", mapOf("t" to task))
            val status = """<div id="status" hx-swap-oob="true">Added "${task.title}".</div>"""
            call.respondText(item + status, ContentType.Text.Html)
        } else {
            call.respondRedirect("/tasks")
        }
    }
}

    /**
     * POST /tasks/{id}/delete - Delete task
     * Dual-mode: HTMX empty response or PRG redirect
     */
    post("/tasks/{id}/delete") {
        val id = call.parameters["id"]?.toIntOrNull()
        val removed = id?.let { TaskRepository.delete(it) } ?: false

        if (call.isHtmx()) {
            val message = if (removed) "Task deleted." else "Could not delete task."
            val status = """<div id="status" hx-swap-oob="true">$message</div>"""
            // Return empty content to trigger outerHTML swap (removes the <li>)
            return@post call.respondText(status, ContentType.Text.Html)
        }

        // No-JS: POST-Redirect-GET pattern (303 See Other)
        call.respondRedirect("/tasks")
    }

    // TODO: Week 7 Lab 1 Activity 2 Steps 2-5
    // Add inline edit routes here
    // Follow instructions in mdbook to implement:
    // - GET /tasks/{id}/edit - Show edit form (dual-mode)
    // - POST /tasks/{id}/edit - Save edits with validation (dual-mode)
    // - GET /tasks/{id}/view - Cancel edit (HTMX only)
}

get("/tasks/{id}/edit") {
    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.NotFound)
    val task = TaskRepository.find(id) ?: return@get call.respond(HttpStatusCode.NotFound)
    val errorParam = call.request.queryParameters["error"]

    val errorMessage = when (errorParam) {
        "blank" -> "Title is required. Please enter at least one character."
        else -> null
    }

    if (call.isHtmx()) {
        // HTMX path: return edit fragment
        val template = pebble.getTemplate("templates/tasks/_edit.peb")
        val model = mapOf("task" to task, "error" to null)
        val writer = StringWriter()
        template.evaluate(writer, model)
        call.respondText(writer.toString(), ContentType.Text.Html)
    } else {
        // No-JS path: full-page render with editingId
        val model = mapOf(
            "title" to "Tasks",
            "tasks" to TaskRepository.all(),
            "editingId" to id,
            "errorMessage" to null
        )
        val template = pebble.getTemplate("templates/tasks/index.peb")
        val writer = StringWriter()
        template.evaluate(writer, model)
        call.respondText(writer.toString(), ContentType.Text.Html)
    }
}

post("/tasks/{id}/edit") {
    val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.NotFound)
    val task = TaskRepository.find(id) ?: return@post call.respond(HttpStatusCode.NotFound)

    val newTitle = call.receiveParameters()["title"].orEmpty().trim()

    // Validation
    if (newTitle.isBlank()) {
        if (call.isHtmx()) {
            // HTMX path: return edit fragment with error
            val template = pebble.getTemplate("templates/tasks/_edit.peb")
            val model = mapOf(
                "task" to task,
                "error" to "Title is required. Please enter at least one character."
            )
            val writer = StringWriter()
            template.evaluate(writer, model)
            return@post call.respondText(writer.toString(), ContentType.Text.Html, HttpStatusCode.BadRequest)
        } else {
            // No-JS path: redirect with error flag
            return@post call.respondRedirect("/tasks/${id}/edit?error=blank")
        }
    }

    // Update task
    task.title = newTitle
    TaskRepository.update(task)

    if (call.isHtmx()) {
        // HTMX path: return view fragment + OOB status
        val viewTemplate = pebble.getTemplate("templates/tasks/_item.peb")
        val viewWriter = StringWriter()
        viewTemplate.evaluate(viewWriter, mapOf("task" to task))

        val status = """<div id="status" hx-swap-oob="true">Task "${task.title}" updated successfully.</div>"""

        return@post call.respondText(viewWriter.toString() + status, ContentType.Text.Html)
    }

    // No-JS path: PRG redirect
    call.respondRedirect("/tasks")
}

get("/tasks/{id}/view") {
    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.NotFound)
    val task = TaskRepository.find(id) ?: return@get call.respond(HttpStatusCode.NotFound)

    // HTMX path only (cancel is just a link to /tasks in no-JS)
    val template = pebble.getTemplate("templates/tasks/_item.peb")
    val model = mapOf("task" to task)
    val writer = StringWriter()
    template.evaluate(writer, model)
    call.respondText(writer.toString(), ContentType.Text.Html)
}

object TaskRepository {
    // ... existing methods ...

    fun find(id: Int): Task? = tasks.find { it.id == id }

    fun update(task: Task) {
        tasks.find { it.id == task.id }?.let { it.title = task.title }
        persist()
    }
}
