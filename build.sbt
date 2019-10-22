name := "json-scala-values"
version := "0.4"
scalaVersion := "2.13.0"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.2" % "test"
libraryDependencies += "org.scalatest" % "scalatest_2.13" % "3.0.8" % "test"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.10.0"

scalacOptions ++= Seq("-deprecation", "-feature")


val NEXUS_USERNAME = sys.env.get("NEXUS_USERNAME")
val NEXUS_PASSWORD = sys.env.get("NEXUS_PASSWORD")

credentials += Credentials("Sonatype Nexus Repository Manager",
                           "oss.sonatype.org",
                           NEXUS_USERNAME.getOrElse("user"),
                           NEXUS_PASSWORD.getOrElse("password")
                           )

ThisBuild / organization := "com.github.imrafaelmerino"
ThisBuild / organizationName := "Rafael Merino García"
ThisBuild / organizationHomepage := Some(url("https://github.com/imrafaelmerino/imrafaelmerino.github.io"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/imrafaelmerino/json-scala-values.git"),
    "git@github.com:imrafaelmerino/json-scala-values.git"
    )
  )
ThisBuild / developers := List(
  Developer(
    id = "com.github.imrafaelmerino",
    name = "Rafael Merino García",
    email = "imrafael.merino@gmail.com",
    url = url("https://github.com/imrafaelmerino/imrafaelmerino.github.io")
    )
  )

ThisBuild / description := "Declarative and immutable Json implemented with persistent data structures."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/imrafaelmerino/json-scala-values"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository :=
{ _ => false }

ThisBuild / publishTo :=
{
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
