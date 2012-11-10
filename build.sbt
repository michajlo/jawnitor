name := "jawnitor"

organization := "org.michajlo"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.2"

scalacOptions += "-deprecation"

publishMavenStyle := true

unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_))

unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_))

resolvers += "Sonatype OSS Releases" at
  "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"

