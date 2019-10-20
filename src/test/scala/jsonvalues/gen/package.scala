package jsonvalues

import org.scalacheck.Gen

package object gen
{

  type JsPairGen = (JsPath,Gen[JsValue])

  implicit def strGenToJsStrGen(gen: Gen[String]): Gen[JsValue] = gen.map(s => JsStr(s))

  implicit def intGenToJsIntGen(gen: Gen[Int]): Gen[JsValue] = gen.map(s => JsInt(s))

  implicit def longGenToJsLongGen(gen: Gen[Long]): Gen[JsValue] = gen.map(s => JsLong(s))

  implicit def bigIntGenToJsBigIntGen(gen: Gen[BigInt]): Gen[JsValue] = gen.map(s => JsBigInt(s))

  implicit def doubleGenToJsDoubleGen(gen: Gen[Double]): Gen[JsValue] = gen.map(s => JsDouble(s))

  implicit def bigDecGenToJsBigDecGen(gen: Gen[BigDecimal]): Gen[JsValue] = gen.map(s => JsBigDec(s))

  implicit def boolGenToJsBoolGen(gen: Gen[Boolean]): Gen[JsValue] = gen.map(s => JsBool(s))

}
