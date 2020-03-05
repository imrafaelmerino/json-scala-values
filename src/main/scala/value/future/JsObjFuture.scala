package value.future
import value.{JsObj, JsPath, JsValue,JsStr,JsInt,JsLong,JsDouble,JsBigDec,JsBigInt,JsBool,JsArray,JsNull}
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

object JsObjFuture
  val empty: Future[JsObj] = Future.successful(JsObj.empty)

  def apply(pairs: (JsPath, Future[JsValue])*)
           (given executor: ExecutionContext): Future[JsObj] =
    @scala.annotation.tailrec
    def apply0(result: Future[JsObj],
               seq: immutable.Seq[(JsPath, Future[JsValue])]
              ): Future[JsObj] =
      if seq.isEmpty
      then result
      else
        val head = seq.head
        apply0(result.flatMap(obj => head._2.map(value => obj.inserted(head._1,
                                                                       value
                                                                       )
                                                 )
                              ),
               seq.tail
               )
    apply0(empty,
           pairs
           )


