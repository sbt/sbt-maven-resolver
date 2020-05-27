import Dependencies._

lazy val plugin = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    version := "0.1.0",
    organization := "org.scala-sbt",
    name := "sbt-maven-resolver",
    homepage := Some(url("https://github.com/sbt/sbt-maven-resolver")),
    libraryDependencies ++= deps ++ depsTest,
    startYear := Some(2020),
    bintrayRepository := "sbt-plugin-releases",
    bintrayOrganization := Some("sbt"),
    crossSbtVersions := List("1.3.0"),
    scalacOptions ++= Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature"),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    // don't do any API docs
    doc / sources := Seq(),
    packageDoc / publishArtifact := false
  )
  .enablePlugins(SbtPlugin)
