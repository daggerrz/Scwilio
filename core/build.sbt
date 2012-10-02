name := "Scwilio Core"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.8",
  "org.slf4j" % "slf4j-api" % "1.6.1",
  "ch.qos.logback" % "logback-classic" % "0.9.26",
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
  "org.mockito" % "mockito-core" % "1.7"
)
