package json

abstract private[json] class AbstractJsObj[T <: collection.Map[String, JsElem]](protected val map: T)
{

  def isObj: Boolean = true

  def isArr: Boolean = false

  def isEmpty: Boolean = map.isEmpty

  def head: (String, JsElem) = map.head

  def headOption(): Option[(String, JsElem)] = map.headOption

  def last: (String, JsElem) = map.last

  def lastOption: Option[(String, JsElem)] = map.lastOption

  def keys: Iterable[String] = map.keys

  def keySet: collection.Set[String]

  def apply(key: String): JsElem = apply(Key(key))

  def apply(pos: Position): JsElem =
  {
    pos match
    {
      case Key(name) => map.applyOrElse(name,
                                        (_: String) => JsNothing
                                        )
      case Index(_) => JsNothing
    }
  }

  def size: Int = map.size

  override def toString: String = map.toString()


}

