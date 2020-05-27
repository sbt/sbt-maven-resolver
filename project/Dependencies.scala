import sbt._
import sbt.Keys._

object Dependencies {
  resolvers += "bintray-sbt-maven-releases" at "https://dl.bintray.com/sbt/maven-releases/"

  val libraryManagementVersion = "1.3.2"

  lazy val depsTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "org.scala-sbt" %% "util-testing" % "1.1.3"
  )

  val mvnVersion = "3.3.9"
  val aetherVersion = "1.1.0"

  lazy val deps: Seq[ModuleID] = Seq(
    "org.scala-sbt" %% "librarymanagement-core" % libraryManagementVersion,
    "org.scala-sbt" %% "librarymanagement-ivy" % libraryManagementVersion,

    "org.apache.maven" % "maven-aether-provider" % mvnVersion,
    "org.eclipse.aether" % "aether" % aetherVersion,
    "org.eclipse.aether" % "aether-impl" % aetherVersion,
    "org.eclipse.aether" % "aether-util" % aetherVersion,
    "org.eclipse.aether" % "aether-transport-file" % aetherVersion,
    "org.eclipse.aether" % "aether-transport-wagon" % aetherVersion,
    "org.eclipse.aether" % "aether-transport-http" % aetherVersion,
    "org.eclipse.aether" % "aether-connector-basic" % aetherVersion,

    "org.eclipse.sisu" % "org.eclipse.sisu.plexus" % "0.3.4" excludeAll(
      ExclusionRule("javax.enterprise", "cdi-api"),
      ExclusionRule("com.google.code.findbugs", "jsr305")
    ),
    "com.google.inject" % "guice" % "4.2.3",
    "com.google.guava" % "guava" % "19.0",
    "javax.inject" % "javax.inject" % "1",
    "org.apache.ivy" % "ivy" % "2.5.0",
    "org.scala-sbt" %% "io" % "1.4.0-M6"
  )
}