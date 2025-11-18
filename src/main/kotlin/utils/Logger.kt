object Logger {
    private val file = File("data/metrics.csv").apply {
        parentFile?.mkdirs()
        if (!exists()) writeText("ts_iso,session_id,request_id,task_code,step,outcome,ms,http_status,js_mode\n")
    }

    @Synchronized
    fun write(session: String, req: String, task: String, step: String, outcome: String, ms: Long, status: Int, js: String) {
        val ts = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        file.appendText("$ts,$session,$req,$task,$step,$outcome,$ms,$status,$js\n")
    }
}
