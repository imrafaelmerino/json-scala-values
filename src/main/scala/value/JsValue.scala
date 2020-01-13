package value

import java.io.{ByteArrayOutputStream, OutputStream}
import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.exc.InputCoercionException
import value.spec.{ArrayOfObjSpec, Invalid, JsArrayPredicate, JsArraySpec, JsObjSpec, Result}
import monocle.{Lens, Optional, Prism}

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.util.{Success, Try}

/**
 * Every element in a Json is a JsValue.
 */
sealed trait JsValue
{

  /**
   * Every implementation of this trait has an unique identifier.
   *
   * @return unique identifier of the type
   */
  def id: Int

  /**
   * returns true if this type is a decimal number
   *
   * @return {{{ isDouble || isBigDec }}}
   */
  def isDecimal: Boolean = isDouble || isBigDec

  /**
   * returns true if this type is an integral number
   *
   * @return {{{ isInt || isLong || isBigInt }}}
   */
  def isIntegral: Boolean = isInt || isLong || isBigInt

  /**
   * returns true is this type is an array or an object
   *
   * @return
   */
  def isJson: Boolean = isObj || isArr

  /**
   * returns true if this is a json that satisfy a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isJson(predicate: Json[_] =>
    Boolean
            ): Boolean = isJson && predicate(asJson)

  /**
   * returns true if this is neither an object nor an array
   *
   * @return
   */
  def isNotJson: Boolean = !isJson

  /**
   * returns true if this is a string
   *
   * @return
   */
  def isStr: Boolean

  /**
   * returns true if this is a string that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isStr(predicate: String => Boolean): Boolean = isStr && predicate(asJsStr.value)

  /**
   * returns true if this is an object
   *
   * @return
   */
  def isObj: Boolean

  /**
   * returns true if this is an object that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isObj(predicate: JsObj => Boolean): Boolean = isObj && predicate(asJsObj)

  /**
   * returns true if this is an array
   *
   * @return
   */
  def isArr: Boolean

  /**
   * returns true if this is an array that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isArr(predicate: JsArray => Boolean): Boolean = isArr && predicate(asJsArray)

  /**
   * returns true if this is a boolean
   *
   * @return
   */
  def isBool: Boolean

  /**
   * returns true if this is a number
   *
   * @return
   */
  def isNumber: Boolean

  /**
   * returns true if this is not a number
   *
   * @return
   */
  def isNotNumber: Boolean = !isNumber

  /**
   * returns true if this is an integer (32 bit precision number)
   *
   * @return
   */
  def isInt: Boolean

  /**
   * returns true if this is an integer that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isInt(predicate: Int => Boolean): Boolean = isInt && predicate(asJsInt.value)

  /**
   * returns true if this is a long (62 bit precision number)
   *
   * @return true if this is a long and false otherwise. If this is an integer, it returns false.
   */
  def isLong: Boolean

  /**
   * returns true if this is a long that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a long that satisfies the predicate and false otherwise.
   *         If this is an integer, it returns false.
   */
  def isLong(predicate: Long => Boolean): Boolean = isLong && predicate(asJsLong.value)

  /**
   * returns true if this is a double
   *
   * @return
   */
  def isDouble: Boolean

  /**
   * returns true if this is a double that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a double that satisfies the predicate
   */
  def isDouble(predicate: Double => Boolean): Boolean = isDouble && predicate(asJsDouble.value)

  /**
   * returns true if this is a big integer.
   *
   * @return true if this is a big integer and false otherwise. If this is either an integer or a long, it
   *         returns false.
   */
  def isBigInt: Boolean

  /**
   * returns true if this is a big integer that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a big integer that satisfies the predicate. If this is either an integer or a long, it
   *         returns false.
   */
  def isIntegral(predicate: BigInt => Boolean): Boolean = isBigInt && predicate(asJsBigInt.value)

  /**
   * returns true if this is a big decimal.
   *
   * @return true if this is a big decimal and false otherwise. If this is a double, it
   *         returns false.
   */
  def isBigDec: Boolean

  /**
   * returns true if this is a big decimal that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a big decimal that satisfies the predicate. If this is a double, it returns false
   */
  def isDecimal(predicate: BigDecimal => Boolean): Boolean = isBigDec && predicate(asJsBigDec.value)


  /**
   * returns true if this is [[JsNull]]
   *
   * @return true if this is [[JsNull]], false otherwise
   */
  def isNull: Boolean

  /**
   * returns true if this is [[JsNothing]]
   *
   * @return true if this is [[JsNothing]], false otherwise
   */
  def isNothing: Boolean

  /**
   * returns this value as a [[JsLong]] if it is a [[JsLong]] or a [[JsInt]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  {{{ isInt || isLong }}} can help to that purpose.
   *
   * @return this value as a [[JsLong]]
   */
  @throws(classOf[value.UserError])
  def asJsLong: JsLong


  /**
   * returns this value as a [[JsInt]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isInt }}} can help to that purpose.
   *
   * @return this value as a [[JsInt]]
   */
  @throws(classOf[value.UserError])
  def asJsInt: JsInt

  /**
   * returns this value as a [[JsBigInt]] if it's an integral number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isIntegral }}} can help to that purpose.
   *
   * @return this value as a [[JsBigInt]]
   */
  @throws(classOf[value.UserError])
  def asJsBigInt: JsBigInt

  /**
   * returns this value as a [[JsBigDec]] if it's a decimal number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isDecimal }}} can help to that purpose.
   *
   * @return this value as a [[JsBigDec]]
   */
  @throws(classOf[value.UserError])
  def asJsBigDec: JsBigDec

