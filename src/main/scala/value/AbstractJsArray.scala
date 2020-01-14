package value

import java.util.Objects.requireNonNull
import java.io.IOException
import com.fasterxml.jackson.core.{JsonParser, JsonToken}
import com.fasterxml.jackson.core.JsonTokenId.{ID_END_ARRAY, ID_FALSE, ID_NULL, ID_NUMBER_FLOAT, ID_NUMBER_INT, ID_START_ARRAY, ID_START_OBJECT, ID_STRING, ID_TRUE}
import value.JsPath.MINUS_ONE

import scala.collection.immutable
import scala.collection.immutable.HashMap

/**
 * abstract class to reduce class file size in subclass.
 *
 * @param seq the seq of values
 */
private[value] abstract class AbstractJsArray(private[value] val seq: immutable.Seq[JsValue])
{

  def toJsObj: JsObj = throw UserError.asJsObjOfJsArray

  def isObj: Boolean = false

  def isArr: Boolean = true

  private lazy val str = super.toString

  /**
   * string representation of this Json array. It's a lazy value which is only computed once.
   *
   * @return string representation of this Json array
   */
  override def toString: String = str

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
  final private[value] def fillWith[E <: JsValue, P <: JsValue](seq: immutable.Seq[JsValue],
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


  def prependedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.prependedAll(requireNonNull(xs).iterator.filterNot(e => e == JsNothing)))

  def appendedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.appendedAll(requireNonNull(xs).iterator.filterNot(e => e == JsNothing)))

  def init: JsArray = JsArray(seq.init)

  def tail: JsArray = JsArray(seq.tail)


  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsArray(m) => m == seq
      case _ => false
    }
  }

  def filter(p: (JsPath, JsPrimitive) => Boolean): JsArray = JsArray(AbstractJsArray.filter(MINUS_ONE,
                                                                                            seq,
                                                                                            Vector.empty,
                                                                                            requireNonNull(p)
                                                                                            )
                                                                     )


  def filterJsObj(p: (JsPath, JsObj) => Boolean): JsArray = JsArray(AbstractJsArray.filterJsObj(MINUS_ONE,
                                                                                                seq,
                                                                                                Vector.empty,
                                                                                                requireNonNull(p)
                                                                                                )
                                                                    )


  def filterKey(p: (JsPath, JsValue) => Boolean): JsArray = JsArray(AbstractJsArray.filterKey(MINUS_ONE,
                                                                                              seq,
                                                                                              immutable.Vector.empty,
                                                                                              requireNonNull(p)
                                                                                              )
                                                                    )


  def flatMap(f: JsValue => JsArray): JsArray = JsArray(seq.flatMap(f))

  def iterator: Iterator[JsValue] = seq.iterator

  def foreach(f: JsValue => Unit): Unit = seq.foreach(f)

  def map[J <: JsValue](m: (JsPath, JsPrimitive) => J,
                        p    : (JsPath, JsPrimitive) => Boolean = (_, _) => true
                       ): JsArray = JsArray(AbstractJsArray.map(MINUS_ONE,
                                                                seq,
                                                                Vector.empty,
                                                                requireNonNull(m),
                                                                requireNonNull(p)
                                                                )
                                            )


  def reduce[V](p: (JsPath, JsPrimitive) => Boolean = (_, _) => true,
                m: (JsPath, JsPrimitive) => V,
                r: (V, V) => V
               ): Option[V] = AbstractJsArray.reduce(JsPath.empty / MINUS_ONE,
                                                     seq,
                                                     requireNonNull(p),
                                                     requireNonNull(m),
                                                     requireNonNull(r),
                                                     Option.empty
                                                     )

  def mapKey(m: (JsPath, JsValue) => String,
             p: (JsPath, JsValue) => Boolean = (_, _) => true
            ): JsArray = JsArray(AbstractJsArray.mapKey(MINUS_ONE,
                                                        seq,
                                                        Vector.empty,
                                                        requireNonNull(m),
                                                        requireNonNull(p)
                                                        )
                                 )

  def filter(p: JsPrimitive => Boolean): JsArray = JsArray(AbstractJsArray.filter(seq,
                                                                                  Vector.empty,
                                                                                  requireNonNull(p)
                                                                                  )
                                                           )

  def map[J <: JsValue](m: JsPrimitive => J): JsArray =
    JsArray(AbstractJsArray.map(seq,
                                Vector.empty,
                                requireNonNull(m)
                                )
            )

  def mapKey(m: String => String): JsArray =
    JsArray(AbstractJsArray.mapKey(seq,
                                   Vector.empty,
                                   requireNonNull(m)
                                   )
            )


  def filterJsObj(p: JsObj => Boolean): JsArray =
    JsArray(AbstractJsArray.filterJsObj(seq,
                                        Vector.empty,
                                        requireNonNull(p)
                                        )
            )

  def filterKey(p: String => Boolean): JsArray =
    JsArray(AbstractJsArray.filterKey(seq,
                                      immutable.Vector.empty,
                                      requireNonNull(p)
                                      )
            )

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
  def flatten: LazyList[(JsPath, JsValue)] = AbstractJsArray.flatten(MINUS_ONE,
                                                                     seq
                                                                     )
}


