package com.negm.egyptology

import android.app.Application
import android.util.Log
import com.negm.egyptology.debug.DebugLogger
import com.negm.egyptology.debug.LogcatCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class EgyptologyApplication : Application() {

  private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun onCreate() {
    super.onCreate()

    DebugLogger.init(this)
    LogcatCollector.start(appScope)

    val prevHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      Log.e("CRASH", "Uncaught exception on thread: ${thread.name}", throwable)
      DebugLogger.log("CRASH", "Uncaught exception on thread: ${thread.name}: ${throwable}")
      DebugLogger.log("CRASH", "${throwable.javaClass.name}: ${throwable.localizedMessage}")
      for (element in throwable.stackTrace) {
        DebugLogger.log(
          "CRASH",
          "  at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
        )
      }
      throwable.cause?.let { cause ->
        DebugLogger.log("CRASH", "Caused by: ${cause.javaClass.name}: ${cause.localizedMessage}")
        for (element in cause.stackTrace) {
          DebugLogger.log(
            "CRASH",
            "  at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
          )
        }
      }
      if (prevHandler != null) {
        prevHandler.uncaughtException(thread, throwable)
      } else {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
      }
    }

    DebugLogger.log("EgyptologyApp", "Application onCreate complete")
  }
}
