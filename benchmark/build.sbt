enablePlugins(JmhPlugin)

name := "json-scala-values-benchmarking"
version := "0.1"
scalaVersion := "2.13.0"
val JACKSON_VERSION = "2.10.1"
val JSON_SCALA_VALUES_VERSION = "2.1.0-SNAPSHOT"
val JSON_SCHEMA_VALIDATOR_VERSION = "2.2.11"
val JUSTIFY_VERSION = "2.0.0"

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % JSON_SCALA_VALUES_VERSION
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % JACKSON_VERSION
libraryDependencies += "com.github.java-json-tools" % "json-schema-validator" % JSON_SCHEMA_VALIDATOR_VERSION
libraryDependencies += "org.leadpony.justify" % "justify" % JUSTIFY_VERSION
libraryDependencies += "org.glassfish" % "javax.json" % "1.1.4"

