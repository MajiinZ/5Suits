package com.fivesuits.app.data

import platform.Foundation.NSUserDefaults

actual class LocalStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    actual fun read(key: String): String? = defaults.stringForKey("five_suits.$key")
    actual fun write(key: String, value: String) {
        defaults.setObject(value, forKey = "five_suits.$key")
    }
    actual fun remove(key: String) {
        defaults.removeObjectForKey("five_suits.$key")
    }
}
