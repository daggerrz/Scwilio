import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "org.scwilio"
  val buildVersion      = "0.1.1-SNAPSHOT"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    crossScalaVersions := Seq("2.8.1", "2.9.0-1", "2.9.1"),
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object ScwilioBuild extends Build {

  import BuildSettings._

  def scwilioProject(dir: String) =
    Project(dir, file(dir), settings = buildSettings)

  lazy val core = scwilioProject("core")
  lazy val unfiltered = scwilioProject("unfiltered") dependsOn(core)
  lazy val examples = scwilioProject("examples") dependsOn(unfiltered)
}
