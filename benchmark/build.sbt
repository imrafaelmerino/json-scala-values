enablePlugins(JmhPlugin)

name := "json-scala-values-benchmarking"
version := "0.1"
scalaVersion := "2.13.0"

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "2.1.0-SNAPSHOT"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.1"
libraryDependencies += "com.github.java-json-tools" % "json-schema-validator" % "2.2.11"
libraryDependencies += "org.leadpony.justify" % "justify" % "2.0.0"
libraryDependencies += "org.glassfish" % "javax.json" % "1.1.4"

