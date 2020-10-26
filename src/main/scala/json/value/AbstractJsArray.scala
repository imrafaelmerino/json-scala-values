package json.value

import java.util.Objects.requireNonNull

import json.value.Functions._
import json.value.JsPath.MINUS_ONE

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
    JsArray(AbstractJsArrayFns.filter(MINUS_ONE,
                                      seq,
                                      Vector.empty,
                                      requireNonNull(p)
                                      )
            )

  def filterAllJsObj(p: (JsPath, JsObj) => Boolean): JsArray =
    JsArray(AbstractJsArrayFns.filterJsObj(MINUS_ONE,
                                           seq,
                                           Vector.empty,
                                           requireNonNull(p)
                                           )
            )

  def filterAllKeys(p: (JsPath, JsValue) => Boolean): JsArray =
    JsArray(AbstractJsArrayFns.filterKey(MINUS_ONE,
                                         seq,
                                         immutable.Vector.empty,
                                         requireNonNull(p)
                                         )
            )

  def flatMap(f: JsValue => JsArray): JsArray = JsArray(seq.flatMap(f))

  def iterator: Iterator[JsValue] = seq.iterator

  def mapAll(m: (JsPath, JsPrimitive) => JsValue,
             p: (JsPath, JsPrimitive) => Boolean = (_, _) => true
            ): JsArray = JsArray(AbstractJsArrayFns.map(MINUS_ONE,
                                                        seq,
                                                        Vector.empty,
                                                        requireNonNull(m),
                                                        requireNonNull(p)
                                                        )
                                 )

  def reduceAll[V](p: (JsPath, JsPrimitive) => Boolean = (_, _) => true,
                   m: (JsPath, JsPrimitive) => V,
                   r: (V, V) => V
                  ): Option[V] = AbstractJsArrayFns.reduce(JsPath.empty / MINUS_ONE,
                                                           seq,
                                                           Option.empty,
                                                           p,
                                                           m,
                                                           r
                                                           )

  def mapAllKeys(m: (JsPath, JsValue) => String,
                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                ): JsArray = JsArray(AbstractJsArrayFns.mapKey(MINUS_ONE,
                                                               seq,
                                                               Vector.empty,
                                                               requireNonNull(m),
                                                               requireNonNull(p)
                                                               )
                                     )

  def filterAll(p: JsPrimitive => Boolean): JsArray = JsArray(AbstractJsArrayFns.filter(seq,
                                                                                        Vector.empty,
                                                                                        requireNonNull(p)
                                                                                        )
                                                              )


  def filter(p: JsValue => Boolean): JsArray = JsArray(seq.filter(p))

  def mapAll(m: JsPrimitive => JsValue): JsArray =
    JsArray(AbstractJsArrayFns.map(seq,
                                   Vector.empty,
                                   requireNonNull(m)
                                   )
            )

  def map(m: JsValue => JsValue): JsArray = JsArray(seq.map(m))

  def mapAllKeys(m: String => String): JsArray =
    JsArray(AbstractJsArrayFns.mapKey(seq,
                                      Vector.empty,
                                      requireNonNull(m)
                                      )
            )


  def filterAllJsObj(p: JsObj => Boolean): JsArray =
    JsArray(AbstractJsArrayFns.filterJsObj(seq,
                                           Vector.empty,
                                           requireNonNull(p)
                                           )
            )

  def filterAllKeys(p: String => Boolean): JsArray =
    JsArray(AbstractJsArrayFns.filterKey(seq,
                                         immutable.Vector.empty,
                                         requireNonNull(p)
                                         )
            )

  /**
   *
   * @return a lazy list of pairs of path and json.value
   */
  def flatten: LazyList[(JsPath, JsValue)] = AbstractJsArrayFns.flatten(MINUS_ONE,
                                                                        seq
                                                                        )

  private[value] def apply(pos: Position): JsValue =
  {
    requireNonNull(pos) match
    {
      case Index(i) => apply(i)
      case Key(_) => json.value.JsNothing
    }
  }

  def apply(i: Int): JsValue =
  {
    if i == -1
    then seq.lastOption.getOrElse(JsNothing)
    else seq.applyOrElse(i,
                         (_: Int) => JsNothing
                         )
  }

  @scala.annotation.tailrec
  final private[value] def fillWith[E <: JsValue, P <: JsValue](seq: immutable.Seq[JsValue],
                                                                i  : Int,
                                                                e  : E,
                                                                p  : P
                                                               ): immutable.Seq[JsValue] =
  {
    val length: Int = seq.length
    if i < length && i > -1
    then seq.updated(i,
                     e
                     )
    else if i == -1 then
      if seq.isEmpty
      then seq.appended(e)
      else seq.updated(seq.length - 1,
                       e
                       )
    else if i == length
    then seq.appended(e)
    else fillWith(seq.appended(p),
                  i,
                  e,
                  p
                  )
  }
}

