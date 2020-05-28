import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.scala-sbt"
ThisBuild / description := "sbt is an interactive build tool"
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-maven-resolver"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/sbt/sbt-maven-resolver"), "git@github.com:sbt/sbt-maven-resolver.git"))
ThisBuild / developers := List(
  Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n")),
  Developer("jsuereth", "Josh Suereth", "@jsuereth", url("https://github.com/jsuereth")),
)

lazy val plugin = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-maven-resolver",
    libraryDependencies ++= deps ++ depsTest,
    startYear := Some(2020),
    bintrayRepository := "sbt-plugin-releases",
    bintrayOrganization := Some("sbt"),
    crossSbtVersions := List("1.3.0"),
    scalacOptions ++= Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature", "-Xlint"),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    // don't do any API docs
    doc / sources := Seq(),
    packageDoc / publishArtifact := false,
    testFrameworks += new TestFramework("verify.runner.Framework"),
  )
