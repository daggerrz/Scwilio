import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {

  override def packageSrcJar = defaultJarPath("-sources.jar")
  val sourceArtifact = Artifact.sources(artifactID)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  class ScwilioModule(info: ProjectInfo) extends DefaultProject(info) {
    val scalaToolsRepo = "Scala Tools Repo" at "http://scala-tools.org/repo-releases/"
    val LOGBACK_VERSION = "0.9.24"
    val SLF4J_VERSION = "1.6.1"

  }

  lazy val core = project("core", "Scwilio Core", new Core(_))
  lazy val unfiltered = project("unfiltered", "Scwilio Unfiltered", new Unfiltered(_), core)
  lazy val examples = project("examples", "Scwilio Examples", new Examples(_), core, unfiltered)

  class Core(info: ProjectInfo) extends ScwilioModule(info) {
    override def libraryDependencies = Set(
      "net.databinder" %% "dispatch-http" % "0.7.7",
      "org.slf4j" % "slf4j-api" % "1.6.1",
      "ch.qos.logback" % "logback-classic" % "0.9.26",
      "org.scalatest" % "scalatest" % "1.2" % "test",
      "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test",
      "org.mockito" % "mockito-core" % "1.7"

      )

  }

  class Unfiltered(info: ProjectInfo) extends ScwilioModule(info) {
    lazy val ufJetty = "net.databinder" %% "unfiltered-jetty" % "0.2.2"
    lazy val ufFilter = "net.databinder" %% "unfiltered-filter" % "0.2.2"

  }

  class Examples(info: ProjectInfo) extends ScwilioModule(info) {

  }
}

