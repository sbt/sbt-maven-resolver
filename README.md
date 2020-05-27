sbt-maven-resolver
==================

A plugin to resolve dependencies deployed by sbt or maven on a maven-repository. Especially it solves the problem:

- sbt does not resolve the proper snapshot-version with timestamp dependencies deployed by sbt
- the host repository's "Maven Snapshot Version Behaviour" is "Unique" (Nonunique will overwrite jar/pom at each time and update all maven-metadata.xml automatically updated once a jar was been deployed)

related question:

- [SBT cannot find snapshots in an Artifactory maven repository
](https://stackoverflow.com/questions/23584264/sbt-cannot-find-snapshots-in-an-artifactory-maven-repository/23585401)

### Usage

In `plugins.sbt`
```
addSbtPlugin("org.scala-sbt" % "sbt-maven-resolver" % "0.1.0-SNAPSHOT")
```

`addMavenResolverPlugin` does not been supported due to it use `sbtVersion` as the package version
