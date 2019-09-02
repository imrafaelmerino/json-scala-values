package jsonvalues

case class JsArray(seq: MySeq) extends Json[JsArray]
{

  override def isObj: Boolean = false

  override def isArr: Boolean = true

  override def put(path: JsPath,
                   elem: JsElem
                  ): JsArray = null


  override def remove(path: JsPath): JsArray = null

  override def isEmpty: Boolean = seq.isEmpty

  override def stream_(): LazyList[JsPair] =
  {
    JsArray.stream_(JsPath.fromIndex(-1),
                    seq
                    )
  }

  override def stream(): LazyList[JsPair] =
  {


    def stream(i: Int,
               arr: MySeq
              ): LazyList[JsPair] =
    {
      if (arr.isEmpty) LazyList.empty

      else (JsPath.fromIndex(i), seq.head) +: stream(i + 1,
                                                     arr.tail
                                                     )
    }

    stream(0,
           seq
           )


  }

  override def get(pos: Position): JsElem = pos match
  {

    case Index(i) => seq.applyOrElse(i,
                                     (_: Int) => JsNothing
                                     )
    case Key(_) => JsNothing
  }

  override def toString: String = seq.toString()
}

object JsArray
{
  def empty = JsArray(scala.collection.immutable.Vector.empty)

  def _empty_ = JsArray(scala.collection.mutable.ArrayBuffer())

  def parse(str         : String): JsObj =
  {
    null
  }

  def stream_(path: JsPath,
              value: MySeq
             ): LazyList[JsPair] =
  {
    if (value.isEmpty) return LazyList.empty
    val head = value.head
    val headPath = path.inc()
    head match
    {
      case JsObj(obj) => JsObj.stream_(headPath,
                                       obj
                                       ) ++: stream_(headPath,
                                                     value.tail
                                                     )
      case JsArray(arr) => stream_(-1 +: headPath,
                                   arr
                                   ) ++: stream_(headPath,
                                                 value.tail
                                                 )
      case _ => (headPath, head) +: stream_(headPath,
                                            value.tail
                                            )
    }

  }


  def _parse_(str         : String): JsObj =
  {
    null
  }

}