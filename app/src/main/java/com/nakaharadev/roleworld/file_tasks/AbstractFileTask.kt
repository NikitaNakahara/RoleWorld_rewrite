package com.nakaharadev.roleworld.file_tasks

import java.io.File

abstract class AbstractFileTask {
    open val taskType = "save"

    /**
     * Save file
     */
    abstract fun save(): String?
}