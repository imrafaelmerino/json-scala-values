package json.gen

import json.immutable.Json
import json.{JsElem, JsPath}
import org.scalacheck.Gen

object JsGen
{


  @scala.annotation.tailrec
  def json[T <: Json[T]](acc: Gen[T],
                         pairs: (JsPath, Gen[JsElem])*
                        ): Gen[T] =
  {
    if (pairs.isEmpty) acc
    else json(acc.flatMap(o => for
      {
      a <- pairs.head._2
    } yield o.inserted(pairs.head._1,
                       a
                       )
                          ),
              pairs.tail: _*
              )
  }


}
