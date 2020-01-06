package value

import java.io.InputStream
import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.START_ARRAY
import com.fasterxml.jackson.core.JsonTokenId._
import value.Preamble._
import value.spec.{Invalid, JsObjSpec}
import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}

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
   *  NoSuchElementException if the Json object is empty
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
   *  NoSuchElementException if the Json object is empty
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

  /**Collects all keys of this map in a set.
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


  override def updated(path : JsPath,
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


  override def inserted(path   : JsPath,
                        value  : JsValue,
                        padWith: JsValue = JsNull
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
  override def filter(p   : (JsPath, JsValue) => Boolean): JsObj =
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


  override def filterJsObj(p   : (JsPath, JsObj) => Boolean): JsObj =
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


  override def filterKey(p   : (JsPath, JsValue) => Boolean): JsObj =
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
                                 p   : (JsPath, JsValue) => Boolean = (_, _) => true
                                ): JsObj = JsObj(JsObj.map(JsPath.empty,
                                                           this.map,
                                                           HashMap.empty,
                                                           requireNonNull(m),
                                                           requireNonNull(p)
                                                           )
                                                 )


  override def reduce[V](p   : (JsPath, JsValue) => Boolean = (_, _) => true,
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