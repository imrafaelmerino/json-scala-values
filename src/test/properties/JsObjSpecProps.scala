package value.properties

import valuegen.Preamble._
import valuegen.{JsArrayGen, JsObjGen, RandomJsObjGen}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import value.Preamble._
import value.Preamble.strSpec2KeySpec
import value.JsPath.empty
import value.spec.JsArraySpecs._
import value.spec.JsBoolSpecs.bool
import value.spec.JsNumberSpecs._
import value.spec.JsObjSpecs._
import value.spec.JsSpecs.any
import value.spec.JsStrSpecs._
import value.spec.{Invalid, JsArraySpec, JsObjSpec, JsSpecs, Result, Valid}
import value.{JsArray, JsObj, JsPath}
import valuegen.JsArrayGen.noneEmptyOf


class JsObjSpecProps extends BasePropSpec
{


  property("string spec")
  {
    check(forAll(JsObjGen("b" -> Gen.alphaStr,
                          "d" -> JsArrayGen(Gen.alphaStr,
                                            JsArrayGen.ofN(10,
                                                       Gen.alphaStr
                                                       )
                                          ),
                          "j" -> Gen.oneOf("a",
                                           "b"
                                           ),
                          "e" -> JsObjGen("f" -> "male"),
                          "f" -> "Hi buddy!"
                          )
                 )
          {
            obj =>
              val value = obj.validate(JsObjSpec("b" -> str(nullable = false,
                                                            required = false
                                                            ),
                                                 "d" -> JsArraySpec(str,
                                                                    arrayOfStrSuchThat((a: JsArray) => if (a.size == 10) Valid else Invalid("not size of 10"))
                                                                    ),
                                                 "j" -> consts("a",
                                                             "b"
                                                             ),
                                                 "e" -> JsObjSpec("f" -> enum("male",
                                                                              "female"
                                                                              )
                                                                  ),
                                                 "f" -> strSuchThat((str: String) => if (str.endsWith("!")) Valid else Invalid(s"$str doesn't end with !"))
                                                 )

                                       )
              value.isEmpty
          }
          )
  }

  property("int spec")
  {
    check(forAll(
      JsObjGen("a" -> 1,
               "l" -> JsObjGen("m" -> JsArrayGen(Gen.choose[Int](0,
                                                               1000
                                                               )
                                               )
                               ),
               "o" -> Gen.choose(1,
                                 10
                                 ),
               "p" -> Gen.choose(0,
                                 10
                                 )
               )
      )
          {
            objGenerated =>

              val errors = objGenerated.validate(JsObjSpec("a" -> 1,
                                                           "l" -> JsObjSpec("m" -> JsArraySpec(intSuchThat((i: Int) => if (i >= 0 && i <= 1000) Valid else Invalid("not in [0,1000]")))),
                                                           "o" -> intSuchThat((i: Int) => if (i > 0 && i < 11) Valid else Invalid("not in [1,10]")),
                                                           "p" -> intSuchThat((i: Int) => if (i >= 0 && i < 11) Valid else Invalid("not in [0,10]"))
                                                           ),
                                                 )
              errors.isEmpty
          }
          )
  }

