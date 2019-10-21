package jsonvalues

import scala.language.implicitConversions

trait JsValue
{

  val isJson: Boolean = isObj || isArr

  def isStr: Boolean

  def isObj: Boolean

  def isArr: Boolean

  def isBool: Boolean

  def isNumber: Boolean

  def isInt: Boolean

  def isLong: Boolean

  def isDouble: Boolean

  def isBigInt: Boolean

  def isBigDec: Boolean

  def isNull: Boolean

  def isNothing: Boolean

}

object JsValue
{

  implicit def asJson(e: JsValue): Json[_] = e.asInstanceOf[Json[_]]

}