  /**
   * returns this value as a [[JsBool]] if it's a boolean, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isBool }}} can help to that purpose.
   *
   * @return this value as a [[JsBool]]
   */
  @throws(classOf[value.UserError])
  def asJsBool: JsBool

  /**
   * returns this value as a [[JsNull]] if it's null, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isNull }}} can help to that purpose.
   *
   * @return this value as a [[JsNull]]
   */
  @throws(classOf[value.UserError])
  def asJsNull: JsNull.type

  /**
   * returns this value as a [[JsObj]] if it's an object, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isObj }}} can help to that purpose.
   *
   * @return this value as a [[JsObj]]
   */
  @throws(classOf[value.UserError])
  def asJsObj: JsObj

  /**
   * returns this value as a [[JsStr]] if it's a string, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isStr }}} can help to that purpose.
   *
   * @return this value as a [[JsStr]]
   */
  @throws(classOf[value.UserError])
  def asJsStr: JsStr


  /**
   * returns this value as a [[JsDouble]] if it is a [[JsLong]] or a [[JsInt]] or a [[JsDouble]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  {{{ isInt || isLong || isDouble }}} can help to that purpose.
   *
   * @return this value as a [[JsDouble]]
   */
  @throws(classOf[value.UserError])
  def asJsDouble: JsDouble

  /**
   * returns this value as a [[JsArray]] if it's an array, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isArr }}} can help to that purpose.
   *
   * @return this value as a [[JsArray]]
   */
  @throws(classOf[value.UserError])
  def asJsArray: JsArray

  /**
   * returns this value as a [[JsNumber]] if it's a number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the invocation to this function doesn't fail. The guard
   * condition {{{ isNumber }}} can help to that purpose.
   *
   * @return this value as a [[JsNumber]]
   *
   */
  @throws(classOf[value.UserError])
  def asJsNumber: JsNumber

  /**
   * returns this value as a [[Json]] if it's an object or an array, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isJson }}} can help to that purpose.
   *
   * @return this value as a [[Json]]
   */
  @throws(classOf[value.UserError])
  def asJson: Json[_]


}

sealed trait JsPrimitive extends JsValue
{
  override def isArr: Boolean = false

  override def isObj: Boolean = false

  override def isNothing: Boolean = false

}

/**
 * represents an immutable string
 *
 * @param value the value of the string
 */
final case class JsStr(value: String) extends JsPrimitive
{
  override def isStr: Boolean = true

  override def isObj: Boolean = false

  override def isArr: Boolean = false

  override def isBool: Boolean = false

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = false

  override def isNothing: Boolean = false

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsStr

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJsStr

  override def asJsStr: JsStr = this

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsStr

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsStr

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsStr

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJsStr

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsStr

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsStr

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsStr

  override def toString: String = s"""\"$value\""""

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsStr

  def map(m: String => String): JsStr = JsStr(requireNonNull(m)(value))

  override def asJson: Json[_] = throw UserError.asJsonOfJsStr

  override def id: Int = 2


}

/**
 * Represents an immutable number
 */
sealed trait JsNumber extends JsPrimitive
{

  override def isStr: Boolean = false

  override def isBool: Boolean = false

  override def isNull: Boolean = false

  override def isNumber: Boolean = true

  override def asJsStr: JsStr = throw UserError.asJsStrOfJsNumber

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJsNumber

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJsNumber

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsNumber

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsNumber

  override def asJson: Json[_] = throw UserError.asJsonOfJsNumber

  override def asJsNumber: JsNumber = this

}

/**
 * Represents an immutable number of type `Int`
 *
 * @param value the value of the number
 */
final case class JsInt(value: Int) extends JsNumber
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

  override def hashCode(): Int = value

  override def asJsLong: JsLong = JsLong(value)

  override def asJsInt: JsInt = this

  override def asJsBigInt: JsBigInt = JsBigInt(value)

  override def asJsBigDec: JsBigDec = JsBigDec(value)

  override def asJsDouble: JsDouble = JsDouble(value)

  def id: Int = 9

}

/**
 * Represents an immutable number of type `Double`
 *
 * @param value the value of the number
 */
final case class JsDouble(value: Double) extends JsNumber
{
  override def isInt: Boolean = false

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
      case Success(n) => n
      case _ => Try(decimal.toLongExact) match
      {
        case Success(n) => (n ^ (n >>> 32)).toInt
        case _ => decimal.toBigIntExact match
        {
          case Some(n) => n.hashCode
          case _ => decimal.hashCode
        }
      }
    }
  }

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsDouble

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsDouble

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsDouble

  override def asJsBigDec: JsBigDec = JsBigDec(value)

  override def asJsDouble: JsDouble = this

  def id: Int = 8
}

/**
 * Represents an immutable number of type `Long`
 *
 * @param value the value of the number
 */
final case class JsLong(value: Long) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = true

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

  override def asJsLong: JsLong = this

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsLong

  override def asJsBigInt: JsBigInt = JsBigInt(value)

  override def asJsBigDec: JsBigDec = JsBigDec(value)

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsLong

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
    case Success(n) => n
    case _ =>
      (value ^ (value >>> 32)).toInt
  }

  def id: Int = 7

}

/**
 * Represents an immutable number of type `BigDecimal`
 *
 * @param value the value of the number
 */
final case class JsBigDec(value: BigDecimal) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = true

  override def toString: String = value.toString

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsBigDec

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsBigDec

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsBigDec

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsBigDec

  override def asJsBigDec: JsBigDec = this

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
      case Success(n) => n
      case _ => Try(value.toLongExact) match
      {
        case Success(n) => (n ^ (n >>> 32)).toInt
        case _ => value.toBigIntExact match
        {
          case Some(n) => n.hashCode()
          case _ => value.hashCode()
        }
      }
    }
  }

  def id: Int = 5

}

