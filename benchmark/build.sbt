enablePlugins(JmhPlugin)

name := "json-scala-values-benchmarking"
version := "0.1"
scalaVersion := "2.13.0"

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "0.9.5"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.10.1"
