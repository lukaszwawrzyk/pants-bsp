package com.virtuslab.pants

import com.virtuslab.pants.bsp.BspClient
import com.virtuslab.pants.bsp.BspCommunication
import com.virtuslab.pants.bsp.ForwardingBspServer
import com.virtuslab.pants.cli.Options
import com.virtuslab.pants.log.Logger

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
