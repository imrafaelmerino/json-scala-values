package jsonvalues

import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.core.JsonParser

import scala.util.{Success, Try}

/**
 * Represents a number
 */
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

/**
 * Represents a number of type `Int`
 *
 * @param value the value of the number
 */
case class JsInt(value: Int) extends JsNumber
{

  override def isInt: Boolean = true

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsInt(n) => value == n
      case JsLong(n) => value.toLong == n
      case JsBigInt(n) => Try(n.bigInteger.intValueExact) match
      {
        case Success(m) => value == m
        case _ => false
      }
      case JsDouble(n) => value.toDouble == n
      case JsBigDec(n) => Try(n.toIntExact) match
      {
        case Success(m) => value == m
        case _ => false
      }
      case _ => false
    }

  }

  override def hashCode(): Int = value.hashCode()
}

/**
 * Represents a number of type `Double`
 *
 * @param value the value of the number
 */
case class JsDouble(value: Double) extends JsNumber
{

  override def isInt: Boolean = true

  override def isLong: Boolean = false

  override def isDouble: Boolean = true

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsInt(n) => value == n.toDouble
      case JsLong(n) => value == n.toDouble
      case JsBigInt(n) => BigDecimal(value).toBigIntExact match
      {
        case Some(m) => n == m
        case _ => false
      }
      case JsDouble(n) => value == n
      case JsBigDec(n) => BigDecimal(value) == n
      case _ => false
    }
  }

  override def hashCode(): Int =
  {
    val decimal = BigDecimal(value)
    Try(decimal.toIntExact) match
    {
      case Success(n) => n.hashCode()
      case _ => Try(decimal.toLongExact) match
      {
        case Success(n) => n.hashCode()
        case _ => decimal.toBigIntExact match
        {
          case Some(n) => n.hashCode()
          case _ => decimal.hashCode()
        }
      }
    }
  }
}

/**
 * Represents a number of type `Long`
 *
 * @param value the value of the number
 */
case class JsLong(value: Long) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = true

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString


  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsInt(n) => value == n.toLong
      case JsLong(n) => value == n
      case JsBigInt(n) => Try(n.bigInteger.longValueExact()) match
      {
        case Success(m) => value == m
        case _ => false
      }
      case JsDouble(n) => value.toDouble == n
      case JsBigDec(n) => Try(n.toLongExact) match
      {
        case Success(m) => value == m
        case _ => false
      }
      case _ => false
    }
  }

  override def hashCode(): Int = Try(Math.toIntExact(value)) match
  {
    case Success(n) => n.hashCode()
    case _ => value.hashCode()
  }


}

/**
 * Represents a number of type `BigDecimal`
 *
 * @param value the value of the number
 */
case class JsBigDec(value: BigDecimal) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = true

  override def toString: String = value.toString

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsInt(n) => Try(value.toIntExact) match
      {
        case Success(m) => n == m
        case _ => false
      }
      case JsLong(n) => Try(value.toLongExact) match
      {
        case Success(m) => n == m
        case _ => false
      }
      case JsBigInt(n) => value.toBigIntExact match
      {
        case Some(m) => n == m
        case _ => false
      }
      case JsDouble(n) => BigDecimal(n) == value
      case JsBigDec(n) => value == n
      case _ => false
    }
  }

  override def hashCode(): Int =
  {
    Try(value.toIntExact) match
    {
      case Success(n) => n.hashCode()
      case _ => Try(value.toLongExact) match
      {
        case Success(n) => n.hashCode()
        case _ => value.toBigIntExact match
        {
          case Some(n) => n.hashCode()
          case _ => value.hashCode()
        }
      }
    }
  }
}

/**
 * Represents a number of type `BigInt`
 *
 * @param value the value of the number
 */
case class JsBigInt(value: BigInt) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = true

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsInt(n) => Try(value.bigInteger.intValueExact) match
      {
        case Success(m) => n == m
        case _ => false
      }
      case JsLong(n) => value == n
      case JsBigInt(n) => value == n
      case JsDouble(n) => BigDecimal(n).toBigIntExact match
      {
        case Some(m) => value == m
        case _ => false
      }
      case JsBigDec(n) => n.toBigIntExact match
      {
        case Some(m) => value == m
        case _ => false
      }
      case _ => false
    }
  }

  override def hashCode(): Int =
  {
    Try(value.bigInteger.intValueExact()) match
    {
      case Success(n) => n.hashCode()
      case _ => Try(value.bigInteger.longValueExact()) match
      {
        case Success(n) => n.hashCode()
        case _ => value.hashCode()
      }
    }
  }
}


object JsNumber
{

  /**
   * It creates a number from a parser whose current token is a string that represents an integral number.
   * Tries to convert the number into an Int, if it doesn't fit in an Int, tries to turn it into a Long, and if it
   * doesn't fit in a Long, it converts it into a BigInt
   *
   * @param parser the parser which current token is an integral number
   * @return a JsNumber
   */
  private[jsonvalues] def apply(parser: JsonParser): JsNumber =
    try JsInt(parser.getIntValue)
    catch
    {
      case _: InputCoercionException =>
        try JsLong(parser.getLongValue)
        catch
        {
          case _: InputCoercionException => JsBigInt(parser.getBigIntegerValue)
        }
    }

}

