package com.virtuslab

object PantsBsp {

  def startPantsServer(
      logger: Logger,
      options: Options
  ): BspCommunication[BspClient, ForwardingBspServer] = {
    val server = new PantsBspServer(options, logger)

    BspCommunication
      .prepare[BspClient, ForwardingBspServer](
        localService = server,
        input = System.in,
        output = System.out,
        traceLog = options.home.resolve("pants-bsp-trace-log.txt")
      )
  }
}
