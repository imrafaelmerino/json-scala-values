package json

import org.scalacheck.Gen

package object gen
{

  type JsPairGen = (JsPath,Gen[JsElem])

  implicit def strGenToJsStrGen(gen: Gen[String]): Gen[JsElem] = gen.map(s => JsStr(s))

  implicit def intGenToJsIntGen(gen: Gen[Int]): Gen[JsElem] = gen.map(s => JsInt(s))

  implicit def longGenToJsLongGen(gen: Gen[Long]): Gen[JsElem] = gen.map(s => JsLong(s))

  implicit def bigIntGenToJsBigIntGen(gen: Gen[BigInt]): Gen[JsElem] = gen.map(s => JsBigInt(s))

  implicit def doubleGenToJsDoubleGen(gen: Gen[Double]): Gen[JsElem] = gen.map(s => JsDouble(s))

  implicit def bigDecGenToJsBigDecGen(gen: Gen[BigDecimal]): Gen[JsElem] = gen.map(s => JsBigDec(s))

  implicit def boolGenToJsBoolGen(gen: Gen[Boolean]): Gen[JsElem] = gen.map(s => JsBool(s))

}
