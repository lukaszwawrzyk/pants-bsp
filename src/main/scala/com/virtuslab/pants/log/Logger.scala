package com.virtuslab.pants.log

import java.io.PrintStream

class Logger(bspWindow: PrintStream, logFile: PrintStream) {

  def exception(e: Throwable): Unit = {
    e.printStackTrace(bspWindow)
    e.printStackTrace(logFile)
  }

  def log(message: String): Unit = {
    bspWindow.println(message)
    verbose(message)
  }

  def verbose(message: String): Unit = {
    logFile.println(logFile)
  }

}
