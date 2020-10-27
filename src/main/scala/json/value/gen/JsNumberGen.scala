package json.value.gen

import org.scalacheck.{Arbitrary, Gen}
import json.value._
import json.value.gen.Preamble._

object JsNumberGen
{

  val intGen: Gen[JsInt] = Arbitrary.arbitrary[Int].map(it=>JsInt(it))
  val doubleGen: Gen[JsDouble] = Arbitrary.arbitrary[Double].map(it=>JsDouble(it))
  val longGen: Gen[JsLong] = Arbitrary.arbitrary[Long].map(it=>JsLong(it))
  val bigDecGen: Gen[JsBigDec] = Arbitrary.arbitrary[BigDecimal].map(it=>JsBigDec(it))
  val bigIntGen: Gen[JsBigInt] = Arbitrary.arbitrary[BigInt].map(it=>JsBigInt(it))

  def apply(): Gen[JsNumber] = Gen.oneOf(intGen,
                                         doubleGen,
                                         longGen,
                                         bigDecGen,
                                         bigIntGen
                                         )


}
