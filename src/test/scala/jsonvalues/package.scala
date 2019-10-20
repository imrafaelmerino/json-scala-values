package jsonvalues

import org.scalacheck.{Arbitrary, Gen}

package object pkg
{

  val ALPHABET: Array[String] = "abcdefghijklmnopqrstuvwzyz".split("")

  case class FreqTypeOfPrimitive(str: Int = 2,
                                 number: Int = 2,
                                 bool: Int = 1,
                                 nullFreq: Int = 1
                                )

  case class FreqTypeOfNumber(int: Int = 1,
                              long: Int = 1,
                              double: Int = 1,
                              bigInt: Int = 1,
                              bigDec: Int = 1
                             )

  case class FreqObjPair(primitive: Int = 5,
                         obj      : Int = 1,
                         arr      : Int = 1
                        )

  case class FreqTypeOfArr(number: Int = 10,
                           str: Int = 10,
                           bool: Int = 10,
                           primitive: Int = 10,
                           obj      : Int = 2,
                           arr      : Int = 1,
                           int      : Int = 10,
                           double   : Int = 10,
                           long     : Int = 10
                          )

  case class JsPrimitiveGen(private val strGen: Gen[String] = Gen.oneOf(ALPHABET),
                            private val intGen: Gen[Int] = Arbitrary.arbitrary[Int],
                            private val longGen: Gen[Long] = Arbitrary.arbitrary[Long],
                            private val doubleGen: Gen[Double] = Arbitrary.arbitrary[Double],
                            private val floatGen : Gen[Float] = Arbitrary.arbitrary[Float],
                            private val boolGen  : Gen[Boolean] = Arbitrary.arbitrary[Boolean],
                            private val bigIntGen: Gen[BigInt] = Arbitrary.arbitrary[BigInt],
                            private val bigDecGen: Gen[BigDecimal] = Arbitrary.arbitrary[BigDecimal],
                            private val numberTypeFreq: FreqTypeOfNumber = FreqTypeOfNumber(),
                            private val primitiveTypeFreq: FreqTypeOfPrimitive = FreqTypeOfPrimitive(),
                           )
  {

    val str: Gen[JsStr] = strGen.map(it => JsStr(it))

    val int: Gen[JsInt] = intGen.map(it => JsInt(it))

    val long: Gen[JsLong] = longGen.map(it => JsLong(it))

    val double: Gen[JsDouble] = doubleGen.map(it => JsDouble(it))

    val bigInt: Gen[JsBigInt] = bigIntGen.map(it => JsBigInt(it))

    val bool: Gen[JsBool] = boolGen.map(it => JsBool(it))

    val bigDec: Gen[JsBigDec] = bigDecGen.map(it => JsBigDec(it))

    val number: Gen[JsNumber] = Gen.frequency((numberTypeFreq.int, int),
                                              (numberTypeFreq.long, long),
                                              (numberTypeFreq.double, double),
                                              (numberTypeFreq.bigDec, bigDec),
                                              (numberTypeFreq.bigInt, bigInt)
                                              )

    val primitive: Gen[JsValue] = Gen.frequency((primitiveTypeFreq.str, str),
                                                (primitiveTypeFreq.number, number),
                                                (primitiveTypeFreq.bool, bool),
                                                (primitiveTypeFreq.nullFreq, Gen.const(JsNull))
                                                )

  }

}
