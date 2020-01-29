package value

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import valuegen.RandomJsObjGen

object JsObjSpecification extends Properties("JsObj") {


  property("if two object are equals, they have the same hashcode") =
    forAll(RandomJsObjGen()) { (a: JsObj) =>
    val either = JsObjParser.parse(a.toString)
    either.contains(a) && either.exists(_.hashCode() == a.hashCode())
  }

}