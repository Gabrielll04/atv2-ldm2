package com.estocadao

import kotlinx.serialization.Serializable
import java.io.File

data class SupabaseConfig(
    val url: String,
    val key: String
)

@Serializable
data class ErrorResponse(
    val error: String
)

fun loadSupabaseConfig(
    env: Map<String, String> = System.getenv(),
    currentDir: File = File(System.getProperty("user.dir")),
    dotenv: String = findDotenv(currentDir)?.readText().orEmpty()
): SupabaseConfig {
    val fileValues = parseDotenv(dotenv)

    return SupabaseConfig(
        url = env["SUPABASE_URL"] ?: fileValues["SUPABASE_URL"].orEmpty(),
        key = env["SUPABASE_KEY"] ?: fileValues["SUPABASE_KEY"].orEmpty()
    )
}

private fun findDotenv(startDir: File): File? {
    var dir: File? = startDir.absoluteFile

    while (dir != null) {
        val dotenv = File(dir, ".env")
        if (dotenv.exists()) return dotenv
        dir = dir.parentFile
    }

    return null
}

private fun parseDotenv(content: String): Map<String, String> =
    content
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
        .associate { line ->
            val key = line.substringBefore("=").trim()
            val value = line.substringAfter("=").trim().trim('"')
            key to value
        }