/**
 * Represents an immutable number of type `BigInt`
 *
 * @param value the value of the number
 */
final case class JsBigInt(value: BigInt) extends JsNumber
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
      case Success(n) => n
      case _ => Try(value.bigInteger.longValueExact()) match
      {
        case Success(n) => (n ^ (n >>> 32)).toInt
        case _ => value.hashCode()
      }
    }
  }

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsBigInt

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsBigInt

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsBigInt

  override def asJsBigInt: JsBigInt = this

  override def asJsBigDec: JsBigDec = JsBigDec(BigDecimal(value))

  def id: Int = 6
}

/**
 * represents an immutable boolean
 *
 * @param value the value associated, either true or false
 */
case class JsBool(private[value] val value: Boolean) extends JsPrimitive
{
  override def isStr: Boolean = false

  override def isObj: Boolean = false

  override def isArr: Boolean = false

  override def isBool: Boolean = true

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = false

  override def isNothing: Boolean = false

  override def toString: String = value.toString

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsBool

  override def asJsStr: JsStr = throw UserError.asJsStrOfJsBool

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsBool

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsBool

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsBool

  override def asJsBool: JsBool = this

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJsBool

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsBool

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsBool

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsBool

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsBool

  override def asJson: Json[_] = throw UserError.asJsonOfJsBool

  override def id: Int = 0
}

trait Json[T <: Json[T]] extends JsValue
{

  /** Converts the string representation of this Json to a pretty print version
   *
   * @return pretty print version of the string representation of this Json
   */
  def toPrettyString: String =
  {
    val baos = new ByteArrayOutputStream
    dslJson.serialize(this,
                      new MyPrettifyOutputStream(baos)
                      )
    baos.toString("UTF-8")
  }

  /** Returns the string representation of this Json
   *
   * @return the string representation of this Json
   */
  override def toString: String =
  {
    val baos = new ByteArrayOutputStream
    dslJson.serialize(this,
                      baos
                      )
    baos.toString("UTF-8")

  }

  /**
   * Returns a zero-argument function that when called, it serializes this Json into the given
   * output stream, no returning anything
   *
   * @param outputStream the output stream
   * @return () => Unit function that serializes this Json into the given output stream
   */
  def serialize(outputStream: OutputStream): () => Unit =
  {
    () =>
    {
      dslJson.serialize(this,
                        requireNonNull(outputStream)
                        )
    }
  }

  /** Serialize this Json into an array of bytes. When possible,
   * it's more efficient to work on byte level that with strings
   *
   * @return this Json serialized into an array of bytes
   */
  def serialize: Array[Byte] =
  {
    val outputStream = new ByteArrayOutputStream()
    dslJson.serialize(this,
                      outputStream
                      )
    outputStream.flush()
    outputStream.toByteArray
  }


  /** Removes a path from this Json
   *
   * @param path the path to be removed
   * @return If this Json does not contain a binding for path it is returned unchanged.
   *         Otherwise, returns a new Json without a binding for path
   */
  def removed(path: JsPath): T

  /** Creates a new Json from this Json by removing all paths of another collection
   *
   * @param xs the collection containing the paths to remove
   * @return a new Json with the given paths removed.
   */
  def removedAll(xs: IterableOnce[JsPath]): T

  /** return false
   *
   * @return false
   */
  override def isStr: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isBool: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isNumber: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isInt: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isLong: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isDouble: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isBigInt: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isBigDec: Boolean = false

  def isNotEmpty: Boolean = !isEmpty

  /** return false
   *
   * @return false
   */
  override def isNull: Boolean = false

  /** return false
   *
   * @return false
   */
  override def isNothing: Boolean = false

  /** throws an UserError exception
   *
   *
   */
  override def asJsLong: JsLong = throw UserError.asJsLongOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsInt: JsInt = throw UserError.asJsIntOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsBool: JsBool = throw UserError.asJsBoolOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJson

  /** throws an UserError exception
   *
   *
   */
  override def asJsStr: JsStr = throw UserError.asJsStrOfJson

  /** throws an UserError exception
   *
   * @return
   */
  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJson

  private[value] def apply(pos: Position): JsValue

  /** Returns the element located at a specified path. This function is total on its argument.
   * If no element is found, JsNothing is returned
   *
   * @param path the path
   * @return the json value found at the path
   */
  final def apply(path: JsPath): JsValue =
  {
    if (requireNonNull(path).isEmpty) this
    else
    {
      if (path.tail.isEmpty) this (path.head)
      else if (!this (path.head).isJson) JsNothing
      else this (path.head).asJson.apply(path.tail)
    }
  }

  /** Returns true if there is an element at the specified path
   *
   * @param path the path
   * @return true if the path exists, false otherwise
   */
  def containsPath(path: JsPath): Boolean = !apply(requireNonNull(path)).isNothing

  /** Returns the number of elements that satisfy the given predicate
   *
   * @param p the predicate to test each path/value pair
   * @return number of elements that satisfy the predicate
   */
  def count(p: ((JsPath, JsValue)) => Boolean =
            (_: (JsPath, JsValue)) => true): Int = flatten.count(requireNonNull(p))


  /** Tests whether a predicate holds for at least one element of this Json
   *
   * @param p the predicate to test each path/value pair
   * @return true if the given predicate  is satisfied by at least one path/value pair, otherwise false
   */
  def exists(p: ((JsPath, JsValue)) => Boolean): Boolean =
    flatten.exists(requireNonNull(p))