private[value] object AbstractJsArray
{

  private[value] def flatten(path: JsPath,
                             seq : immutable.Seq[JsValue]
                            ): LazyList[(JsPath, JsValue)] =
  {
    if (seq.isEmpty) return LazyList.empty
    val head: JsValue = seq.head
    val headPath: JsPath = path.inc
    head match
    {
      case JsArray(headSeq) =>
        if (headSeq.isEmpty) (headPath, JsArray.empty) +: flatten(headPath,
                                                                  seq.tail
                                                                  )
        else flatten(headPath / MINUS_ONE,
                     headSeq
                     ) ++: flatten(headPath,
                                   seq.tail
                                   )
      case JsObj(headMap) =>
        if (headMap.isEmpty) (headPath, JsObj.empty) +: flatten(headPath,
                                                                seq.tail
                                                                )
        else AbstractJsObj.flatten(headPath,
                                   headMap
                                   ) ++: flatten(headPath,
                                                 seq.tail
                                                 )
      case _ => (headPath, head) +: flatten(headPath,
                                            seq.tail
                                            )
    }
  }

  private[value] def filterKey(input: immutable.Seq[JsValue],
                               result: immutable.Seq[JsValue],
                               p     : String => Boolean
                              ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => filterKey(input.tail,
                                         result.appended(JsObj(AbstractJsObj.filterKey(headMap,
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

  private[value] def remove(i: Int,
                            seq: immutable.Seq[JsValue]
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
        case ID_START_OBJECT => value = AbstractJsObj.parse(parser)
        case ID_START_ARRAY => value = AbstractJsArray.parse(parser)
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

  private[value] def reduce[V](path: JsPath,
                               input: immutable.Seq[JsValue],
                               p    : (JsPath, JsPrimitive) => Boolean,
                               m    : (JsPath, JsPrimitive) => V,
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
                                      AbstractJson.reduceHead(r,
                                                              acc,
                                                              AbstractJsObj.reduce(headPath,
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
                                        AbstractJson.reduceHead(r,
                                                                acc,
                                                                reduce(headPath / MINUS_ONE,
                                                                       headSeq,
                                                                       p,
                                                                       m,
                                                                       r,
                                                                       Option.empty
                                                                       )
                                                                )
                                        )
        case value: JsPrimitive => if (p(headPath,
                                         value
                                         )) reduce(headPath,
                                                   input.tail,
                                                   p,
                                                   m,
                                                   r,
                                                   AbstractJson.reduceHead(r,
                                                                              acc,
                                                                              m(headPath,
                                                                                value
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
                               r: (V, V) => V,
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
                                      AbstractJson.reduceHead(r,
                                                              acc,
                                                              AbstractJsObj.reduce(
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
                                        AbstractJson.reduceHead(r,
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
                                               AbstractJson.reduceHead(r,
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
                                              result.appended(JsObj(AbstractJsObj.filterJsObj(headPath,
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
                                             result.appended(JsArray(filterJsObj(headPath / MINUS_ONE,
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

  private[value] def filterJsObj(input : immutable.Seq[JsValue],
                                 result: immutable.Seq[JsValue],
                                 p     : JsObj => Boolean
                                ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case o: JsObj => if (p(o)) filterJsObj(input.tail,
                                               result.appended(JsObj(AbstractJsObj.filterJsObj(o.map,
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
                            p     : (JsPath, JsPrimitive) => Boolean
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
                                      result.appended(JsObj(AbstractJsObj.filter(headPath,
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
                                        result.appended(JsArray(filter(headPath / MINUS_ONE,
                                                                       headSeq,
                                                                       Vector.empty,
                                                                       p
                                                                       )
                                                                )
                                                        ),
                                        p
                                        )
        case head: JsPrimitive => if (p(headPath,
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
                            p     : JsPrimitive => Boolean
                           ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => filter(
          input.tail,
          result.appended(JsObj(AbstractJsObj.filter(headMap,
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
        case head: JsPrimitive => if (p(head
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
                         m     : (JsPath, JsPrimitive) => JsValue,
                         p     : (JsPath, JsPrimitive) => Boolean
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
                                   result.appended(JsObj(AbstractJsObj.map(headPath,
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
                                     result.appended(JsArray(map(headPath / MINUS_ONE,
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
        case head: JsPrimitive => if (p(headPath,
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


  private[value] def map(input : immutable.Seq[JsValue],
                         result: immutable.Seq[JsValue],
                         m     : JsPrimitive => JsValue
                        ): immutable.Seq[JsValue] =
  {

    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case JsObj(headMap) => map(input.tail,
                                   result.appended(JsObj(AbstractJsObj.map(headMap,
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
        case head: JsPrimitive => map(input.tail,
                                      result.appended(m(
                                        head
                                        )
                                                      ),
                                      m
                                      )
      }
    }
  }


  private[value] def mapKey(path: JsPath,
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
                                      result.appended(JsObj(AbstractJsObj.mapKey(headPath,
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
                                        result.appended(JsArray(mapKey(headPath / MINUS_ONE,
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
                                      result.appended(JsObj(AbstractJsObj.mapKey(headMap,
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
                                         result.appended(JsObj(AbstractJsObj.filterKey(headPath,
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
                                           result.appended(JsArray(filterKey(headPath / MINUS_ONE,
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
}