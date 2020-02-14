package value

import java.util.Objects.requireNonNull
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

  def toJsObj: JsObj = throw UserError.toJsObjOfJsArray

  def isObj: Boolean = false

  def isArr: Boolean = true

  def isEmpty: Boolean = seq.isEmpty

  def length(): Int = seq.length

  def head: JsValue = seq.head

  def last: JsValue = seq.last

  def size: Int = seq.size

  def prependedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.prependedAll(requireNonNull(xs).iterator.filterNot(e => e == JsNothing)))

  def appendedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.appendedAll(requireNonNull(xs).iterator.filterNot(e => e == JsNothing)))

  def init: JsArray = JsArray(seq.init)

  def tail: JsArray = JsArray(seq.tail)

  def filterAll(p: (JsPath, JsPrimitive) => Boolean): JsArray =
    JsArray(AbstractJsArray.filter(MINUS_ONE,
                                   seq,
                                   Vector.empty,
                                   requireNonNull(p)
                                   )
            )

  def filterAllJsObj(p: (JsPath, JsObj) => Boolean): JsArray =
    JsArray(AbstractJsArray.filterJsObj(MINUS_ONE,
                                        seq,
                                        Vector.empty,
                                        requireNonNull(p)
                                        )
            )

  def filterAllKeys(p: (JsPath, JsValue) => Boolean): JsArray =
    JsArray(AbstractJsArray.filterKey(MINUS_ONE,
                                      seq,
                                      immutable.Vector.empty,
                                      requireNonNull(p)
                                      )
            )

  def flatMap(f: JsValue => JsArray): JsArray = JsArray(seq.flatMap(f))

  def iterator: Iterator[JsValue] = seq.iterator

  def mapAll(m: (JsPath, JsPrimitive) => JsValue,
             p: (JsPath, JsPrimitive) => Boolean = (_, _) => true
            ): JsArray = JsArray(AbstractJsArray.map(MINUS_ONE,
                                                     seq,
                                                     Vector.empty,
                                                     requireNonNull(m),
                                                     requireNonNull(p)
                                                     )
                                 )

  def reduceAll[V](p: (JsPath, JsPrimitive) => Boolean = (_, _) => true,
                m: (JsPath, JsPrimitive) => V,
                r: (V, V) => V
               ): Option[V] = AbstractJsArray.reduce(JsPath.empty / MINUS_ONE,
                                                     seq,
                                                     requireNonNull(p),
                                                     requireNonNull(m),
                                                     requireNonNull(r),
                                                     Option.empty
                                                     )

  def mapAllKeys(m: (JsPath, JsValue) => String,
                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                ): JsArray = JsArray(AbstractJsArray.mapKey(MINUS_ONE,
                                                            seq,
                                                            Vector.empty,
                                                            requireNonNull(m),
                                                            requireNonNull(p)
                                                            )
                                     )

  def filterAll(p: JsPrimitive => Boolean): JsArray = JsArray(AbstractJsArray.filter(seq,
                                                                                     Vector.empty,
                                                                                     requireNonNull(p)
                                                                                     )
                                                              )


  def filter(p: JsValue => Boolean): JsArray = ???
  def mapAll(m: JsPrimitive => JsValue): JsArray =
    JsArray(AbstractJsArray.map(seq,
                                Vector.empty,
                                requireNonNull(m)
                                )
            )

   def map(m: JsValue => JsValue): JsArray = ???

  def mapAllKeys(m: String => String): JsArray =
    JsArray(AbstractJsArray.mapKey(seq,
                                   Vector.empty,
                                   requireNonNull(m)
                                   )
            )


  def filterAllJsObj(p: JsObj => Boolean): JsArray =
    JsArray(AbstractJsArray.filterJsObj(seq,
                                        Vector.empty,
                                        requireNonNull(p)
                                        )
            )

  def filterAllKeys(p: String => Boolean): JsArray =
    JsArray(AbstractJsArray.filterKey(seq,
                                      immutable.Vector.empty,
                                      requireNonNull(p)
                                      )
            )

  /**
   *
   * @return a lazy list of pairs of path and value
   */
  def flatten: LazyList[(JsPath, JsValue)] = AbstractJsArray.flatten(MINUS_ONE,
                                                                     seq
                                                                     )

  private[value] def apply(pos: Position): JsValue = requireNonNull(pos) match
  {
    case Index(i) => apply(i)
    case Key(_) => value.JsNothing
  }

  def apply(i: Int): JsValue =
  {
    if (i == -1) seq.lastOption.getOrElse(JsNothing)
    else seq.applyOrElse(i,
                         (_: Int) => JsNothing
                         )
  }

  @scala.annotation.tailrec
  final private[value] def fillWith[E <: JsValue, P <: JsValue](seq: immutable.Seq[JsValue],
                                                                i: Int,
                                                                e: E,
                                                                p: P
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
}


