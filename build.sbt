name := "jawnitor"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.2"

unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_))

unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_))

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

