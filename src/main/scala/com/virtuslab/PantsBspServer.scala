package com.virtuslab
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp4j.WorkspaceBuildTargetsResult

import scala.sys.process.Process

case class Options(
    workspace: Path,
    output: Path,
    targets: Seq[String]
)

class PantsBspServer(options: Options) extends ForwardingBspServer {

  override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {
    println(s"Refreshing pants projects ${options.targets} in ${options.workspace}")
    val command = Seq(
      "coursier",
      "launch",
      "org.scalameta:metals_2.12:0.8.0+66-2c63fb7c-SNAPSHOT",
      "-r",
      "sonatype:snapshots",
      "--main",
      "scala.meta.internal.pantsbuild.BloopPants",
      "--",
      s"--out",
      options.output.toString
    ) ++ options.targets
    runCmd(command, options.workspace)
    super.workspaceBuildTargets()
  }

  private def runCmd(cmd: Seq[String], cwd: Path): Unit = {
    val cmdString = cmd.mkString(" ")
    println(s"running $cmdString")
    val exitCode = Process(cmd, cwd = cwd.toFile).!(StdoutLogger)
    if (exitCode != 0) {
      throw new RuntimeException(s"Command $cmdString exited with $exitCode. See logs for more details")
    }
  }
}
