import org.scalacheck.{Arbitrary, Gen}
import value.{JsBigDec, JsBigInt, JsDouble, JsInt, JsLong, JsNumber, JsObjParser, Json}
import value.spec.{*, JsArraySpec, JsObjSpec}
import value.Preamble._
import value.spec.JsNumberSpecs._
import value.spec.JsStrSpecs._
import value.spec.JsBoolSpecs._
import value.spec.JsArraySpecs._
import value.spec.JsObjSpecs._
import value.spec.JsSpecs._
import valuegen.Preamble._
import valuegen.{JsArrayGen, JsObjGen, RandomJsArrayGen, RandomJsObjGen}

val gen = JsObjGen("a" -> Arbitrary.arbitrary[Int],
                   "b" -> Gen.alphaStr,
                   "c" -> Gen.oneOf(true,
                                    false
                                    ),
                   "d" -> Arbitrary.arbitrary[BigDecimal],
                   "e" -> Arbitrary.arbitrary[BigInt],
                   "f" -> Gen.oneOf[JsNumber](Arbitrary.arbitrary[Int].map(JsInt),
                                              Arbitrary.arbitrary[Long].map(JsLong),
                                              Arbitrary.arbitrary[Double].map(JsDouble),
                                              Arbitrary.arbitrary[BigInt].map(JsBigInt),
                                              Arbitrary.arbitrary[BigDecimal].map(JsBigDec)
                                              ),
                   "g" -> Arbitrary.arbitrary[Long],
                   "h" -> RandomJsObjGen(),
                   "i" -> JsArrayGen(Arbitrary.arbitrary[Int],
                                     Arbitrary.arbitrary[String]
                                     ),
                   "j" -> JsObjGen("a" -> JsArrayGen.of(Arbitrary.arbitrary[String]),
                                   "b" -> JsArrayGen.of(Arbitrary.arbitrary[Int]),
                                   "c" -> JsArrayGen.of(Arbitrary.arbitrary[Long]),
                                   "d" -> JsArrayGen.of(Arbitrary.arbitrary[BigInt]),
                                   "e" -> RandomJsArrayGen(),
                                   "f" -> JsArrayGen.of(JsObjGen("a" -> Arbitrary.arbitrary[String],
                                                                 "b" -> JsArrayGen.of(Gen.oneOf(true,
                                                                                                false
                                                                                                )
                                                                                      ),
                                                                 "c" -> Gen.const("constant")
                                                                 )
                                                        ),
                                   "g" -> JsArrayGen.of(RandomJsObjGen())
                                   ),
                   "k" -> Gen.oneOf("a",
                                    "b",
                                    "c"
                                    )
                   )

val spec = JsObjSpec("a" -> int,
                     "b" -> str,
                     "c" -> bool,
                     "d" -> decimal,
                     "e" -> integral,
                     "f" -> number,
                     "g" -> long,
                     "h" -> obj,
                     "i" -> JsArraySpec(int,
                                        any,
                                        str
                                        ),
                     "j" -> JsObjSpec("a" -> arrayOfStr,
                                      "b" -> arrayOfInt,
                                      "c" -> arrayOfLong,
                                      "d" -> arrayOfIntegral,
                                      "e" -> array,
                                      "f" -> arrayOf(JsObjSpec("a" -> str,
                                                               "b" -> arrayOfBool,
                                                               "c" -> "constant"
                                                               )
                                                     ),
                                      "g" -> arrayOfObj
                                      ),
                     "k" -> enum("a",
                                 "b",
                                 "c"
                                 ),
                     * -> any
                     )

val parser = JsObjParser(spec)





