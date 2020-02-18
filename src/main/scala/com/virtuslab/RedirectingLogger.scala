package com.virtuslab

import scala.sys.process.ProcessLogger

class RedirectingLogger(log: String => Unit) extends ProcessLogger {
  override def out(s: => String): Unit = log(s)

  override def err(s: => String): Unit = log(s)

  override def buffer[T](f: => T): T = f
}
