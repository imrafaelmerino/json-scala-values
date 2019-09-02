package jsonvalues


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

  def put(path: JsPath,
          elem: JsElem
         ): T

  def get(pos: Position): JsElem

  def get(path: JsPath): JsElem =
  {
    if (path.isEmpty) return this
    val e = get(path.head)
    val tail = path.tail
    if (tail.isEmpty) return e
    if (!e.isJson) return JsNothing
    JsElem.asJson(e).get(tail)
  }

  def isEmpty: Boolean

  def remove(path: JsPath): T

  def stream_(): LazyList[JsPair]

  def stream(): LazyList[JsPair]
}

object Json
{

  def parse(str: String): Json[_] =
  {
    null
  }

  def _parse_(str: String): Json[_] =
  {
    null
  }

}