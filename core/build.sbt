name := "Scwilio Core"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.7.8",
  "org.slf4j" % "slf4j-api" % "1.6.1",
  "ch.qos.logback" % "logback-classic" % "0.9.26",
  "org.scalatest" % "scalatest" % "1.2" % "test",
  "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test",
  "org.mockito" % "mockito-core" % "1.7"
)
