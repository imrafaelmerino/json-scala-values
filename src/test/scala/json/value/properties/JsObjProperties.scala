package json.value.properties
import json.value.Preamble.{_,given _}
import scala.language.implicitConversions
import json.value.gen._
import json.value.gen.Preamble.{_,given _}
import json.value.spec.Preamble.{_,given _}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import json.value.spec.JsArraySpecs._
import json.value.spec.JsBoolSpecs.bool
import json.value.spec.JsNumberSpecs._
import json.value.spec.JsStrSpecs._
import json.value.spec.JsObjSpecs.objSuchThat
import json.value.spec.JsSpecs.any
import json.value.spec.JsStrSpecs.strSuchThat
import json.value.spec.{Invalid, JsArraySpec, JsObjSpec, Valid}
import json.value._
import gen.JsArrayGen.noneEmptyOf

object JsObjProperties extends Properties("JsObj")
{


  property("if two object are equals, they have the same hashcode") =
    forAll(RandomJsObjGen())
    { (x: JsObj) =>
      val either = JsObjParser.parse(x.toString)
      either.contains(x) && either.exists(_.hashCode() == x.hashCode())
    }

  property("an object is a set of path/json.value pairs") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x == JsObj(x.flatten: _*)
    }

  property("inserted function is honest: it always inserts at the specified path") =
    forAll(RandomJsObjGen())
    {
      (x: JsObj) =>

        @scala.annotation.tailrec
        def insertPairs(pairs: LazyList[(JsPath, JsValue)],
                        y    : JsObj
                       ): JsObj =
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
                    JsObj.empty
                    ) == x
    }

  property("apply function returns the element located at the specified path") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x.flatten.forall((p: (JsPath, JsValue)) => x(p._1) == p._2)
    }

  property("head + tail returns the same object") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x.tail.inserted(x.head._1,
                        x.head._2
                        ) == x
    }

  property("init + last returns the same object") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x.init.inserted(x.last._1,
                        x.last._2
                        ) == x
    }

  property("map traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.mapAll((path: JsPath, value: JsValue) =>
                  if (x(path) != value) throw  RuntimeException()
                  else value
                ) == x
      }

    }

  property("mapKeys traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.mapAllKeys((path: JsPath, value: JsValue) =>
                      if (x(path) != value) throw  RuntimeException()
                      else path.last.asKey.name
                    ) == x
      }

    }

  property("filter traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.filterAll((path: JsPath, value: JsValue) =>
                     if (x(path) != value) false
                     else true
                   ) == x
      }

    }


  property("filterKeys traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.filterAllKeys((path: JsPath, value: JsValue) =>
                         if (x(path) != value) false
                         else true
                       ) == x
      }

    }

  property("obj satisfies spec") =
    {
      forAll(JsObjGen("a" -> Arbitrary.arbitrary[String],
                      "b" -> Arbitrary.arbitrary[Int],
                      "c" -> Gen.oneOf(true,
                                       false
                                       )
                      )
             )
      {
        (o: JsObj) =>
          o.validate(JsObjSpec("a" -> str,
                               "b" -> int,
                               "c" -> bool
                               )
                     ).isEmpty


      }
    }

  property("array spec") =
    {
      forAll(
        JsObjGen("b" -> noneEmptyOf(Arbitrary.arbitrary[Long]),
                 "c" -> noneEmptyOf(Arbitrary.arbitrary[Int]),
                 "d" -> noneEmptyOf(Arbitrary.arbitrary[BigInt]),
                 "e" -> noneEmptyOf(Arbitrary.arbitrary[BigDecimal]),
                 "f" -> JsArrayGen(Gen.choose[Int](1,
                                                   10
                                                   ),
                                   Arbitrary.arbitrary[Boolean],
                                   Gen.oneOf("red",
                                             "blue",
                                             "pink",
                                             "yellow"
                                             ),
                                   RandomJsObjGen(),
                                   JsObjGen("h" -> "i",
                                            "j" -> true,
                                            "m" -> 1
                                            )
                                   ),
                 "n" -> JsArray(1,
                                2,
                                3
                                ),
                 "s" -> JsArrayGen(BigDecimal(1.5),
                                   BigInt(10),
                                   1.5
                                   ),
                 "t" -> JsArray(1,
                                1.5,
                                2L
                                )
                 )
        )
      {

        (o: JsObj) =>
          o.validate(JsObjSpec("b" -> arrayOfLongSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                               "c" -> arrayOfIntSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                               "d" -> arrayOfIntegralSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                               "e" -> arrayOfDecimalSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                               "f" -> JsArraySpec(intSuchThat((i: Int) => if (i < 11 && i > 0) Valid else Invalid("")),
                                                  bool,
                                                  strSuchThat((s: String) => if (s.length > 2 || s.length < 7) Valid else Invalid("length not in [3,6]")),
                                                  any,
                                                  objSuchThat((o: JsObj) => if (o.containsKey("h") && o.size == 3) Valid else Invalid(""))
                                                  ),
                               "n" -> arraySuchThat((array: JsArray) => if (array.length() == 3) Valid else Invalid("")),
                               "s" -> JsArraySpec(decimalSuchThat((bd: BigDecimal) => if (bd < 5) Valid else Invalid("greater than 5")),
                                                  integralSuchThat((bd: BigInt) => if (bd > 5) Valid else Invalid("lower than 5")),
                                                  decimalSuchThat((bd: BigDecimal) => if (bd > 0) Valid else Invalid("lower than zero"))
                                                  ),
                               "t" -> arrayOfNumberSuchThat((a: JsArray) => if (a.length() > 1 && a.length() < 5) Valid else Invalid(""))
                               )
                     ).isEmpty


      }
    }
}
