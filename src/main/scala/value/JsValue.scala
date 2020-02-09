package value

import java.io.{ByteArrayOutputStream, OutputStream}
import java.util.Objects
import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.exc.InputCoercionException
import value.spec.{ArrayOfObjSpec, Invalid, JsArrayPredicate, JsArraySpec, JsObjSpec, Result}

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

/** Represents any element in a Json.
 * All the value types are immutable, being the Json array and Json object implemented with
 * persistent data structures
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
   * @return isDouble || isBigDec
   */
  def isDecimal: Boolean = isDouble || isBigDec

  /**
   * returns true if this type is an integral number
   *
   * @return isInt || isLong || isBigInt
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
            ): Boolean = isJson && predicate(toJson)

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
  def isStr(predicate: String => Boolean): Boolean = isStr && predicate(toJsStr.value)

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
  def isObj(predicate: JsObj => Boolean): Boolean = isObj && predicate(toJsObj)

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
  def isArr(predicate: JsArray => Boolean): Boolean = isArr && predicate(toJsArray)

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
  def isInt(predicate: Int => Boolean): Boolean = isInt && predicate(toJsInt.value)

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
  def isLong(predicate: Long => Boolean): Boolean = isLong && predicate(toJsLong.value)

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
  def isDouble(predicate: Double => Boolean): Boolean = isDouble && predicate(toJsDouble.value)

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
  def isIntegral(predicate: BigInt => Boolean): Boolean = isBigInt && predicate(toJsBigInt.value)

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
  def isDecimal(predicate: BigDecimal => Boolean): Boolean = isBigDec && predicate(toJsBigDec.value)


  /**
   * returns true if this is [[JsNull]]
   *
   * @return true if this is [[JsNull]], false otherwise
   */
  def isNull: Boolean

  /**
   * returns true if this is not null
   *
   * @return true if this is not null, false otherwise
   */
  def isNotNull: Boolean = !isNull

  /**
   * returns true if this is [[JsNothing]]
   *
   * @return true if this is [[JsNothing]], false otherwise
   */
  def isNothing: Boolean

  /**
   * returns this value as a [[JsLong]] if it is a [[JsLong]] or a [[JsInt]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  isInt || isLong can help to that purpose.
   *
   * @return this value as a [[JsLong]]
   */
  @throws(classOf[value.UserError])
  def toJsLong: JsLong


  /**
   * returns this value as a [[JsInt]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isInt can help to that purpose.
   *
   * @return this value as a [[JsInt]]
   */
  @throws(classOf[value.UserError])
  def toJsInt: JsInt

  /**
   * returns this value as a [[JsBigInt]] if it's an integral number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isIntegral can help to that purpose.
   *
   * @return this value as a [[JsBigInt]]
   */
  @throws(classOf[value.UserError])
  def toJsBigInt: JsBigInt

  /**
   * returns this value as a [[JsBigDec]] if it's a decimal number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isDecimal can help to that purpose.
   *
   * @return this value as a [[JsBigDec]]
   */
  @throws(classOf[value.UserError])
  def toJsBigDec: JsBigDec

  /**
   * returns this value as a [[JsBool]] if it's a boolean, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isBool can help to that purpose.
   *
   * @return this value as a [[JsBool]]
   */
  @throws(classOf[value.UserError])
  def toJsBool: JsBool

  /**
   * returns this value as a [[JsNull]] if it's null, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isNull can help to that purpose.
   *
   * @return this value as a [[JsNull]]
   */
  @throws(classOf[value.UserError])
  def toJsNull: JsNull.type

  /**
   * returns this value as a [[JsObj]] if it's an object, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isObj can help to that purpose.
   *
   * @return this value as a [[JsObj]]
   */
  @throws(classOf[value.UserError])
  def toJsObj: JsObj

  /**
   * returns this value as a [[JsStr]] if it's a string, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isStr can help to that purpose.
   *
   * @return this value as a [[JsStr]]
   */
  @throws(classOf[value.UserError])
  def toJsStr: JsStr


  /**
   * returns this value as a [[JsDouble]] if it is a [[JsLong]] or a [[JsInt]] or a [[JsDouble]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  isInt || isLong || isDouble  can help to that purpose.
   *
   * @return this value as a [[JsDouble]]
   */
  @throws(classOf[value.UserError])
  def toJsDouble: JsDouble

  /**
   * returns this value as a [[JsArray]] if it's an array, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  isArr  can help to that purpose.
   *
   * @return this value as a [[JsArray]]
   */
  @throws(classOf[value.UserError])
  def toJsArray: JsArray

  /**
   * returns this value as a [[JsNumber]] if it's a number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the invocation to this function doesn't fail. The guard
   * condition  isNumber  can help to that purpose.
   *
   * @return this value as a [[JsNumber]]
   *
   */
  @throws(classOf[value.UserError])
  def toJsNumber: JsNumber

  /**
   * returns this value as a [[Json]] if it's an object or an array, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition isJson can help to that purpose.
   *
   * @return this value as a [[Json]]
   */
  @throws(classOf[value.UserError])
  def toJson: Json[_]


}

