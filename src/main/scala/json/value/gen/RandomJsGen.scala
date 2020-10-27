package json.value.gen

import org.scalacheck.Gen
import json.value.Preamble._
import json.value._
import json.value.gen.Preamble._

import scala.collection.immutable

private[gen] final case class RandomJsGen(objectPrimitiveGen: PrimitiveGen,
                                               arrayPrimitiveGen : PrimitiveGen,
                                               arrLengthGen      : Gen[Int],
                                               objSizeGen        : Gen[Int],
                                               keyGen            : Gen[String],
                                               arrayValueFreq    : ValueFreq,
                                               objectValueFreq   : ValueFreq
                                              )
{

  //noinspection ForwardReference
  private val pair: Gen[(String, JsValue)] =
  {
    for
      {
      key <- keyGen
      value <- Gen.frequency((objectValueFreq.str, objectPrimitiveGen.str),
                             (objectValueFreq.int, objectPrimitiveGen.int),
                             (objectValueFreq.long, objectPrimitiveGen.long),
                             (objectValueFreq.bigDec, objectPrimitiveGen.bigDec),
                             (objectValueFreq.bigInt, objectPrimitiveGen.bigInt),
                             (objectValueFreq.bool, objectPrimitiveGen.bool),
                             (objectValueFreq.double, objectPrimitiveGen.double),
                             (objectValueFreq.`null`, JsNull),
                             (objectValueFreq.obj, obj),
                             (objectValueFreq.arr, arr),
                             )
    } yield (key, value)
  }

  val obj: Gen[JsObj] =
  {
    for
      {
      size <- objSizeGen
      pairs <- Gen.containerOfN[immutable.Seq, (String, JsValue)](size,
                                                                  pair
                                                                  )
    } yield JsObj(immutable.Map[String, JsValue](pairs: _*))

  }

  val arr: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[immutable.Seq, JsValue](size,
                                                         Gen.frequency((arrayValueFreq.int, arrayPrimitiveGen.int),
                                                                       (arrayValueFreq.long, arrayPrimitiveGen.long),
                                                                       (arrayValueFreq.double, arrayPrimitiveGen.double),
                                                                       (arrayValueFreq.str, arrayPrimitiveGen.str),
                                                                       (arrayValueFreq.bigDec, arrayPrimitiveGen.bigDec),
                                                                       (arrayValueFreq.bigInt, arrayPrimitiveGen.bigInt),
                                                                       (arrayValueFreq.bool, arrayPrimitiveGen.bool),
                                                                       (arrayValueFreq.obj, obj),
                                                                       (arrayValueFreq.arr, arr),
                                                                       )
                                                         )
    } yield JsArray(vector)

  }


  val json: Gen[Json[_]] = Gen.oneOf(obj,
                                     arr
                                     )


}
