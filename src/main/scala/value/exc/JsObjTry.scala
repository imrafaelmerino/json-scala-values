package value.exc

import value.{JsObj, JsPath, JsValue}
import scala.util.{Success, Try}

object JsObjTry
{
  val empty: Try[JsObj] = Success(JsObj.empty)

  def apply(pairs: (String, Try[JsValue])*): Try[JsObj] =
  {
    @scala.annotation.tailrec
    def apply0(result: Try[JsObj],
               seq: collection.Seq[(String, Try[JsValue])]
              ): Try[JsObj] =
    {
      if (seq.isEmpty) result
      else
      {
        val head = seq.head
        apply0(result.flatMap(obj => head._2.map(value => obj.insert(JsPath.empty.append(head._1),
                                                                     value
                                                                     )
                                                 )
                              ),
               seq.tail
               )
      }
    }

    apply0(empty,
           pairs.toSeq
           )
  }
}
