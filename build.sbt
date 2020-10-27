val dottyVersion = "0.27.0-RC1"
val scala213Version = "2.13.0"
val jsonValuesVersion = "4.0.0"

val NEXUS_USERNAME = sys.env.get("NEXUS_USERNAME")
val NEXUS_PASSWORD = sys.env.get("NEXUS_PASSWORD")

lazy val root = project
  .in(file("."))
  .settings(
    name := "json-dotty-values",

    version := jsonValuesVersion,

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

    scmInfo := Some(ScmInfo( url("https://github.com/imrafaelmerino/json-scala-values.git"),
                                  "git@github.com:imrafaelmerino/json-scala-values.git"
                           )
                   ),

    developers := List(Developer( id = "com.github.imrafaelmerino",
                                  name = "Rafael Merino García",
                                  email = "imrafael.merino@gmail.com",
                                  url = url("https://github.com/imrafaelmerino/imrafaelmerino.github.io")
                                 )
                      ),

    description := "Declarative and immutable Json implemented with persistent data structures.",

    licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),

    homepage := Some(url("https://github.com/imrafaelmerino/json-scala-values")),

    pomIncludeRepository := { _ => false },

    publishTo :=
    {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,

    Test / parallelExecution := true,

    libraryDependencies += "com.github.julien-truffaut" % "monocle-core_2.13" % "2.1.0",
    libraryDependencies += "com.dslplatform" % "dsl-json" % "1.9.6",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.11.3",
    libraryDependencies += "org.scalacheck" % "scalacheck_2.13" % "1.14.3",
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",

    jacocoReportSettings := JacocoReportSettings("Jacoco Coverage Report",
                                                 None,
                                                 JacocoThresholds(),
                                                 Seq(JacocoReportFormats.ScalaHTML,
                                                     JacocoReportFormats.XML
                                                    ), // note XML formatter
                                                 "utf-8"
                                                 )

    )


