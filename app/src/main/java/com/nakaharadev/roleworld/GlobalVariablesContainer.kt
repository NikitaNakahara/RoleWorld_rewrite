package com.nakaharadev.roleworld

object GlobalVariablesContainer {
    private val map = HashMap<String, Any>()

    fun add(key: String, value: Any) {
        map[key] = value
    }

    fun get(key: String): Any? {
        return map[key]
    }
}