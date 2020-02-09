package value.future

import value.{JsArray,JsValue}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

object JsArrayFuture
{

  val empty: Future[JsArray] = Future.successful(JsArray.empty)


  def apply(seq: Future[JsValue]*)
           (implicit executor: ExecutionContext): Future[JsArray] =
  {
    @scala.annotation.tailrec
    def apply0(result: Future[JsArray],
               seq   : Seq[Future[JsValue]]
              ): Future[JsArray] =
    {
      if (seq.isEmpty) result
      else apply0(result.flatMap(arr => seq.head.map(result => arr.appended(result
                                                                            )
                                                     )
                                 ),
                  seq.tail
                  )
    }

    apply0(empty,
           seq
           )
  }


}