  /** returns true if the Json is empty
   *
   * @return true if empty, false otherwise
   */
  def isEmpty: Boolean

  /** returns true if the Json is non empty
   *
   * @return true if non empty, false otherwise
   */
  final def nonEmpty: Boolean = !isEmpty

  def flatten: LazyList[(JsPath, JsValue)]

  /** The initial part of the Json object without its last element.
   */
  def init: T

  /** The rest of the Json object without its first element. */
  def tail: T

  def size: Int

  /** Selects all the values of this Json which satisfy a predicate and are not Jsons. When a Json is
   * found, it is filtered recursively.
   * {{{
   *
   *
   * }}}
   *
   * @param p the predicate uses to test elements. The predicate accepts the path/value pair of each element
   * @return a new Json  consisting of all elements of this
   *         Json that satisfy the given predicate p.
   */
  def filter(p: (JsPath, JsPrimitive) => Boolean): T

  /** Selects all the values of this Json which satisfy a predicate and are not Jsons. When a Json is
   * found, it is filtered recursively.
   * {{{
   *
   *
   * }}}
   *
   * @param p the predicate uses to test elements. The predicate accepts the value of each element
   * @return a new Json  consisting of all elements of this
   *         Json that satisfy the given predicate p.
   */
  def filter(p: JsPrimitive => Boolean): T

  /**
   * Builds a new Json by applying a function to all elements of this Json that are not Json and satisfies a
   * given predicate. When a Json is found, it it mapped recursively.
   * {{{
   *
   *
   * }}}
   *
   * @param m the function to apply to each element. The predicate accepts the path/value pair of each element
   * @param p filter to select which elements will be mapped. By default all the elements are selected.
   * @tparam J type of the output of the map function
   * @return a new Json resulting from applying the given map function to each element of this Json that satisfies the filter
   *         and collecting the results.
   */
  def map[J <: JsValue](m: (JsPath, JsPrimitive) => J,
                        p: (JsPath, JsPrimitive) => Boolean
                       ): T

  /**
   * Builds a new Json by applying a function to all elements of this Json that are not Json.
   * When a Json is found, it it mapped recursively.
   * {{{
   *
   *
   * }}}
   *
   * @param m the function to apply to each element. It accepts the value of each element
   * @tparam J type of the output of the map function
   * @return a new Json resulting from applying the given map function to each element of this Json that satisfies the filter
   *         and collecting the results.
   */
  def map[J <: JsValue](m: JsPrimitive => J): T


  /**
   * Builds a new Json by applying a function to all the keys of this Json that satisfies a given predicate.
   * If the element associated to a key is a Json, the function is applied recursively,
   *
   * @param m the function to apply to each key. It accepts the path/value pair as parameters
   * @param p the predicate to select which keys will be mapped
   * @return
   */
  def mapKey(m: (JsPath, JsValue) => String,
             p: (JsPath, JsValue) => Boolean
            ): T

  /**
   * Builds a new Json by applying a function to all the keys of this Json.
   * If the element associated to a key is a Json, the function is applied recursively,
   *
   * @param m the function to apply to each key. It accepts the key name as a parameter
   * @return
   */
  def mapKey(m: String => String
            ): T

  def reduce[V](p: (JsPath, JsPrimitive) => Boolean,
                m: (JsPath, JsPrimitive) => V,
                r: (V, V) => V
               ): Option[V]

  /** Removes all the Json object of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively (if it passes the filter).
   * {{{
   *
   *
   * }}}
   *
   * @param p the predicate uses to test the path/object pairs.
   * @return a new Json consisting of all its elements except those
   *         Json object that dont satisfy the given predicate p.
   */
  def filterJsObj(p: (JsPath, JsObj) => Boolean): T

  /** Removes all the Json object of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively (if it passes the filter).
   * {{{
   *
   *
   * }}}
   *
   * @param p the predicate uses to test the Json object.
   * @return a new Json consisting of all its elements except those
   *         Json object that dont satisfy the given predicate p.
   */
  def filterJsObj(p: JsObj => Boolean): T


  /** Removes all the keys of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively.
   * {{{
   *
   *
   * }}}
   *
   * @param p the predicate uses to test the path/value pairs.
   * @return a new Json consisting of all array elements of this
   *         Json and those key/value pairs that satisfy the given predicate p.
   */
  def filterKey(p: (JsPath, JsValue) => Boolean): T

  /** Removes all the keys of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively.
   * {{{
   *
   *
   * }}}
   *
   * @param p the predicate uses to test the keys.
   * @return a new Json consisting of all array elements of this
   *         Json and those key/value pairs that satisfy the given predicate p.
   */
  def filterKey(p: String => Boolean): T


  /** Creates a new Json obtained by inserting a given path/value pair into this Json.
   * The given element is always inserted at the given path, even if it requires to create new Json
   * or padding arrays.
   * {{{
   *  JsObj.empty.inserted(path,value)(path) == value //always true
   *
   * }}}
   *
   * @param    path  the path
   * @param    value the value
   * @return A new Json   with the new path/value mapping added to this Json.
   * @note [[inserted]] function unless updated, always inserts the given path/value pair
   */
  def inserted(path: JsPath,
               value: JsValue,
               padWith: JsValue = JsNull
              ): T
}