/** Represents any value in a Json that is not a container, i.e. a Json object or a Json array
 *
 */
sealed trait JsPrimitive extends JsValue
{
  override def isArr: Boolean = false

  override def isObj: Boolean = false

  override def isNothing: Boolean = false

}

/** Represents an immutable string
 *
 * @param value the value of the string
 */
final case class JsStr(value: String) extends JsPrimitive
{
  Objects.requireNonNull(value)

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

  override def toJsLong: JsLong = throw UserError.toJsLongOfJsStr

  override def toJsNull: JsNull.type = throw UserError.toJsNullOfJsStr

  override def toJsStr: JsStr = this

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsStr

  override def toJsBigInt: JsBigInt = throw UserError.toJsBigIntOfJsStr

  override def toJsBigDec: JsBigDec = throw UserError.toJsBigDecOfJsStr

  override def toJsBool: JsBool = throw UserError.toJsBoolOfJsStr

  override def toJsObj: JsObj = throw UserError.toJsObjOfJsStr

  override def toJsDouble: JsDouble = throw UserError.toJsDoubleOfJsStr

  override def toJsArray: JsArray = throw UserError.toJsArrayOfJsStr

  override def toString: String = s"""\"$value\""""

  override def toJsNumber: JsNumber = throw UserError.toJsNumberOfJsStr

  def map(m: String => String): JsStr = JsStr(requireNonNull(m)(value))

  override def toJson: Json[_] = throw UserError.toJsonOfJsStr

  override def id: Int = 2

}

/** Represents an immutable number
 *
 */
sealed trait JsNumber extends JsPrimitive
{

  override def isStr: Boolean = false

  override def isBool: Boolean = false

  override def isNull: Boolean = false

  override def isNumber: Boolean = true

  override def toJsStr: JsStr = throw UserError.toJsStrOfJsNumber

  override def toJsNull: JsNull.type = throw UserError.toJsNullOfJsNumber

  override def toJsBool: JsBool = throw UserError.toJsBoolOfJsNumber

  override def toJsObj: JsObj = throw UserError.toJsObjOfJsNumber

  override def toJsArray: JsArray = throw UserError.toJsArrayOfJsNumber

  override def toJson: Json[_] = throw UserError.toJsonOfJsNumber

  override def toJsNumber: JsNumber = this

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

  override def toJsLong: JsLong = JsLong(value.toLong)

  override def toJsInt: JsInt = this

  override def toJsBigInt: JsBigInt = JsBigInt(value)

  override def toJsBigDec: JsBigDec = JsBigDec(value)

  override def toJsDouble: JsDouble = JsDouble(value.toDouble)

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

  /** returns true if that represents the same number, no matter the type it's wrapped in:
   *
   * JsInt(1)    ==    JsDouble(1.0)   // true
   * JsLong(1)   ==    JsDouble(1.0)   // true
   * JsBigInt(1) ==    JsDouble(1.0)   // true
   *
   * @param that
   * @return
   */
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

  override def toJsLong: JsLong = throw UserError.toJsLongOfJsDouble

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsDouble

  override def toJsBigInt: JsBigInt = throw UserError.toJsBigIntOfJsDouble

  override def toJsBigDec: JsBigDec = JsBigDec(value)

  override def toJsDouble: JsDouble = this

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

  override def toJsLong: JsLong = this

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsLong

  override def toJsBigInt: JsBigInt = JsBigInt(value)

  override def toJsBigDec: JsBigDec = JsBigDec(value)

  override def toJsDouble: JsDouble = JsDouble(value.toDouble)

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

  Objects.requireNonNull(value)

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = true

  override def toString: String = value.toString

