package com.virtuslab

object Bloop {
  def startBloopServer(options: Options): BspCommunication[BspServer, ForwardingBspClient] = {
    val command = createCommand(options.bloopVersion)
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

  private def createCommand(version: String) = Seq(
    "coursier",
    "launch",
    s"ch.epfl.scala:bloop-launcher-core_2.12:$version",
    "--",
    version
  )
}
