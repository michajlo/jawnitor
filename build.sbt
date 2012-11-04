name := "jawnitor"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.2"

scalacOptions += "-deprecation"

unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_))

unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_))

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

resolvers += "Sonatype OSS Releases" at
  "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5"

