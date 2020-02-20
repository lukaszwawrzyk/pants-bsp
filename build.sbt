name := "pants-bsp"

version := "0.1"

scalaVersion := "2.12.10"

val bspVersion = "2.0.0-M5"

libraryDependencies ++= Seq(
  "ch.epfl.scala" % "bsp4j" % bspVersion,
  "com.geirsson" %% "metaconfig-core" % "0.9.8",
  "io.methvin" % "directory-watcher" % "0.9.9"
)

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version, "bspVersion" -> bspVersion)
buildInfoPackage := "com.virtuslab.pants"
