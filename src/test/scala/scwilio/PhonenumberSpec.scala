package scwilio

import org.specs.Specification
import java.lang.IllegalArgumentException


object PhonenumberSpec extends Specification {

  import PhonenumberParser._

  val pn = Phonenumber("47", "90055383")

  "Parser.parse" should {
    "accept valid numbers" in {
      parse("+4790055383") must_== pn
    }
    "not accept invalid numbers" in {
      parse("foobar") must throwAn [IllegalArgumentException]
    }
  }

  "Phonenumber" should {
    "format itself to E.164 as default" in {
      pn.toString must_== "+4790055383"
    }
  }
}