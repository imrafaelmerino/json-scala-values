package jsonvalues

import com.fasterxml.jackson.core.JsonFactory

trait Json[T <: Json[T]] extends JsValue
{

  @`inline` final def +!(pair: (JsPath, JsValue)): T = inserted(pair)

  @`inline` final def -(path: JsPath): T = removed(path)

  def removed(path: JsPath): T

  @`inline` final def +(pair: (JsPath, JsValue)): T = updated(pair)

  def updated(pair: (JsPath, JsValue)): T

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
    if (path.isEmpty)  this
    else
    {
      if (path.tail.isEmpty) this (path.head)
      else if (!this (path.head).isJson) JsNothing
      else JsValue.asJson(this (path.head)).apply(path.tail)
    }
  }

  def contains(path: JsPath): Boolean = !apply(path).isNothing

  def count(p: ((JsPath, JsValue)) => Boolean): Int = toLazyList.count(p)

  def countRec(p: ((JsPath, JsValue)) => Boolean): Int = toLazyListRec.count(p)

  def empty: T

  def exists(p: ((JsPath, JsValue)) => Boolean): Boolean = toLazyListRec.exists(p)

  def isEmpty: Boolean

  final def nonEmpty: Boolean = !isEmpty

  def toLazyListRec: LazyList[(JsPath, JsValue)]

  def toLazyList: LazyList[(JsPath, JsValue)]

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


  //  def partition(p: (JsPath, JsValue) => Boolean): (T, T)

  //  def drop(n: Int): T
  //  def find(p: (JsPath, JsValue) => Boolean): Option[(JsPath, JsValue)] = toLazyListRec.find(p)
  //
  //  def filter(p: (JsPath, JsValue) => Boolean): T
  //
  //  def filterNot(p: (JsPath, JsValue) => Boolean): T
  //
  //  def forall(p: (JsPath, JsValue) => Boolean): Boolean = toLazyListRec.forall(p)
  //
  //  @`inline` final def :+(pair: (JsPath, JsValue)): T = appended(pair)
  //
  //  def appended(pair: (JsPath, JsValue)): T
  //
  //  @`inline` final def +:(pair: (JsPath, JsValue)): T = prepended(pair)
  //
  //  def prepended(pair: (JsPath, JsValue)): T
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


  def inserted(pair: (JsPath, JsValue)): T
}

object Json
{
  private[jsonvalues] val JACKSON_FACTORY = new JsonFactory

}