/**
 * represents an immutable Json object. There are several ways of creating a Json object, being the most
 * common the following:
 *
 *  - From a string, array of bytes or an input stream of bytes, using the parse functions of the companion object
 *  - From the apply function of the companion object:
 *
 * {{{
 *    JsObj("a" -> 1,
 *          "b" -> "hi",
 *          "c" -> JsArray(1,2),
 *          "d" -> JsObj("e" -> 1,
 *                       "f" -> true
 *                      )
 *          )
 *
 *    // alternative way, from a set of pairs (path,value)
 *
 *    JsObj(
 *          ("a", 1),
 *          ("b", "hi"),
 *          ("c" / 0, 1),
 *          ("c" / 1, 2),
 *          ("d" / "e", 1),
 *          ("d" / "f", true)
 *         )
 * }}}
 *
 * @param map immutable map of JsValue
 */
final case class JsObj(override private[value] val map: immutable.Map[String, JsValue] = HashMap.empty) extends AbstractJsObj(map) with IterableOnce[(String, JsValue)] with Json[JsObj]
{

  def id: Int = 3

  override def init: JsObj = JsObj(map.init)

  override def tail: JsObj = JsObj(map.tail)

  override def removed(path: JsPath): JsObj =
  {
    if (requireNonNull(path).isEmpty) return this

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.removed(k))


        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.removed(tail)
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.removed(tail)
                                                     )
                                         )
            case _ => this
          }

        }

      }
    }
  }


  override def removedAll(xs: IterableOnce[JsPath]): JsObj =
  {
    @scala.annotation.tailrec
    def apply0(iter: Iterator[JsPath],
               obj : JsObj
              ): JsObj =
    {

      if (iter.isEmpty) obj
      else apply0(iter,
                  obj.removed(iter.next())
                  )
    }

    apply0(requireNonNull(xs).iterator,
           this
           )
  }


  override def inserted(path: JsPath,
                        value: JsValue,
                        padWith: JsValue = JsNull
                       ): JsObj =
  {
    if (requireNonNull(path).isEmpty) return this
    if (requireNonNull(value) == JsNothing) return this.removed(path)

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.updated(k,
                                               value
                                               )
                                   )
        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.inserted(tail,
                                                                  value,
                                                                  requireNonNull(padWith)
                                                                  )
                                                       )
                                           )
            case _ => JsObj(map.updated(k,
                                        JsArray.empty.inserted(tail,
                                                               value,
                                                               requireNonNull(padWith)
                                                               )
                                        )
                            )
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.inserted(tail,
                                                                value,
                                                                requireNonNull(padWith)
                                                                )
                                                     )
                                         )
            case _ => JsObj(map.updated(k,
                                        JsObj().inserted(tail,
                                                         value,
                                                         requireNonNull(padWith)

                                                         )
                                        )
                            )
          }
        }
      }
    }
  }


  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsObj(m) => m == map
      case _ => false
    }
  }

  def validate(spec: JsObjSpec): LazyList[(JsPath, Invalid)] = requireNonNull(spec).validate(this)

  /** Returns this Json object
   *
   * @return this Json object as a `JsObj`
   */
  override def asJsObj: JsObj = this

  /** Returns this Json object as a `Json`
   *
   * @return this Json object as a `Json`
   */
  override def asJson: Json[_] = this


}

/**
 * represents an immutable Json array. There are several ways of creating a Json array, being the most
 * common the following:
 *
 *  - From a string, array of bytes or an input stream of bytes, using the parse functions of the companion object
 *  - From the apply function of the companion object:
 *
 * {{{
 *    JsArray("a",
 *            true,
 *            JsObj("a" -> 1,
 *                  "b" -> true,
 *                  "c" -> "hi"
 *                  ),
 *            JsArray(1,2)
 *            )
 * }}}
 *
 * @param seq immutable seq of JsValue
 */
final case class JsArray(override private[value] val seq: immutable.Seq[JsValue] = Vector.empty) extends AbstractJsArray(seq) with IterableOnce[JsValue] with Json[JsArray]
{

  def id: Int = 4

  private lazy val str = super.toString

  /**
   * string representation of this Json array. It's a lazy value which is only computed once.
   *
   * @return string representation of this Json array
   */
  override def toString: String = str


  def appended(value: JsValue): JsArray = if (requireNonNull(value).isNothing) this else JsArray(seq.appended(value))

  def prepended(value: JsValue): JsArray = if (requireNonNull(value).isNothing) this else JsArray(seq.prepended(value))

  override def init: JsArray = JsArray(seq.init)

  override def tail: JsArray = JsArray(seq.tail)

  override def inserted(path: JsPath,
                        value: JsValue,
                        padWith: JsValue = JsNull
                       ): JsArray =
  {
    if (requireNonNull(path).isEmpty) this
    else if (requireNonNull(value).isNothing) this
    else path.head match
    {
      case Key(_) => this
      case Index(i) => path.tail match
      {
        case JsPath.empty => JsArray(fillWith(seq,
                                              i,
                                              value,
                                              requireNonNull(padWith)
                                              )
                                     )

        case tail: JsPath => tail.head match
        {
          case Index(_) => seq.lift(i) match
          {
            case Some(a: JsArray) => JsArray(fillWith(seq,
                                                      i,
                                                      a.inserted(tail,
                                                                 value,
                                                                 padWith
                                                                 ),
                                                      requireNonNull(padWith)
                                                      )
                                             )
            case _ => JsArray(fillWith(seq,
                                       i,
                                       JsArray.empty.inserted(tail,
                                                              value,
                                                              requireNonNull(padWith)
                                                              ),
                                       requireNonNull(padWith)
                                       )
                              )
          }
          case Key(_) => seq.lift(i) match
          {
            case Some(o: JsObj) => JsArray(fillWith(seq,
                                                    i,
                                                    o.inserted(tail,
                                                               value,
                                                               requireNonNull(padWith)
                                                               ),
                                                    requireNonNull(padWith)
                                                    )
                                           )
            case _ => JsArray(fillWith(seq,
                                       i,
                                       JsObj().inserted(tail,
                                                        value,
                                                        requireNonNull(padWith)
                                                        ),
                                       requireNonNull(padWith)
                                       )
                              )
          }
        }
      }
    }
  }

  override def removed(path: JsPath): JsArray =
  {

    if (requireNonNull(path).isEmpty) return this
    path.head match
    {
      case Key(_) => this
      case Index(i) => path.tail match
      {
        case JsPath.empty => JsArray(AbstractJsArray.remove(i,
                                                            seq
                                                            )
                                     )

        case tail: JsPath => tail.head match
        {
          case Index(_) => seq.lift(i) match
          {
            case Some(a: JsArray) =>
              JsArray(seq.updated(i,
                                  a.removed(tail
                                            )
                                  )
                      )
            case _ => this
          }
          case Key(_) => seq.lift(i) match
          {
            case Some(o: JsObj) =>
              JsArray(seq.updated(i,
                                  o.removed(tail
                                            )
                                  )
                      )
            case _ => this
          }
        }
      }
    }
  }

  override def removedAll(xs: IterableOnce[JsPath]): JsArray =
  {

    @scala.annotation.tailrec
    def removeRec(iter: Iterator[JsPath],
                  arr: JsArray
                 ): JsArray =
    {

      if (iter.isEmpty) arr
      else removeRec(iter,
                     arr.removed(iter.next())
                     )
    }

    removeRec(requireNonNull(xs).iterator,
              this
              )

  }

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsArray(m) => m == seq
      case _ => false
    }
  }

  def validate(predicate: JsArrayPredicate): Result = requireNonNull(predicate).test(this)

  def validate(spec: JsArraySpec): LazyList[(JsPath, Invalid)] = requireNonNull(spec).validate(this)

  def validate(spec: ArrayOfObjSpec): LazyList[(JsPath, Invalid)] = requireNonNull(spec).validate(this)

  override def asJsArray: JsArray = this

  override def asJson: Json[_] = this

}

