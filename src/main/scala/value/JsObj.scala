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
 * @param map immutable map of JsValue
 */
final case class JsObj(private[value] val map: immutable.Map[String, JsValue] = HashMap.empty) extends Json[JsObj]
{

  def id: Int = 3

  private lazy val str = super.toString

  /**
   * string representation of this Json object. It's a lazy value which is only computed once.
   * @return string representation of this Json object
   */
  override def toString: String = str

  /**
   * returns a LazyList of pairs of (JsPath,JsValue) of the first level of this Json object:
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
   * (c,[1,2])
   * (d,{"e":1,"f":true})
   *
   * }}}
   * @return a lazy list of pairs of path and value
   * @note the difference with [[toLazyListRec]]
   */
  override def toLazyList: LazyList[(JsPath, JsValue)] =
  {
    def toLazyList(obj: JsObj
                  ): LazyList[(JsPath, JsValue)] =
    {

      if (obj.isEmpty) LazyList.empty

      else obj.head #:: toLazyList(obj.tail)
    }

    toLazyList(this)
  }
  /**
   * returns a LazyList of pairs of (JsPath,JsValue) of this Json object, traversing recursively every Json:
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
   * @return a lazy list of pairs of path and value
   * @note the difference with [[toLazyList]]
   */
  override def toLazyListRec: LazyList[(JsPath, JsValue)] = JsObj.toLazyList_(JsPath.empty,
                                                                              this
                                                                              )

  def containsKey(key: String): Boolean = map.contains(requireNonNull(key))

  override def isObj: Boolean = true

  override def isArr: Boolean = false

  override def isEmpty: Boolean = map.isEmpty

  def head: (String, JsValue) = map.head

  def headOption(): Option[(String, JsValue)] = map.headOption

  def last: (String, JsValue) = map.last

  def lastOption: Option[(String, JsValue)] = map.lastOption

  def keys: Iterable[String] = map.keys

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

  override def size: Int = map.size

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

  override def asJsObj: JsObj = this

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsObj

  override def filterRec(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filterRec(JsPath.empty,
                                                                                         map,
                                                                                         HashMap.empty,
                                                                                         requireNonNull(p)
                                                                                         )
                                                                         )

  override def filter(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filter(JsPath.empty,
                                                                                   map,
                                                                                   HashMap.empty,
                                                                                   requireNonNull(p)
                                                                                   )
                                                                      )


  override def filterJsObjRec(p: (JsPath, JsObj) => Boolean): JsObj = JsObj(JsObj.filterJsObjRec(JsPath.empty,
                                                                                                 map,
                                                                                                 HashMap.empty,
                                                                                                 requireNonNull(p)
                                                                                                 )
                                                                            )

  override def filterJsObj(p: (JsPath, JsObj) => Boolean): JsObj = JsObj(JsObj.filterJsObj(JsPath.empty,
                                                                                           map,
                                                                                           HashMap.empty,
                                                                                           requireNonNull(p)
                                                                                           )
                                                                         )

  override def filterKeyRec(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filterKeysRec(JsPath.empty,
                                                                                                map,
                                                                                                HashMap.empty,
                                                                                                requireNonNull(p)
                                                                                                )
                                                                            )

  override def filterKey(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filterKeys(JsPath.empty,
                                                                                          map,
                                                                                          HashMap.empty,
                                                                                          requireNonNull(p)
                                                                                          )
                                                                         )


  override def mapRec[J <: JsValue](m: (JsPath, JsValue) => J,
                                    p: (JsPath, JsValue) => Boolean = (_, _) => true
                                   ): JsObj = JsObj(JsObj.mapRec(JsPath.empty,
                                                                 this.map,
                                                                 HashMap.empty,
                                                                 requireNonNull(m),
                                                                 requireNonNull(p)
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

  override def reduceRec[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                            m: (JsPath, JsValue) => V,
                            r: (V, V) => V
                           ): Option[V] = JsObj.reduceRec(JsPath.empty,
                                                          map,
                                                          requireNonNull(p),
                                                          requireNonNull(m),
                                                          requireNonNull(r),
                                                          Option.empty
                                                          )

  override def reduce[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                         m: (JsPath, JsValue) => V,
                         r: (V, V) => V
                        ): Option[V] = JsObj.reduce(JsPath.empty,
                                                    map,
                                                    requireNonNull(p),
                                                    requireNonNull(m),
                                                    requireNonNull(r),
                                                    Option.empty
                                                    )

  override def asJson: Json[_] = this

  override def mapKeyRec(m: (JsPath, JsValue) => String,
                         p: (JsPath, JsValue) => Boolean = (_, _) => true
                        ): JsObj = JsObj(JsObj.mapKeyRec(JsPath.empty,
                                                         map,
                                                         HashMap.empty,
                                                         requireNonNull(m),
                                                         requireNonNull(p)
                                                         )
                                         )

  override def mapKey(m: (JsPath, JsValue) => String,
                      p: (JsPath, JsValue) => Boolean = (_, _) => true
                     ): JsObj = JsObj(JsObj.mapKey(JsPath.empty,
                                                   map,
                                                   HashMap.empty,
                                                   requireNonNull(m),
                                                   requireNonNull(p)
                                                   )
                                      )

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

  private[value] def toLazyList_(path : JsPath,
                                 value: JsObj
                                ): LazyList[(JsPath, JsValue)] =
  {
    if (value.isEmpty) return LazyList.empty
    val head = value.head

    head._2 match
    {
      case o: JsObj => if (o.isEmpty) (path / head._1, o) +: toLazyList_(path,
                                                                         value.tail
                                                                         ) else toLazyList_(path / head._1,
                                                                                            o
                                                                                            ) ++: toLazyList_(path,
                                                                                                              value.tail
                                                                                                              )
      case a: JsArray => if (a.isEmpty) (path / head._1, a) +: toLazyList_(path,
                                                                           value.tail
                                                                           ) else JsArray.toLazyList_(path / head._1 / -1,
                                                                                                      a
                                                                                                      ) ++: toLazyList_(path,
                                                                                                                        value.tail
                                                                                                                        )
      case _ => (path / head._1, head._2) +: toLazyList_(path,
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
   * @param bytes a Json object serialized in an array of bytes
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
   * @param str a Json object serialized in a string
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
   * @param inputStream the input stream of bytes
   * @param parser parser which define the spec that the Json object must conform
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


  @scala.annotation.tailrec
  private[value] def filter(path: JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, json: Json[_]) => filter(path,
                                          input.tail,
                                          result.updated(key,
                                                         json
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

  private[value] def filterRec(path: JsPath,
                               input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               p     : (JsPath, JsValue) => Boolean
                              ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => filterRec(path,
                                              input.tail,
                                              result.updated(key,
                                                             JsObj(filterRec(path / key,
                                                                             headMap,
                                                                             HashMap.empty,
                                                                             p
                                                                             )
                                                                   )
                                                             ),
                                              p
                                              )
      case (key, JsArray(headSeq)) => filterRec(path,
                                                input.tail,
                                                result.updated(key,
                                                               JsArray(JsArray.filterRec(path / key / -1,
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
                                         )) filterRec(path,
                                                      input.tail,
                                                      result.updated(key,
                                                                     head
                                                                     ),
                                                      p
                                                      ) else filterRec(path,
                                                                       input.tail,
                                                                       result,
                                                                       p
                                                                       )
    }
  }

  private[value] def mapRec(path: JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : (JsPath, JsValue) => JsValue,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => mapRec(path,
                                           input.tail,
                                           result.updated(key,
                                                          JsObj(mapRec(path / key,
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
      case (key, JsArray(headSeq)) => mapRec(path,
                                             input.tail,
                                             result.updated(key,
                                                            JsArray(JsArray.mapRec(path / key / -1,
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
              )) mapRec(path,
                        input.tail,
                        result.updated(key,
                                       m(headPath,
                                         head
                                         )
                                       ),
                        m,
                        p
                        ) else mapRec(path,
                                      input.tail,
                                      result.updated(key,
                                                     head
                                                     ),
                                      m,
                                      p
                                      )
    }
  }

  @scala.annotation.tailrec
  private[value] def map(path: JsPath,
                         input : immutable.Map[String, JsValue],
                         result: immutable.Map[String, JsValue],
                         m     : (JsPath, JsValue) => JsValue,
                         p     : (JsPath, JsValue) => Boolean
                        ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => map(path,
                                  input.tail,
                                  result.updated(key,
                                                 o
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

  private[value] def filterJsObjRec(path  : JsPath,
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
                                    )) filterJsObjRec(path,
                                                      input.tail,
                                                      result.updated(key,
                                                                     JsObj(filterJsObjRec(path / key,
                                                                                          o.map,
                                                                                          HashMap.empty,
                                                                                          p
                                                                                          )
                                                                           )
                                                                     ),
                                                      p
                                                      ) else filterJsObjRec(path,
                                                                            input.tail,
                                                                            result,
                                                                            p
                                                                            )
      case (key, JsArray(headSeq)) => filterJsObjRec(path,
                                                     input.tail,
                                                     result.updated(key,
                                                                    JsArray(JsArray.filterJsObjRec(path / key / -1,
                                                                                                   headSeq,
                                                                                                   Vector.empty,
                                                                                                   p
                                                                                                   )
                                                                            )
                                                                    ),
                                                     p
                                                     )
      case (key, head: JsValue) => filterJsObjRec(path,
                                                  input.tail,
                                                  result.updated(key,
                                                                 head
                                                                 ),
                                                  p
                                                  )
    }
  }


  @scala.annotation.tailrec
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
                                                                  o
                                                                  ),
                                                   p
                                                   ) else filterJsObj(path,
                                                                      input.tail,
                                                                      result,
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

  private[value] def mapKeyRec(path  : JsPath,
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
        mapKeyRec(path,
                  input.tail,
                  result.updated(if (p(headPath,
                                       o
                                       )) m(headPath,
                                            o
                                            ) else key,
                                 JsObj(mapKeyRec(headPath,
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
        mapKeyRec(path,
                  input.tail,
                  result.updated(if (p(headPath,
                                       arr
                                       )) m(headPath,
                                            arr
                                            ) else key,
                                 JsArray(JsArray.mapKeyRec(path / key / -1,
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
        mapKeyRec(path,
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

  @scala.annotation.tailrec
  private[value] def mapKey(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : (JsPath, JsValue) => String,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) return result
    val key = input.head._1
    val headPath = path / key
    mapKey(path,
           input.tail,
           result.updated(if (p(headPath,
                                input.head._2
                                )) m(headPath,
                                     input.head._2
                                     ) else key,
                          input.head._2
                          ),
           m,
           p
           )


  }

  @scala.annotation.tailrec
  private[value] def filterKeys(path  : JsPath,
                                input : immutable.Map[String, JsValue],
                                result: immutable.Map[String, JsValue],
                                p     : (JsPath, JsValue) => Boolean
                               ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else
    {

      val key = input.head._1
      val headPath = path / key
      if (p(headPath,
            input.head._2
            )) filterKeys(path,
                          input.tail,
                          result.updated(key,
                                         input.head._2
                                         ),
                          p
                          ) else filterKeys(path,
                                            input.tail,
                                            result,
                                            p
                                            )
    }


  }


  private[value] def filterKeysRec(path  : JsPath,
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
                                    )) filterKeysRec(path,
                                                     input.tail,
                                                     result.updated(key,
                                                                    JsObj(filterKeysRec(path / key,
                                                                                        o.map,
                                                                                        HashMap.empty,
                                                                                        p
                                                                                        )
                                                                          )
                                                                    ),
                                                     p
                                                     ) else filterKeysRec(path,
                                                                          input.tail,
                                                                          result,
                                                                          p
                                                                          )
      case (key, arr: JsArray) => if (p(path / key,
                                        arr
                                        )) filterKeysRec(path,
                                                         input.tail,
                                                         result.updated(key,
                                                                        JsArray(JsArray.filterKeyRec(path / key / -1,
                                                                                                     arr.seq,
                                                                                                     Vector.empty,
                                                                                                     p
                                                                                                     )
                                                                                )
                                                                        ),
                                                         p
                                                         ) else filterKeysRec(path,
                                                                              input.tail,
                                                                              result,
                                                                              p
                                                                              )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filterKeysRec(path,
                                                          input.tail,
                                                          result.updated(key,
                                                                         head
                                                                         ),
                                                          p
                                                          ) else filterKeysRec(path,
                                                                               input.tail,
                                                                               result,
                                                                               p
                                                                               )
    }
  }

  private[value] def reduceRec[V](path   : JsPath,
                                  input  : immutable.Map[String, JsValue],
                                  p      : (JsPath, JsValue) => Boolean,
                                  m      : (JsPath, JsValue) => V,
                                  r      : (V, V) => V,
                                  acc    : Option[V]
                                 ): Option[V] =
  {

    if (input.isEmpty) acc
    else
    {
      val (key, head): (String, JsValue) = input.head
      head match
      {
        case JsObj(headMap) => reduceRec(path,
                                         input.tail,
                                         p,
                                         m,
                                         r,
                                         Json.reduceHead(r,
                                                         acc,
                                                         reduceRec(path / key,
                                                                   headMap,
                                                                   p,
                                                                   m,
                                                                   r,
                                                                   Option.empty
                                                                   )
                                                         )
                                         )
        case JsArray(headSeq) => reduceRec(path,
                                           input.tail,
                                           p,
                                           m,
                                           r,
                                           Json.reduceHead(r,
                                                           acc,
                                                           JsArray.reduceRec(path / key / -1,
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
                                     )) reduceRec(path,
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
                                                  ) else reduceRec(path,
                                                                   input.tail,
                                                                   p,
                                                                   m,
                                                                   r,
                                                                   acc
                                                                   )
      }
    }
  }

  @scala.annotation.tailrec
  protected[value] def reduce[V](path : JsPath,
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
      if (p(path / key,
            head
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