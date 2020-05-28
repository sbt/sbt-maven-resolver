package testpkg

import sbt.internal.librarymanagement.BaseIvySpecification
import sbt.internal.librarymanagement.mavenint.PomExtraDependencyAttributes
import sbt.librarymanagement._
import Configurations.{ Compile, ScalaTool, Test }
import sbt.librarymanagement.ivy._
import sbtmavenresolver.MavenResolverConverter
import verify._

object MavenResolutionSpec extends BasicTestSuite with BaseIvySpecification {

  test("the maven resolution should handle sbt plugins") {
    def sha(f: java.io.File): String = sbt.io.Hash.toHex(sbt.io.Hash(f))
    def findSbtIdeaJars(dep: ModuleID, name: String) = {
      val m = module(
        ModuleID("com.example", name, "0.1.0").withConfigurations(Some("compile")),
        Vector(dep),
        None,
        defaultUpdateOptions
      )
      val report = ivyUpdate(m)
      for {
        conf <- report.configurations if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if (m.module.name contains "sbt-idea")
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield (f, sha(f))
    }

    val oldJars = findSbtIdeaJars(oldSbtPlugin, "old")
    System.err.println(s"${oldJars.mkString("\n")}")
    val newJars = findSbtIdeaJars(sbtPlugin, "new")
    System.err.println(s"${newJars.mkString("\n")}")

    assert(newJars.size == 1)
    assert(oldJars.size == 1)
    assert(oldJars.map(_._2) != newJars.map(_._2))
  }

  test("use ivy for conflict resolution") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(majorConflictLib),
      None,
      defaultUpdateOptions
    )
    val report = ivyUpdate(m) // should not(throwAn[IllegalStateException])
    val jars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if (m.module.name contains "stringtemplate")
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f
    assert(jars.size == 1)
  }

  test("handle cross configuration deps") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(scalaCompiler, scalaContinuationPlugin),
      None,
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val jars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(ScalaTool.name)
        m <- conf.modules
        if (m.module.name contains "scala-compiler")
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f
    assert(jars.size == 1)
  }

  test("publish with maven-metadata") {
    val m = module(
      ModuleID("com.example", "test-it", "1.0-SNAPSHOT").withConfigurations(Some("compile")),
      Vector(),
      None,
      defaultUpdateOptions.withLatestSnapshots(true)
    )
    sbt.io.IO.withTemporaryDirectory { dir =>
      val pomFile = new java.io.File(dir, "pom.xml")
      sbt.io.IO.write(
        pomFile,
        """
          |<project>
          |   <groupId>com.example</groupId>
          |   <name>test-it</name>
          |   <version>1.0-SNAPSHOT</version>
          |</project>
        """.stripMargin
      )
      val jarFile = new java.io.File(dir, "test-it-1.0-SNAPSHOT.jar")
      sbt.io.IO.touch(jarFile)
      System.err.println(s"DEBUGME - Publishing $m to ${Resolver.publishMavenLocal}")
      ivyPublish(
        m,
        mkPublishConfiguration(
          Resolver.publishMavenLocal,
          Map(
            Artifact("test-it-1.0-SNAPSHOT.jar") -> pomFile,
            Artifact("test-it-1.0-SNAPSHOT.pom", "pom", "pom") -> jarFile
          )
        )
      )
    }
    val baseLocalMavenDir: java.io.File = Resolver.publishMavenLocal.rootFile
    val allFiles: Seq[java.io.File] =
      sbt.io.PathFinder(new java.io.File(baseLocalMavenDir, "com/example/test-it")).allPaths.get
    val metadataFiles = allFiles.filter(_.getName contains "maven-metadata-local")
    // TODO - maybe we check INSIDE the metadata, or make sure we can get a publication date on resolve...
    // We end up with 4 files, two mavne-metadata files, and 2 maven-metadata-local files.
    assert(metadataFiles.size == 2)
  }

  test("resolve transitive maven dependencies") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(akkaActor),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val jars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if m.module.name == "scala-library"
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f

    assert(report.configurations.size == configurations.size)
    assert(jars.nonEmpty)
    assert(jars.forall(_.exists))
  }

  test("resolve intransitive maven dependencies") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(akkaActorTestkit.intransitive()),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val transitiveJars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if (m.module.name contains "akka-actor") && !(m.module.name contains "testkit")
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f
    val directJars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if (m.module.name contains "akka-actor") && (m.module.name contains "testkit")
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f

    assert(report.configurations.size == configurations.size)
    assert(transitiveJars.isEmpty)
    assert(directJars.forall(_.exists))
  }

  test("handle transitive configuration shifts") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(akkaActorTestkit),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val jars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Test.name)
        m <- conf.modules
        if m.module.name contains "akka-actor"
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f

    assert(report.configurations.size == configurations.size)
    assert(jars.nonEmpty)
    assert(jars.forall(_.exists))
  }

  test("resolve source and doc") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("sources")),
      Vector(
        akkaActor
          .artifacts(Artifact(akkaActor.name, "javadoc"), Artifact(akkaActor.name, "sources"))
      ),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val jars =
      for {
        conf <- report.configurations
        //  We actually injected javadoc/sources into the compile scope, due to how we did the request.
        //  SO, we report that here.
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        (a, f) <- m.artifacts
        if (f.getName contains "sources") || (f.getName contains "javadoc")
      } yield f

    assert(report.configurations.size == configurations.size)
    assert(jars.size == 2)
  }

  test("resolve nonstandard (jdk5) classifier") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(testngJdk5),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val jars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if m.module.name == "testng"
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f

    assert(report.configurations.size == configurations.size)
    assert(jars.size == 1)
    assert(jars.forall(_.exists))
  }

  test("Resolve pom artifact dependencies") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(scalaLibraryAll),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    val report = ivyUpdate(m)
    val jars =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if (m.module.name == "scala-library") || (m.module.name contains "parser")
        (a, f) <- m.artifacts
        if a.extension == "jar"
      } yield f

    assert(jars.size == 2)
  }

  test("Fail if JAR artifact is not found w/ POM") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(jmxri),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    intercept[Exception] { ivyUpdate(m) }
  }

  test("Fail if POM.xml is not found") {
    // TODO - we need the jar to not exist too.
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(
        ModuleID("org.scala-sbt", "does-not-exist", "1.0").withConfigurations(Some("compile"))
      ),
      Some("2.10.2"),
      defaultUpdateOptions
    )
    intercept[Exception] { ivyUpdate(m) }
  }

  test("resolve publication date for -SNAPSHOT") {
    val m = module(
      ModuleID("com.example", "foo", "0.1.0").withConfigurations(Some("compile")),
      Vector(testSnapshot),
      Some("2.10.2"),
      defaultUpdateOptions.withLatestSnapshots(true)
    )
    val report = ivyUpdate(m)
    val pubTime =
      for {
        conf <- report.configurations
        if conf.configuration == ConfigRef(Compile.name)
        m <- conf.modules
        if m.module.revision endsWith "-SNAPSHOT"
        date <- m.publicationDate
      } yield date

    assert(pubTime.size == 1)
  }

  // TODO - test latest.integration and .+

  def akkaActor =
    ModuleID("com.typesafe.akka", "akka-actor_2.11", "2.3.8").withConfigurations(Some("compile"))
  def akkaActorTestkit =
    ModuleID("com.typesafe.akka", "akka-testkit_2.11", "2.3.8").withConfigurations(Some("test"))
  def testngJdk5 =
    ModuleID("org.testng", "testng", "5.7").withConfigurations(Some("compile")).classifier("jdk15")
  def jmxri = ModuleID("com.sun.jmx", "jmxri", "1.2.1").withConfigurations(Some("compile"))
  def scalaLibraryAll =
    ModuleID("org.scala-lang", "scala-library-all", "2.11.4").withConfigurations(Some("compile"))
  def scalaCompiler =
    ModuleID("org.scala-lang", "scala-compiler", "2.8.1").withConfigurations(
      Some("scala-tool->default(compile)")
    )
  def scalaContinuationPlugin =
    ModuleID("org.scala-lang.plugins", "continuations", "2.8.1").withConfigurations(
      Some("plugin->default(compile)")
    )
  def sbtPlugin =
    ModuleID("com.github.mpeltonen", "sbt-idea", "1.6.0")
      .withConfigurations(Some("compile"))
      .extra(
        PomExtraDependencyAttributes.SbtVersionKey -> "0.13",
        PomExtraDependencyAttributes.ScalaVersionKey -> "2.10"
      )
      .withCrossVersion(CrossVersion.disabled)
  def oldSbtPlugin =
    ModuleID("com.github.mpeltonen", "sbt-idea", "1.6.0")
      .withConfigurations(Some("compile"))
      .extra(
        PomExtraDependencyAttributes.SbtVersionKey -> "0.12",
        PomExtraDependencyAttributes.ScalaVersionKey -> "2.9.2"
      )
      .withCrossVersion(CrossVersion.disabled)
  def majorConflictLib =
    ModuleID("com.joestelmach", "natty", "0.3").withConfigurations(Some("compile"))
  // TODO - This snapshot and resolver should be something we own/control so it doesn't disappear on us.
  def testSnapshot =
    ModuleID("com.typesafe", "config", "0.4.9-SNAPSHOT").withConfigurations(Some("compile"))
  val SnapshotResolver =
    MavenRepository("some-snapshots", "https://oss.sonatype.org/content/repositories/snapshots/")

  override def resolvers =
    Vector(Resolver.DefaultMavenRepository, SnapshotResolver, Resolver.publishMavenLocal)
  import Configurations.{ Compile, Test, Runtime, CompilerPlugin, ScalaTool }
  override def configurations = Vector(Compile, Test, Runtime, CompilerPlugin, ScalaTool)
  def defaultUpdateOptions = UpdateOptions().withResolverConverter(MavenResolverConverter.converter)
}
