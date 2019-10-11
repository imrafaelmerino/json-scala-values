package json


abstract private[json] class AbstractJsArray[T <: collection.Seq[JsElem]](protected val seq: T)
{


  def isObj: Boolean = false

  def isArr: Boolean = true

  def isEmpty: Boolean = seq.isEmpty

  def length(): Int = seq.length

  def apply(i: Int): JsElem = apply(Index(i))

  def apply(pos: Position): JsElem = pos match
  {

    case Index(i) => seq.applyOrElse(i,
                                     (_: Int) => json.JsNothing
                                     )
    case Key(_) => json.JsNothing
  }

  def head: JsElem = seq.head

  def size: Int = seq.size

  override def toString: String = seq.toString()


}

