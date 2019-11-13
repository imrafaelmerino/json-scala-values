package value

import value.JsPath.empty
import value.spec.JsStringSpecs._
import value.spec.JsIntSpecs._
import value.spec.JsLongSpecs._
import value.spec.JsObjSpecs._
import value.spec.JsArraySpecs._
import value.spec.JsNumberSpecs._

import scala.language.implicitConversions
import value.spec.{JsBoolSpecs, JsSpec, Invalid, Valid, JsValueSpec}

object Implicits
{
  implicit def str2Spec(cons: String): JsSpec = string(s => s == cons,
                                                       (value: String) => s"$value not equals to $cons"
                                                       )


  implicit def int2Spec(cons: Int): JsSpec = int(s => s == cons,
                                                 (value: Int) => s"$value is not equals to $cons"
                                                 )


  implicit def long2Spec(cons: Long): JsSpec = long(s => s == cons,
                                                    (value: Long) => s"$value is not equals to $cons"
                                                    )


  implicit def bigInt2Spec(cons: BigInt): JsSpec = integral(s => s == cons,
                                                            (value: BigInt) => s"$value is not equals to $cons"
                                                            )


  implicit def bigDec2Spec(cons: BigDecimal): JsSpec = decimal(s => s == cons,
                                                               (value: BigDecimal) => s"$value is not equals to $cons"
                                                               )


  implicit def double2Spec(cons: Double): JsSpec = decimal(s => s == BigDecimal(cons),
                                                           (value: BigDecimal) => s"$value is not equals to $cons"

                                                           )

  implicit def obj2Spec(cons: JsObj): JsSpec = obj(s => s == cons,
                                                   (value: JsObj) => s"$value is not equals to $cons"
                                                   )


  implicit def arr2Spec(cons: JsArray): JsSpec = array(s => s == cons,
                                                       (value: JsArray) => s"$value is not equals to $cons"
                                                       )

  //  implicit def spec2ValueSpec(validator: JsSpec): JsValueSpec = validator.asInstanceOf[JsValueSpec]

  implicit def boolean2Spec(cons: Boolean): JsSpec = if (cons) JsBoolSpecs.TRUE else JsBoolSpecs.FALSE

  implicit def null2Spec(cons: JsNull.type): JsSpec = spec.JsValueSpec((value: JsValue) => if (value.isNull) Valid else Invalid("not null"))

  implicit def nothing2Spec(cons: JsNothing.type): JsSpec = spec.JsValueSpec((value: JsValue) => if (value.isNothing) Valid else Invalid("exists value"))

  implicit def keyJsValueToJsPair[E <: JsValue](pair: (String, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexJsValueToJsPair[E <: JsValue](pair: (Int, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyStr2JsPair(pair: (String, String)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyInt2JsPair(pair: (String, Int)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyLong2JsPair(pair: (String, Long)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyDouble2JsPair(pair: (String, Double)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBigDec2JsPair(pair: (String, BigDecimal)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBigInt2JsPair(pair: (String, BigInt)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBool2JsPair(pair: (String, Boolean)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyNull2JsPair(pair: (String, Null)): (JsPath, JsValue) = (pair._1, JsNull)

  implicit def str2JsValue(str: String): JsStr = JsStr(str)

  implicit def bool2JsValue(bool: Boolean): JsBool = if (bool) JsBool.TRUE else JsBool.FALSE

  implicit def long2JsValue(n: Long): JsLong = JsLong(n)

  implicit def bigDec2JsValue(n: BigDecimal): JsBigDec = JsBigDec(n)

  implicit def bigInt2JsValue(n: BigInt): JsBigInt = JsBigInt(n)

  implicit def double2JsValue(n: Double): JsDouble = JsDouble(n)

  implicit def int2JsValue(n: Int): JsInt = JsInt(n)

  implicit def str2JsPath(name: String): JsPath =
  {
    empty / name
  }

  implicit def int2JsPath(n: Int): JsPath =
  {
    empty / n
  }

}
