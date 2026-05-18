package com.estocadao

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigTest {
    @Test
    fun readsSupabaseConfigFromEnvironmentFirst() {
        val config = loadSupabaseConfig(
            env = mapOf(
                "SUPABASE_URL" to "https://env.supabase.co",
                "SUPABASE_KEY" to "env-key"
            ),
            dotenv = "SUPABASE_URL=https://file.supabase.co\nSUPABASE_KEY=file-key"
        )

        assertEquals("https://env.supabase.co", config.url)
        assertEquals("env-key", config.key)
    }

    @Test
    fun readsSupabaseConfigFromDotenvWhenEnvironmentIsEmpty() {
        val config = loadSupabaseConfig(
            env = emptyMap(),
            dotenv = "SUPABASE_URL=https://file.supabase.co\nSUPABASE_KEY=file-key"
        )

        assertEquals("https://file.supabase.co", config.url)
        assertEquals("file-key", config.key)
    }

    @Test
    fun findsDotenvInParentDirectory() {
        val tempRoot = createTempDir()
        val serverDir = File(tempRoot, "server").also { it.mkdirs() }
        File(tempRoot, ".env").writeText(
            "SUPABASE_URL=https://parent.supabase.co\nSUPABASE_KEY=parent-key"
        )

        val config = loadSupabaseConfig(env = emptyMap(), currentDir = serverDir)

        assertEquals("https://parent.supabase.co", config.url)
        assertEquals("parent-key", config.key)
    }
}
