package demo

import java.io.{File, IOException, FileInputStream}

object DemoConfig {
  val props = new java.util.Properties
  var accountSid: String = _
  var authToken: String = _
  try {
    props.load(new FileInputStream("scwilio.properties"))
    accountSid = props.getProperty("SID")
    authToken = props.getProperty("AuthToken")
  } catch {
    case e: IOException =>
      println("Please update " + new File("scwilo.properties").getAbsolutePath + " with SID and AuthToken keys in the current directory.")
      System.exit(0)
  }
  val port = 8080

}