  property("array spec")
  {
    check(forAll(

      JsObjGen("b" -> noneEmptyOf(Arbitrary.arbitrary[Long]),
               "c" -> noneEmptyOf(Arbitrary.arbitrary[Int]),
               "d" -> noneEmptyOf(Arbitrary.arbitrary[BigInt]),
               "e" -> noneEmptyOf(Arbitrary.arbitrary[BigDecimal]),
               "f" -> JsArrayGen(Gen.choose(1,
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
            objGenerated =>

              val errors = objGenerated.validate(JsObjSpec("b" -> arrayOfLongSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                                           "c" -> arrayOfIntSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                                           "d" -> arrayOfIntegralSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                                           "e" -> arrayOfDecimalSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                                           "f" -> JsArraySpec(intSuchThat((i: Int) => if (i < 11 && i > 0) Valid else Invalid("")),
                                                                              bool,
                                                                              strSuchThat((s: String) => if (s.length > 2 || s.length < 7) Valid else Invalid("length not in [3,6]")),
                                                                              JsSpecs.any,
                                                                              objSuchThat((o: JsObj) => if (o.containsKey("h") && o.size == 3) Valid else Invalid(""))
                                                                              ),
                                                           "n" -> arraySuchThat((array: JsArray) => if (array.length() == 3) Valid else Invalid("")),
                                                           "s" -> JsArraySpec(decimalSuchThat((bd: BigDecimal) => if (bd < 5) Valid else Invalid("greater than 5")),
                                                                              integralSuchThat((bd: BigInt) => if (bd > 5) Valid else Invalid("lower than 5")),
                                                                              decimalSuchThat((bd: BigDecimal) => if (bd > 0) Valid else Invalid("lower than zero"))
                                                                              ),
                                                           "t" -> arrayOfNumberSuchThat((a: JsArray) => if (a.length() > 1 && a.length() < 5) Valid else Invalid(""))
                                                           )
                                                 )

              errors.isEmpty
          }
          )
  }

  property("operating with JsObjSpecs")
  {
    check(forAll(JsObjGen("a" -> Arbitrary.arbitrary[String],
                          "b" -> ?(Arbitrary.arbitrary[Int]),

                          )
                 )
          {
            obj =>
              val a = obj.validate(JsObjSpec("a" -> str) ++ JsObjSpec("b" -> int(nullable = false,
                                                                                 required = false
                                                                                 )
                                                                      )
                                   )

              val b = obj.validate(JsObjSpec("a" -> str) + ("b", int(nullable = false,
                                                                     required = false
                                                                     ))
                                   )
              a.isEmpty &&
              b.isEmpty
          }
          )
  }

  property("string errors")
  {
    check(forAll(JsObjGen("a" -> "too short",
                          "b" -> "too long",
                          "c" -> "123",
                          "d" -> "man"
                          )
                 )
          { o =>

            val result: Seq[(JsPath, Result)] = o.validate(JsObjSpec("a" -> strSuchThat((s: String) => if (s.length > 10) Valid else Invalid("too short")),
                                                                     "b" -> strSuchThat((s: String) => if (s.length < 2) Valid else Invalid("too long")),
                                                                     "c" -> strSuchThat((s: String) => if (s.matches("\\d")) Valid else Invalid("doesnt match pattern \\d")),
                                                                     "d" -> consts("MALE",
                                                                                 "FEMALE"
                                                                                 )
                                                                     )
                                                           )
            findFieldResult(result,
                            empty / "a"
                            ).isInvalid(message => message == "too short") &&
            findFieldResult(result,
                            empty / "b"
                            ).isInvalid(message => message == "too long") &&
            findFieldResult(result,
                            empty / "c"
                            ).isInvalid(message => message == "doesnt match pattern \\d") &&
            findFieldResult(result,
                            empty / "d"
                            ).isInvalid(message => message == "'man' not in MALE,FEMALE")
          }
          )
  }


  property("boolean errors")
  {
    check(forAll(JsObjGen("a" -> 1,
                          "b" -> 2,
                          "c" -> "hi"
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("a" -> true,
                                              "b" -> false,
                                              "c" -> bool
                                              )
                                    )

            findFieldResult(result,
                            "a"
                            ).isInvalid(message => message == "1 is not true") &&
            findFieldResult(result,
                            "b"
                            ).isInvalid(message => message == "2 is not false"
                                        ) &&
            findFieldResult(result,
                            "c"
                            ).isInvalid(message => message == "\"hi\" is not a boolean"
                                        )
          }
          )
  }

  property("big decimal errors")
  {
    check(forAll(JsObjGen("h" -> BigDecimal(10),
                          "j" -> 1

                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec(
              "h" -> decimalSuchThat((bd: BigDecimal) => if (bd < 5) Valid else Invalid("greater than 5")),
              "j" -> decimal
              )
                                    )

            findFieldResult(result,
                            empty / "h"
                            ).isInvalid(message => message == "greater than 5"
                                        ) &&
            findFieldResult(result,
                            empty / "j"
                            ).isInvalid(message => message == "1 is not a decimal number")

          }
          )
  }

  property("int errors")
  {
    check(forAll(JsObjGen("e" -> 10,
                          "j" -> 1.2,
                          "k" -> 11
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("e" -> intSuchThat((i: Int) => if (i > 10) Valid else Invalid("not greater than 10")),
                                              "j" -> int,
                                              "k" -> intSuchThat((i: Int) => if (i % 5 == 0) Valid else Invalid("not multiple of 5")),
                                              )
                                    )

            findFieldResult(result,
                            empty / "e"
                            ).isInvalid(message => message == "not greater than 10"
                                        ) &&
            findFieldResult(result,
                            empty / "j"
                            ).isInvalid(message => message == "1.2 is not an int number (32 bits)") &&
            findFieldResult(result,
                            empty / "k"
                            ).isInvalid(message => message == "not multiple of 5")

          }
          )
  }

  property("object errors")
  {
    check(forAll(JsObjGen("a" -> JsObjGen("a" -> "b",
                                          "b" -> 1,
                                          "c" -> true
                                          ),
                          "b" -> JsObjGen("d" -> false
                                          )
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("a" -> objSuchThat((o: JsObj) => if (o.size == 4) Valid else Invalid("number of keys is not 4")),
                                              "b" -> objSuchThat((o: JsObj) => if (o.containsKey("a")) Valid else Invalid("not contain key named 'a'")),
                                              )
                                    )
            findFieldResult(result,
                            "a"
                            ).isInvalid(message => message == "number of keys is not 4"
                                        ) &&
            findFieldResult(result,
                            "b"
                            ).isInvalid(message => message == "not contain key named 'a'"
                                        )

          }
          )
  }

  property("long errors")
  {
    check(forAll(JsObjGen("f" -> 10L,
                          "l" -> 11L,
                          "c" -> -1L,
                          "d" -> 4L
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("f" -> longSuchThat((i: Long) => if (i < 9) Valid else Invalid("not lower than 9")),
                                              "l" -> longSuchThat((i: Long) => if (i % 3 == 0) Valid else Invalid("not multiple of 3")),
                                              "c" -> longSuchThat((i: Long) => if (i >= 0) Valid else Invalid("not a positive number")),
                                              "d" -> longSuchThat((i: Long) => if (i == 3L) Valid else Invalid("not equal to 3"))
                                              )
                                    )


            findFieldResult(result,
                            empty / "f"
                            ).isInvalid(message => message == "not lower than 9"
                                        ) &&
            findFieldResult(result,
                            empty / "l"
                            ).isInvalid(message => message == "not multiple of 3") &&
            findFieldResult(result,
                            empty / "c"
                            ).isInvalid(message => message == "not a positive number") &&
            findFieldResult(result,
                            empty / "d"
                            ).isInvalid(message => message == "not equal to 3")
          }
          )
  }

  property("array errors")
  {
    check(forAll(JsObjGen("a" -> JsArray(true,
                                         true
                                         ),
                          "l" -> JsArray(1,
                                         1,
                                         3
                                         ),
                          "m" -> JsArray(1.5,
                                         1.5,
                                         4.0
                                         ),
                          "n" -> JsArray("orange",
                                         "lemon"
                                         ),
                          "o" -> JsArray(1,
                                         2.5,
                                         Long.MinValue,
                                         1
                                         )
                          )
                 )
          { o =>

            val result: Seq[(JsPath, Result)] = o.validate(
              JsObjSpec("a" -> arrayOfBool,
                        "l" -> arrayOfIntegral,
                        "m" -> arrayOfDecimal,
                        "n" -> arrayOfStr,
                        "o" -> arrayOfNumber
                        )
              )

            result.isEmpty
          }
          )
  }

  def findByPath(result: Seq[(JsPath, Result)],
                 path: JsPath
                ): Option[(JsPath, Result)] =
  {
    result.find((pair: (JsPath, Result)) => pair._1 == path)

  }

  def findFieldResult(result: Seq[(JsPath, Result)],
                      path: JsPath
                     ): Result =
  {
    findByPath(result,
               path
               ).get._2

  }

}


