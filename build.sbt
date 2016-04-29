import Dependencies._

lazy val mavenResolverPluginProj = (project in file(".")).
  settings(
    // baseSettings,
    // sbtBinaryVersion := "1.0.0-SNAPSHOT",
    name := "sbt-maven-resolver",
    libraryDependencies ++= aetherLibs ++ Seq(utilTesting % Test, (libraryManagement % Test).classifier("tests"), libraryManagement % Test)
    // sbtPlugin := true
  )
