package value

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import value.JsObjSpecification.property
import valuegen.{RandomJsArrayGen, RandomJsObjGen}

object JsArraySpecification extends Properties("JsArray") {


  property("if two arrays are equals, they have the same hashcode") =
    forAll(RandomJsArrayGen()) { (x: JsArray) =>
    val either = JsArrayParser.parse(x.toString)
    either.contains(x) && either.exists(_.hashCode() == x.hashCode())
  }

  property("an array is just a set of path/value pairs") =
    forAll(RandomJsArrayGen(arrLengthGen = Gen.choose(1,20))){
      (x:JsArray) =>
        val pairs = x.flatten
        x == JsArray(pairs.head,pairs.tail: _*)
    }
}