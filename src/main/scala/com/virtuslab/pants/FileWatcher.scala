package com.virtuslab.pants

import java.nio.file.Path

import ch.epfl.scala.bsp4j.BuildTargetEvent
import ch.epfl.scala.bsp4j.BuildTargetEventKind
import ch.epfl.scala.bsp4j.BuildTargetIdentifier
import ch.epfl.scala.bsp4j.DidChangeBuildTarget
import com.virtuslab.pants.bsp.BspClient
import com.virtuslab.pants.cli.Options
import com.virtuslab.pants.log.Logger
import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryChangeEvent.EventType
import io.methvin.watcher.DirectoryChangeListener
import io.methvin.watcher.DirectoryWatcher
import io.methvin.watcher.hashing.FileHasher
import org.slf4j.helpers.NOPLogger

import scala.jdk.CollectionConverters._

object FileWatcher {

  private var initialized = false
  private var options: Options = _
  private var logger: Logger = _
  private var client: BspClient = _

  def ensureBloopUpToDate(): Unit = {
    if (!initialized) {
      bloopRefresh()
      initialized = true
    }
  }

  private var watcher: DirectoryWatcher = _

  def start(options: Options, logger: Logger, client: BspClient): Unit = {
    this.options = options
    this.logger = logger
    this.client = client
    watcher = DirectoryWatcher
      .builder()
      .path(options.workspace)
      .fileHasher(FileHasher.LAST_MODIFIED_TIME)
      .logger(NOPLogger.NOP_LOGGER)
      .listener(listener)
      .build()
    watcher.watchAsync()
  }

  def stop(): Unit = {
    Option(watcher).foreach(_.close())
  }

  private def listener: DirectoryChangeListener = (event: DirectoryChangeEvent) => {
    val path = event.path
    if (path.getFileName.toString.equals("BUILD")) {
      event.eventType match {
        case EventType.CREATE =>
          onEvent(path, BuildTargetEventKind.CREATED)
        case EventType.DELETE =>
          onEvent(path, BuildTargetEventKind.DELETED)
        case EventType.MODIFY =>
          onEvent(path, BuildTargetEventKind.CHANGED)
      }
    }
  }

  private def onEvent(path: Path, kind: BuildTargetEventKind): Unit = {
    bloopRefresh()
    // TODO compute actual changed targets, current implementation is OK for intellij
    //  as it doesn't check the content of this event at all
    val uri = path.toUri.toString
    val event = new BuildTargetEvent(new BuildTargetIdentifier(uri))
    event.setKind(kind)
    client.onBuildTargetDidChange(new DidChangeBuildTarget(Seq(event).asJava))
  }

  private def bloopRefresh(): Unit = {
    new BloopRefresh(options, logger).run()
  }
}