/**
 * It's a special Json value that represents 'nothing'. Inserting nothing in a json leaves the json
 * unchanged. Functions that return a [[JsValue]], return JsNothing when no element is found, what makes
 * them total on their arguments.
 *
 * {{{
 *   val obj = JsObj.empty
 *   obj("a") == JsNothing
 *   obj.inserted("a",JsNothing) == obj
 * }}}
 */
case object JsNothing extends JsValue
{
  override def isStr = false

  override def isObj = false

  override def isArr = false

  override def isBool = false

  override def isNumber = false

  override def isInt = false

  override def isLong = false

  override def isDouble = false

  override def isBigInt = false

  override def isBigDec = false

  override def isNull = false

  override def isNothing = true

  override def asJsLong = throw UserError.asJsLongOfJsNothing

  override def asJsNull = throw UserError.asJsNullOfJsNothing

  override def asJsStr = throw UserError.asJsStrOfJsNothing

  override def asJsInt = throw UserError.asJsIntOfJsNothing

  override def asJsBigInt = throw UserError.asJsBigIntOfJsNothing

  override def asJsBigDec = throw UserError.asJsBigDecOfJsNothing

  override def asJsBool = throw UserError.asJsBoolOfJsNothing

  override def asJsObj = throw UserError.asJsObjOfJsNothing

  override def asJsArray = throw UserError.asJsArrayOfJsNothing

  override def asJsDouble = throw UserError.asJsDoubleOfJsNothing

  override def asJsNumber = throw UserError.asJsNumberOfJsNothing

  override def asJson = throw UserError.asJsonOfJsNothing

  override def id = 10
}

/**
 * Json null singleton object
 */
case object JsNull extends JsPrimitive
{
  override def isStr: Boolean = false

  override def isObj: Boolean = false

  override def isArr: Boolean = false

  override def isBool: Boolean = false

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = true

  override def isNothing: Boolean = false

  override def toString: String = "null"

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsNull

  override def asJsNull: JsNull.type = this

  override def asJsStr: JsStr = throw UserError.asJsStrOfJsNull

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsNull

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsNull

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsNull

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJsNull

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsNull

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsNull

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsNull

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsNull

  override def asJson: Json[_] = throw UserError.asJsonOfJsNull

  override def id: Int = 1


}

object JsStr
{
  val prism: Prism[JsValue, String] =
  {
    Prism((value: JsValue) => value match
    {
      case JsStr(value) => Some(value)
      case _ => None
    }
          )((str: String) => JsStr(str))
  }
}

object JsInt
{
  val prism: Prism[JsValue, Int] =
  {
    Prism((value: JsValue) => value match
    {
      case JsInt(value) => Some(value)
      case _ => None
    }
          )((int: Int) => JsInt(int))
  }
}

object JsLong
{
  val prism: Prism[JsValue, Long] =
  {
    Prism((value: JsValue) => value match
    {
      case long: JsLong => Some(long.value)
      case int: JsInt => Some(int.value.toLong)
      case _ => None
    }
          )((l: Long) => JsLong(l))
  }
}

object JsBigInt
{
  val prism: Prism[JsValue, BigInt] =
  {
    Prism((value: JsValue) => value match
    {
      case bi: JsBigInt => Some(bi.value)
      case long: JsLong => Some(BigInt(long.value))
      case int: JsInt => Some(BigInt(int.value))
      case _ => None
    }
          )((bi: BigInt) => JsBigInt(bi))
  }
}

object JsDouble
{
  val prism: Prism[JsValue, Double] =
  {
    Prism((value: JsValue) => value match
    {
      case double: JsDouble => Some(double.value)
      case _ => None
    }
          )((d: Double) => JsDouble(d))
  }
}

