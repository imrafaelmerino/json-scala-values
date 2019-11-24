package value.properties

import valuegen.Implicits._
import valuegen.JsArrayGen.noneEmptyOf
import valuegen.{JsArrayGen, JsObjGen, RandomJsObjGen}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import value.Implicits._
import value.JsPath.empty
import value.spec.JsArraySpecs._
import value.spec.JsBoolSpecs.{FALSE, TRUE, boolean}
import value.spec.JsIntSpecs._
import value.spec.JsLongSpecs.{long, longGT, longLT}
import value.spec.JsNumberSpecs._
import value.spec.JsObjSpecs.obj
import value.spec.JsStringSpecs._
import value.spec.JsValueSpec.{and, any}
import value.spec.{JsArraySpec, JsIntSpecs, JsObjSpec, JsObjSpec_?, JsStringSpecs, Result}
import value.{JsArray, JsObj, JsPath, JsValue}


class JsObjSpecProps extends BasePropSpec
{


  property("string spec")
  {
    check(forAll(JsObjGen("b" -> ?(Gen.alphaStr),
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
              val value = obj.validate(JsObjSpec("b" -> string.?,
                                                 "d" -> JsArraySpec(string,
                                                                    arrayOfString(minItems = 10,
                                                                                  maxItems = 10
                                                                                  )
                                                                    ).?,
                                                 "j" -> and(enum("a",
                                                                 "b"
                                                                 ),
                                                            string(pattern = "\\w".r)
                                                            ),
                                                 "e" -> JsObjSpec("f" -> enum("male",
                                                                              "female"
                                                                              )
                                                                  ).?,
                                                 "f" -> string((str: String) => str.endsWith("!"),
                                                               (value: String) => s"$value doesn't end with !"
                                                               )
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

                                                           "l" -> JsObjSpec("m" -> JsArraySpec(int(minimum = 0,
                                                                                                   maximum = 1000,
                                                                                                   multipleOf = 1
                                                                                                   )
                                                                                               )
                                                                            ),
                                                           "o" -> and(intGT(exclusiveMinimum = 0,
                                                                            multipleOf = 1
                                                                            ),
                                                                      intLTE(10,
                                                                             multipleOf = 1
                                                                             )
                                                                      ),
                                                           "p" -> and(intGTE(minimum = 0,
                                                                             multipleOf = 1
                                                                             ),
                                                                      intLT(11,
                                                                            multipleOf = 1
                                                                            )
                                                                      )
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

              val errors = objGenerated.validate(JsObjSpec("b" -> arrayOfLong(minItems = 1),
                                                           "c" -> arrayOfInt(minItems = 1),
                                                           "d" -> arrayOfIntegral(minItems = 1),
                                                           "e" -> arrayOfDecimal(minItems = 1),
                                                           "f" -> JsArraySpec(int(minimum = 1,
                                                                                  maximum = 10,
                                                                                  multipleOf = 1
                                                                                  ),
                                                                              boolean,
                                                                              string(minLength = 3,
                                                                                     maxLength = 6
                                                                                     ),
                                                                              any,
                                                                              obj(required = List("h",
                                                                                                  "j"
                                                                                                  ),
                                                                                  dependentRequired = List(("m",
                                                                                                             List("h",
                                                                                                                  "j"
                                                                                                                  ))
                                                                                                           ),
                                                                                  minKeys = 2,
                                                                                  maxKeys = 5
                                                                                  )

                                                                              ).?,
                                                           "n" -> array(array => array.length() == 3,
                                                                        (value: JsValue) => s"$value is not an array of length 3"
                                                                        ),

                                                           "s" -> JsArraySpec(decimal(1,
                                                                                      5
                                                                                      ),
                                                                              integral(0,
                                                                                       20
                                                                                       ),
                                                                              decimalLTE(1.5)
                                                                              ),
                                                           "t" -> arrayOfNumber(minItems = 1,
                                                                                maxItems = 4,
                                                                                unique = true
                                                                                )
                                                           ),
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
              obj.validate(JsObjSpec("a" -> string) ++ JsObjSpec("b" -> int.?)).isEmpty &&
              obj.validate(JsObjSpec("a" -> string) + ("b", int.?)).isEmpty
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

            val result: Seq[(JsPath, Result)] = o.validate(JsObjSpec("a" -> string(minLength = 10),
                                                                     "b" -> string(maxLength = 2),
                                                                     "c" -> string(minLength = 0,
                                                                                   maxLength = 10,
                                                                                   pattern = "\\d".r
                                                                                   ),
                                                                     "d" -> enum("MALE",
                                                                                 "FEMALE"
                                                                                 )
                                                                     )
                                                           )
            findFieldResult(result,
                            empty / "a"
                            ).isInvalid(messages => messages(0) == "'too short' of length lower than minimum 10") &&
            findFieldResult(result,
                            empty / "b"
                            ).isInvalid(messages => messages(0) == "'too long' of length greater than maximum 2") &&
            findFieldResult(result,
                            empty / "c"
                            ).isInvalid(messages => messages(0) == "'123' doesn't match the pattern \\d") &&
            findFieldResult(result,
                            empty / "d"
                            ).isInvalid(messages => messages(0) == "'man' not in MALE,FEMALE")
          }
          )
  }

  property("integral errors")
  {
    check(forAll(JsObjGen("a" -> BigInt(10),
                          "b" -> BigInt(100),
                          "c" -> BigInt(10),
                          "d" -> JsArray(BigInt(5),
                                         BigInt(3),
                                         BigInt(7)
                                         )
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("a" -> integral(minimum = 11,
                                                              maximum = 0,
                                                              multipleOf = 11
                                                              ),
                                              "b" -> integralGT(exclusiveMinimum = 100),
                                              "c" -> integralLT(exclusiveMaximum = 10),
                                              "d" -> JsArraySpec(integralGT(exclusiveMinimum = 3,
                                                                            multipleOf = 2
                                                                            ),
                                                                 integralLT(exclusiveMaximum = 5,
                                                                            multipleOf = 2
                                                                            ),
                                                                 integral(value => if (value % 10 == 0) true else false,
                                                                          value => s"$value is not multiple of 10"
                                                                          )
                                                                 )
                                              )
                                    )
            findFieldResult(result,
                            "a"
                            ).isInvalid(messages => messages(0) == "10 is lower than minimum 11" &&
                                                    messages(1) == "10 is greater than maximum 0" &&
                                                    messages(2) == "10 is not multiple of 11"
                                        ) &&
            findFieldResult(result,
                            "b"
                            ).isInvalid(messages => messages(0) == "100 is equal to the exclusiveMinimum 100"
                                        ) &&
            findFieldResult(result,
                            "c"
                            ).isInvalid(messages => messages(0) == "10 is equal to the exclusiveMaximum 10"
                                        ) &&
            findFieldResult(result,
                            "d" / 0
                            ).isInvalid(messages => messages(0) == "5 is not multiple of 2"
                                        ) &&
            findFieldResult(result,
                            "d" / 1
                            ).isInvalid(messages => messages(0) == "3 is not multiple of 2"
                                        ) &&
            findFieldResult(result,
                            "d" / 2
                            ).isInvalid(messages => messages(0) == "7 is not multiple of 10"
                                        )
          }
          )
  }

  property("decimal errors")
  {
    check(forAll(JsObjGen("a" -> BigDecimal(10),
                          "b" -> BigDecimal(100),
                          "c" -> BigDecimal(10),
                          "d" -> JsArray(BigDecimal(5),
                                         BigDecimal(3),
                                         BigDecimal(7)
                                         )
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("a" -> decimal(minimum = 11,
                                                             maximum = 0,
                                                             multipleOf = 11
                                                             ),
                                              "b" -> decimalGT(exclusiveMinimum = 100),
                                              "c" -> decimalLT(exclusiveMaximum = 10),
                                              "d" -> JsArraySpec(decimalGT(exclusiveMinimum = 3,
                                                                           multipleOf = 2
                                                                           ),
                                                                 decimalLT(exclusiveMaximum = 5,
                                                                           multipleOf = 2
                                                                           ),
                                                                 decimal(value => if (value % 10 == 0) true else false,
                                                                         value => s"$value is not multiple of 10"
                                                                         )
                                                                 )
                                              )
                                    )
            findFieldResult(result,
                            "a"
                            ).isInvalid(messages => messages(0) == "10 is lower than minimum 11" &&
                                                    messages(1) == "10 is greater than maximum 0" &&
                                                    messages(2) == "10 is not multiple of 11"
                                        ) &&
            findFieldResult(result,
                            "b"
                            ).isInvalid(messages => messages(0) == "100 is equal to the exclusiveMinimum 100"
                                        ) &&
            findFieldResult(result,
                            "c"
                            ).isInvalid(messages => messages(0) == "10 is equal to the exclusiveMaximum 10"
                                        ) &&
            findFieldResult(result,
                            "d" / 0
                            ).isInvalid(messages => messages(0) == "5 is not multiple of 2"
                                        ) &&
            findFieldResult(result,
                            "d" / 1
                            ).isInvalid(messages => messages(0) == "3 is not multiple of 2"
                                        ) &&
            findFieldResult(result,
                            "d" / 2
                            ).isInvalid(messages => messages(0) == "7 is not multiple of 10"
                                        )
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

            val result = o.validate(JsObjSpec("a" -> TRUE,
                                              "b" -> FALSE,
                                              "c" -> boolean
                                              )
                                    )

            findFieldResult(result,
                            "a"
                            ).isInvalid(messages => messages(0) == "1 is not true"
                                        ) &&
            findFieldResult(result,
                            "b"
                            ).isInvalid(messages => messages(0) == "2 is not false"
                                        ) &&
            findFieldResult(result,
                            "c"
                            ).isInvalid(messages => messages(0) == "\"hi\" is not a boolean"
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
              "h" -> decimal(minimum = 11,
                             maximum = 0,
                             multipleOf = 11
                             ),
              "j" -> decimal
              )
                                    )

            findFieldResult(result,
                            empty / "h"
                            ).isInvalid(messages => messages(0) == "10 is lower than minimum 11" &&
                                                    messages(1) == "10 is greater than maximum 0" &&
                                                    messages(2) == "10 is not multiple of 11"
                                        ) &&
            findFieldResult(result,
                            empty / "j"
                            ).isInvalid(messages => messages(0) == "1 is not a decimal number")

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

            val result = o.validate(JsObjSpec("e" -> int(minimum = 11,
                                                         maximum = 0,
                                                         multipleOf = 11
                                                         ),
                                              "j" -> int,
                                              "k" -> intLT(exclusiveMaximum = 11
                                                           ),
                                              )
                                    )

            findFieldResult(result,
                            empty / "e"
                            ).isInvalid(messages => messages(0) == "10 is lower than minimum 11" &&
                                                    messages(1) == "10 is greater than maximum 0" &&
                                                    messages(2) == "10 is not multiple of 11"
                                        ) &&
            findFieldResult(result,
                            empty / "j"
                            ).isInvalid(messages => messages(0) == "1.2 is not an int number (32 bits)") &&
            findFieldResult(result,
                            empty / "k"
                            ).isInvalid(messages => messages(0) == "11 is equal to the exclusiveMaximum 11")

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

            val result = o.validate(JsObjSpec("a" -> obj(minKeys = 4,
                                                         maxKeys = 2,
                                                         required = List("e",
                                                                         "f"
                                                                         ),
                                                         dependentRequired = List(("a", List("d")))
                                                         ),
                                              "b" -> obj(o => o("d").isStr,
                                                         v => s"$v is not valid"
                                                         )
                                              )
                                    )
            findFieldResult(result,
                            "a"
                            ).isInvalid(messages => messages(0) == "The number of keys 3 is lower than the minimum 4" &&
                                                    messages(1) == "The number of keys 3 is greater than the maximum 2" &&
                                                    messages(2) == "The key e doesn't exist" &&
                                                    messages(3) == "The key f doesn't exist" &&
                                                    messages(4) == "The key a exists but d doesn't exist"
                                        ) &&
            findFieldResult(result,
                            "b"
                            ).isInvalid(messages => messages(0) == "{\"d\":false} is not valid"
                                        )

          }
          )
  }

  property("long errors")
  {
    check(forAll(JsObjGen("f" -> 10L,
                          "l" -> 11L,
                          "c" -> 3L,
                          "d" -> 4L
                          )
                 )
          { o =>

            val result = o.validate(JsObjSpec("f" -> long(minimum = 11,
                                                          maximum = 0,
                                                          multipleOf = 11
                                                          ),
                                              "l" -> longLT(exclusiveMaximum = 11
                                                            ),
                                              "c" -> longGT(exclusiveMinimum = 5),
                                              "d" -> longGT(exclusiveMinimum = 4)
                                              )
                                    )

            findFieldResult(result,
                            empty / "f"
                            ).isInvalid(messages => messages(0) == "10 is lower than minimum 11"
                                                    && messages(1) == "10 is greater than maximum 0"
                                                    && messages(2) == "10 is not multiple of 11"
                                        ) &&
            findFieldResult(result,
                            empty / "l"
                            ).isInvalid(messages => messages(0) == "11 is equal to the exclusiveMaximum 11") &&
            findFieldResult(result,
                            empty / "c"
                            ).isInvalid(messages => messages(0) == "3 is lower than minimum 5") &&
            findFieldResult(result,
                            empty / "d"
                            ).isInvalid(messages => messages(0) == "4 is equal to the exclusiveMinimum 4")
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
              JsObjSpec("l" -> arrayOfIntegral(minItems = 4,
                                               maxItems = 1,
                                               unique = true
                                               ),
                        "m" -> arrayOfDecimal(minItems = 4,
                                              maxItems = 1,
                                              unique = true
                                              ),
                        "n" -> arrayOf(enum("cat",
                                            "dog",
                                            "bird"
                                            ),
                                       "It's not an array of animals"
                                       ),
                        "o" -> arrayOfNumber(minItems = 5,
                                             maxItems = 2,
                                             unique = true
                                             )
                        )
              )

            findFieldResult(result,
                            "l"
                            ).isInvalid(messages => messages(0) == "Array of length 3 lower than minimum 4" &&
                                                    messages(1) == "Array of length 3 greater than maximum 1" &&
                                                    messages(2) == "Array contains duplicates"
                                        ) &&
            findFieldResult(result,
                            "m"
                            ).isInvalid(messages => messages(0) == "Array of length 3 lower than minimum 4" &&
                                                    messages(1) == "Array of length 3 greater than maximum 1" &&
                                                    messages(2) == "Array contains duplicates"
                                        ) &&
            findFieldResult(result,
                            "n"
                            ).isInvalid(messages => messages(0) == "It's not an array of animals"
                                        ) &&
            findFieldResult(result,
                            "o"
                            ).isInvalid(messages => messages(0) == "Array of length 4 lower than minimum 5" &&
                                                    messages(1) == "Array of length 4 greater than maximum 2" &&
                                                    messages(2) == "Array contains duplicates"
                                        )
          }
          )
  }

  def findByPath(result: Seq[(JsPath, Result)],
                 path  : JsPath
                ): Option[(JsPath, Result)] =
  {
    result.find((pair: (JsPath, Result)) => pair._1 == path)

  }

  def findFieldResult(result: Seq[(JsPath, Result)],
                      path  : JsPath
                     ): Result =
  {
    findByPath(result,
               path
               ).get._2

  }

}


