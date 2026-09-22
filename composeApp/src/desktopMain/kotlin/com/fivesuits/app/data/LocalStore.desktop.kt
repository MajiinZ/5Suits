package com.fivesuits.app.data

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/** File storage avoids the small value limit of java.util.prefs for saved games. */
actual class LocalStore {
    private val directory: Path = Path.of(System.getProperty("user.home"), ".five-suits")

    private fun path(key: String): Path {
        require(key.matches(Regex("[a-zA-Z0-9_.-]+"))) { "Invalid storage key" }
        return directory.resolve("$key.json")
    }

    actual fun read(key: String): String? = path(key).let {
        if (Files.exists(it)) Files.readString(it) else null
    }

    actual fun write(key: String, value: String) {
        Files.createDirectories(directory)
        val destination = path(key)
        val temporary = directory.resolve("$key.tmp")
        Files.writeString(temporary, value)
        Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING)
    }

    actual fun remove(key: String) {
        Files.deleteIfExists(path(key))
    }
}
