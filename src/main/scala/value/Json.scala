package value

import java.io.{ByteArrayOutputStream, OutputStream}
import java.util.Objects
import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.JsonFactory

trait Json[T <: Json[T]] extends JsValue
{


  def toPrettyString: String =
  {
    val baos = new ByteArrayOutputStream
    dslJson.serialize(this,
                      new MyPrettifyOutputStream(baos)
                      )
    baos.toString("UTF-8")
  }

  override def toString: String =
  {
    val baos = new ByteArrayOutputStream
    dslJson.serialize(this,
                      baos
                      )
    baos.toString("UTF-8")

  }

  def serialize(outputStream: OutputStream): () => Unit =
  {
    () =>
      dslJson.serialize(this,
                        requireNonNull(outputStream)
                        )
  }

  def serialize: Array[Byte] =
  {
    val outputStream = new ByteArrayOutputStream()
    dslJson.serialize(this,
                      outputStream
                      )
    outputStream.flush()
    outputStream.toByteArray
  }


  @`inline` final def +!(path: JsPath,
                         value  : JsValue,
                         padWith: JsValue = JsNull
                        ): T = inserted(requireNonNull(path),
                                        requireNonNull(value),
                                        requireNonNull(padWith)
                                        )

  @`inline` final def -(path: JsPath): T = removed(requireNonNull(path))

  def removed(path: JsPath): T

  @`inline` final def +(path : JsPath,
                        value: JsValue,
                       ): T = updated(requireNonNull(path),
                                      requireNonNull(value)
                                      )

  def updated(path: JsPath,
              value: JsValue,
             ): T

  @`inline` final def --(xs: IterableOnce[JsPath]): T = removedAll(requireNonNull(xs))

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

  def int(path: JsPath): Option[Int] = get(requireNonNull(path)).filter(_.isInt).map(_.asJsInt.value)

  def long(path: JsPath): Option[Long] = get(requireNonNull(path)).filter((v: JsValue) => v.isLong || v.isInt).map(_.asJsLong.value)

  def bigInt(path: JsPath): Option[BigInt] = get(requireNonNull(path)).filter((v: JsValue) => v.isIntegral).map(_.asJsBigInt.value)

  def double(path: JsPath): Option[Double] = get(requireNonNull(path)).filter((v: JsValue) => v.isDouble).map(_.asJsDouble.value)

  def bigDecimal(path: JsPath): Option[BigDecimal] = get(requireNonNull(path)).filter((v: JsValue) => v.isDecimal).map(_.asJsBigDec.value)

  def string(path: JsPath): Option[String] = get(requireNonNull(path)).filter(_.isStr).map(_.asJsStr.value)

  def bool(path: JsPath): Option[Boolean] = get(requireNonNull(path)).filter(_.isBool).map(_.asJsBool.value)

  def obj(path: JsPath): Option[JsObj] = get(requireNonNull(path)).filter(_.isObj).map(_.asJsObj)

  def array(path: JsPath): Option[JsArray] = get(requireNonNull(path)).filter(_.isArr).map(_.asJsArray)

  def get(path: JsPath): Option[JsValue] = apply(requireNonNull(path)) match
  {
    case JsNothing => Option.empty
    case value: JsValue => Some(value)
  }


  private[value] def apply(pos: Position): JsValue

  private[value] def get(pos: Position): Option[JsValue] = apply(pos) match
  {
    case JsNothing => Option.empty
    case value: JsValue => Some(value)
  }

  final def apply(path: JsPath): JsValue =
  {
    if (requireNonNull(path).isEmpty) this
    else
    {
      if (path.tail.isEmpty) this (path.head)
      else if (!this (path.head).isJson) JsNothing
      else this (path.head).asJson.apply(path.tail)
    }
  }

  def containsPath(path: JsPath): Boolean = !apply(requireNonNull(path)).isNothing

  def count(p: ((JsPath, JsValue)) => Boolean = (_: (JsPath, JsValue)) => true): Int = flatten.count(requireNonNull(p))

  def countRec(p: ((JsPath, JsValue)) => Boolean = (_: (JsPath, JsValue)) => true): Int = flattenRec.count(requireNonNull(p))

  def exists(p: ((JsPath, JsValue)) => Boolean): Boolean = flattenRec.exists(requireNonNull(p))

  def isEmpty: Boolean

  final def nonEmpty: Boolean = !isEmpty

  def flattenRec: LazyList[(JsPath, JsValue)]

  def flatten: LazyList[(JsPath, JsValue)]

  def init: T

  def tail: T

  def size: Int

  def filterRec(p: (JsPath, JsValue) => Boolean): T

  def filter(p: (JsPath, JsValue) => Boolean): T

  def mapRec[J <: JsValue](m: (JsPath, JsValue) => J,
                           p: (JsPath, JsValue) => Boolean
                          ): T

  def map[J <: JsValue](m: (JsPath, JsValue) => J,
                        p: (JsPath, JsValue) => Boolean
                       ): T

  def mapKeyRec(m: (JsPath, JsValue) => String,
                p: (JsPath, JsValue) => Boolean
               ): T

  def mapKey(m: (JsPath, JsValue) => String,
             p: (JsPath, JsValue) => Boolean
            ): T

  def reduceRec[V](p: (JsPath, JsValue) => Boolean,
                   m: (JsPath, JsValue) => V,
                   r: (V, V) => V
                  ): Option[V]

  def reduce[V](p: (JsPath, JsValue) => Boolean,
                m: (JsPath, JsValue) => V,
                r: (V, V) => V
               ): Option[V]

  def filterJsObjRec(p: (JsPath, JsObj) => Boolean): T

  def filterJsObj(p: (JsPath, JsObj) => Boolean): T

  def filterKeyRec(p: (JsPath, JsValue) => Boolean): T

  def filterKey(p: (JsPath, JsValue) => Boolean): T

  def inserted(path: JsPath,
               value  : JsValue,
               padWith: JsValue = JsNull
              ): T
}

object Json
{


  private[value] def reduceHead[V](r: (V, V) => V,
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

  private[value] def reduceHead[V](r: (V, V) => V,
                                   acc: Option[V],
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
