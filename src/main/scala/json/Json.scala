package json

trait Json[T <: Json[T]] extends JsElem
{

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

//  @`inline` final def ++(xs: IterableOnce[JsPair]): T = concat(xs)

//  def concat(xs: IterableOnce[JsPair]): T

//  @`inline` final def ++(other: T): T = concat(other)

//  def concat(other: T): T

  def get(path: JsPath): Option[JsElem] =
  {
    val elem = apply(path)
    if (elem.isNothing) Option.empty
    else Some(elem)
  }

  def apply(pos: Position): JsElem

  final def apply(path: JsPath): JsElem =
  {
    if (path.isEmpty) return this
    val e = this (path.head)
    val tail = path.tail
    if (tail.isEmpty) return e
    if (!e.isJson) return JsNothing
    JsElem.asJson(e).apply(tail)
  }

  def contains(path: JsPath): Boolean = !apply(path).isNothing

  def count(p: (JsPair => Boolean)): Int = toLazyList.count(p)

  def countRec(p: (JsPair => Boolean)): Int = toLazyListRec.count(p)

//  def drop(n: Int): T

  def empty: T

  def exists(p: (JsPair => Boolean)): Boolean = toLazyListRec.exists(p)

  def isEmpty: Boolean

  final def nonEmpty: Boolean = !isEmpty

  def toLazyListRec: LazyList[JsPair]

  def toLazyList: LazyList[JsPair]

  def init: T

  def tail: T

//  def partition(p: JsPair => Boolean): (T, T)

  final def mkString: String = toLazyList.mkString

  final def mkString(sep: String): String = toLazyList.mkString(sep)

  final def mkString(start: String,
                     sep  : String,
                     end  : String
                    ): String = toLazyList.mkString(start,
                                                    sep,
                                                    end
                                                    )

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

  def size: Int

}

object Json
{


}