private[value] object AbstractJsArray
{

  @scala.annotation.tailrec
  def concatSets(a: JsArray,
                 b: JsArray
                ): JsArray =
  {
    if (b.isEmpty) a
    else
    {
      val head = b.head
      if (a.seq.contains(head)) concatSets(a,
                                           b.tail
                                           )
      else concatSets(a.appended(head),
                      b.tail
                      )
    }
  }

  def concatLists(a: JsArray,
                  b: JsArray
                 ): JsArray =
  {
    val asize = a.size
    val bsize = b.size
    if (asize == bsize || asize > bsize) a
    else JsArray(a.seq.appendedAll(b.seq.dropRight(asize)))
  }

  def concatMultisets(a: JsArray,
                      b: JsArray
                     ): JsArray =
  {
    JsArray(a.seq.appendedAll(b.seq))
  }

  private[value] def flatten(path: JsPath,
                             seq: immutable.Seq[JsValue]
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
                               p: String => Boolean
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
    else if (i == -1) seq.init
    else if (i == 0) seq.tail
    else
    {
      val (prefix, suffix): (immutable.Seq[JsValue], immutable.Seq[JsValue]) = seq.splitAt(i)
      prefix.appendedAll(suffix.tail)
    }
  }

  private[value] def reduce[V](path: JsPath,
                               input: immutable.Seq[JsValue],
                               p: (JsPath, JsPrimitive) => Boolean,
                               m: (JsPath, JsPrimitive) => V,
                               r: (V, V) => V,
                               acc: Option[V]
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
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.reduce"
                                                                   )

      }
    }

  }

  private[value] def filterJsObj(path: JsPath,
                                 input: immutable.Seq[JsValue],
                                 result: immutable.Seq[JsValue],
                                 p: (JsPath, JsObj) => Boolean
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
                                                                                              o.bindings,
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
                                               result.appended(JsObj(AbstractJsObj.filterJsObj(o.bindings,
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

  private[value] def filter(path: JsPath,
                            input: immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            p: (JsPath, JsPrimitive) => Boolean
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
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.filter"
                                                                   )

      }
    }
  }

  private[value] def filter(input: immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            p: JsPrimitive => Boolean
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
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.filter"
                                                                   )

      }
    }
  }

  private[value] def map(path: JsPath,
                         input: immutable.Seq[JsValue],
                         result: immutable.Seq[JsValue],
                         m: (JsPath, JsPrimitive) => JsValue,
                         p: (JsPath, JsPrimitive) => Boolean
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
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.map"
                                                                   )

      }
    }
  }

  private[value] def map(input: immutable.Seq[JsValue],
                         result: immutable.Seq[JsValue],
                         m: JsPrimitive => JsValue
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
                                      result.appended(m(head)),
                                      m
                                      )
        case JsNothing => throw InternalError.typeNotExpectedInMatcher(JsNothing,
                                                                       "AbstractJsArray.map"
                                                                       )

      }
    }
  }

  private[value] def mapKey(path: JsPath,
                            input: immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            m: (JsPath, JsValue) => String,
                            p: (JsPath, JsValue) => Boolean
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

  private[value] def mapKey(input: immutable.Seq[JsValue],
                            result: immutable.Seq[JsValue],
                            m: String => String
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

  private[value] def filterKey(path: JsPath,
                               input: immutable.Seq[JsValue],
                               result: immutable.Seq[JsValue],
                               p: (JsPath, JsValue) => Boolean
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