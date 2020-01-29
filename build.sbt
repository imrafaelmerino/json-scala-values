val dottyVersion = "0.21.0-RC1"
val scala213Version = "2.13.1"
val NEXUS_USERNAME = sys.env.get("NEXUS_USERNAME")
val NEXUS_PASSWORD = sys.env.get("NEXUS_PASSWORD")

lazy val root = project
  .in(file("."))
  .settings(
    name := "json-scala-values",
    version := "3.0.0-RC.1",
    scalaVersion := dottyVersion,
    crossScalaVersions := Seq(dottyVersion,
                              scala213Version
                              ),
    credentials += Credentials("Sonatype Nexus Repository Manager",
                               "oss.sonatype.org",
                               NEXUS_USERNAME.getOrElse("user"),
                               NEXUS_PASSWORD.getOrElse("password")
                               ),
    organization := "com.github.imrafaelmerino",
    organizationName := "Rafael Merino García",
    organizationHomepage := Some(url("https://github.com/imrafaelmerino/imrafaelmerino.github.io")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/imrafaelmerino/json-scala-values.git"),
        "git@github.com:imrafaelmerino/json-scala-values.git"
        )
      ),
    developers := List(
      Developer(
        id = "com.github.imrafaelmerino",
        name = "Rafael Merino García",
        email = "imrafael.merino@gmail.com",
        url = url("https://github.com/imrafaelmerino/imrafaelmerino.github.io")
        )
      ),

    description := "Declarative and immutable Json implemented with persistent data structures.",
    licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage := Some(url("https://github.com/imrafaelmerino/json-scala-values")),

    pomIncludeRepository :=
    { _ => false },

    publishTo :=
    {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,

    Test / parallelExecution := true,

    libraryDependencies += "com.dslplatform" % "dsl-json" % "1.9.5",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.10.1",
    libraryDependencies += "org.scalacheck" % "scalacheck_2.13" % "1.14.3" % "test",
    libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values-generator_2.13" % "1.1.0" % "test",

    )


