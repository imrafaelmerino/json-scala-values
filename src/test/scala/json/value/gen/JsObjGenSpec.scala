package json.value.gen

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import json.value.Preamble._
import json.value.gen.Preamble._
import json.value.{JsArray, JsNothing, JsNull, JsObj}

class JsObjGenSpec extends BasePropSpec
{

  property("random person")
  {
    check(forAll(JsObjGen("a" -> Gen.choose(1,
                                            100
                                            ),
                          "b" -> JsArrayGen(Gen.oneOf(1,
                                                      2,
                                                      3,
                                                      JsNothing
                                                      ),
                                            Gen.alphaNumStr
                                            ),
                          "c" -> JsObjGen("d" -> Gen.choose(1,
                                                            10
                                                            )
                                          ),
                          "d" -> 2
                          )
                 )
          { obj =>
            obj.isObj
          }
          )
  }


  property("implicits conversion are applied")
  {
    check(forAll(JsObjGen("a" -> "hola",
                          "b" -> 1,
                          "c" -> 1.2,
                          "d" -> Long.MaxValue,
                          "e" -> true,
                          "d" -> Gen.alphaNumStr,
                          "e" -> Gen.oneOf(JsNothing,
                                           JsNull
                                           ),
                          "f" -> JsObj(),
                          "g" -> JsArray(),
                          "h" -> BigInt(10),
                          "i" -> BigDecimal(10),
                          "j" -> JsNull,
                          "k" -> Arbitrary.arbitrary[Long],
                          "l" -> Arbitrary.arbitrary[Double],
                          "m" -> Arbitrary.arbitrary[BigDecimal],
                          "n" -> Arbitrary.arbitrary[BigInt],
                          "o" -> Arbitrary.arbitrary[Boolean]
                          )
                 )
          { obj =>
            obj.isObj
          }
          )
  }


  property("syntax sugar for optional values(100%")
  {
    check(forAll(JsObjGen("a" -> ?(100,
                                   Gen.alphaNumStr
                                   )
                          )
                 )
          { obj =>
            obj.isObj && !obj("a").isNothing
          }
          )
  }


  property("syntax sugar for optional values(0%")
  {
    check(forAll(JsObjGen("a" -> ?(0,
                                   Gen.alphaNumStr
                                   )
                          )
                 )
          { obj =>
            obj.isObj && obj("a").isNothing
          }
          )
  }

  property("syntax sugar for optional values(50%")
  {
    var times = 0
    var isNothingCounter: Int = 0
    var existsCounter: Int = 0
    check(forAll(JsObjGen("a" -> ?(Gen.alphaNumStr
                                   )
                          )
                 )
          { obj =>

            if (obj("a").isNothing) isNothingCounter = isNothingCounter + 1
            else existsCounter = existsCounter + 1
            times = times + 1
            (times < 50 && (isNothingCounter > 0 || existsCounter > 0)) || (times >= 50 && isNothingCounter > 0 && existsCounter > 0)
          }
          )
  }

  property("obj from pairs")
  {

    check(forAll(JsObjGen.fromPairs(("a", 1),
                                    ("b", "hi"),
                                    ("c", 2.5),
                                    ("d", true),
                                    ("e", JsNull),
                                    ("f" / "g", Long.MinValue),
                                    ("f" / "g" / "h" / 0, JsObj()),
                                    ("f" / "g" / "h" / 1, JsArray()),

                                    )
                 )
          {
            obj => obj.isObj
          }
          )
  }

  property("array from pairs")
  {

    check(forAll(JsArrayGen.fromPairs((0 / "a", 1),
                                      (1 / "b", "hi"),
                                      (2 / "c", 2.5),
                                      (3 / "d", true),
                                      (4 / "e", JsNull),
                                      (5 / "f" / "g", Long.MinValue),
                                      (6 / "f" / "g" / "h" / 0, JsObj()),
                                      (7 / "f" / "g" / "h" / 1, JsArray()),

                                      )
                 )
          {
            arr => arr.isArr
          }
          )
  }

  property("adding pairs to JsObj generators")
  {

    check(forAll(JsObjGen.inserted(JsObjGen("a" -> 1,
                                            "b" -> 2,
                                            "c" -> JsObjGen("d" -> JsArrayGen(Gen.choose(0,
                                                                                         10
                                                                                         ),
                                                                              Gen.alphaStr
                                                                              )
                                                            )
                                            ),
                                   ("d" / "e" / 0, Gen.choose(10,
                                                              20
                                                              )
                                   ),
                                   ("d" / "e" / 1, Gen.alphaStr
                                   )
                                   )
                 )
          {
            (obj) =>
              obj("d" / "e" / 0).isInt && obj("d" / "e" / 1).isStr
          }
          )
  }

  property("concat JsObj generators")
  {

    check(forAll(JsObjGen.concat(JsObjGen("a" -> 1,
                                          "b" -> 2,
                                          "c" -> JsObjGen("d" -> JsArrayGen(Gen.choose(0,
                                                                                       10
                                                                                       ),
                                                                            Gen.alphaStr
                                                                            )
                                                          )
                                          ),
                                 JsObjGen("d" -> 1,
                                          "e" -> true
                                          )
                                 )
                 )
          {
            (obj) =>
              obj("a").isInt && obj("d").isInt && obj("e").isBool
          }
          )
  }



}
