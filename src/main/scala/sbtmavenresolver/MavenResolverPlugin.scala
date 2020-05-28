package sbtmavenresolver

import sbt._
import Keys._

object MavenResolverPlugin extends AutoPlugin {
  override def requires = sbt.plugins.IvyPlugin
  override def trigger = allRequirements

  override lazy val buildSettings: Seq[Setting[_]] = Seq(
    useCoursier := false
  )
  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    updateOptions := updateOptions.value.withResolverConverter(MavenResolverConverter.converter)
  )
}
