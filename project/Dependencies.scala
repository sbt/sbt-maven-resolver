import sbt._
import Keys._

object Dependencies {
  val utilVersion = "0.1.0-M8"
  val librarymanagementVersion = "0.1.0-M7"
  lazy val utilTesting = "org.scala-sbt" %% "util-testing" % utilVersion
  lazy val libraryManagement = "org.scala-sbt" %% "librarymanagement" % librarymanagementVersion

  // Maven related dependnecy craziness
  //val mvnEmbedder = "org.apache.maven" % "maven-embedder" % mvnVersion
  val mvnWagonVersion = "2.4"
  val mvnVersion = "3.2.3"
  val aetherVersion = "1.0.1.v20141111"

  val mvnAether = "org.apache.maven" % "maven-aether-provider" % mvnVersion
  val aether = "org.eclipse.aether" % "aether" % aetherVersion
  val aetherImpl = "org.eclipse.aether" % "aether-impl" % aetherVersion
  val aetherUtil = "org.eclipse.aether" % "aether-util" % aetherVersion
  val aetherTransportFile = "org.eclipse.aether" % "aether-transport-file" % aetherVersion
  val aetherTransportWagon = "org.eclipse.aether" % "aether-transport-wagon" % aetherVersion
  val aetherTransportHttp = "org.eclipse.aether" % "aether-transport-http" % aetherVersion
  val aetherConnectorBasic = "org.eclipse.aether" % "aether-connector-basic" % aetherVersion
  val sisuPlexus = ("org.eclipse.sisu" % "org.eclipse.sisu.plexus" % "0.3.0.M1").exclude("javax.enterprise", "cdi-api").exclude("com.google.code.findbugs", "jsr305")
  val guice = "com.google.inject" % "guice" % "3.0"
  val guava = "com.google.guava" % "guava" % "18.0"
  val javaxInject = "javax.inject" % "javax.inject" % "1"

  //val sisuGuice = ("org.eclipse.sisu" % "sisu-guice" % "3.1.0").classifier("no_aop").exclude("javax.enterprise", "cdi-api", )

  /*
  val mvnWagon = "org.apache.maven.wagon" % "wagon-http" % mvnWagonVersion
  val mvnWagonProviderApi = "org.apache.maven.wagon" % "wagon-provider-api" % mvnWagonVersion
  val mvnWagonLwHttp = "org.apache.maven.wagon" % "wagon-http-lightweight" % mvnWagonVersion
  val mvnWagonFile = "org.apache.maven.wagon" % "wagon-file" % mvnWagonVersion
  */
  def aetherLibs =
    Seq(
      guava,
      javaxInject,
      sisuPlexus,
      aetherImpl,
      aetherConnectorBasic,
      mvnAether)
}
