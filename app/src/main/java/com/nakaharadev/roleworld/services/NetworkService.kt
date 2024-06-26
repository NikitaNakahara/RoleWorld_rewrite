package com.nakaharadev.roleworld.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.tasks.AbstractTask
import java.util.LinkedList

class NetworkService : Service() {
    private var stopPingLoop = true
    private var stopTasksLoop = true

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPingLoop()
        startTasksLoop()

        return START_STICKY
    }

    override fun onDestroy() {
        stopPingLoop = true
        stopTasksLoop = true
    }

    private fun startTasksLoop() {
        if (stopTasksLoop) {
            stopTasksLoop = false
        } else return

        Thread {
            while (!stopTasksLoop) {
                while (!tasks.isEmpty()) {
                    try {
                        tasks.poll()?.task()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    private fun startPingLoop() {
        if (stopPingLoop) {
            stopPingLoop = false
        } else return

        Thread {
            while (!stopPingLoop) {


                Thread.sleep(5000)
            }
        }.start()
    }

    companion object {
        private val tasks = LinkedList<AbstractTask>()

        fun addTask(task: AbstractTask, callback: (AbstractResponse) -> Unit) {
            task.callback = callback
            tasks.add(task)
        }
    }
}