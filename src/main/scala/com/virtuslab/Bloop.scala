package com.virtuslab

import java.nio.file.Path

object Bloop {
  private val bloopCmd = Seq(
    "coursier",
    "launch",
    "ch.epfl.scala:bloop-launcher-core_2.12:1.4.0-RC1",
    "--",
    "1.4.0-RC1"
  )

  def startBloopServer(home: Path): BspCommunication[BspServer, ForwardingBspClient] = {
    val pb = new ProcessBuilder(bloopCmd: _*).start()

    val client = new ForwardingBspClient

    BspCommunication
      .prepare[BspServer, ForwardingBspClient](
        localService = client,
        input = pb.getInputStream,
        output = pb.getOutputStream,
        traceLog = home.resolve("bloop-trace-log.txt")
      )
  }
}
