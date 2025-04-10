sbt-maven-resolver
==================

An sbt plugin to resolve dependencies using [Aether](https://wiki.eclipse.org/Aether/What_Is_Aether) (Maven's dependency resolver) together with Apache Ivy.

It solves the problems:

- sbt does not resolve the proper snapshot-version with timestamp dependencies deployed by sbt
- the host repository's "Maven Snapshot Version Behaviour" is "Unique" (Nonunique will overwrite jar/pom at each time and update all maven-metadata.xml automatically updated once a jar was been deployed)

related question:

- [SBT cannot find snapshots in an Artifactory maven repository
](https://stackoverflow.com/questions/23584264/sbt-cannot-find-snapshots-in-an-artifactory-maven-repository/23585401)

### Usage

In `plugins.sbt`:

```scala
addSbtPlugin("org.scala-sbt" % "sbt-maven-resolver" % "0.1.1")
```

### Credits

- In 2015, Josh Suereth implemented sbt-maven-resolver as part of sbt 0.13.8 in [sbt/sbt#1793](https://github.com/sbt/sbt/pull/1793).

### License

Licensed under Apache v2 license
