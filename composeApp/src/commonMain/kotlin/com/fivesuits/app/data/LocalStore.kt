package com.fivesuits.app.data

/** Small platform-backed key/value store; the game itself remains shared Kotlin. */
expect class LocalStore() {
    fun read(key: String): String?
    fun write(key: String, value: String)
    fun remove(key: String)
}
