package value

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import valuegen.RandomJsObjGen

object JsObjSpecification extends Properties("JsObj") {


  property("if two object are equals, they have the same hashcode") =
    forAll(RandomJsObjGen()) { (x: JsObj) =>
    val either = JsObjParser.parse(x.toString)
    either.contains(x) && either.exists(_.hashCode() == x.hashCode())
  }

  property("an object is just a set of path/value pairs") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,20))){
      (x:JsObj) =>
        x == JsObj(x.flatten: _*)
    }

}