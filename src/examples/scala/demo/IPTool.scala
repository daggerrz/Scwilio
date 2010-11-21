package demo

object IPTool {

  /**
   * Gets the public IP of the machin using domaintools.com.
   */
  def publicIp = {
    import dispatch._
    import scala.xml._
    val http = new Http
    val req = :/("ip-address.domaintools.com") / "myip.xml"
    http(req <> { _ \\ "ip_address" text } )
  }
}

object AbsoluteUrl {
  lazy val publicIp = IPTool.publicIp
  def apply(url: String) = Some("http://" + publicIp + ":" + DemoConfig.port  + url)
}