  override def toJsLong: JsLong = throw UserError.toJsLongOfJsBigDec

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsBigDec

  override def toJsBigInt: JsBigInt = throw UserError.toJsBigIntOfJsBigDec

  override def toJsDouble: JsDouble = throw UserError.toJsDoubleOfJsBigDec

  override def toJsBigDec: JsBigDec = this

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
  Objects.requireNonNull(value)

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

  override def toJsLong: JsLong = throw UserError.toJsLongOfJsBigInt

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsBigInt

  override def toJsDouble: JsDouble = throw UserError.toJsDoubleOfJsBigInt

  override def toJsBigInt: JsBigInt = this

  override def toJsBigDec: JsBigDec = JsBigDec(BigDecimal(value))

  def id: Int = 6
}

/**
 * represents an immutable boolean
 *
 * @param value the value associated, either true or false
 */
sealed case class JsBool(value: Boolean) extends JsPrimitive
{
  if (value) TRUE
  else FALSE

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

  override def toJsLong: JsLong = throw UserError.toJsLongOfJsBool

  override def toJsStr: JsStr = throw UserError.toJsStrOfJsBool

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsBool

  override def toJsBigInt: JsBigInt = throw UserError.toJsBigIntOfJsBool

  override def toJsBigDec: JsBigDec = throw UserError.toJsBigDecOfJsBool

  override def toJsBool: JsBool = this

  override def toJsNull: JsNull.type = throw UserError.toJsNullOfJsBool

  override def toJsObj: JsObj = throw UserError.toJsObjOfJsBool

  override def toJsArray: JsArray = throw UserError.toJsArrayOfJsBool

  override def toJsDouble: JsDouble = throw UserError.toJsDoubleOfJsBool

  override def toJsNumber: JsNumber = throw UserError.toJsNumberOfJsBool

  override def toJson: Json[_] = throw UserError.toJsonOfJsBool

  override def id: Int = 0
}

sealed trait Json[T <: Json[T]] extends JsValue
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
  override def toJsLong: JsLong = throw UserError.toJsLongOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsNull: JsNull.type = throw UserError.toJsNullOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsInt: JsInt = throw UserError.toJsIntOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsBigInt: JsBigInt = throw UserError.toJsBigIntOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsBigDec: JsBigDec = throw UserError.toJsBigDecOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsBool: JsBool = throw UserError.toJsBoolOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsNumber: JsNumber = throw UserError.toJsNumberOfJson

  /** throws an UserError exception
   *
   *
   */
  override def toJsStr: JsStr = throw UserError.toJsStrOfJson

  /** throws an UserError exception
   *
   * @return
   */
  override def toJsDouble: JsDouble = throw UserError.toJsDoubleOfJson

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
      else this (path.head).toJson.apply(path.tail)
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
            (_: (JsPath, JsValue)) => true
           ): Int = flatten.count(requireNonNull(p))


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
   *
   * @param p the predicate uses to test elements. The predicate accepts the path/value pair of each element
   * @return a new Json  consisting of all elements of this
   *         Json that satisfy the given predicate p.
   */
  def filter(p: (JsPath, JsPrimitive) => Boolean): T

  /** Selects all the values of this Json which satisfy a predicate and are not Jsons. When a Json is
   * found, it is filtered recursively.
   *
   * @param p the predicate uses to test elements. The predicate accepts the value of each element
   * @return a new Json  consisting of all elements of this
   *         Json that satisfy the given predicate p.
   */
  def filter(p: JsPrimitive => Boolean): T

  /**
   * Builds a new Json by applying a function to all elements of this Json that are not Json and satisfies a
   * given predicate. When a Json is found, it it mapped recursively.
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
  def mapKeys(m: (JsPath, JsValue) => String,
              p: (JsPath, JsValue) => Boolean
             ): T

  /**
   * Builds a new Json by applying a function to all the keys of this Json.
   * If the element associated to a key is a Json, the function is applied recursively,
   *
   * @param m the function to apply to each key. It accepts the key name as a parameter
   * @return
   */
  def mapKeys(m: String => String
             ): T

  def reduce[V](p: (JsPath, JsPrimitive) => Boolean,
                m: (JsPath, JsPrimitive) => V,
                r: (V, V) => V
               ): Option[V]

  /** Removes all the Json object of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively (if it passes the filter).
   *
   * @param p the predicate uses to test the path/object pairs.
   * @return a new Json consisting of all its elements except those
   *         Json object that dont satisfy the given predicate p.
   */
  def filterJsObj(p: (JsPath, JsObj) => Boolean): T

  /** Removes all the Json object of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively (if it passes the filter).
   *
   * @param p the predicate uses to test the Json object.
   * @return a new Json consisting of all its elements except those
   *         Json object that dont satisfy the given predicate p.
   */
  def filterJsObj(p: JsObj => Boolean): T


  /** Removes all the keys of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively.
   *
   * @param p the predicate uses to test the path/value pairs.
   * @return a new Json consisting of all array elements of this
   *         Json and those key/value pairs that satisfy the given predicate p.
   */
  def filterKeys(p: (JsPath, JsValue) => Boolean): T

  /** Removes all the keys of this Json which dont' satisfy a predicate. When a Json is
   * found, it is filtered recursively.
   *
   * @param p the predicate uses to test the keys.
   * @return a new Json consisting of all array elements of this
   *         Json and those key/value pairs that satisfy the given predicate p.
   */
  def filterKeys(p: String => Boolean): T


  /** Creates a new Json obtained by inserting a given path/value pair into this Json.
   * The given element is always inserted at the given path, even if it requires to create new Json
   * or padding arrays.
   *
   * @param    path  the path
   * @param    value the value
   * @return A new Json  with the new path/value mapping added to this Json.
   * @note [[inserted]] function unless updated, always inserts the given path/value pair
   */
  def inserted(path: JsPath,
               value  : JsValue
              ): T

  def inserted(path: JsPath,
               value: JsValue,
               padWith: JsValue
              ): T

  def inserted(path: JsPath,
               value: Try[JsValue],
               padWith   : JsValue
              ): Try[T]

  def inserted(path: JsPath,
               value     : Try[JsValue]
              ): Try[T]

  def inserted(path: JsPath,
               value  : Future[JsValue],
               padWith: JsValue
              )
              (implicit executor: ExecutionContext): Future[T]

  def inserted(path: JsPath,
               value  : Future[JsValue]
              )
              (implicit executor: ExecutionContext): Future[T]
}

