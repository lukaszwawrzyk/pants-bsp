package com.virtuslab.pants.cli

import java.nio.file.Path
import java.nio.file.Paths

import metaconfig.Conf
import metaconfig.ConfDecoder
import metaconfig.ConfEncoder
import metaconfig.annotation._
import metaconfig.generic
import metaconfig.generic.Settings

case class Options(
    @Description("List of pants targets to export to bsp")
    @ExtraName("remainingArgs")
    targets: Seq[String] = Seq("/::"),
    @Description("Path to the root of pants repository")
    @ExtraName("w")
    workspace: Path = Paths.get("."),
    @Description(
      "Path to output directory for generated bloop metadata. " +
        "The directory that should contain '.bloop' directory")
    @ExtraName("o")
    output: Path = Paths.get("."),
    @Description("Version of bloop to start")
    bloopVersion: String = "1.4.0-RC1",
    @Description("Pants bsp home directory - for logs.")
    @ExtraName("home")
    home: Path = Paths.get(".pants-bsp")
) {
  def bloopDir: Path = output.resolve(".bloop")
  def bspRoot: Path = output.resolve(".bsp")
  def coursier: Path = bspRoot.resolve("coursier")
}

object Options {
  val default: Options = Options()
  implicit lazy val surface: generic.Surface[Options] = generic.deriveSurface[Options]
  implicit lazy val encoder: ConfEncoder[Options] = generic.deriveEncoder[Options]
  implicit lazy val decoder: ConfDecoder[Options] = generic.deriveDecoder[Options](default)
  implicit lazy val settings: Settings[Options] = Settings[Options]
  implicit val pathEncoder: ConfEncoder[Path] = (value: Path) => Conf.Str(value.toString)
  implicit val pathDecoder: ConfDecoder[Path] = ConfDecoder.stringConfDecoder.map(Paths.get(_))
}
