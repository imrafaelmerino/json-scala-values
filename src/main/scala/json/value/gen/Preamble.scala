package json.value.gen

import org.scalacheck.Gen
import json.value._
import json.value.gen.Preamble._
import scala.language.implicitConversions

object Preamble
{

  implicit def ?(prob: Int,
                 gen : Gen[JsValue]
                ): Gen[JsValue] =
  {
    if (prob < 0 || prob > 100) throw new IllegalArgumentException("prob must be [0,100]")
    Gen.frequency((prob, gen),
                  (100 - prob, JsNothing)
                  )
  }

  implicit def ?(gen: Gen[JsValue]
                ): Gen[JsValue] = ?(50,
                                    gen
                                    )

  implicit def intToConstantGen(value: Int): Gen[JsValue] = Gen.const(JsInt(value))

  implicit def booleanToConstantGen(value: Boolean): Gen[JsValue] = Gen.const(JsBool(value))

  implicit def longToConstant(value: Long): Gen[JsValue] = Gen.const(JsLong(value))

  implicit def strToConstant(value: String): Gen[JsValue] = Gen.const(JsStr(value))

  implicit def doubleToConstant(value: Double): Gen[JsValue] = Gen.const(JsDouble(value))

  implicit def bigIntToConstant(value: BigInt): Gen[JsValue] = Gen.const(JsBigInt(value))

  implicit def jsNullToConstant(value: JsNull.type): Gen[JsValue] = Gen.const(JsNull)

  implicit def bigDecToConstant(value: BigDecimal): Gen[JsValue] = Gen.const(JsBigDec(value))

  implicit def jsObjDecToConstant(value: JsObj): Gen[JsValue] = Gen.const(value)

  implicit def jsArrayDecToConstant(value: JsArray): Gen[JsValue] = Gen.const(value)

  implicit def strGenToJsStrGen(gen: Gen[String]): Gen[JsStr] = gen.map(s => JsStr(s))

  implicit def intGenToJsIntGen(gen: Gen[Int]): Gen[JsValue] = gen.map(s => JsInt(s))

  implicit def longGenToJsLongGen(gen: Gen[Long]): Gen[JsValue] = gen.map(s => JsLong(s))

  implicit def bigIntGenToJsBigIntGen(gen: Gen[BigInt]): Gen[JsValue] = gen.map(s => JsBigInt(s))

  implicit def doubleGenToJsDoubleGen(gen: Gen[Double]): Gen[JsValue] = gen.map(s => JsDouble(s))

  implicit def bigDecGenToJsBigDecGen(gen: Gen[BigDecimal]): Gen[JsValue] = gen.map(s => JsBigDec(s))

  implicit def boolGenToJsBoolGen(gen: Gen[Boolean]): Gen[JsValue] = gen.map(s => JsBool(s))

}
