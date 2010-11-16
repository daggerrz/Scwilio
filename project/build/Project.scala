import sbt._

class Project(info: ProjectInfo) extends DefaultWebProject(info) {
  val scalaToolsRepo = "Scala Tools Repo" at "http://scala-tools.org/repo-releases/"

  val LOGBACK_VERSION = "0.9.24"
  val SLF4J_VERSION = "1.6.1"

  override def libraryDependencies = Set(
    "net.databinder" %% "dispatch-http" % "0.7.7",
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scalatest" % "scalatest" % "1.2" % "test",
    "org.scala-tools.testing" %% "specs" % "1.6.5" % "test",
    "org.mockito" % "mockito-core" % "1.7",

    "net.databinder" %% "uf-jetty" % "0.2.2",
    "net.databinder" %% "uf-filter" % "0.2.2"
    )
}

