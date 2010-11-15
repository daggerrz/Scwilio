package scwilio.util

import org.slf4j.LoggerFactory

trait Logging {
  lazy val log = LoggerFactory.getLogger(this.getClass.getName)
}