package com.virtuslab

import java.nio.file.Path
import java.nio.file.Paths

import metaconfig.Conf
import metaconfig.Configured
import metaconfig.generic
import metaconfig.annotation._
import metaconfig.generic.Settings
import metaconfig.{ConfDecoder, ConfEncoder}

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
)

object Options {
  val default: Options = Options()
  implicit lazy val surface: generic.Surface[Options] = generic.deriveSurface[Options]
  implicit lazy val encoder: ConfEncoder[Options] = generic.deriveEncoder[Options]
  implicit lazy val decoder: ConfDecoder[Options] = generic.deriveDecoder[Options](default)
  implicit lazy val settings: Settings[Options] = Settings[Options]
  implicit val pathEncoder: ConfEncoder[Path] = (value: Path) => Conf.Str(value.toString)
  implicit val pathDecoder: ConfDecoder[Path] = ConfDecoder.stringConfDecoder.map(Paths.get(_))
}
