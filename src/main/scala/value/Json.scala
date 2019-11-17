package value

import com.fasterxml.jackson.core.JsonFactory

trait Json[T <: Json[T]] extends JsValue
{

  @`inline` final def +!(path: JsPath,
                         value  : JsValue,
                         padWith: JsValue = JsNull
                        ): T = inserted(path,
                                        value,
                                        padWith
                                        )

  @`inline` final def -(path: JsPath): T = removed(path)

  def removed(path: JsPath): T

  @`inline` final def +(path : JsPath,
                        value: JsValue,
                       ): T = updated(path,
                                      value
                                      )

  def updated(path : JsPath,
              value: JsValue,
             ): T

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

  def isNotEmpty: Boolean = !isEmpty

  override def isNull: Boolean = false

  override def isNothing: Boolean = false

  override def asJsLong: JsLong = throw UserError.asJsLongOfJson

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJson

  override def asJsInt: JsInt = throw UserError.asJsIntOfJson

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJson

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJson

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJson

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJson

  override def asJsStr: JsStr = throw UserError.asJsStrOfJson

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJson


  def get(path: JsPath): Option[JsValue] =
  {
    val value = apply(path)
    if (value.isNothing) Option.empty
    else Some(value)
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

  def count(p: ((JsPath, JsValue)) => Boolean = (_: (JsPath, JsValue)) => true): Int = toLazyList.count(p)

  def countRec(p: ((JsPath, JsValue)) => Boolean = (_: (JsPath, JsValue)) => true): Int = toLazyListRec.count(p)

  def empty: T

  def exists(p: ((JsPath, JsValue)) => Boolean): Boolean = toLazyListRec.exists(p)

  def isEmpty: Boolean

  final def nonEmpty: Boolean = !isEmpty

  def toLazyListRec: LazyList[(JsPath, JsValue)]

  def toLazyList: LazyList[(JsPath, JsValue)]

  def init: T

  def tail: T

  def size: Int

  def filterRec(p: (JsPath, JsValue) => Boolean): T

  def mapRec[J <: JsValue](m: (JsPath, JsValue) => J,
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

  def inserted(path   : JsPath,
               value  : JsValue,
               padWith: JsValue = JsNull
              ): T
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
