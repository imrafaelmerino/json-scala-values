package value

import java.io.{ByteArrayOutputStream, IOException, InputStream, OutputStream}
import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.{JsonParser, JsonToken}
import com.fasterxml.jackson.core.JsonTokenId.{ID_END_ARRAY, ID_FALSE, ID_NULL, ID_NUMBER_FLOAT, ID_NUMBER_INT, ID_START_ARRAY, ID_START_OBJECT, ID_STRING, ID_TRUE}
import com.fasterxml.jackson.core.exc.InputCoercionException
import value.spec.{ArrayOfObjSpec, Invalid, JsArrayPredicate, JsArraySpec, JsObjSpec, Result}
import com.fasterxml.jackson.core.JsonToken.START_ARRAY
import com.fasterxml.jackson.core.JsonToken.START_OBJECT
import monocle.{Lens, Optional, Prism}

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}

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
final case class JsStr(value: String) extends JsValue
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
case class JsBool(private[value] val value: Boolean) extends JsValue
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


  /** Creates a new Json obtained by updating this Json  with a given path/value pair.
   * If the update requires creating a new Json or an index doesn't exist in an array, it's not carried
   * out, returning this Json unchanged.
   * {{{
   *  JsObj.empty.updated(path,value)(path) == value //NOT always true
   *
   *
   * }}}
   *
   * @param    path  the path
   * @param    value the value
   * @return A new Json  with the new path/value pair added or the same this Json
   *         if the update it's not carried out.
   * @note [[inserted]] function unless updated, always inserts the given path/value pair
   */
  def updated(path : JsPath,
              value: JsValue,
             ): T


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

  /** Returns the integer located at a specified path as an option, None if it's not found or
   * it doesn't fit in 32 bits
   *
   * @param path the path
   * @return integer wrapped in an option if it exists, none otherwise
   */
  def int(path: JsPath): Option[Int] = get(requireNonNull(path)).filter(_.isInt).map(_.asJsInt.value)

  /** Returns the long located at a specified path as an option, None if not found or
   * it doesn't fit in 64 bits
   *
   * @param path the path
   * @return long wrapped in an option if it exists, none otherwise
   */
  def long(path: JsPath): Option[Long] = get(requireNonNull(path)).filter((v: JsValue) => v.isLong || v.isInt).map(_.asJsLong.value)

  /** Returns the integral number located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return integral as a big integer wrapped in an option if it exists, none otherwise
   */
  def bigInt(path: JsPath): Option[BigInt] = get(requireNonNull(path)).filter((v: JsValue) => v.isIntegral).map(_.asJsBigInt.value)

  /** Returns the double located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return double wrapped in an option if it exists, none otherwise
   */
  def double(path: JsPath): Option[Double] = get(requireNonNull(path)).filter((v: JsValue) => v.isDouble).map(_.asJsDouble.value)

  /** Returns the decimal number located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return big decimal wrapped in an option if it exists, none otherwise
   */
  def bigDecimal(path: JsPath): Option[BigDecimal] = get(requireNonNull(path)).filter((v: JsValue) => v.isDecimal).map(_.asJsBigDec.value)

  /** Returns the string located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return string wrapped in an option if it exists, none otherwise
   */
  def string(path: JsPath): Option[String] = get(requireNonNull(path)).filter(_.isStr).map(_.asJsStr.value)

  /** Returns the boolean located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return boolean wrapped in an option if it exists, none otherwise
   */
  def bool(path: JsPath): Option[Boolean] = get(requireNonNull(path)).filter(_.isBool).map(_.asJsBool.value)

  /** Returns the object located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return object wrapped in an option if it exists, none otherwise
   */
  def obj(path: JsPath): Option[JsObj] = get(requireNonNull(path)).filter(_.isObj).map(_.asJsObj)

  /** Returns the array located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return array wrapped in an option if it exists, none otherwise
   */
  def array(path: JsPath): Option[JsArray] = get(requireNonNull(path)).filter(_.isArr).map(_.asJsArray)

  /** Returns the element located at a specified path as an option, None if not found
   *
   * @param path the path
   * @return a values wrapped in an option if it exist, None otherwise
   */
  def get(path: JsPath): Option[JsValue] = apply(requireNonNull(path)) match
  {
    case JsNothing => Option.empty
    case value: JsValue => Some(value)
  }


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
  def count(p   : ((JsPath, JsValue)) => Boolean = (_: (JsPath, JsValue)) => true): Int = flatten.count(requireNonNull(p))


  /** Tests whether a predicate holds for at least one element of this Json
   *
   * @param p the predicate to test each path/value pair
   * @return true if the given predicate  is satisfied by at least one path/value pair, otherwise false
   */
  def exists(p: ((JsPath, JsValue)) => Boolean): Boolean = flatten.exists(requireNonNull(p))

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
  def filter(p: (JsPath, JsValue) => Boolean): T

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
  def filter(p: JsValue => Boolean): T

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
  def map[J <: JsValue](m: (JsPath, JsValue) => J,
                        p   : (JsPath, JsValue) => Boolean
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
  def map[J <: JsValue](m: JsValue => J): T


  /**
   * Builds a new Json by applying a function to all the keys of this Json that satisfies a given predicate.
   * If the element associated to a key is a Json, the function is applied recursively,
   *
   * @param m the function to apply to each key. It accepts the path/value pair as parameters
   * @param p the predicate to select which keys will be mapped
   * @return
   */
  def mapKey(m: (JsPath, JsValue) => String,
             p   : (JsPath, JsValue) => Boolean
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

  def reduce[V](p: (JsPath, JsValue) => Boolean,
                m   : (JsPath, JsValue) => V,
                r   : (V, V) => V
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
final case class JsObj(private[value] val map: immutable.Map[String, JsValue] = HashMap.empty) extends Json[JsObj] with IterableOnce[(String, JsValue)]
{

  def id: Int = 3

  private lazy val str = super.toString

  /**
   * string representation of this Json object. It's a lazy value which is only computed once.
   *
   * @return string representation of this Json object
   */
  override def toString: String = str


  /** Flatten this Json object into a `LazyList` of pairs of `(JsPath,JsValue)`
   * traversing recursively every noe-empty Json found along the way:
   *
   * {{{
   * val obj = JsObj("a" -> 1,
   *                 "b" -> "hi",
   *                 "c" -> JsArray(1,2,),
   *                 "d" -> JsObj("e" -> 1,
   *                              "f" -> true
   *                             )
   *                 )
   *
   * val pairs = obj.toLazyList
   *
   * pairs.foreach { println }
   *
   * //prints out the following:
   *
   * (a,1)
   * (b,"hi")
   * (c/0,1)
   * (c/1,2)
   * (d/e,{})
   * (d/f,true)
   *
   * }}}
   *
   * @return a `LazyList` of pairs of `JsPath` and `JsValue`
   **/
  override def flatten: LazyList[(JsPath, JsValue)] = JsObj.flatten(JsPath.empty,
                                                                    this
                                                                    )


  /** Tests whether this json object contains a binding for a key.
   *
   * @param key the key
   * @return `true` if there is a binding for `key` in this map, `false` otherwise.
   */
  def containsKey(key: String): Boolean = map.contains(requireNonNull(key))


  /** Returns `true`
   *
   * @return true
   */
  override def isObj: Boolean = true

  /** Returns `false`
   *
   * @return false
   */
  override def isArr: Boolean = false

  /** Tests whether the Json object is empty.
   *
   * @return `true` if the Json object contains no elements, `false` otherwise.
   */
  override def isEmpty: Boolean = map.isEmpty

  /** Selects the next element of the [[iterator]] of this Json object, throwing a
   * NoSuchElementException if the Json object is empty
   *
   * @return the next element of the [[iterator]] of this Json object.
   */
  def head: (String, JsValue) = map.head

  /** Optionally selects the next element of the [[iterator]] of this Json object.
   *
   * @return the first element of this Json object if it is nonempty.
   *         `None` if it is empty.
   */
  def headOption: Option[(String, JsValue)] = map.headOption

  /** Selects the last element of the iterator of this Json object, throwing a
   * NoSuchElementException if the Json object is empty
   *
   * @return the last element of the iterator of this Json object.
   */
  def last: (String, JsValue) = map.last

  /** Optionally selects the last element of the iterator of this Json object.
   *
   * @return the last element of the iterator of this Json object,
   *         `None` if it is empty.
   */
  def lastOption: Option[(String, JsValue)] = map.lastOption

  /** Collects all keys of this Json object in an iterable collection.
   *
   * @return the keys of this Json object as an iterable.
   */
  def keys: Iterable[String] = map.keys

  /** Retrieves the value which is associated with the given key. If there is no mapping
   * from the given key to a value, `JsNothing` is returned.
   *
   * @param  key the key
   * @return the value associated with the given key
   */
  def apply(key: String): JsValue = apply(Key(requireNonNull(key)))

  private[value] def apply(pos: Position): JsValue =
  {
    requireNonNull(pos) match
    {
      case Key(name) => map.applyOrElse(name,
                                        (_: String) => JsNothing
                                        )
      case Index(_) => JsNothing
    }
  }

  /** The size of this Json object.
   * *
   *
   * @return the number of elements in this Json object.
   */
  override def size: Int = map.size

  /** Collects all keys of this map in a set.
   *
   * @return a set containing all keys of this map.
   */
  def keySet: Set[String] = map.keySet


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


  override def updated(path: JsPath,
                       value: JsValue,
                      ): JsObj =
  {

    if (requireNonNull(path).isEmpty) return this
    if (requireNonNull(value).isNothing) return this

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
                                                       a.updated(tail,
                                                                 value
                                                                 )
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.updated(tail,
                                                               value
                                                               )
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

  /** Throws an UserError exception
   *
   * @return Throws an UserError exception
   */
  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsObj

  /**
   *
   * @param p
   * @return
   */
  override def filter(p: (JsPath, JsValue) => Boolean): JsObj =
    JsObj(JsObj.filter(JsPath.empty,
                       map,
                       HashMap.empty,
                       requireNonNull(p)
                       )
          )

  override def filter(p: JsValue => Boolean): JsObj =
    JsObj(JsObj.filter(map,
                       HashMap.empty,
                       requireNonNull(p)
                       )
          )


  override def filterJsObj(p: (JsPath, JsObj) => Boolean): JsObj =
    JsObj(JsObj.filterJsObj(JsPath.empty,
                            map,
                            HashMap.empty,
                            requireNonNull(p)
                            )
          )

  override def filterJsObj(p: JsObj => Boolean): JsObj =
    JsObj(JsObj.filterJsObj(map,
                            HashMap.empty,
                            requireNonNull(p)
                            )
          )


  override def filterKey(p: (JsPath, JsValue) => Boolean): JsObj =
    JsObj(JsObj.filterKey(JsPath.empty,
                          map,
                          HashMap.empty,
                          requireNonNull(p)
                          )
          )

  override def filterKey(p: String => Boolean): JsObj =
    JsObj(JsObj.filterKey(map,
                          HashMap.empty,
                          requireNonNull(p)
                          )
          )

  override def map[J <: JsValue](m: JsValue => J): JsObj =
    JsObj(JsObj.map(this.map,
                    HashMap.empty,
                    requireNonNull(m)
                    )
          )

  override def map[J <: JsValue](m: (JsPath, JsValue) => J,
                                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                                ): JsObj = JsObj(JsObj.map(JsPath.empty,
                                                           this.map,
                                                           HashMap.empty,
                                                           requireNonNull(m),
                                                           requireNonNull(p)
                                                           )
                                                 )


  override def reduce[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                         m   : (JsPath, JsValue) => V,
                         r   : (V, V) => V
                        ): Option[V] = JsObj.reduce(JsPath.empty,
                                                    map,
                                                    requireNonNull(p),
                                                    requireNonNull(m),
                                                    requireNonNull(r),
                                                    Option.empty
                                                    )


  override def mapKey(m   : (JsPath, JsValue) => String,
                      p   : (JsPath, JsValue) => Boolean = (_, _) => true
                     ): JsObj = JsObj(JsObj.mapKey(JsPath.empty,
                                                   map,
                                                   HashMap.empty,
                                                   requireNonNull(m),
                                                   requireNonNull(p)
                                                   )
                                      )

  override def mapKey(m: String => String): JsObj =
    JsObj(JsObj.mapKey(map,
                       HashMap.empty,
                       requireNonNull(m)
                       )
          )


  /** Returns an iterator of this Json object. Can be used only once
   *
   * @return an iterator
   */
  def iterator: Iterator[(String, JsValue)] = map.iterator


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
final case class JsArray(private[value] val seq: immutable.Seq[JsValue] = Vector.empty) extends Json[JsArray] with IterableOnce[JsValue]
{

  def id: Int = 4

  private lazy val str = super.toString

  /**
   * string representation of this Json array. It's a lazy value which is only computed once.
   *
   * @return string representation of this Json array
   */
  override def toString: String = str


  /**
   * returns a LazyList of pairs of (JsPath,JsValue) of the first level of this Json array:
   * {{{
   * val array = JsArray(1,
   *                     "hi",
   *                     JsArray(1,2),
   *                     JsObj("e" -> 1,
   *                           "f" -> true
   *                          )
   *                     )
   * val pairs = array.toLazyListRec
   *
   * pairs.foreach { println }
   *
   * //prints out the following:
   *
   * (0, 1)
   * (1, "hi")
   * (2 / 0, 1)
   * (2 / 1, 2)
   * (3 / e, 1)
   * (3 / f, true)
   *
   * }}}
   *
   * @return a lazy list of pairs of path and value
   */
  def flatten: LazyList[(JsPath, JsValue)] = JsArray.flatten(JsPath.MINUS_ONE,
                                                             this
                                                             )

  def isObj: Boolean = false

  def isArr: Boolean = true

  def isEmpty: Boolean = seq.isEmpty

  def length(): Int = seq.length

  def apply(i: Int): JsValue =
  {
    if (i == -1) seq.lastOption.getOrElse(JsNothing)
    else seq.applyOrElse(i,
                         (_: Int) => JsNothing
                         )
  }

  private[value] def apply(pos: Position): JsValue = requireNonNull(pos) match
  {
    case Index(i) => apply(i)
    case Key(_) => value.JsNothing
  }

  def head: JsValue = seq.head

  def last: JsValue = seq.last

  def size: Int = seq.size

  @scala.annotation.tailrec
  private[value] def fillWith[E <: JsValue, P <: JsValue](seq: immutable.Seq[JsValue],
                                                          i  : Int,
                                                          e  : E,
                                                          p  : P
                                                         ): immutable.Seq[JsValue] =
  {
    val length = seq.length
    if (i < length && i > -1) seq.updated(i,
                                          e
                                          )
    else if (i == -1)
      if (seq.isEmpty) seq.appended(e)
      else
        seq.updated(seq.length - 1,
                    e
                    )

    else if (i == length) seq.appended(e)
    else fillWith(seq.appended(p),
                  i,
                  e,
                  p
                  )

  }

  def appended(value: JsValue): JsArray = if (requireNonNull(value).isNothing) this else JsArray(seq.appended(value))

  def prepended(value: JsValue): JsArray = if (requireNonNull(value).isNothing) this else JsArray(seq.prepended(value))

  def prependedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.prependedAll(requireNonNull(xs).iterator.filterNot(e => e.isNothing)))

  def appendedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.appendedAll(requireNonNull(xs).iterator.filterNot(e => e.isNothing)))

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
        case JsPath.empty => JsArray(JsArray.remove(i,
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

  override def updated(path: JsPath,
                       value: JsValue,
                      ): JsArray =
  {

    if (requireNonNull(value).isNothing) return this
    if (requireNonNull(path).isEmpty) return this

    path.head match
    {
      case Key(_) => this
      case Index(i) =>
        path.tail match
        {
          case JsPath.empty => JsArray(seq.updated(if (i == -1) seq.length - 1 else i,
                                                   value
                                                   )
                                       )
          case tail: JsPath => tail.head match
          {
            case Index(_) => seq.lift(i) match
            {
              case Some(a: JsArray) =>
                val updated: immutable.Seq[JsValue] = seq.updated(i,
                                                                  a.updated(tail,
                                                                            value
                                                                            )
                                                                  )
                JsArray(updated)
              case _ => this
            }
            case Key(_) => seq.lift(i) match
            {
              case Some(o: JsObj) =>
                val updated: immutable.Seq[JsValue] = seq.updated(i,
                                                                  o.updated(tail,
                                                                            value
                                                                            )
                                                                  )
                JsArray(updated)
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
                  arr : JsArray
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

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsArray


  override def filter(p   : (JsPath, JsValue) => Boolean): JsArray = JsArray(JsArray.filter(JsPath.MINUS_ONE,
                                                                                            seq,
                                                                                            Vector.empty,
                                                                                            requireNonNull(p)
                                                                                            )
                                                                             )


  override def filterJsObj(p   : (JsPath, JsObj) => Boolean): JsArray = JsArray(JsArray.filterJsObj(JsPath.MINUS_ONE,
                                                                                                    seq,
                                                                                                    Vector.empty,
                                                                                                    requireNonNull(p)
                                                                                                    )
                                                                                )


  override def filterKey(p: (JsPath, JsValue) => Boolean): JsArray = JsArray(JsArray.filterKey(JsPath.MINUS_ONE,
                                                                                               seq,
                                                                                               immutable.Vector.empty,
                                                                                               requireNonNull(p)
                                                                                               )
                                                                             )


  def flatMap(f: JsValue => JsArray): JsArray = JsArray(seq.flatMap(f))

  def iterator: Iterator[JsValue] = seq.iterator

  def foreach(f: JsValue => Unit): Unit = seq.foreach(f)

  override def map[J <: JsValue](m: (JsPath, JsValue) => J,
                                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                                ): JsArray = JsArray(JsArray.map(JsPath.MINUS_ONE,
                                                                 seq,
                                                                 Vector.empty,
                                                                 requireNonNull(m),
                                                                 requireNonNull(p)
                                                                 )
                                                     )


  override def reduce[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                         m: (JsPath, JsValue) => V,
                         r: (V, V) => V
                        ): Option[V] = JsArray.reduce(JsPath.empty / -1,
                                                      seq,
                                                      requireNonNull(p),
                                                      requireNonNull(m),
                                                      requireNonNull(r),
                                                      Option.empty
                                                      )

  override def asJson: Json[_] = this

  override def mapKey(m: (JsPath, JsValue) => String,
                      p: (JsPath, JsValue) => Boolean = (_, _) => true
                     ): JsArray = JsArray(JsArray.mapKey(JsPath.MINUS_ONE,
                                                         seq,
                                                         Vector.empty,
                                                         requireNonNull(m),
                                                         requireNonNull(p)
                                                         )
                                          )

  override def filter(p: JsValue => Boolean): JsArray = JsArray(JsArray.filter(seq,
                                                                               Vector.empty,
                                                                               requireNonNull(p)
                                                                               )
                                                                )

  override def map[J <: JsValue](m: JsValue => J): JsArray =
    JsArray(JsArray.map(seq,
                        Vector.empty,
                        requireNonNull(m)
                        )
            )

  override def mapKey(m: String => String): JsArray =
    JsArray(JsArray.mapKey(seq,
                           Vector.empty,
                           requireNonNull(m)
                           )
            )


  override def filterJsObj(p: JsObj => Boolean): JsArray =
    JsArray(JsArray.filterJsObj(seq,
                                Vector.empty,
                                requireNonNull(p)
                                )
            )

  override def filterKey(p: String => Boolean): JsArray =
    JsArray(JsArray.filterKey(seq,
                              immutable.Vector.empty,
                              requireNonNull(p)
                              )
            )
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
    def applyRec(acc : JsObj,
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

  private[value] def flatten(path : JsPath,
                             value: JsObj
                            ): LazyList[(JsPath, JsValue)] =
  {
    if (value.isEmpty) return LazyList.empty
    val head = value.head

    head._2 match
    {
      case o: JsObj => if (o.isEmpty) (path / head._1, o) +: flatten(path,
                                                                     value.tail
                                                                     ) else flatten(path / head._1,
                                                                                    o
                                                                                    ) ++: flatten(path,
                                                                                                  value.tail
                                                                                                  )
      case a: JsArray => if (a.isEmpty) (path / head._1, a) +: flatten(path,
                                                                       value.tail
                                                                       ) else JsArray.flatten(path / head._1 / -1,
                                                                                              a
                                                                                              ) ++: flatten(path,
                                                                                                            value.tail
                                                                                                            )
      case _ => (path / head._1, head._2) +: flatten(path,
                                                     value.tail
                                                     )

    }
  }

  import java.io.IOException

  import com.fasterxml.jackson.core.JsonParser

  /**
   * parses an array of bytes into a Json object that must conform the spec of the parser. If the
   * array of bytes doesn't represent a well-formed Json or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param bytes  a Json object serialized in an array of bytes
   * @param parser parser which define the spec that the Json object must conform
   * @return a try computation with the result
   */
  def parse(bytes : Array[Byte],
            parser: JsObjParser
           ): Try[JsObj] = Try(dslJson.deserializeToJsObj(requireNonNull(bytes),
                                                          requireNonNull(parser).objDeserializer
                                                          )
                               )

  /**
   * parses a string into a Json object that must conform the spec of the parser. If the
   * string doesn't represent a well-formed Json or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param str    a Json object serialized in a string
   * @param parser parser which define the spec that the Json object must conform
   * @return a try computation with the result
   */
  def parse(str   : String,
            parser: JsObjParser
           ): Try[JsObj] = Try(dslJson.deserializeToJsObj(requireNonNull(str).getBytes,
                                                          requireNonNull(parser).objDeserializer
                                                          )
                               )

  /**
   * parses an input stream of bytes into a Json object that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json object or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned. Any I/O exception processing the input stream is wrapped in a Try computation as well
   *
   * @param inputStream the input stream of bytes
   * @param parser      parser which define the spec that the Json object must conform
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream,
            parser     : JsObjParser
           ): Try[JsObj] = Try(dslJson.deserializeToJsObj(requireNonNull(inputStream),
                                                          requireNonNull(parser).objDeserializer
                                                          )
                               )

  /**
   * parses an input stream of bytes into a Json object that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json object, a MalformedJson failure wrapped
   * in a Try computation is returned. Any I/O exception processing the input stream is wrapped in a Try
   * computation as well
   *
   * @param inputStream the input stream of bytes
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream): Try[JsObj] =
  {
    var parser: JsonParser = null
    try
    {
      parser = jacksonFactory.createParser(requireNonNull(inputStream))
      val event: JsonToken = parser.nextToken
      if (event eq START_ARRAY) Failure(MalformedJson.jsObjectExpected)
      else Success(parse(parser))
    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsingInputStream(e)
                                     )
    } finally
      if (parser != null) parser.close()
  }

  /**
   * parses an array of bytes into a Json object. If the array of bytes doesn't represent a well-formed
   * Json object, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param bytes a Json object serialized in an array of bytes
   * @return a try computation with the result
   */
  def parse(bytes: Array[Byte]): Try[JsObj] =
  {
    var parser: JsonParser = null
    try
    {
      parser = jacksonFactory.createParser(requireNonNull(bytes))
      val event: JsonToken = parser.nextToken
      if (event eq START_ARRAY) Failure(MalformedJson.jsObjectExpected)
      else Success(parse(parser))
    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsing(bytes,
                                                                     e
                                                                     )
                                     )
    } finally
      if (parser != null) parser.close()
  }

  /**
   * parses a string into a Json object. If the string doesn't represent a well-formed
   * Json object, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param str a Json object serialized in a string
   * @return a try computation with the result
   */
  def parse(str: String): Try[JsObj] =
  {
    var parser: JsonParser = null
    try
    {
      parser = jacksonFactory.createParser(requireNonNull(str))
      val event: JsonToken = parser.nextToken
      if (event eq START_ARRAY) Failure(MalformedJson.jsObjectExpected)
      else Success(parse(parser))
    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsing(str,
                                                                     e
                                                                     )
                                     )
    } finally
      if (parser != null) parser.close()
  }

  @throws[IOException]
  private[value] def parse(parser  : JsonParser): JsObj =
  {
    var map: immutable.Map[String, JsValue] = HashMap.empty
    var key = parser.nextFieldName
    while (
    {key != null})
    {
      var value: JsValue = null
      parser.nextToken.id match
      {
        case ID_STRING => value = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => value = JsNumber(parser)
        case ID_NUMBER_FLOAT => value = JsBigDec(parser.getDecimalValue)
        case ID_FALSE => value = FALSE
        case ID_TRUE => value = TRUE
        case ID_NULL => value = JsNull
        case ID_START_OBJECT => value = JsObj.parse(parser)
        case ID_START_ARRAY => value = JsArray.parse(parser)
        case _ => throw InternalError.tokenNotFoundParsingStringIntoJsObj(parser.currentToken.name)
      }
      map = map.updated(key,
                        value
                        )
      key = parser.nextFieldName
    }
    JsObj(map)
  }


  private[value] def filter(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => filter(path,
                                           input.tail,
                                           result.updated(key,
                                                          JsObj(filter(path / key,
                                                                       headMap,
                                                                       HashMap.empty,
                                                                       p
                                                                       )
                                                                )
                                                          ),
                                           p
                                           )
      case (key, JsArray(headSeq)) => filter(path,
                                             input.tail,
                                             result.updated(key,
                                                            JsArray(JsArray.filter(path / key / -1,
                                                                                   headSeq,
                                                                                   Vector.empty,
                                                                                   p
                                                                                   )
                                                                    )
                                                            ),
                                             p
                                             )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filter(path,
                                                   input.tail,
                                                   result.updated(key,
                                                                  head
                                                                  ),
                                                   p
                                                   ) else filter(path,
                                                                 input.tail,
                                                                 result,
                                                                 p
                                                                 )
    }
  }

  private[value] def filter(input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            p     : JsValue => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => filter(input.tail,
                                           result.updated(key,
                                                          JsObj(filter(headMap,
                                                                       HashMap.empty,
                                                                       p
                                                                       )
                                                                )
                                                          ),
                                           p
                                           )
      case (key, JsArray(headSeq)) => filter(input.tail,
                                             result.updated(key,
                                                            JsArray(JsArray.filter(headSeq,
                                                                                   Vector.empty,
                                                                                   p
                                                                                   )
                                                                    )
                                                            ),
                                             p
                                             )
      case (key, head: JsValue) => if (p(head
                                         )) filter(input.tail,
                                                   result.updated(key,
                                                                  head
                                                                  ),
                                                   p
                                                   ) else filter(input.tail,
                                                                 result,
                                                                 p
                                                                 )
    }
  }

  private[value] def map(path  : JsPath,
                         input : immutable.Map[String, JsValue],
                         result: immutable.Map[String, JsValue],
                         m     : (JsPath, JsValue) => JsValue,
                         p     : (JsPath, JsValue) => Boolean
                        ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => map(path,
                                        input.tail,
                                        result.updated(key,
                                                       JsObj(map(path / key,
                                                                 headMap,
                                                                 HashMap.empty,
                                                                 m,
                                                                 p
                                                                 )
                                                             )
                                                       ),
                                        m,
                                        p
                                        )
      case (key, JsArray(headSeq)) => map(path,
                                          input.tail,
                                          result.updated(key,
                                                         JsArray(JsArray.map(path / key / -1,
                                                                             headSeq,
                                                                             Vector.empty,
                                                                             m,
                                                                             p
                                                                             )
                                                                 )
                                                         ),
                                          m,
                                          p
                                          )
      case (key, head: JsValue) =>
        val headPath = path / key
        if (p(headPath,
              head
              )) map(path,
                     input.tail,
                     result.updated(key,
                                    m(headPath,
                                      head
                                      )
                                    ),
                     m,
                     p
                     ) else map(path,
                                input.tail,
                                result.updated(key,
                                               head
                                               ),
                                m,
                                p
                                )
    }
  }

  private[value] def map(input: immutable.Map[String, JsValue],
                         result: immutable.Map[String, JsValue],
                         m: JsValue => JsValue
                        ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => map(input.tail,
                                        result.updated(key,
                                                       JsObj(map(headMap,
                                                                 HashMap.empty,
                                                                 m
                                                                 )
                                                             )
                                                       ),
                                        m
                                        )
      case (key, JsArray(headSeq)) => map(input.tail,
                                          result.updated(key,
                                                         JsArray(JsArray.map(headSeq,
                                                                             Vector.empty,
                                                                             m
                                                                             )
                                                                 )
                                                         ),
                                          m
                                          )
      case (key, head: JsValue) => map(input.tail,
                                       result.updated(key,
                                                      m(
                                                        head
                                                        )
                                                      ),
                                       m
                                       )
    }
  }


  private[value] def filterJsObj(path  : JsPath,
                                 input : immutable.Map[String, JsValue],
                                 result: immutable.Map[String, JsValue],
                                 p     : (JsPath, JsObj) => Boolean
                                ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(path / key,
                                    o
                                    )) filterJsObj(path,
                                                   input.tail,
                                                   result.updated(key,
                                                                  JsObj(filterJsObj(path / key,
                                                                                    o.map,
                                                                                    HashMap.empty,
                                                                                    p
                                                                                    )
                                                                        )
                                                                  ),
                                                   p
                                                   ) else filterJsObj(path,
                                                                      input.tail,
                                                                      result,
                                                                      p
                                                                      )
      case (key, JsArray(headSeq)) => filterJsObj(path,
                                                  input.tail,
                                                  result.updated(key,
                                                                 JsArray(JsArray.filterJsObj(path / key / -1,
                                                                                             headSeq,
                                                                                             Vector.empty,
                                                                                             p
                                                                                             )
                                                                         )
                                                                 ),
                                                  p
                                                  )
      case (key, head: JsValue) => filterJsObj(path,
                                               input.tail,
                                               result.updated(key,
                                                              head
                                                              ),
                                               p
                                               )
    }
  }

  private[value] def filterJsObj(input : immutable.Map[String, JsValue],
                                 result: immutable.Map[String, JsValue],
                                 p     : JsObj => Boolean
                                ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(
        o
        )) filterJsObj(
        input.tail,
        result.updated(key,
                       JsObj(filterJsObj(
                         o.map,
                         HashMap.empty,
                         p
                         )
                             )
                       ),
        p
        ) else filterJsObj(
        input.tail,
        result,
        p
        )
      case (key, JsArray(headSeq)) => filterJsObj(
        input.tail,
        result.updated(key,
                       JsArray(JsArray.filterJsObj(
                         headSeq,
                         Vector.empty,
                         p
                         )
                               )
                       ),
        p
        )
      case (key, head: JsValue) => filterJsObj(
        input.tail,
        result.updated(key,
                       head
                       ),
        p
        )
    }
  }

  private[value] def mapKey(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : (JsPath, JsValue) => String,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) =>
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if (p(headPath,
                                    o
                                    )) m(headPath,
                                         o
                                         ) else key,
                              JsObj(mapKey(headPath,
                                           o.map,
                                           HashMap.empty,
                                           m,
                                           p
                                           )
                                    )
                              ),
               m,
               p
               )
      case (key, arr: JsArray) =>
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if (p(headPath,
                                    arr
                                    )) m(headPath,
                                         arr
                                         ) else key,
                              JsArray(JsArray.mapKey(path / key / -1,
                                                     arr.seq,
                                                     Vector.empty,
                                                     m,
                                                     p
                                                     )
                                      )
                              ),
               m,
               p
               )
      case (key, head: JsValue) =>
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if (p(headPath,
                                    head
                                    )) m(headPath,
                                         head
                                         ) else key,
                              head
                              ),
               m,
               p
               )
    }

  }

  private[value] def mapKey(input: immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m: String => String
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => mapKey(input.tail,
                                     result.updated(m(key),
                                                    JsObj(mapKey(
                                                      o.map,
                                                      HashMap.empty,
                                                      m
                                                      )
                                                          )
                                                    ),
                                     m
                                     )
      case (key, arr: JsArray) => mapKey(input.tail,
                                         result.updated(m(key),
                                                        JsArray(JsArray.mapKey(arr.seq,
                                                                               Vector.empty,
                                                                               m
                                                                               )
                                                                )
                                                        ),
                                         m
                                         )
      case (key, head: JsValue) => mapKey(input.tail,
                                          result.updated(m(key),
                                                         head
                                                         ),
                                          m
                                          )
    }

  }


  private[value] def filterKey(path  : JsPath,
                               input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               p     : (JsPath, JsValue) => Boolean
                              ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(path / key,
                                    o
                                    )) filterKey(path,
                                                 input.tail,
                                                 result.updated(key,
                                                                JsObj(filterKey(path / key,
                                                                                o.map,
                                                                                HashMap.empty,
                                                                                p
                                                                                )
                                                                      )
                                                                ),
                                                 p
                                                 ) else filterKey(path,
                                                                  input.tail,
                                                                  result,
                                                                  p
                                                                  )
      case (key, arr: JsArray) => if (p(path / key,
                                        arr
                                        )) filterKey(path,
                                                     input.tail,
                                                     result.updated(key,
                                                                    JsArray(JsArray.filterKey(path / key / -1,
                                                                                              arr.seq,
                                                                                              Vector.empty,
                                                                                              p
                                                                                              )
                                                                            )
                                                                    ),
                                                     p
                                                     ) else filterKey(path,
                                                                      input.tail,
                                                                      result,
                                                                      p
                                                                      )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filterKey(path,
                                                      input.tail,
                                                      result.updated(key,
                                                                     head
                                                                     ),
                                                      p
                                                      ) else filterKey(path,
                                                                       input.tail,
                                                                       result,
                                                                       p
                                                                       )
    }
  }


  private[value] def filterKey(input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               p     : String => Boolean
                              ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(key)
      ) filterKey(input.tail,
                  result.updated(key,
                                 JsObj(filterKey(o.map,
                                                 HashMap.empty,
                                                 p
                                                 )
                                       )
                                 ),
                  p
                  ) else filterKey(input.tail,
                                   result,
                                   p
                                   )
      case (key, arr: JsArray) => if (p(key
                                        )) filterKey(input.tail,
                                                     result.updated(key,
                                                                    JsArray(JsArray.filterKey(arr.seq,
                                                                                              Vector.empty,
                                                                                              p
                                                                                              )
                                                                            )
                                                                    ),
                                                     p
                                                     ) else filterKey(input.tail,
                                                                      result,
                                                                      p
                                                                      )
      case (key, head: JsValue) => if (p(key
                                         )) filterKey(input.tail,
                                                      result.updated(key,
                                                                     head
                                                                     ),
                                                      p
                                                      ) else filterKey(input.tail,
                                                                       result,
                                                                       p
                                                                       )
    }
  }

  private[value] def reduce[V](path : JsPath,
                               input: immutable.Map[String, JsValue],
                               p    : (JsPath, JsValue) => Boolean,
                               m    : (JsPath, JsValue) => V,
                               r    : (V, V) => V,
                               acc  : Option[V]
                              ): Option[V] =
  {

    if (input.isEmpty) acc
    else
    {
      val (key, head): (String, JsValue) = input.head
      head match
      {
        case JsObj(headMap) => reduce(path,
                                      input.tail,
                                      p,
                                      m,
                                      r,
                                      Json.reduceHead(r,
                                                      acc,
                                                      reduce(path / key,
                                                             headMap,
                                                             p,
                                                             m,
                                                             r,
                                                             Option.empty
                                                             )
                                                      )
                                      )
        case JsArray(headSeq) => reduce(path,
                                        input.tail,
                                        p,
                                        m,
                                        r,
                                        Json.reduceHead(r,
                                                        acc,
                                                        JsArray.reduce(path / key / -1,
                                                                       headSeq,
                                                                       p,
                                                                       m,
                                                                       r,
                                                                       Option.empty
                                                                       )
                                                        )
                                        )
        case value: JsValue => if (p(path / key,
                                     value
                                     )) reduce(path,
                                               input.tail,
                                               p,
                                               m,
                                               r,
                                               Json.reduceHead(r,
                                                               acc,
                                                               m(path / key,
                                                                 head
                                                                 )
                                                               )
                                               ) else reduce(path,
                                                             input.tail,
                                                             p,
                                                             m,
                                                             r,
                                                             acc
                                                             )
      }
    }
  }

  private[value] def reduce[V](input: immutable.Map[String, JsValue],
                               p    : JsValue => Boolean,
                               m    : JsValue => V,
                               r    : (V, V) => V,
                               acc  : Option[V]
                              ): Option[V] =
  {

    if (input.isEmpty) acc
    else
    {
      val (_, head) = input.head
      head match
      {
        case JsObj(headMap) => reduce(input.tail,
                                      p,
                                      m,
                                      r,
                                      Json.reduceHead(r,
                                                      acc,
                                                      reduce(headMap,
                                                             p,
                                                             m,
                                                             r,
                                                             Option.empty
                                                             )
                                                      )
                                      )
        case JsArray(headSeq) => reduce(input.tail,
                                        p,
                                        m,
                                        r,
                                        Json.reduceHead(r,
                                                        acc,
                                                        JsArray.reduce(headSeq,
                                                                       p,
                                                                       m,
                                                                       r,
                                                                       Option.empty
                                                                       )
                                                        )
                                        )
        case value: JsValue => if (p(value
                                     )) reduce(input.tail,
                                               p,
                                               m,
                                               r,
                                               Json.reduceHead(r,
                                                               acc,
                                                               m(
                                                                 head
                                                                 )
                                                               )
                                               ) else reduce(input.tail,
                                                             p,
                                                             m,
                                                             r,
                                                             acc
                                                             )
      }
    }
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


  private[value] def reduceHead[V](r   : (V, V) => V,
                                   acc : Option[V],
                                   head: V
                                  ): Option[V] =
  {
    acc match
    {
      case Some(accumulated) => Some(r(accumulated,
                                       head
                                       )
                                     )
      case None => Some(head)
    }
  }

  private[value] def reduceHead[V](r         : (V, V) => V,
                                   acc       : Option[V],
                                   headOption: Option[V]
                                  ): Option[V] =
  {
    acc match
    {
      case Some(accumulated) => headOption match
      {
        case Some(head) => Some(r(accumulated,
                                  head
                                  )
                                )
        case None => Some(accumulated)
      }
      case None => headOption
    }
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
    val get: JsArray => Option[String] = arr => JsStr.prism.getOption(arr(path))
    val set: String => JsArray => JsArray = str => arr => arr.inserted(path,
                                                                       JsStr(str)
                                                                       )
    Optional[JsArray, String](get)(set)
  }

  def intAccessor(path: JsPath): Optional[JsArray, Int] =
  {
    val get: JsArray => Option[Int] = arr => JsInt.prism.getOption(arr(path))
    val set: Int => JsArray => JsArray = int => arr => arr.inserted(path,
                                                                    JsInt(int)
                                                                    )
    Optional[JsArray, Int](get)(set)
  }

  def objAccessor(path: JsPath): Optional[JsArray, JsObj] =
  {
    val get: JsArray => Option[JsObj] = arr => JsObj.prism.getOption(arr(path))
    val set: JsObj => JsArray => JsArray = obj => arr => arr.inserted(path,
                                                                      obj
                                                                      )
    Optional[JsArray, JsObj](get)(set)
  }

  def arrAccessor(path: JsPath): Optional[JsArray, JsArray] =
  {
    val get: JsArray => Option[JsArray] = arr => JsArray.prism.getOption(arr(path))
    val set: JsArray => JsArray => JsArray = newArr => arr => arr.inserted(path,
                                                                           newArr
                                                                           )
    Optional[JsArray, JsArray](get)(set)
  }

  def bigIntAccessor(path: JsPath): Optional[JsArray, BigInt] =
  {
    val get: JsArray => Option[BigInt] = arr => JsBigInt.prism.getOption(arr(path))
    val set: BigInt => JsArray => JsArray = bigint => arr => arr.inserted(path,
                                                                          JsBigInt(bigint)
                                                                          )
    Optional[JsArray, BigInt](get)(set)
  }

  def bigDecAccessor(path: JsPath): Optional[JsArray, BigDecimal] =
  {
    val get: JsArray => Option[BigDecimal] = arr => JsBigDec.prism.getOption(arr(path))
    val set: BigDecimal => JsArray => JsArray = bigdec => arr => arr.inserted(path,
                                                                              JsBigDec(bigdec)
                                                                              )
    Optional[JsArray, BigDecimal](get)(set)
  }

  def doubleAccessor(path: JsPath): Optional[JsArray, Double] =
  {
    val get: JsArray => Option[Double] = arr => JsDouble.prism.getOption(arr(path))
    val set: Double => JsArray => JsArray = d => arr => arr.inserted(path,
                                                                     JsDouble(d)
                                                                     )
    Optional[JsArray, Double](get)(set)
  }


  def longAccessor(path: JsPath): Optional[JsArray, Long] =
  {
    val get: JsArray => Option[Long] = arr => JsLong.prism.getOption(arr(path))
    val set: Long => JsArray => JsArray = long => arr => arr.inserted(path,
                                                                      JsLong(long)
                                                                      )
    Optional[JsArray, Long](get)(set)
  }

  def boolAccessor(path: JsPath): Optional[JsArray, Boolean] =
  {
    val get: JsArray => Option[Boolean] = arr => JsBool.prism.getOption(arr(path))
    val set: Boolean => JsArray => JsArray = bool => arr => arr.inserted(path,
                                                                         JsBool(bool)
                                                                         )
    Optional[JsArray, Boolean](get)(set)
  }


  /**
   * parses an array of bytes into a Json array that must conform the spec of the parser. If the
   * array of bytes doesn't represent a well-formed Json  or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param bytes  a Json array serialized in an array of bytes
   * @param parser parser which define the spec that the Json array must conform
   * @return a try computation with the result
   */
  def parse(bytes : Array[Byte],
            parser: JsArrayParser
           ): Try[JsArray] = Try(dslJson.deserializeToJsArray(requireNonNull(bytes),
                                                              requireNonNull(parser).deserializer
                                                              )
                                 )

  /**
   * parses a string into a Json array that must conform the spec of the parser. If the
   * string doesn't represent a well-formed Json array or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param str    a Json array serialized in a string
   * @param parser parser which define the spec that the Json array must conform
   * @return a try computation with the result
   */
  def parse(str   : String,
            parser: JsArrayParser
           ): Try[JsArray] =
    Try(dslJson.deserializeToJsArray(requireNonNull(str).getBytes(),
                                     requireNonNull(parser).deserializer
                                     )
        )

  /**
   * parses an input stream of bytes into a Json array that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json array or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned. Any I/O exception processing the input stream is wrapped in a Try computation as well
   *
   * @param inputStream the input stream of bytes
   * @param parser      parser which define the spec that the Json array must conform
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream,
            parser     : JsArrayParser
           ): Try[JsArray] = Try(dslJson.deserializeToJsArray(requireNonNull(inputStream),
                                                              requireNonNull(parser).deserializer
                                                              )
                                 )

  /**
   * parses an input stream of bytes into a Json array that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json array, a MalformedJson failure wrapped
   * in a Try computation is returned. Any I/O exception processing the input stream is wrapped in a Try
   * computation as well
   *
   * @param inputStream the input stream of bytes
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream): Try[JsArray] =
  {
    var parser: JsonParser = null
    try
    {
      parser = jacksonFactory.createParser(requireNonNull(inputStream))
      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT)
        Failure(MalformedJson.jsArrayExpected)
      else
        Success(parse(parser))
    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsingInputStream(e)
                                     )
    } finally
      if (parser != null) parser.close()
  }

  /**
   * parses an array of bytes into a Json array. If the array of bytes doesn't represent a well-formed
   * Json array, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param bytes a Json array serialized in an array of bytes
   * @return a try computation with the result
   */
  def parse(bytes: Array[Byte]): Try[JsArray] =
  {
    var parser: JsonParser = null
    try
    {
      parser = jacksonFactory.createParser(requireNonNull(bytes))
      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT)
        Failure(MalformedJson.jsArrayExpected)
      else
        Success(parse(parser))
    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsing(bytes,
                                                                     e
                                                                     )
                                     )
    } finally
      if (parser != null) parser.close()
  }

  /**
   * parses a string into a Json array. If the string doesn't represent a well-formed
   * Json array, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param str a Json array serialized in a string
   * @return a try computation with the result
   */
  def parse(str: String): Try[JsArray] =
  {
    var parser: JsonParser = null
    try
    {
      parser = jacksonFactory.createParser(requireNonNull(str))
      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT)
        Failure(MalformedJson.jsArrayExpected)
      else
        Success(parse(parser))
    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsing(str,
                                                                     e
                                                                     )
                                     )
    } finally
      if (parser != null) parser.close()
  }

  private[value] def reduce[V](path : JsPath,
                               input: immutable.Seq[JsValue],
                               p    : (JsPath, JsValue) => Boolean,
                               m    : (JsPath, JsValue) => V,
                               r    : (V, V) => V,
                               acc  : Option[V]
                              ): Option[V] =
  {
    if (input.isEmpty) acc
    else
    {
      val headPath = path.inc
      val head = input.head
      head match
      {
        case JsObj(headMap) => reduce(headPath,
                                      input.tail,
                                      p,
                                      m,
                                      r,
                                      Json.reduceHead(r,
                                                      acc,
                                                      JsObj.reduce(headPath,
                                                                   headMap,
                                                                   p,
                                                                   m,
                                                                   r,
                                                                   Option.empty
                                                                   )
                                                      )
                                      )
        case JsArray(headSeq) => reduce(headPath,
                                        input.tail,
                                        p,
                                        m,
                                        r,
                                        Json.reduceHead(r,
                                                        acc,
                                                        reduce(headPath / -1,
                                                               headSeq,
                                                               p,
                                                               m,
                                                               r,
                                                               Option.empty
                                                               )
                                                        )
                                        )
        case value: JsValue => if (p(headPath,
                                     value
                                     )) reduce(headPath,
                                               input.tail,
                                               p,
                                               m,
                                               r,
                                               Json.reduceHead(r,
                                                               acc,
                                                               m(headPath,
                                                                 head
                                                                 )
                                                               )
                                               ) else reduce(headPath,
                                                             input.tail,
                                                             p,
                                                             m,
                                                             r,
                                                             acc
                                                             )
      }
    }

  }

  private[value] def reduce[V](input: immutable.Seq[JsValue],
                               p    : JsValue => Boolean,
                               m    : JsValue => V,
                               r    : (V, V) => V,
                               acc  : Option[V]
                              ): Option[V] =
  {
    if (input.isEmpty) acc
    else
    {
      val head = input.head
      head match
      {
        case JsObj(headMap) => reduce(input.tail,
                                      p,
                                      m,
                                      r,
                                      Json.reduceHead(r,
                                                      acc,
                                                      JsObj.reduce(
                                                        headMap,
                                                        p,
                                                        m,
                                                        r,
                                                        Option.empty
                                                        )
                                                      )
                                      )
        case JsArray(headSeq) => reduce(input.tail,
                                        p,
                                        m,
                                        r,
                                        Json.reduceHead(r,
                                                        acc,
                                                        reduce(
                                                          headSeq,
                                                          p,
                                                          m,
                                                          r,
                                                          Option.empty
                                                          )
                                                        )
                                        )
        case value: JsValue => if (p(value
                                     )) reduce(input.tail,
                                               p,
                                               m,
                                               r,
                                               Json.reduceHead(r,
                                                               acc,
                                                               m(
                                                                 head
                                                                 )
                                                               )
                                               ) else reduce(input.tail,
                                                             p,
                                                             m,
                                                             r,
                                                             acc
                                                             )
      }
    }

  }


  private[value] def filterJsObj(path  : JsPath,
                                 input : immutable.Seq[JsValue],
                                 result: immutable.Seq[JsValue],
                                 p     : (JsPath, JsObj) => Boolean
                                ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      val headPath = path.inc
      input.head match
      {
        case o: JsObj => if (p(headPath,
                               o
                               )) filterJsObj(headPath,
                                              input.tail,
                                              result.appended(JsObj(JsObj.filterJsObj(headPath,
                                                                                      o.map,
                                                                                      HashMap.empty,
                                                                                      p
                                                                                      )
                                                                    )
                                                              ),
                                              p
                                              ) else filterJsObj(headPath,
                                                                 input.tail,
                                                                 result,
                                                                 p
                                                                 )
        case JsArray(headSeq) => filterJsObj(headPath,
                                             input.tail,
                                             result.appended(JsArray(filterJsObj(headPath / -1,
                                                                                 headSeq,
                                                                                 Vector.empty,
                                                                                 p
                                                                                 )
                                                                     )
                                                             ),
                                             p
                                             )
        case head: JsValue => filterJsObj(headPath,
                                          input.tail,
                                          result.appended(head
                                                          ),
                                          p
                                          )
      }
    }
  }

  private[value] def filterJsObj(input: immutable.Seq[JsValue],
                                 result: immutable.Seq[JsValue],
                                 p: JsObj => Boolean
                                ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case o: JsObj => if (p(o)) filterJsObj(input.tail,
                                               result.appended(JsObj(JsObj.filterJsObj(o.map,
                                                                                       HashMap.empty,
                                                                                       p
                                                                                       )
                                                                     )
                                                               ),
                                               p
                                               ) else filterJsObj(input.tail,
                                                                  result,
                                                                  p
                                                                  )
        case JsArray(headSeq) => filterJsObj(input.tail,
                                             result.appended(JsArray(filterJsObj(headSeq,
                                                                                 Vector.empty,
                                                                                 p
                                                                                 )
                                                                     )
                                                             ),
                                             p
                                             )
        case head: JsValue => filterJsObj(input.tail,
                                          result.appended(head
                                                          ),
                                          p
                                          )
      }
    }
  }

  private[value] def filter(path  : JsPath,
                            input : immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      val headPath = path.inc
      input.head match
      {
        case JsObj(headMap) => filter(headPath,
                                      input.tail,
                                      result.appended(JsObj(JsObj.filter(headPath,
                                                                         headMap,
                                                                         immutable.HashMap.empty,
                                                                         p
                                                                         )
                                                            )
                                                      ),
                                      p
                                      )
        case JsArray(headSeq) => filter(headPath,
                                        input.tail,
                                        result.appended(JsArray(filter(headPath / -1,
                                                                       headSeq,
                                                                       Vector.empty,
                                                                       p
                                                                       )
                                                                )
                                                        ),
                                        p
                                        )
        case head: JsValue => if (p(headPath,
                                    head
                                    )) filter(headPath,
                                              input.tail,
                                              result.appended(head
                                                              ),
                                              p
                                              ) else filter(headPath,
                                                            input.tail,
                                                            result,
                                                            p
                                                            )
      }
    }
  }

  private[value] def filter(input : immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            p     : JsValue => Boolean
                           ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => filter(
          input.tail,
          result.appended(JsObj(JsObj.filter(headMap,
                                             immutable.HashMap.empty,
                                             p
                                             )
                                )
                          ),
          p
          )
        case JsArray(headSeq) => filter(
          input.tail,
          result.appended(JsArray(filter(
            headSeq,
            Vector.empty,
            p
            )
                                  )
                          ),
          p
          )
        case head: JsValue => if (p(head
                                    )) filter(input.tail,
                                              result.appended(head
                                                              ),
                                              p
                                              ) else filter(input.tail,
                                                            result,
                                                            p
                                                            )
      }
    }
  }


  private[value] def map(path  : JsPath,
                         input : immutable.Seq[JsValue],
                         result: immutable.Seq[JsValue],
                         m     : (JsPath, JsValue) => JsValue,
                         p     : (JsPath, JsValue) => Boolean
                        ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      val headPath = path.inc
      input.head match
      {
        case JsObj(headMap) => map(headPath,
                                   input.tail,
                                   result.appended(JsObj(JsObj.map(headPath,
                                                                   headMap,
                                                                   immutable.HashMap.empty,
                                                                   m,
                                                                   p
                                                                   )
                                                         )
                                                   ),
                                   m,
                                   p
                                   )
        case JsArray(headSeq) => map(headPath,
                                     input.tail,
                                     result.appended(JsArray(map(headPath / -1,
                                                                 headSeq,
                                                                 Vector.empty,
                                                                 m,
                                                                 p
                                                                 )
                                                             )
                                                     ),
                                     m,
                                     p
                                     )
        case head: JsValue => if (p(headPath,
                                    head
                                    )) map(headPath,
                                           input.tail,
                                           result.appended(m(headPath,
                                                             head
                                                             )
                                                           ),
                                           m,
                                           p
                                           ) else map(headPath,
                                                      input.tail,
                                                      result.appended(head),
                                                      m,
                                                      p
                                                      )
      }
    }
  }


  private[value] def map(input: immutable.Seq[JsValue],
                         result: immutable.Seq[JsValue],
                         m: JsValue => JsValue
                        ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => map(input.tail,
                                   result.appended(JsObj(JsObj.map(headMap,
                                                                   immutable.HashMap.empty,
                                                                   m
                                                                   )
                                                         )
                                                   ),
                                   m
                                   )
        case JsArray(headSeq) => map(input.tail,
                                     result.appended(JsArray(map(headSeq,
                                                                 Vector.empty,
                                                                 m
                                                                 )
                                                             )
                                                     ),
                                     m
                                     )
        case head: JsValue => map(input.tail,
                                  result.appended(m(
                                    head
                                    )
                                                  ),
                                  m
                                  )
      }
    }
  }


  private[value] def mapKey(path  : JsPath,
                            input : immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            m     : (JsPath, JsValue) => String,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      val headPath = path.inc
      input.head match
      {
        case JsObj(headMap) => mapKey(headPath,
                                      input.tail,
                                      result.appended(JsObj(JsObj.mapKey(headPath,
                                                                         headMap,
                                                                         immutable.HashMap.empty,
                                                                         m,
                                                                         p
                                                                         )
                                                            )
                                                      ),
                                      m,
                                      p
                                      )
        case JsArray(headSeq) => mapKey(headPath,
                                        input.tail,
                                        result.appended(JsArray(mapKey(headPath / -1,
                                                                       headSeq,
                                                                       Vector.empty,
                                                                       m,
                                                                       p
                                                                       )
                                                                )
                                                        ),
                                        m,
                                        p
                                        )
        case head: JsValue => mapKey(headPath,
                                     input.tail,
                                     result.appended(head),
                                     m,
                                     p
                                     )
      }
    }
  }

  private[value] def mapKey(input : immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            m     : String => String
                           ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => mapKey(input.tail,
                                      result.appended(JsObj(JsObj.mapKey(headMap,
                                                                         immutable.HashMap.empty,
                                                                         m
                                                                         )
                                                            )
                                                      ),
                                      m
                                      )
        case JsArray(headSeq) => mapKey(input.tail,
                                        result.appended(JsArray(mapKey(headSeq,
                                                                       Vector.empty,
                                                                       m
                                                                       )
                                                                )
                                                        ),
                                        m
                                        )
        case head: JsValue => mapKey(input.tail,
                                     result.appended(head),
                                     m
                                     )
      }
    }
  }


  private[value] def filterKey(path  : JsPath,
                               input : immutable.Seq[JsValue],
                               result: immutable.Seq[JsValue],
                               p     : (JsPath, JsValue) => Boolean
                              ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      val headPath = path.inc
      input.head match
      {
        case JsObj(headMap) => filterKey(headPath,
                                         input.tail,
                                         result.appended(JsObj(JsObj.filterKey(headPath,
                                                                               headMap,
                                                                               immutable.HashMap.empty,
                                                                               p
                                                                               )
                                                               )

                                                         ),
                                         p
                                         )
        case JsArray(headSeq) => filterKey(headPath,
                                           input.tail,
                                           result.appended(JsArray(filterKey(headPath / -1,
                                                                             headSeq,
                                                                             Vector.empty,
                                                                             p
                                                                             )
                                                                   ),

                                                           ),
                                           p
                                           )
        case head: JsValue => filterKey(headPath,
                                        input.tail,
                                        result.appended(head
                                                        ),
                                        p
                                        )
      }
    }
  }

  private[value] def filterKey(input: immutable.Seq[JsValue],
                               result: immutable.Seq[JsValue],
                               p: String => Boolean
                              ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => filterKey(input.tail,
                                         result.appended(JsObj(JsObj.filterKey(headMap,
                                                                               immutable.HashMap.empty,
                                                                               p
                                                                               )
                                                               )

                                                         ),
                                         p
                                         )
        case JsArray(headSeq) => filterKey(input.tail,
                                           result.appended(JsArray(filterKey(headSeq,
                                                                             Vector.empty,
                                                                             p
                                                                             )
                                                                   ),

                                                           ),
                                           p
                                           )
        case head: JsValue => filterKey(input.tail,
                                        result.appended(head),
                                        p
                                        )
      }
    }
  }

  private[value] def remove(i        : Int,
                            seq      : immutable.Seq[JsValue]
                           ): immutable.Seq[JsValue] =
  {

    if (seq.isEmpty) seq
    else if (i >= seq.size) seq
    else
    {
      val (prefix, suffix): (immutable.Seq[JsValue], immutable.Seq[JsValue]) = seq.splitAt(i)
      prefix.appendedAll(suffix.tail)
    }
  }

  private[value] def flatten(path : JsPath,
                             value: JsArray
                            ): LazyList[(JsPath, JsValue)] =
  {
    if (value.isEmpty) return LazyList.empty
    val head: JsValue = value.head
    val headPath: JsPath = path.inc
    head match
    {
      case a: JsArray => if (a.isEmpty) (headPath, a) +: flatten(headPath,
                                                                 value.tail
                                                                 ) else flatten(headPath / -1,
                                                                                a
                                                                                ) ++: flatten(headPath,
                                                                                              value.tail
                                                                                              )
      case o: JsObj => if (o.isEmpty) (headPath, o) +: flatten(headPath,
                                                               value.tail
                                                               ) else JsObj.flatten(headPath,
                                                                                    o
                                                                                    ) ++: flatten(headPath,
                                                                                                  value.tail
                                                                                                  )
      case _ => (headPath, head) +: flatten(headPath,
                                            value.tail
                                            )
    }
  }


  def apply(pair: (JsPath, JsValue),
            xs  : (JsPath, JsValue)*
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

  def apply(value : JsValue,
            values: JsValue*
           ): JsArray = JsArray(requireNonNull(values)).prepended(requireNonNull(value))

  import java.io.IOException

  import com.fasterxml.jackson.core.JsonParser

  @throws[IOException]
  private[value] def parse(parser: JsonParser): JsArray =
  {
    var root: Vector[JsValue] = Vector.empty
    while (
    {
      true
    })
    {

      val token: JsonToken = parser.nextToken
      var value: JsValue = null
      token.id match
      {
        case ID_END_ARRAY => return JsArray(root)
        case ID_START_OBJECT => value = JsObj.parse(parser)
        case ID_START_ARRAY => value = JsArray.parse(parser)
        case ID_STRING => value = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => value = JsNumber(parser)
        case ID_NUMBER_FLOAT => value = JsBigDec(parser.getDecimalValue)
        case ID_TRUE => value = TRUE
        case ID_FALSE => value = FALSE
        case ID_NULL => value = JsNull
        case _ => throw InternalError.tokenNotFoundParsingStringIntoJsArray(token.name)
      }
      root = root.appended(value)
    }
    throw InternalError.endArrayTokenExpected()
  }
}

object TRUE extends JsBool(true)

object FALSE extends JsBool(false)

/**
 * Json null singleton object
 */
case object JsNull extends JsValue
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