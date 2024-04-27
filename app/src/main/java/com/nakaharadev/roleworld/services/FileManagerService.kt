package com.nakaharadev.roleworld.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.nakaharadev.roleworld.file_tasks.AbstractFileTask
import com.nakaharadev.roleworld.file_tasks.AbstractReadFileTask
import java.io.File
import java.util.LinkedList

class FileManagerService : Service() {
    private var stopTasksLoop = true

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTasksLoop()

        return START_STICKY
    }

    override fun onDestroy() {
        stopTasksLoop = true
    }

    private fun startTasksLoop() {
        if (stopTasksLoop) {
            stopTasksLoop = false
        } else return

        Thread {
            while (!stopTasksLoop) {
                while (!tasks.isEmpty()) {
                    val task = tasks.poll()
                    if (task?.taskType == "read") {
                        task as AbstractReadFileTask
                        Log.i("FileManagerService", "Read: ${task.read()}")
                    } else if (task?.taskType == "save") {
                        Log.i("FileManagerService", "Save: ${task.save()}")
                    }
                }
            }
        }.start()
    }

    companion object {
        private val tasks = LinkedList<AbstractFileTask>()

        fun addTask(task: AbstractFileTask) {
            tasks.add(task)
        }

        fun addReadTask(task: AbstractReadFileTask, callback: (Any?) -> Unit) {
            task.callback = callback
            tasks.add(task)
        }
    }
}