object JsBool
{
  val prism: Prism[JsValue, Boolean] =
  {
    Prism((value: JsValue) => value match
    {
      case bool: JsBool => Some(bool.value)
      case _ => None
    }
          )((d: Boolean) => JsBool(d))
  }
}

object JsBigDec
{
  val prism: Prism[JsValue, BigDecimal] =
  {
    Prism((value: JsValue) => value match
    {
      case double: JsDouble => Some(BigDecimal(double.value))
      case dec: JsBigDec => Some(dec.value)
      case _ => None
    }
          )((bd: BigDecimal) => JsBigDec(bd))
  }
}

object JsNumber
{
  val prism: Prism[JsValue, JsNumber] =
  {
    Prism[JsValue, JsNumber]((value: JsValue) => value match
    {
      case int: JsInt => Some(int)
      case long: JsLong => Some(long)
      case bigInt: JsBigInt => Some(bigInt)
      case double: JsDouble => Some(double)
      case dec: JsBigDec => Some(dec)
      case _ => None
    }
                             )((n: JsNumber) => n)
  }

  /**
   * It creates a number from a Jackson parser whose current token is a string that represents an integral number.
   * Tries to convert the number into an Int, if it doesn't fit in an Int, tries to turn it into a Long, and if it
   * doesn't fit in a Long, it converts it into a BigInt
   *
   * @param parser the parser which current token is an integral number
   * @return a JsNumber
   */
  protected[value] def apply(parser: JsonParser): JsNumber =
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

object JsObj
{

  val empty = new JsObj(immutable.HashMap.empty)

  val prism: Prism[JsValue, JsObj] =
  {
    Prism((value: JsValue) => value match
    {
      case obj: JsObj => Some(obj)
      case _ => None
    }
          )((d: JsObj) => d)
  }

  def accessor(path: JsPath): Lens[JsObj, JsValue] =
  {
    val get: JsObj => JsValue = (obj: JsObj) => obj(path)
    val set: JsValue => JsObj => JsObj = (value: JsValue) =>
      (obj: JsObj) => obj.inserted(path,
                                   value
                                   )
    Lens[JsObj, JsValue](get)(set)
  }

  def objAccessor(path: JsPath): Optional[JsObj, JsObj] =
  {
    val get: JsObj => Option[JsObj] = arr => JsObj.prism.getOption(arr(path))
    val set: JsObj => JsObj => JsObj = newObj => obj => obj.inserted(path,
                                                                     newObj
                                                                     )
    Optional[JsObj, JsObj](get)(set)
  }

  def arrAccessor(path: JsPath): Optional[JsObj, JsArray] =
  {
    val get: JsObj => Option[JsArray] = obj => JsArray.prism.getOption(obj(path))
    val set: JsArray => JsObj => JsObj = arr => obj => obj.inserted(path,
                                                                    arr
                                                                    )
    Optional[JsObj, JsArray](get)(set)
  }

  def strAccessor(path: JsPath): Optional[JsObj, String] =
  {
    val get: JsObj => Option[String] = obj => JsStr.prism.getOption(obj(path))
    val set: String => JsObj => JsObj = str => obj => obj.inserted(path,
                                                                   JsStr(str)
                                                                   )
    Optional[JsObj, String](get)(set)
  }

  def intAccessor(path: JsPath): Optional[JsObj, Int] =
  {
    val get: JsObj => Option[Int] = obj => JsInt.prism.getOption(obj(path))
    val set: Int => JsObj => JsObj = int => obj => obj.inserted(path,
                                                                JsInt(int)
                                                                )
    Optional[JsObj, Int](get)(set)
  }

  def doubleAccessor(path: JsPath): Optional[JsObj, Double] =
  {
    val get: JsObj => Option[Double] = obj => JsDouble.prism.getOption(obj(path))
    val set: Double => JsObj => JsObj = d => obj => obj.inserted(path,
                                                                 JsDouble(d)
                                                                 )
    Optional[JsObj, Double](get)(set)
  }

  def longAccessor(path: JsPath): Optional[JsObj, Long] =
  {
    val get: JsObj => Option[Long] = obj => JsLong.prism.getOption(obj(path))
    val set: Long => JsObj => JsObj = long => obj => obj.inserted(path,
                                                                  JsLong(long)
                                                                  )
    Optional[JsObj, Long](get)(set)
  }

  def bigDecAccessor(path: JsPath): Optional[JsObj, BigDecimal] =
  {
    val get: JsObj => Option[BigDecimal] = obj => JsBigDec.prism.getOption(obj(path))
    val set: BigDecimal => JsObj => JsObj = bigdec => obj => obj.inserted(path,
                                                                          JsBigDec(bigdec)
                                                                          )
    Optional[JsObj, BigDecimal](get)(set)
  }

  def bigIntAccessor(path: JsPath): Optional[JsObj, BigInt] =
  {
    val get: JsObj => Option[BigInt] = obj => JsBigInt.prism.getOption(obj(path))
    val set: BigInt => JsObj => JsObj = bigint => obj => obj.inserted(path,
                                                                      JsBigInt(bigint)
                                                                      )
    Optional[JsObj, BigInt](get)(set)
  }

  def boolAccessor(path: JsPath): Optional[JsObj, Boolean] =
  {
    val get: JsObj => Option[Boolean] = obj => JsBool.prism.getOption(obj(path))
    val set: Boolean => JsObj => JsObj = bool => obj => obj.inserted(path,
                                                                     JsBool(bool)
                                                                     )
    Optional[JsObj, Boolean](get)(set)
  }


