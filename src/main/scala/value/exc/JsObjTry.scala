package value.exc

import value.{JsObj, JsPath, JsValue}

import scala.collection.immutable
import scala.util.{Success, Try}

object JsObjTry
{
  val empty: Try[JsObj] = Success(JsObj.empty)

  def apply(pairs: (String, Try[JsValue])*): Try[JsObj] =
  {
    @scala.annotation.tailrec
    def apply0(result: Try[JsObj],
               seq: immutable.Seq[(String, Try[JsValue])]
              ): Try[JsObj] =
    {
      if (seq.isEmpty) result
      else
      {
        val head = seq.head
        apply0(result.flatMap(obj => head._2.map(value => obj.inserted(JsPath.empty.appended(head._1),
                                                                       value
                                                                       )
                                                 )
                              ),
               seq.tail
               )
      }
    }

    apply0(empty,
           pairs
           )
  }
}
