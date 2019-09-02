package jsonvalues

case class JsObj(map: MyMap) extends Json[JsObj]
{
  override def isObj: Boolean = true


  override def isArr: Boolean = false

  override def put(path: JsPath,
                   elem: JsElem
                  ): JsObj = null

  override def remove(path: JsPath): JsObj = null

  override def isEmpty: Boolean = map.isEmpty

  override def stream_(): LazyList[JsPair] =
  {
    JsObj.stream_(JsPath.empty,
                  map
                  )
  }

  override def stream(): LazyList[JsPair] =
  {
    def stream(obj: MyMap
              ): LazyList[JsPair] =
    {
      if (obj.isEmpty) return LazyList.empty
      val head = map.head
      (JsPath.fromKey(head._1), head._2) +: stream(obj.tail)
    }

    stream(map)
  }

  override def get(pos: Position): JsElem =
  {
    pos match
    {
      case Key(name) => map.applyOrElse(name,
                                        (_            : String) => JsNothing
                                        )
      case Index(_) => JsNothing
    }
  }

  override def toString: String = map.toString()
}

object JsObj
{

  def empty = JsObj(new scala.collection.immutable.HashMap[String, JsElem]())

  def _empty_ = JsObj(new scala.collection.mutable.HashMap[String, JsElem]())

  def parse(str         : String): JsObj =
  {
    null
  }

  def stream_(path: JsPath,
              value: MyMap
             ): LazyList[JsPair] =
  {
    if (value.isEmpty) return LazyList.empty
    val head = value.head
    head._2 match
    {
      case JsObj(obj) => stream_(head._1 +: path,
                                 obj
                                 ) ++: stream_(path,
                                               value.tail
                                               )
      case JsArray(arr) => JsArray.stream_(-1 +: head._1 +: path,
                                           arr
                                           ) ++: stream_(path,
                                                         value.tail
                                                         )
      case _ => (head._1 +: path, head._2) +: stream_(path,
                                                      value.tail
                                                      )
    }

  }

  def _parse_(str         : String): JsObj =
  {
    null
  }

}
