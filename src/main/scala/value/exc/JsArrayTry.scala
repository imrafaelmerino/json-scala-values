package value.exc

import value.{JsArray, JsValue}

import scala.util.{Success, Try}

object JsArrayTry
  val empty: Try[JsArray] = Success(JsArray.empty)

  def apply(seq: Try[JsValue]*): Try[JsArray] =
    @scala.annotation.tailrec
    def apply0(result: Try[JsArray], seq: Seq[Try[JsValue]]): Try[JsArray] =
      if seq.isEmpty
      then result
      else apply0(result.flatMap(arr => seq.head.map(result => arr.appended(result))),
                  seq.tail
                  )
    apply0(empty,
           seq
           )