/**
 * represents an immutable Json object. There are several ways of creating a Json object, being the most
 * common the following:
 *
 *  - From a string, array of bytes or an input stream of bytes, using the parse functions of the companion object
 *  - From the apply function of the companion object.
 *
 * @param map immutable map of JsValue
 */
final case class JsObj(override private[value] val map: immutable.Map[String, JsValue] = HashMap.empty) extends AbstractJsObj(map) with IterableOnce[(String, JsValue)] with Json[JsObj]
{


  Objects.requireNonNull(map)

  def id: Int = 3

  private lazy val str = super.toString

  /**
   * string representation of this Json array. It's a lazy value which is only computed once.
   *
   * @return string representation of this Json array
   */
  override def toString: String = str

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
                        value: JsValue
                       ): JsObj =
  {
    inserted(path,
             value,
             JsNull
             )
  }

  override def inserted(path: JsPath,
                        value  : JsValue,
                        padWith: JsValue
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

  override def inserted(path: JsPath,
                        value: Future[JsValue]
                       )
                       (implicit executor: ExecutionContext): Future[JsObj] = inserted(path,
                                                                                       value,
                                                                                       JsNull
                                                                                       )

  override def inserted(path: JsPath,
                        value: Future[JsValue],
                        padWith: JsValue
                       )
                       (implicit executor: ExecutionContext): Future[JsObj] =
    value.map(v => this.inserted(path,
                                 v,
                                 padWith = padWith
                                 )
              )

  override def inserted(path: JsPath,
                        value: Try[JsValue]
                       ): Try[JsObj] = inserted(path,
                                                value,
                                                JsNull
                                                )

  override def inserted(path: JsPath,
                        value: Try[JsValue],
                        padWith: JsValue
                       ): Try[JsObj] =
    value.map(v => this.inserted(path,
                                 v,
                                 padWith = padWith
                                 )
              )


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
  override def toJsObj: JsObj = this

  /** Returns this Json object as a `Json`
   *
   * @return this Json object as a `Json`
   */
  override def toJson: Json[_] = this


}

/**
 * represents an immutable Json array. There are several ways of creating a Json array, being the most
 * common the following:
 *
 *  - From a string, array of bytes or an input stream of bytes, using the parse functions of the companion object
 *  - From the apply function of the companion object:
 *
 * @param seq immutable seq of JsValue
 */
