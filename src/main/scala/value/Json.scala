package value

import java.io.{ByteArrayOutputStream, OutputStream}
import java.util.Objects.requireNonNull



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
    () => {
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


  /**Removes a path from this Json
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


  /**Creates a new Json from this Json by removing all paths of another collection
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
  def map[J <: JsValue](m   : (JsPath, JsValue) => J,
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
  def mapKey(m   : (JsPath, JsValue) => String,
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

  def reduce[V](p   : (JsPath, JsValue) => Boolean,
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
  def filterJsObj(p   : (JsPath, JsObj) => Boolean): T

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
  def filterKey(p   : (JsPath, JsValue) => Boolean): T

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
  def inserted(path   : JsPath,
               value  : JsValue,
               padWith: JsValue = JsNull
              ): T
}

object Json
{


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
