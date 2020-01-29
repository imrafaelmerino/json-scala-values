package value

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import value.JsObjSpecification.property
import valuegen.{RandomJsArrayGen, RandomJsObjGen}
import value.Preamble._

import scala.language.implicitConversions
object JsArraySpecification extends Properties("JsArray") {


  property("if two arrays are equals, they have the same hashcode") =
    forAll(RandomJsArrayGen()) { (x: JsArray) =>
    val either = JsArrayParser.parse(x.toString)
    either.contains(x) && either.exists(_.hashCode() == x.hashCode())
  }

  property("an array is a set of path/value pairs") =
    forAll(RandomJsArrayGen(arrLengthGen = Gen.choose(1,20))){
      (x:JsArray) =>
        val pairs = x.flatten
        x == JsArray(pairs.head,pairs.tail: _*)
    }

  property("inserted function is honest: it always inserts at the specified path") =
    forAll(RandomJsArrayGen())
    {
      (x: JsArray) =>

        @scala.annotation.tailrec
        def insertPairs(pairs: LazyList[(JsPath, JsValue)],
                        y    : JsArray
                       ): JsArray =
          if (pairs.isEmpty) y else
          {
            val head = pairs.head
            insertPairs(pairs.tail,
                        y.inserted(head._1,
                                   head._2
                                   )
                        )
          }

        insertPairs(x.flatten,
                    JsArray.empty
                    ) == x
    }

  property("apply function returns the element located at the specified path")=
    forAll(RandomJsArrayGen(arrLengthGen = Gen.choose(1,20))){
      (x:JsArray) =>
        x.flatten.forall((p:(JsPath,JsValue))=> x(p._1)==p._2)
    }

  property("head + tail returns the same object")=
    forAll(RandomJsArrayGen(arrLengthGen = Gen.choose(1,20))){
      (x:JsArray) =>
        x.tail.prepended(x.head) == x
    }

  property("init + last returns the same object")=
    forAll(RandomJsArrayGen(arrLengthGen = Gen.choose(1,20))){
      (x:JsArray) =>
        x.init.appended(x.last) == x
    }

  property("map traverses the whole array") =
    {
      forAll(RandomJsArrayGen()
             )
      {
        (x: JsArray) =>
          x.map((path: JsPath, value: JsValue) =>
                  if (x(path) != value) throw new RuntimeException
                  else value
                ) == x
      }

    }

  property("mapKeys traverses the whole array") =
    {
      forAll(RandomJsArrayGen()
             )
      {
        (x: JsArray) =>
          x.mapKeys((path: JsPath, value: JsValue) =>
                  if (x(path) != value) throw new RuntimeException
                  else  path.last.asKey.name
                ) == x
      }

    }

  property("filter traverses the whole array") =
    {
      forAll(RandomJsArrayGen()
             )
      {
        (x: JsArray) =>
          x.filter((path: JsPath, value: JsValue) =>
                  if (x(path) != value) false
                  else true
                ) == x
      }

    }

  property("filterKeys traverses the whole array") =
    {
      forAll(RandomJsArrayGen()
             )
      {
        (x: JsArray) =>
          x.filterKeys((path: JsPath, value: JsValue) =>
                     if (x(path) != value) false
                     else true
                   ) == x
      }

    }
}