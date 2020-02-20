package com.virtuslab.pants.cli

import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING

import com.virtuslab.pants.Bloop
import com.virtuslab.pants.FileWatcher
import com.virtuslab.pants.PantsBsp
import com.virtuslab.pants.log.Logger
import metaconfig.cli.CliApp
import metaconfig.cli.Command
import metaconfig.cli.Messages
import org.typelevel.paiges.Doc

import scala.util.control.NonFatal

object ServerCommand extends Command[Options]("server") {

  override def description: Doc =
    Doc.paragraph("Start a new pants-bsp server that also starts bloop and communicates with it")

  override def options: Doc = Messages.options(Options.default)

  override def run(options: Options, app: CliApp): Int = {
    val logger = setupLogging(options)

    try {
      startServers(options, logger)
      0
    } catch {
      case NonFatal(e) =>
        logger.exception(e)
        1
    }
  }

  private def setupLogging(options: Options): Logger = {
    Files.createDirectories(options.home)
    val logPath = options.home.resolve("log.txt")
    val logStream = new PrintStream(
      Files
        .newOutputStream(logPath, CREATE, TRUNCATE_EXISTING)
    )
    new Logger(System.err, logStream)
  }

  private def startServers(options: Options, logger: Logger): Unit = {
    val bloop = Bloop.startBloopServer(options)
    val pantsBsp = PantsBsp.startPantsServer(options)
    bloop.localService.target = pantsBsp.remoteService
    pantsBsp.localService.target = bloop.remoteService

    FileWatcher.start(options, logger, pantsBsp.remoteService)

    val runningBloop = bloop.start()
    val runningPantsBsp = pantsBsp.start()

    runningBloop.get()
    runningPantsBsp.get()
    bloop.close()
    pantsBsp.close()
    FileWatcher.stop()
  }

}