  def apply(pair: (JsPath, JsValue)*): JsObj =
  {
    @scala.annotation.tailrec
    def applyRec(acc: JsObj,
                 pair: Seq[(JsPath, JsValue)]
                ): JsObj =
    {
      if (pair.isEmpty) acc
      else applyRec(acc.inserted(pair.head._1,
                                 pair.head._2
                                 ),
                    pair.tail
                    )
    }

    applyRec(empty,
             requireNonNull(pair)
             )
  }

}

object Json
{
  val prism: Prism[JsValue, Json[_]] =
  {
    Prism[JsValue, Json[_]]((value: JsValue) => value match
    {
      case obj: JsObj => Some(obj)
      case arr: JsArray => Some(arr)
      case _ => None
    }
                            )((json: Json[_]) => json)
  }


}

object JsArray
{
  val empty = JsArray(Vector.empty)

  val prism: Prism[JsValue, JsArray] =
  {
    Prism((value: JsValue) => value match
    {
      case arr: JsArray => Some(arr)
      case _ => None
    }
          )((arr: JsArray) => arr)
  }

  def accessor(path: JsPath): Lens[JsArray, JsValue] =
  {
    val get: JsArray => JsValue = (arr: JsArray) => arr(path)
    val set: JsValue => JsArray => JsArray =
      (value: JsValue) => (arr: JsArray) => arr.inserted(path,
                                                         value
                                                         )
    Lens[JsArray, JsValue](get)(set)
  }

  def strAccessor(path: JsPath): Optional[JsArray, String] =
  {
    val get: JsArray => Option[String] =
      arr => JsStr.prism.getOption(arr(path))
    val set: String => JsArray => JsArray =
      str => arr => arr.inserted(path,
                                 JsStr(str)
                                 )
    Optional[JsArray, String](get)(set)
  }

  def intAccessor(path: JsPath): Optional[JsArray, Int] =
  {
    val get: JsArray => Option[Int] =
      arr => JsInt.prism.getOption(arr(path))
    val set: Int => JsArray => JsArray =
      int => arr => arr.inserted(path,
                                 JsInt(int)
                                 )
    Optional[JsArray, Int](get)(set)
  }

  def objAccessor(path: JsPath): Optional[JsArray, JsObj] =
  {
    val get: JsArray => Option[JsObj] =
      arr => JsObj.prism.getOption(arr(path))
    val set: JsObj => JsArray => JsArray =
      obj => arr => arr.inserted(path,
                                 obj
                                 )
    Optional[JsArray, JsObj](get)(set)
  }

  def arrAccessor(path: JsPath): Optional[JsArray, JsArray] =
  {
    val get: JsArray => Option[JsArray] =
      arr => JsArray.prism.getOption(arr(path))
    val set: JsArray => JsArray => JsArray =
      newArr => arr => arr.inserted(path,
                                    newArr
                                    )
    Optional[JsArray, JsArray](get)(set)
  }

  def bigIntAccessor(path: JsPath): Optional[JsArray, BigInt] =
  {
    val get: JsArray => Option[BigInt] =
      arr => JsBigInt.prism.getOption(arr(path))
    val set: BigInt => JsArray => JsArray =
      bigint => arr => arr.inserted(path,
                                    JsBigInt(bigint)
                                    )
    Optional[JsArray, BigInt](get)(set)
  }

  def bigDecAccessor(path: JsPath): Optional[JsArray, BigDecimal] =
  {
    val get: JsArray => Option[BigDecimal] =
      arr => JsBigDec.prism.getOption(arr(path))
    val set: BigDecimal => JsArray => JsArray =
      bigdec => arr => arr.inserted(path,
                                    JsBigDec(bigdec)
                                    )
    Optional[JsArray, BigDecimal](get)(set)
  }

  def doubleAccessor(path: JsPath): Optional[JsArray, Double] =
  {
    val get: JsArray => Option[Double] =
      arr => JsDouble.prism.getOption(arr(path))
    val set: Double => JsArray => JsArray = d =>
      arr => arr.inserted(path,
                          JsDouble(d)
                          )
    Optional[JsArray, Double](get)(set)
  }


  def longAccessor(path: JsPath): Optional[JsArray, Long] =
  {
    val get: JsArray => Option[Long] =
      arr => JsLong.prism.getOption(arr(path))
    val set: Long => JsArray => JsArray =
      long => arr => arr.inserted(path,
                                  JsLong(long)
                                  )
    Optional[JsArray, Long](get)(set)
  }

  def boolAccessor(path: JsPath): Optional[JsArray, Boolean] =
  {
    val get: JsArray => Option[Boolean] =
      arr => JsBool.prism.getOption(arr(path))
    val set: Boolean => JsArray => JsArray =
      bool => arr => arr.inserted(path,
                                  JsBool(bool)
                                  )
    Optional[JsArray, Boolean](get)(set)
  }

  def apply(pair: (JsPath, JsValue),
            xs: (JsPath, JsValue)*
           ): JsArray =
  {
    @scala.annotation.tailrec
    def apply0(arr: JsArray,
               seq: Seq[(JsPath, JsValue)]
              ): JsArray =
    {
      if (seq.isEmpty) arr
      else apply0(arr.inserted(seq.head._1,
                               seq.head._2
                               ),
                  seq.tail
                  )
    }

    apply0(empty,
           xs
           )
  }

  def apply(value: JsValue,
            values: JsValue*
           ): JsArray = JsArray(requireNonNull(values)).prepended(requireNonNull(value))
}

object TRUE extends JsBool(true)

object FALSE extends JsBool(false)