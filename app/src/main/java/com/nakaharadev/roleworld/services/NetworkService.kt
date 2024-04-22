package com.nakaharadev.roleworld.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.Queue

class NetworkService : Service() {
    private var stopPingLoop = false
    private var stopTasksLoop = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPingLoop()

        return START_STICKY
    }

    override fun onDestroy() {
        stopPingLoop = true
        stopTasksLoop
    }

    private fun startTasksLoop() {
        Thread {
            while (!stopTasksLoop) {

            }
        }.start()
    }

    private fun startPingLoop() {
        Thread {
            while (!stopPingLoop) {


                Thread.sleep(5000)
            }
        }.start()
    }

    companion object {
        fun addTask(task: Task) {

        }
    }

    class Task {
        var taskName = ""

        fun task() {

        }
    }
}