final case class JsArray(override private[value] val seq: immutable.Seq[JsValue] = Vector.empty) extends AbstractJsArray(seq) with IterableOnce[JsValue] with Json[JsArray]
{
  Objects.requireNonNull(seq)

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

  override def inserted(path   : JsPath,
                        value  : JsValue
                       ): JsArray = inserted(path,
                                             value,
                                             JsNull
                                             )

  override def inserted(path   : JsPath,
                        value  : JsValue,
                        padWith: JsValue
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

  override def inserted(path: JsPath,
                        value: Future[JsValue]
                       )
                       (implicit executor : ExecutionContext): Future[JsArray] = inserted(path,
                                                                                          value,
                                                                                          JsNull
                                                                                          )

  override def inserted(path: JsPath,
                        value: Future[JsValue],
                        padWith: JsValue
                       )
                       (implicit executor : ExecutionContext): Future[JsArray] = value.map(v => this.inserted(path,
                                                                                                              v,
                                                                                                              padWith
                                                                                                              )
                                                                                           )

  override def inserted(path: JsPath,
                        value: Try[JsValue]
                       ): Try[JsArray] = inserted(path,
                                                  value,
                                                  JsNull
                                                  )

  override def inserted(path: JsPath,
                        value: Try[JsValue],
                        padWith: JsValue
                       ): Try[JsArray] = value.map(v => this.inserted(path,
                                                                      v,
                                                                      padWith
                                                                      )
                                                   )

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

  override def toJsArray: JsArray = this

  override def toJson: Json[_] = this

}

/**
 * It's a special Json value that represents 'nothing'. Inserting nothing in a json leaves the json
 * unchanged. Functions that return a [[JsValue]], return JsNothing when no element is found, what makes
 * them total on their arguments.
 *
 * val obj = JsObj.empty
 * obj("a") == JsNothing
 *   obj.inserted("a",JsNothing) == obj
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

  override def toJsLong = throw UserError.toJsLongOfJsNothing

  override def toJsNull = throw UserError.toJsNullOfJsNothing

  override def toJsStr = throw UserError.toJsStrOfJsNothing

  override def toJsInt = throw UserError.toJsIntOfJsNothing

  override def toJsBigInt = throw UserError.toJsBigIntOfJsNothing

  override def toJsBigDec = throw UserError.toJsBigDecOfJsNothing

  override def toJsBool = throw UserError.toJsBoolOfJsNothing

  override def toJsObj = throw UserError.toJsObjOfJsNothing

  override def toJsArray = throw UserError.toJsArrayOfJsNothing

  override def toJsDouble = throw UserError.toJsDoubleOfJsNothing

  override def toJsNumber = throw UserError.toJsNumberOfJsNothing

  override def toJson = throw UserError.toJsonOfJsNothing

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

  override def toJsLong: JsLong = throw UserError.toJsLongOfJsNull

  override def toJsNull: JsNull.type = this

  override def toJsStr: JsStr = throw UserError.toJsStrOfJsNull

  override def toJsInt: JsInt = throw UserError.toJsIntOfJsNull

  override def toJsBigInt: JsBigInt = throw UserError.toJsBigIntOfJsNull

  override def toJsBigDec: JsBigDec = throw UserError.toJsBigDecOfJsNull

  override def toJsBool: JsBool = throw UserError.toJsBoolOfJsNull

  override def toJsObj: JsObj = throw UserError.toJsObjOfJsNull

  override def toJsArray: JsArray = throw UserError.toJsArrayOfJsNull

  override def toJsDouble: JsDouble = throw UserError.toJsDoubleOfJsNull

  override def toJsNumber: JsNumber = throw UserError.toJsNumberOfJsNull

  override def toJson: Json[_] = throw UserError.toJsonOfJsNull

  override def id: Int = 1


}

object JsNumber
{
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

}


object JsArray
{
  val empty = JsArray(Vector.empty)

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

    apply0(empty.inserted(pair._1,
                          pair._2
                          ),
           xs
           )
  }

  def apply(value : JsValue,
            values: JsValue*
           ): JsArray = JsArray(requireNonNull(values)).prepended(requireNonNull(value))
}

object TRUE extends JsBool(true)

object FALSE extends JsBool(false)

