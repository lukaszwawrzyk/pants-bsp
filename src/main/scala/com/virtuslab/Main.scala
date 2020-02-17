package com.virtuslab

import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import scala.util.control.NonFatal

object Main {
  def main(args: Array[String]): Unit = {
    val options = Options(
      workspace = Paths.get(args(0)),
      output = Paths.get(args(1)),
      targets = args.drop(2)
    )

    val stdin = System.in
    val stdout = System.out

    val homePath = Paths.get(".pants-bsp").toAbsolutePath
    Files.createDirectories(homePath)
    val logPath = homePath.resolve("log.txt")
    val logStream = stream(logPath)
    System.setOut(logStream)
    System.setErr(logStream)

    try {
      val bloop = Bloop.startBloopServer(homePath)
      val pantsBsp = PantsBsp.startPantsServer(stdin, stdout, homePath, options)
      bloop.localService.target = pantsBsp.remoteService
      pantsBsp.localService.target = bloop.remoteService

      val runningBloop = bloop.start()
      val runningPantsBsp = pantsBsp.start()
      runningBloop.get()
      runningPantsBsp.get()
      bloop.close()
      pantsBsp.close()
    } catch {
      case NonFatal(e) =>
        e.printStackTrace(logStream)
        e.printStackTrace(stdout)
        sys.exit(1)
    }

  }

  private def stream(logPath: Path) = {
    new PrintStream(
      Files
        .newOutputStream(logPath, CREATE, TRUNCATE_EXISTING)
    )
  }
}
