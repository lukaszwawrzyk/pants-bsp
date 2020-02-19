package com.virtuslab.pants.cli

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

import ch.epfl.scala.bsp4j.BspConnectionDetails
import com.google.gson.GsonBuilder
import com.virtuslab.pants.BuildInfo
import metaconfig.cli.CliApp
import metaconfig.cli.Command
import metaconfig.cli.Messages
import org.typelevel.paiges.Doc

import scala.jdk.CollectionConverters._

object GenerateCommand extends Command[Options]("generate") {
  override def description: Doc =
    Doc.paragraph("Setup .bsp directory in specified output directory")

  override def options: Doc = Messages.options(Options.default)

  override def run(options: Options, app: CliApp): Int = {
    installCoursier(options)
    val args = bspProcessArgs(options)
    val languages = Seq("java", "scala")
    val details = bspConnectionDetails(args, languages)
    val json = toJson(details)
    val path = options.bspRoot.resolve("pants-bsp.json")
    write(path, json)
    0
  }

  private def bspConnectionDetails(args: Seq[String], languages: Seq[String]) = {
    new BspConnectionDetails(
      "Pants-BSP",
      args.asJava,
      BuildInfo.version,
      BuildInfo.bspVersion,
      languages.asJava
    )
  }

  private def write(path: Path, json: String) = {
    Files.createDirectories(path.getParent)
    Files.write(path, json.getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  private def bspProcessArgs(options: Options) = {
    val runPantsBsp = Seq("java", "-cp", sys.props("java.class.path"), Main.getClass.getName.stripSuffix("$"))
    val command = Seq("server")
    val commandArgs = prepareCommandArgs(options)
    runPantsBsp ++ command ++ commandArgs
  }

  private def prepareCommandArgs(options: Options) = {
    def arg[A](value: Options => A, param: String, format: A => String = (_: A).toString): Seq[String] = {
      val actual = value(options)
      val default = value(Options.default)
      if (actual == default) {
        Nil
      } else {
        Seq(param, format(actual))
      }
    }

    Seq(
      arg[Seq[String]](_.targets, "--targets", _.mkString(" ")),
      arg[Path](_.workspace, "--workspace"),
      arg[Path](_.output, "--output"),
      arg[String](_.bloopVersion, "--bloopVersion"),
      arg[Path](_.home, "--home"),
    ).flatten
  }

  private def toJson(details: BspConnectionDetails) = {
    (new GsonBuilder).setPrettyPrinting().create().toJson(details)
  }

  private def installCoursier(options: Options) = {
    val coursierFile = options.coursier
    if (!Files.exists(coursierFile)) {
      val url = new URL("https://git.io/coursier-cli")
      val stream = url.openConnection().getInputStream
      try Files.copy(stream, coursierFile)
      finally stream.close()
      coursierFile.toFile.setExecutable(true)
    }
  }
}
