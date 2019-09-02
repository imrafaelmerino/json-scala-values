import scala.collection.{AbstractMap, AbstractSeq}
import scala.collection.Map
import scala.collection.Seq
package object jsonvalues
{
  type MyMap = AbstractMap[String, JsElem]
  type MySeq = AbstractSeq[JsElem]
  type JsPair = (JsPath,JsElem)

  implicit def toAbstractMap(map: Map[String, JsElem]): MyMap = map.asInstanceOf[MyMap]
  implicit def toAbstractSeq(seq: Seq[JsElem]): MySeq = seq.asInstanceOf[MySeq]
}
