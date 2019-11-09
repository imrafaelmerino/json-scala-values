package value

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

  override def isStr: Boolean = false

  override def isBool: Boolean = false

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = false

  override def isNothing: Boolean = false

  override def asJsLong: JsLong = throw new UnsupportedOperationException("asJsLong of Json")

  override def asJsInt: JsInt = throw new UnsupportedOperationException("asJsInt of Json")

  override def asJsBigInt: JsBigInt = throw new UnsupportedOperationException("asJsBigInt of Json")

  override def asJsBigDec: JsBigDec = throw new UnsupportedOperationException("asJsBigDec of Json")

  override def asJsBool: JsBool = throw new UnsupportedOperationException("asJsBool of Json")

  override def asJsNumber: JsNumber = throw new UnsupportedOperationException("asJsNumber of Json")


  def get(path: JsPath): Option[JsValue] =
  {
    val elem = apply(path)
    if (elem.isNothing) Option.empty
    else Some(elem)
  }

  def apply(pos: Position): JsValue


  final def apply(path: JsPath): JsValue =
  {
    if (path.isEmpty) this
    else
    {
      if (path.tail.isEmpty) this (path.head)
      else if (!this (path.head).isJson) JsNothing
      else this (path.head).asJson.apply(path.tail)
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

  def filterRec(p: (JsPath, JsValue) => Boolean): T

  def mapRec(m: (JsPath, JsValue) => JsValue,
             p: (JsPath, JsValue) => Boolean
            ): T

  def mapKeyRec(m: (JsPath, JsValue) => String,
                p: (JsPath, JsValue) => Boolean
               ): T

  def reduceRec[V](p: (JsPath, JsValue) => Boolean,
                   m: (JsPath, JsValue) => V,
                   r: (V, V) => V
                  ): Option[V]

  def filterJsObjRec(p: (JsPath, JsObj) => Boolean): T

  def filterKeyRec(p: (JsPath, JsValue) => Boolean): T

  //  def partition(p: (JsPath, JsValue) => Boolean): (T, T)

  //  def drop(n: Int): T
  //  def find(p: (JsPath, JsValue) => Boolean): Option[(JsPath, JsValue)] = toLazyListRec.find(p)
  //
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
  protected[value] val JACKSON_FACTORY = new JsonFactory

  def reduceHead[V](r   : (V, V) => V,
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

  def reduceHead[V](r         : (V, V) => V,
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
