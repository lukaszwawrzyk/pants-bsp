package com.virtuslab.pants

import com.virtuslab.pants.bsp.BspCommunication
import com.virtuslab.pants.bsp.BspServer
import com.virtuslab.pants.bsp.ForwardingBspClient
import com.virtuslab.pants.cli.Options

object Bloop {
  def startBloopServer(options: Options): BspCommunication[BspServer, ForwardingBspClient] = {
    val command = createCommand(options)
    val pb = new ProcessBuilder(command: _*).start()

    val client = new ForwardingBspClient

    BspCommunication
      .prepare[BspServer, ForwardingBspClient](
        localService = client,
        input = pb.getInputStream,
        output = pb.getOutputStream,
        traceLog = options.home.resolve("bloop-trace-log.txt")
      )
  }

  private def createCommand(options: Options): Seq[String] = Seq(
    options.coursier.toAbsolutePath.toString,
    "launch",
    s"ch.epfl.scala:bloop-launcher-core_2.12:${options.bloopVersion}",
    "--",
    options.bloopVersion
  )
}
