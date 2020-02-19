package com.virtuslab.pants.bsp

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption._
import java.util.concurrent.Executors
import java.util.concurrent.Future

import org.eclipse.lsp4j.jsonrpc.Launcher

import scala.reflect.ClassTag

case class BspCommunication[Remote, Local](
    closeables: Seq[() => Unit],
    launcher: Launcher[Remote],
    localService: Local
) {

  def remoteService: Remote = launcher.getRemoteProxy

  def start(): Future[Void] = {
    launcher.startListening()
  }

  def close(): Unit = {
    closeables.foreach(_.apply())
  }

}

object BspCommunication {

  def prepare[Remote, Local](
      localService: Local,
      input: InputStream,
      output: OutputStream,
      traceLog: Path
  )(implicit ct: ClassTag[Remote]): BspCommunication[Remote, Local] = {
    val traceWriter = new PrintWriter(stream(traceLog))

    val executorService = Executors.newCachedThreadPool()

    val launcher = new Launcher.Builder[Remote]()
      .setRemoteInterface(ct.runtimeClass.asInstanceOf[Class[Remote]])
      .setLocalService(localService)
      .setExecutorService(executorService)
      .setInput(input)
      .setOutput(output)
      .traceMessages(traceWriter)
      .create()

    val closeables = Seq[() => Unit](
      () => input.close(),
      () => output.close(),
      () => traceWriter.close(),
      () => executorService.shutdown()
    )

    BspCommunication[Remote, Local](closeables, launcher, localService)
  }

  private def stream(path: Path) = {
    Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING)
  }
}
