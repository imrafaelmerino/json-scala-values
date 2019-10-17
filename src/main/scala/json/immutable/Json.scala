package json.immutable

import com.fasterxml.jackson.core.JsonFactory
import json.{JsNothing, JsPair, JsPath, JsValue, Position}

import scala.collection.immutable.HashMap

trait Json[T <: Json[T]] extends JsValue
{

  @`inline` final def +!(pair: JsPair): T = inserted(pair)

  @`inline` final def -(path: JsPath): T = removed(path)

  def removed(path: JsPath): T

  @`inline` final def +(pair: JsPair): T = updated(pair)

  def updated(pair: JsPair): T

  @`inline` final def --(xs: IterableOnce[JsPath]): T = removedAll(xs)

  def removedAll(xs: IterableOnce[JsPath]): T

  def isStr: Boolean = false

  def isBool: Boolean = false

  def isNumber: Boolean = false

  def isInt: Boolean = false

  def isLong: Boolean = false

  def isDouble: Boolean = false

  def isBigInt: Boolean = false

  def isBigDec: Boolean = false

  def isNull: Boolean = false

  def isNothing: Boolean = false

  def get(path: JsPath): Option[JsValue] =
  {
    val elem = apply(path)
    if (elem.isNothing) Option.empty
    else Some(elem)
  }

  def apply(pos: Position): JsValue

  final def apply(path: JsPath): JsValue =
  {
    if (path.isEmpty) return this
    val e = this (path.head)
    val tail = path.tail
    if (tail.isEmpty) return e
    if (!e.isJson) return JsNothing
    JsValue.asJson(e).apply(tail)
  }

  def contains(path: JsPath): Boolean = !apply(path).isNothing

  def count(p: JsPair => Boolean): Int = toLazyList.count(p)

  def countRec(p: JsPair => Boolean): Int = toLazyListRec.count(p)

  def empty: T

  def exists(p: JsPair => Boolean): Boolean = toLazyListRec.exists(p)

  def isEmpty: Boolean

  final def nonEmpty: Boolean = !isEmpty

  def toLazyListRec: LazyList[JsPair]

  def toLazyList: LazyList[JsPair]

  def init: T

  def tail: T

  final def mkString: String = toLazyList.mkString

  final def mkString(sep: String): String = toLazyList.mkString(sep)

  final def mkString(start: String,
                     sep  : String,
                     end  : String
                    ): String = toLazyList.mkString(start,
                                                    sep,
                                                    end
                                                    )

  def size: Int


  //  def partition(p: JsPair => Boolean): (T, T)

  //  def drop(n: Int): T
  //  def find(p: JsPair => Boolean): Option[JsPair] = toLazyListRec.find(p)
  //
  //  def filter(p: JsPair => Boolean): T
  //
  //  def filterNot(p: JsPair => Boolean): T
  //
  //  def forall(p: JsPair => Boolean): Boolean = toLazyListRec.forall(p)
  //
  //  @`inline` final def :+(pair: JsPair): T = appended(pair)
  //
  //  def appended(pair: JsPair): T
  //
  //  @`inline` final def +:(pair: JsPair): T = prepended(pair)
  //
  //  def prepended(pair: JsPair): T
  //
  //  @`inline` final def ++:(path: JsPath,
  //                          xs  : IterableOnce[JsElem]
  //                         ): T = prependedAll(path,
  //                                             xs
  //                                             )
  //
  //  def prependedAll(path: JsPath,
  //                   xs  : IterableOnce[JsElem]
  //                  ): T
  //
  //  @`inline` final def :++(path: JsPath,
  //                          xs  : IterableOnce[JsElem]
  //                         ): T = appendedAll(path,
  //                                            xs
  //                                            )
  //
  //  def appendedAll(path: JsPath,
  //                  ele : IterableOnce[JsElem]
  //                 ): T


  def inserted(pair: JsPair): T
}

object Json
{
  private[immutable] val JACKSON_FACTORY = new JsonFactory

}
