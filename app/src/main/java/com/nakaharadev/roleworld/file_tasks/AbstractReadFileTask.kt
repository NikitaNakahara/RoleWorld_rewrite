package com.nakaharadev.roleworld.file_tasks

abstract class AbstractReadFileTask : AbstractFileTask() {
    override val taskType = "read"

    /**
     * callback for async read file.
     * Call when file is read
     *
     * @param data data from file
     */
    abstract var callback: (data: Any?) -> Unit

    /**
     * Read file
     * @return file data
     */
    abstract fun read(): String?

    /**
     * Method isn`t used
     */
    override fun save(): String? { return null }
}