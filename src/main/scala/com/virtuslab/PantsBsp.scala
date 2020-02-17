package com.virtuslab

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path

object PantsBsp {

  def startPantsServer(
      stdin: InputStream,
      stdout: OutputStream,
      home: Path,
      options: Options
  ): BspCommunication[BspClient, ForwardingBspServer] = {
    val server = new PantsBspServer(options)

    BspCommunication
      .prepare[BspClient, ForwardingBspServer](
        localService = server,
        input = stdin,
        output = stdout,
        traceLog = home.resolve("pants-bsp-trace-log.txt")
      )
  }
}
