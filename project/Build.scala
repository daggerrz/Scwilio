import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "org.scwilio"
  val buildVersion      = "0.1.1-SNAPSHOT"
  val buildScalaVersion = "2.9.2"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    crossScalaVersions := Seq("2.9.1", "2.9.2"),
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object ScwilioBuild extends Build {

  import BuildSettings._

  def scwilioProject(dir: String) =
    Project(dir, file(dir), settings = buildSettings)

  lazy val core =       scwilioProject("core")
  lazy val unfiltered = scwilioProject("unfiltered") dependsOn(core)
  lazy val examples =   scwilioProject("examples")   dependsOn(unfiltered)

  lazy val root = Project("root", base = file(".")) aggregate (core, unfiltered, examples)
}
