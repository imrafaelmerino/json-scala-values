package jsonvalues.gen

import jsonvalues.{JsPath, JsValue, Json}
import org.scalacheck.Gen

object JsGen
{


  @scala.annotation.tailrec
  def json[T <: Json[T]](acc: Gen[T],
                         pairs: (JsPath, Gen[JsValue])*
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
