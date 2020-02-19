package com.virtuslab.pants

import java.nio.file.Path
import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp4j.WorkspaceBuildTargetsResult
import com.virtuslab.pants.bsp.ForwardingBspServer
import com.virtuslab.pants.cli.Options
import com.virtuslab.pants.log.Logger
import com.virtuslab.pants.log.RedirectingLogger

import scala.sys.process.Process

class PantsBspServer(options: Options, logger: Logger) extends ForwardingBspServer {

  override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {
    logger.log(s"Refreshing pants targets ${options.targets.mkString("[", ", ", "]")} in ${options.workspace}")

    val bloopPants = Seq(
      options.coursier.toAbsolutePath.toString,
      "launch",
      "org.scalameta:metals_2.12:0.8.0+66-2c63fb7c-SNAPSHOT",
      "-r",
      "sonatype:snapshots",
      "--main",
      "scala.meta.internal.pantsbuild.BloopPants"
    )
    val passthroughArgSeparator = Seq("--")
    val args = Seq("--out", options.output.toString) ++ options.targets

    val command = bloopPants ++ passthroughArgSeparator ++ args
    runCmd(command, options.workspace)
    super.workspaceBuildTargets()
  }

  private def runCmd(cmd: Seq[String], cwd: Path): Unit = {
    val cmdString = cmd.mkString(" ")
    logger.log(s"running $cmdString")
    val exitCode = Process(cmd, cwd = cwd.toFile).!(new RedirectingLogger(logger.log))
    if (exitCode != 0) {
      throw new RuntimeException(s"Command $cmdString exited with $exitCode. See logs for more details")
    }
  }
}
