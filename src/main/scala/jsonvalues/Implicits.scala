package jsonvalues

import jsonvalues.JsPath.empty
import jsonvalues.spec.JsArraySpec.array
import jsonvalues.spec.JsIntSpec.int
import jsonvalues.spec.JsLongSpec.long
import jsonvalues.spec.JsNumberSpec.{decimal, integral}
import jsonvalues.spec.JsObjSpec.obj
import jsonvalues.spec.JsStringSpec.string
import jsonvalues.spec.{JsBoolSpec, JsValueSpec}

import scala.language.implicitConversions

object Implicits
{
  implicit def strToValidator(cons: String): JsValidator = string(s => s == cons,
                                                                  (value: String) => s"$value not equals to $cons"
                                                                  )

  implicit def strToValidator(pair: (String, String)): (String, JsValidator) = (pair._1, string(s => s == pair._2,
                                                                                                (value: String) => s"$value not equals to $pair._2"
                                                                                                ))

  implicit def intToValidator(cons: Int): JsValidator = int(s => s == cons,
                                                            (value: Int) => s"$value is not equals to $cons"
                                                            )

  implicit def intToValidator(pair: (String, Int)): (String, JsValidator) = (pair._1, int(s => s == pair._2,
                                                                                          (value: Int) => s"$value is not equals to $pair._2"
                                                                                          ))

  implicit def longToValidator(cons: Long): JsValidator = long(s => s == cons,
                                                               (value: Long) => s"$value is not equals to $cons"
                                                               )

  implicit def longToValidator(pair: (String, Long)): (String, JsValidator) = (pair._1, long(s => s == pair._2,
                                                                                             (value: Long) => s"$value is not equals to $pair._2"
                                                                                             ))

  implicit def bigIntToValidator(cons: BigInt): JsValidator = integral(s => s == cons,
                                                                       (value: BigInt) => s"$value is not equals to $cons"
                                                                       )

  implicit def bigIntToValidator(pair: (String, BigInt)): (String, JsValidator) = (pair._1, integral(s => s == pair._2,
                                                                                                     (value: BigInt) => s"$value is not equals to $pair._2"
                                                                                                     ))

  implicit def bigDecToValidator(cons: BigDecimal): JsValidator = decimal(s => s == cons,
                                                                          (value: BigDecimal) => s"$value is not equals to $cons"
                                                                          )

  implicit def bigDecToValidator(pair: (String, BigDecimal)): (String, JsValidator) = (pair._1, decimal(s => s == pair._2,
                                                                                                        (value: BigDecimal) => s"$value is not equals to $pair._2"
                                                                                                        ))

  implicit def doubleToValidator(cons: Double): JsValidator = decimal(s => s == BigDecimal(cons),
                                                                      (value: BigDecimal) => s"$value is not equals to $cons"

                                                                      )

  implicit def doubleToValidator(pair: (String, Double)): (String, JsValidator) =
    (pair._1, decimal(s => s == pair._2,
                      (value: BigDecimal) => s"$value is not equals to $pair._2"
                      ))

  implicit def objToValidator(cons: JsObj): JsValidator = obj(s => s == cons,
                                                              (value: JsObj) => s"$value is not equals to $cons"
                                                              )

  implicit def objToValidator(pair: (String, JsObj)): (String, JsValidator) = (pair._1, obj(s => s == pair._2,
                                                                                            (value: JsObj) => s"$value is not equals to $pair._2"
                                                                                            ))

  implicit def arrToValidator(cons: JsArray): JsValidator = array(s => s == cons,
                                                                  (value: JsArray) => s"$value is not equals to $cons"
                                                                  )

  implicit def arrToValidator(pair: (String, JsArray)): (String, JsValidator) = (pair._1, array(s => s == pair._2,
                                                                                                (value: JsArray) => s"$value is not equals to $pair._2"
                                                                                                ))

  implicit def toJsValueValidator(validator: JsValidator): JsValueValidator = validator.asInstanceOf[JsValueValidator]

  implicit def booleanToValidator(cons: Boolean): JsValidator = if (cons) JsBoolSpec.TRUE else JsBoolSpec.FALSE

  implicit def booleanToValidator(pair: (String, JsBool)): (String, JsValidator) = (pair._1, if (pair._2.value) JsBoolSpec.TRUE else JsBoolSpec.FALSE)

  implicit def nullToValidator(cons: jsonvalues.JsNull.type): JsValidator = JsValueValidator((value: JsValue) => if (value.isNull) JsValueOk else JsValueError("not null"))

  implicit def nullToValidator(pair: (String, JsNull.type)): (String, JsValidator) = (pair._1, JsValueSpec.`null`)

  implicit def nothingToValidator(cons: jsonvalues.JsNothing.type): JsValidator = JsValueValidator((value: JsValue) => if (value.isNothing) JsValueOk else JsValueError("exists value"))

  implicit def nothingToValidator(pair: (String, jsonvalues.JsNothing.type)): (String, JsValidator) = (pair._1, JsValueValidator((value: JsValue) => if (value.isNothing) JsValueOk else JsValueError("exists value")))

  implicit def keyJsValueToJsPair[E <: JsValue](pair: (String, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexJsValueToJsPair[E <: JsValue](pair: (Int, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyStrToJsPair(pair: (String, String)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexStrToJsPair(pair: (Int, String)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyIntToJsPair(pair: (String, Int)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexIntToJsPair(pair: (Int, Int)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyLongToJsPair(pair: (String, Long)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexLongToJsPair(pair: (Int, Long)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyDoubleToJsPair(pair: (String, Double)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexDoubleToJsPair(pair: (Int, Double)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBigDecToJsPair(pair: (String, BigDecimal)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexBigDecToJsPair(pair: (Int, BigDecimal)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBoolToJsPair(pair: (String, Boolean)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexBollToJsPair(pair: (Int, Boolean)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyNullToJsPair(pair: (String, Null)): (JsPath, JsValue) = (pair._1, JsNull)

  implicit def indexNullToJsPair(pair: (Int, Null)): (JsPath, JsValue) = (pair._1, JsNull)

  implicit def strToJsValue(str: String): JsStr = JsStr(str)

  implicit def boolToJsValue(bool: Boolean): JsBool = if (bool) JsBool.TRUE else JsBool.FALSE

  implicit def longToToJsValue(n: Long): JsLong = JsLong(n)

  implicit def bigDecToJsValue(n: BigDecimal): JsBigDec = JsBigDec(n)

  implicit def bigIntToJsValue(n: BigInt): JsBigInt = JsBigInt(n)

  implicit def doubleToJsValue(n: Double): JsDouble = JsDouble(n)

  implicit def intToJsValue(n: Int): JsInt = JsInt(n)

  implicit def fromStr(name: String): JsPath =
  {
    empty / name
  }

  implicit def fromInt(n: Int): JsPath =
  {
    empty / n
  }

  //  val * : (String,JsValidator) = (,)
}
