package jsonvalues.specifications


import jsonvaluesgen.{JsArrGen, JsObjGen, RandomJsArrayGen, RandomJsObjGen}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import jsonvalues.Implicits._
import jsonvalues.spec.{JsArraySpec, JsBoolSpec, JsIntSpec, JsLongSpec, JsNumberSpec, JsObjSpec, JsStringSpec, JsValueSpec}
import jsonvalues.spec.JsObjSpec.obj
import jsonvaluesgen.Implicits._
import jsonvalues.spec.JsStringSpec._
import jsonvalues.spec.JsBoolSpec._
import jsonvaluesgen.Implicits._
import jsonvalues.spec.JsIntSpec._
import jsonvalues.spec.JsArraySpec._
import jsonvalues.spec.JsBoolSpec.boolean
import jsonvalues.spec.JsLongSpec.{long, longGT, longGTE, longLT, longLTE}
import jsonvalues.spec.JsNumberSpec.{decimal, decimalLTE, integral}
import jsonvalues.spec.JsValueSpec.{and, any}
import jsonvalues.{JsArray, JsArrayValidator, JsArrayValidator_?, JsBool, JsObjValidator, JsObjValidator_?, JsStr, JsValue}
import jsonvaluesgen.JsArrGen.noneEmptyOf

class JsObjValidationSpec extends BasePropSpec
{

  property("1")
  {
    check(forAll(

      JsObjGen("a" -> JsArray(1,
                              2,
                              3
                              ),
               "b" -> Gen.alphaStr,
               "c" -> Arbitrary.arbitrary[Int],
               "d" -> JsArrGen(Gen.choose(1,
                                          10
                                          ),
                               Gen.alphaStr,
                               JsArrGen.ofN(10,
                                            Gen.alphaStr
                                            )
                               ),
               "e" -> JsObjGen("f" -> "male")
               )
      )
          {
            obj =>
              val value = obj.validate(JsObjValidator("a" -> arrayOfInt(maxItems = 3,
                                                                        minItems = 3,
                                                                        unique = true
                                                                        ).?,
                                                      "b" -> string.?,
                                                      "c" -> int,
                                                      "d" -> JsArrayValidator_?(int,
                                                                                string,
                                                                                arrayOfString(minItems = 10,
                                                                                              maxItems = 10
                                                                                              )
                                                                                ),
                                                      "e" -> JsObjValidator_?("f" -> enum("male",
                                                                                          "female"
                                                                                          )
                                                                              )
                                                      )
                                       )
              value.isEmpty
          }
          )
  }

  property("2")
  {
    check(forAll(

      JsObjGen("a" -> 1,
               "b" -> noneEmptyOf(Arbitrary.arbitrary[Long]),
               "c" -> noneEmptyOf(Arbitrary.arbitrary[Int]),
               "d" -> noneEmptyOf(Arbitrary.arbitrary[BigInt]),
               "e" -> noneEmptyOf(Arbitrary.arbitrary[BigDecimal]),
               "f" -> JsArrGen(Gen.choose(1,
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
               "j" -> Gen.oneOf("a",
                                "b"
                                ),
               "k" -> RandomJsArrayGen(),
               "l" -> JsObjGen("m" -> JsArrGen(Gen.choose[Long](0,
                                                                1000
                                                                )
                                               )
                               ),
               "m" -> JsArrGen.of(Gen.asciiStr),
               "n" -> JsArray(1,
                              2,
                              3
                              ),
               "o" -> Gen.choose(1,
                                 10
                                 ),
               "p" -> Gen.choose(0,
                                 10
                                 ),
               "q" -> Gen.choose[Long](1,
                                       10
                                       ),
               "r" -> Gen.choose[Long](0,
                                       10
                                       ),
               "s" -> JsArrGen(BigDecimal(1.5),
                               BigInt(10),
                               1.5
                               ),
               "t" -> JsObjGen("u" -> "123")
               )

      )
          {
            objGenerated =>

              val errors = objGenerated.validate(JsObjValidator("a" -> 1,
                                                                "b" -> arrayOfLong(minItems = 1),
                                                                "c" -> arrayOfInt(minItems = 1),
                                                                "d" -> arrayOfIntegral(minItems = 1),
                                                                "e" -> arrayOfDecimal(minItems = 1),
                                                                "f" -> JsArrayValidator_?(int(minimum = 1,
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

                                                                                          ),
                                                                "j" -> enum("a",
                                                                            "b"
                                                                            ),
                                                                "k" -> array,
                                                                "l" -> JsObjValidator("m" -> JsArrayValidator(long(minimum = 0,
                                                                                                                   maximum = 1000
                                                                                                                   )
                                                                                                              )
                                                                                      ),
                                                                "m" -> arrayOf(string),
                                                                "n" -> array(array => array.length() == 3,
                                                                             (value: JsValue) => s"$value is not an array of length 3"
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
                                                                           ),
                                                                "q" -> and(longGT(exclusiveMinimum = 0,
                                                                                  multipleOf = 1
                                                                                  ),
                                                                           longLTE(10,
                                                                                   multipleOf = 1
                                                                                   )
                                                                           ),
                                                                "r" -> and(longGTE(minimum = 0,
                                                                                   multipleOf = 1
                                                                                   ),
                                                                           longLT(11,
                                                                                  multipleOf = 1
                                                                                  )
                                                                           ),
                                                                "s" -> JsArrayValidator(decimal(1,
                                                                                                5
                                                                                                ),
                                                                                        integral(0,
                                                                                                 20
                                                                                                 ),
                                                                                        decimalLTE(1.5)
                                                                                        ),
                                                                "t" -> JsObjValidator("u" -> string("\\d{3}".r))
                                                                ),
                                                 )
              if (errors.nonEmpty) println(errors)
              errors.isEmpty
          }
          )
  }
}
