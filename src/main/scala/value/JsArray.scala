package value

import java.io.{IOException, InputStream}
import java.util.Objects.requireNonNull

import JsArray.remove
import com.fasterxml.jackson.core.{JsonParser, JsonToken}
import com.fasterxml.jackson.core.JsonToken.START_OBJECT
import com.fasterxml.jackson.core.JsonTokenId._
import value.spec.{ArrayOfObjSpec, Invalid, JsArrayPredicate, JsArraySpec, Result}
import value.Preamble._

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}

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
final case class JsArray(private[value] val seq  : immutable.Seq[JsValue] = Vector.empty) extends Json[JsArray]
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
   * val pairs = array.toLazyList
   *
   * pairs.foreach { println }
   *
   * //prints out the following:
   *
   * (0, 1)
   * (1, "hi")
   * (2, [1,2])
   * (3, {"e":1,"f":true})
   *
   * }}}
   *
   * @return a lazy list of pairs of path and value
   * @note the difference with [[flattenRec]]
   */
  def flatten: LazyList[(JsPath, JsValue)] =
  {

    def flatten(i: Int,
                arr   : JsArray
               ): LazyList[(JsPath, JsValue)] =
    {
      if (arr.isEmpty) LazyList.empty

      else
      {
        val pair = (i, arr.head)
        pair #:: flatten(i + 1,
                         arr.tail
                         )
      }

    }

    flatten(0,
            this
            )
  }

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
   * @note the difference with [[flatten]]
   */
  def flattenRec: LazyList[(JsPath, JsValue)] = JsArray.flattenRec(-1,
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

  @`inline` def :+(value: JsValue): JsArray = appended(requireNonNull(value))

  def appended(value: JsValue): JsArray = if (requireNonNull(value).isNothing) this else JsArray(seq.appended(value))

  @`inline` def +:(value: JsValue): JsArray = prepended(requireNonNull(value))

  def prepended(value: JsValue): JsArray = if (requireNonNull(value).isNothing) this else JsArray(seq.prepended(value))

  @`inline` def ++:(xs: IterableOnce[JsValue]): JsArray = prependedAll(requireNonNull(xs))

  def prependedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.prependedAll(requireNonNull(xs).iterator.filterNot(e => e.isNothing)))

  @`inline` def :++(xs: IterableOnce[JsValue]): JsArray = appendedAll(requireNonNull(xs))

  def appendedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.appendedAll(requireNonNull(xs).iterator.filterNot(e => e.isNothing)))

  override def init: JsArray = JsArray(seq.init)

  override def tail: JsArray = JsArray(seq.tail)

  override def inserted(path   : JsPath,
                        value  : JsValue,
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
        case JsPath.empty => JsArray(remove(i,
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

  override def updated(path : JsPath,
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


  override def filterRec(p: (JsPath, JsValue) => Boolean): JsArray = JsArray(JsArray.filterRec(-1,
                                                                                               seq,
                                                                                               Vector.empty,
                                                                                               requireNonNull(p)
                                                                                               )
                                                                             )

  override def filter(p: (JsPath, JsValue) => Boolean): JsArray = JsArray(JsArray.filter(-1,
                                                                                         seq,
                                                                                         Vector.empty,
                                                                                         requireNonNull(p)
                                                                                         )
                                                                          )

  override def filterJsObjRec(p: (JsPath, JsObj) => Boolean): JsArray = JsArray(JsArray.filterJsObjRec(-1,
                                                                                                       seq,
                                                                                                       Vector.empty,
                                                                                                       requireNonNull(p)
                                                                                                       )
                                                                                )

  override def filterJsObj(p: (JsPath, JsObj) => Boolean): JsArray = JsArray(JsArray.filterJsObj(-1,
                                                                                                 seq,
                                                                                                 Vector.empty,
                                                                                                 requireNonNull(p)
                                                                                                 )
                                                                             )

  override def filterKeyRec(p: (JsPath, JsValue) => Boolean): JsArray = JsArray(JsArray.filterKeyRec(-1,
                                                                                                     seq,
                                                                                                     immutable.Vector.empty,
                                                                                                     requireNonNull(p)
                                                                                                     )
                                                                                )

  override def filterKey(p: (JsPath, JsValue) => Boolean): JsArray = this


  override def mapRec[J <: JsValue](m: (JsPath, JsValue) => J,
                                    p: (JsPath, JsValue) => Boolean = (_, _) => true
                                   ): JsArray = JsArray(JsArray.mapRec(-1,
                                                                       seq,
                                                                       Vector.empty,
                                                                       requireNonNull(m),
                                                                       requireNonNull(p)
                                                                       )
                                                        )

  override def map[J <: JsValue](m: (JsPath, JsValue) => J,
                                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                                ): JsArray = JsArray(JsArray.map(-1,
                                                                 seq,
                                                                 Vector.empty,
                                                                 requireNonNull(m),
                                                                 requireNonNull(p)
                                                                 )
                                                     )

  override def reduceRec[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                            m: (JsPath, JsValue) => V,
                            r: (V, V) => V
                           ): Option[V] = JsArray.reduceRec(JsPath.empty / -1,
                                                            seq,
                                                            requireNonNull(p),
                                                            requireNonNull(m),
                                                            requireNonNull(r),
                                                            Option.empty
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

  override def mapKeyRec(m: (JsPath, JsValue) => String,
                         p: (JsPath, JsValue) => Boolean = (_, _) => true
                        ): JsArray = JsArray(JsArray.mapKeyRec(-1,
                                                               seq,
                                                               Vector.empty,
                                                               requireNonNull(m),
                                                               requireNonNull(p)
                                                               )
                                             )

  override def mapKey(m: (JsPath, JsValue) => String,
                      p: (JsPath, JsValue) => Boolean = (_, _) => true
                     ): JsArray = this

}

object JsArray
{
  val empty = JsArray(Vector.empty)

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

  private[value] def reduceRec[V](path : JsPath,
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
        case JsObj(headMap) => reduceRec(headPath,
                                         input.tail,
                                         p,
                                         m,
                                         r,
                                         Json.reduceHead(r,
                                                         acc,
                                                         JsObj.reduceRec(headPath,
                                                                         headMap,
                                                                         p,
                                                                         m,
                                                                         r,
                                                                         Option.empty
                                                                         )
                                                         )
                                         )
        case JsArray(headSeq) => reduceRec(headPath,
                                           input.tail,
                                           p,
                                           m,
                                           r,
                                           Json.reduceHead(r,
                                                           acc,
                                                           reduceRec(headPath / -1,
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
                                     )) reduceRec(headPath,
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
                                                  ) else reduceRec(headPath,
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
      if (p(headPath,
            head
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

  private[value] def filterJsObjRec(path  : JsPath,
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
                               )) filterJsObjRec(headPath,
                                                 input.tail,
                                                 result.appended(JsObj(JsObj.filterJsObjRec(headPath,
                                                                                            o.map,
                                                                                            HashMap.empty,
                                                                                            p
                                                                                            )
                                                                       )
                                                                 ),
                                                 p
                                                 ) else filterJsObjRec(headPath,
                                                                       input.tail,
                                                                       result,
                                                                       p
                                                                       )
        case JsArray(headSeq) => filterJsObjRec(headPath,
                                                input.tail,
                                                result.appended(JsArray(filterJsObjRec(headPath / -1,
                                                                                       headSeq,
                                                                                       Vector.empty,
                                                                                       p
                                                                                       )
                                                                        )
                                                                ),
                                                p
                                                )
        case head: JsValue => filterJsObjRec(headPath,
                                             input.tail,
                                             result.appended(head
                                                             ),
                                             p
                                             )
      }
    }
  }

  @scala.annotation.tailrec
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
                                              result.appended(o),
                                              p
                                              ) else filterJsObj(headPath,
                                                                 input.tail,
                                                                 result,
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


  private[value] def filterRec(path  : JsPath,
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
        case JsObj(headMap) => filterRec(headPath,
                                         input.tail,
                                         result.appended(JsObj(JsObj.filterRec(headPath,
                                                                               headMap,
                                                                               immutable.HashMap.empty,
                                                                               p
                                                                               )
                                                               )
                                                         ),
                                         p
                                         )
        case JsArray(headSeq) => filterRec(headPath,
                                           input.tail,
                                           result.appended(JsArray(filterRec(headPath / -1,
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
                                    )) filterRec(headPath,
                                                 input.tail,
                                                 result.appended(head
                                                                 ),
                                                 p
                                                 ) else filterRec(headPath,
                                                                  input.tail,
                                                                  result,
                                                                  p
                                                                  )
      }
    }
  }

  @scala.annotation.tailrec
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
        case json: Json[_] => filter(headPath,
                                     input.tail,
                                     result.appended(json),
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

  private[value] def mapRec(path  : JsPath,
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
        case JsObj(headMap) => mapRec(headPath,
                                      input.tail,
                                      result.appended(JsObj(JsObj.mapRec(headPath,
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
        case JsArray(headSeq) => mapRec(headPath,
                                        input.tail,
                                        result.appended(JsArray(mapRec(headPath / -1,
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
                                    )) mapRec(headPath,
                                              input.tail,
                                              result.appended(m(headPath,
                                                                head
                                                                )
                                                              ),
                                              m,
                                              p
                                              ) else mapRec(headPath,
                                                            input.tail,
                                                            result.appended(head),
                                                            m,
                                                            p
                                                            )
      }
    }
  }

  @scala.annotation.tailrec
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
        case o: Json[_] => map(headPath,
                               input.tail,
                               result.appended(o),
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

  private[value] def mapKeyRec(path  : JsPath,
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
        case JsObj(headMap) => mapKeyRec(headPath,
                                         input.tail,
                                         result.appended(JsObj(JsObj.mapKeyRec(headPath,
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
        case JsArray(headSeq) => mapKeyRec(headPath,
                                           input.tail,
                                           result.appended(JsArray(mapKeyRec(headPath / -1,
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
        case head: JsValue => mapKeyRec(headPath,
                                        input.tail,
                                        result.appended(head),
                                        m,
                                        p
                                        )
      }
    }
  }


  private[value] def filterKeyRec(path  : JsPath,
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
        case JsObj(headMap) => filterKeyRec(headPath,
                                            input.tail,
                                            result.appended(JsObj(JsObj.filterKeysRec(headPath,
                                                                                      headMap,
                                                                                      immutable.HashMap.empty,
                                                                                      p
                                                                                      )
                                                                  )

                                                            ),
                                            p
                                            )
        case JsArray(headSeq) => filterKeyRec(headPath,
                                              input.tail,
                                              result.appended(JsArray(filterKeyRec(headPath / -1,
                                                                                   headSeq,
                                                                                   Vector.empty,
                                                                                   p
                                                                                   )
                                                                      ),

                                                              ),
                                              p
                                              )
        case head: JsValue => filterKeyRec(headPath,
                                           input.tail,
                                           result.appended(head
                                                           ),
                                           p
                                           )
      }
    }
  }

  private[value] def remove(i: Int,
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

  private[value] def flattenRec(path: JsPath,
                                value: JsArray
                               ): LazyList[(JsPath, JsValue)] =
  {
    if (value.isEmpty) return LazyList.empty
    val head: JsValue = value.head
    val headPath: JsPath = path.inc
    head match
    {
      case a: JsArray => if (a.isEmpty) (headPath, a) +: flattenRec(headPath,
                                                                    value.tail
                                                                    ) else flattenRec(headPath / -1,
                                                                                      a
                                                                                      ) ++: flattenRec(headPath,
                                                                                                       value.tail
                                                                                                       )
      case o: JsObj => if (o.isEmpty) (headPath, o) +: flattenRec(headPath,
                                                                  value.tail
                                                                  ) else JsObj.flattenRec(headPath,
                                                                                          o
                                                                                          ) ++: flattenRec(headPath,
                                                                                                           value.tail
                                                                                                           )
      case _ => (headPath, head) +: flattenRec(headPath,
                                               value.tail
                                               )
    }
  }

  def apply(value: JsValue,
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
