package com.virtuslab

import metaconfig.cli._

object Main {
  private val app = CliApp(
    version = "0.1",
    binaryName = "pants-bsp",
    commands = List(
      ServerCommand,
      HelpCommand,
      VersionCommand
    )
  )

  def main(args: Array[String]): Unit = {
    val exitCode = app.run(args.toList)
    sys.exit(exitCode)
  }

}
