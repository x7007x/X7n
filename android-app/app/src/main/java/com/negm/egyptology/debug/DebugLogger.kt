package com.negm.egyptology.debug

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Minimal file logger. Appends timestamped lines to a rotating debug log file
 * inside the app-private storage so crashes and events survive process death.
 */
object DebugLogger {

  private const val TAG = "DebugLogger"
  private const val MAX_FILE_BYTES = 512L * 1024L

  private var logFile: File? = null
  private val lock = Any()
  private val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)

  fun init(context: Context) {
    synchronized(lock) {
      try {
        val dir = File(context.filesDir, "logs").apply { mkdirs() }
        logFile = File(dir, "debug.log")
      } catch (t: Throwable) {
        Log.e(TAG, "init failed", t)
      }
    }
    log("DebugLogger", "initialized")
  }

  fun log(tag: String, message: String) {
    val line = "${timeFormat.format(Date())} [$tag] $message\n"
    Log.d(tag, message)
    synchronized(lock) {
      try {
        val file = logFile ?: return
        if (file.exists() && file.length() > MAX_FILE_BYTES) {
          // Simple rotation: keep the previous file only.
          val old = File(file.parentFile, "debug.1.log")
          old.delete()
          file.renameTo(old)
        }
        file.appendText(line)
      } catch (t: Throwable) {
        Log.e(TAG, "write failed", t)
      }
    }
  }
}
