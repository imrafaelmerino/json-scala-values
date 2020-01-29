package value

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import valuegen.{RandomJsArrayGen, RandomJsObjGen}

object JsArraySpecification extends Properties("JsArray") {


  property("if two arrays are equals, they have the same hashcode") =
    forAll(RandomJsArrayGen()) { (a: JsArray) =>
    val either = JsArrayParser.parse(a.toString)
    either.contains(a) && either.exists(_.hashCode() == a.hashCode())
  }
}