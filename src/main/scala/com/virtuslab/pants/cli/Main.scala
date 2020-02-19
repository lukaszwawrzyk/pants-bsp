package com.virtuslab.pants.cli

import com.virtuslab.pants.BuildInfo

import metaconfig.cli._

object Main {
  private val app = CliApp(
    version = BuildInfo.version,
    binaryName = "pants-bsp",
    out = System.err,
    commands = List(
      ServerCommand,
      GenerateCommand,
      HelpCommand,
      VersionCommand
    )
  )

  def main(args: Array[String]): Unit = {
    val exitCode = app.run(args.toList)
    sys.exit(exitCode)
  }

}
