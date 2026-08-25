package com.negm.egyptology.debug

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Streams the device logcat into DebugLogger so runtime warnings/crashes from
 * other processes' tags relevant to this app are captured to file as well.
 */
object LogcatCollector {

  private var job: Job? = null

  @JvmStatic
  fun start(scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {
    if (job?.isActive == true) return
    job = scope.launch(Dispatchers.IO) {
      DebugLogger.log("LogcatCollector", "started")
      while (true) {
        try {
          val process = ProcessBuilder("logcat", "-v", "brief")
            .redirectErrorStream(true)
            .start()
          process.inputStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.forEach { line ->
              if (line.contains("FATAL EXCEPTION") ||
                line.contains("AndroidRuntime") ||
                line.contains("com.negm.egyptology")
              ) {
                DebugLogger.log("Logcat", line.take(500))
              }
            }
          }
        } catch (t: Throwable) {
          DebugLogger.log("LogcatCollector", "reader error: ${t.message}")
        }
        // Reader ended (process killed/log rotated); wait briefly and restart.
        Thread.sleep(2000)
      }
    }
  }

  fun stop() {
    job?.cancel()
    job = null
  }
}
