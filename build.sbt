import Dependencies._

ThisBuild / version := "0.1.1-SNAPSHOT"

lazy val scala212 = "2.12.15"
ThisBuild / crossScalaVersions := Seq(scala212)
ThisBuild / scalaVersion := scala212

lazy val plugin = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-maven-resolver",
    libraryDependencies ++= deps ++ depsTest,
    (pluginCrossBuild / sbtVersion) := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.5.8"
        case _      => "2.0.0-M4"
      }
    },
    startYear := Some(2020),
    scalacOptions ++= Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature", "-Xlint"),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    // don't do any API docs
    doc / sources := Seq(),
    testFrameworks += new TestFramework("verify.runner.Framework"),
  )

ThisBuild / pomIncludeRepository := { _ =>
  false
}
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
ThisBuild / organization := "org.scala-sbt"
ThisBuild / description := "sbt-maven-resolver is a plugin to resolve dependencies using Aether"
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-maven-resolver"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/sbt/sbt-maven-resolver"), "git@github.com:sbt/sbt-maven-resolver.git"))
ThisBuild / developers := List(
  Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n")),
  Developer("jsuereth", "Josh Suereth", "@jsuereth", url("https://github.com/jsuereth")),
)
