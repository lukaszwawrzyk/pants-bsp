package com.virtuslab

import scala.sys.process.ProcessLogger

object StdoutLogger extends ProcessLogger {
  override def out(s: => String): Unit = println(s)
  override def err(s: => String): Unit = println(s)
  override def buffer[T](f: => T): T = f
}
