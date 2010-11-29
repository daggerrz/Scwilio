package scwilio
package uf

object Params {

  /**
   * Extracts the first param of a parameter map and filters empty parameters.
   */
  implicit def paramSeq2FirstParams(p: Map[String, Seq[String]]) : Map[String, String] = {
    p.flatMap{
      case (key, Seq(head, _*)) => List((key, head))
      case _ => List()
    }
  }
}