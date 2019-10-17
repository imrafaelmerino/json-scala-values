package json

import java.io.IOException

import com.fasterxml.jackson.core.{JsonParseException, JsonParser}

sealed trait JsNumber extends JsValue
{

  override def isArr: Boolean = false

  override def isObj: Boolean = false

  override def isStr: Boolean = false

  override def isBool: Boolean = false

  override def isNull: Boolean = false

  override def isNumber: Boolean = true

  override def isNothing: Boolean = false

}

case class JsInt(value: Int) extends JsNumber
{

  override def isInt: Boolean = true

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

}

case class JsDouble(value: Double) extends JsNumber
{

  override def isInt: Boolean = true

  override def isLong: Boolean = false

  override def isDouble: Boolean = true

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

}

case class JsLong(value: Long) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = true

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

}


case class JsBigDec(value: BigDecimal) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = true
}


case class JsBigInt(value: BigInt) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = true

  override def isBigDec: Boolean = false
}

object JsNumber
{

  @throws[IOException]
  private[json] def apply(parser: JsonParser) =
    try JsInt(parser.getIntValue)
    catch
    {
      case _: JsonParseException =>
        try JsLong(parser.getLongValue)
        catch
        {
          case _: JsonParseException => JsBigInt(parser.getBigIntegerValue)
        }
    }

}

