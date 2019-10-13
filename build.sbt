name := "json-scala-values"

version := "0.1.6"

scalaVersion := "2.13.0"

artifactName :=
{ (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "-" + version.value + "." + artifact.extension
}

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
libraryDependencies += "org.scalatest" % "scalatest_2.13" % "3.0.8" % "test"

credentials += Credentials("Sonatype Nexus Repository Manager",
                           "oss.sonatype.org",
                           sys.env.get("NEXUS_USERNAME").get,
                           sys.env.get("NEXUS_PASSWORD").get
                           )


ThisBuild / organization := "com.github.imrafaelmerino"
ThisBuild / organizationName := "Rafael Merino García"
ThisBuild / organizationHomepage := Some(url("https://github.com/imrafaelmerino/imrafaelmerino.github.io"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/imrafaelmerino/json-values.git"),
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

ThisBuild / description := "Some descripiton about your project."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/imrafaelmerino/json-values"))

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
coverageEnabled := true
