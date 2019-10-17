package json

import json.immutable.{JsArray, JsObj, Json}
import org.scalacheck.Gen

import scala.collection.{immutable => i}
import json.pkg._

case class ImmutableJsGen(primitivesGen: JsPrimitiveGen = JsPrimitiveGen(),
                          arrLengthGen: Gen[Int] = Gen.choose(0,
                                                               10
                                                               ),
                          objSizeGen: Gen[Int] = Gen.choose(0,
                                                             10
                                                             ),
                          keyGen: Gen[String] = Gen.oneOf("abcdefghijklmnopqrstuvwzyz".split("")),
                          arrayTypeFreq: FreqTypeOfArr = FreqTypeOfArr(),
                          objectPairFreq: FreqObjPair = FreqObjPair()
                          )
{


  //noinspection ForwardReference
  private val pair: Gen[(String, JsValue)] =
  {
    for
      {
      key <- keyGen
      value <- Gen.frequency((objectPairFreq.primitive, primitivesGen.primitive),
                             (objectPairFreq.obj, obj),
                             (objectPairFreq.arr, arr),
                             )
    } yield (key, value)
  }

  val obj: Gen[JsObj] =
  {
    for
      {
      size <- objSizeGen
      pairs <- Gen.containerOfN[i.Vector, (String, JsValue)](size,
                                                             pair
                                                             )
    } yield new JsObj(i.HashMap[String, JsValue](pairs: _*))

  }

  private val arrOfNumbers: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.number
                                                    )
    } yield JsArray(vector)
  }

  private val arrOfStrs: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.str
                                                    )
    } yield JsArray(vector)
  }

  val arrOfBools: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.bool
                                                    )
    } yield JsArray(vector)
  }

  private val arrOfInts: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.int
                                                    )
    } yield JsArray(vector)

  }

  private val arrOfDoubles: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.double
                                                    )
    } yield JsArray(vector)

  }


  private val arrOfObjs: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsObj](size,
                                                  obj
                                                  )
    } yield JsArray(vector)

  }

  private val arrOfLongs: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.long
                                                    )
    } yield JsArray(vector)

  }

  private val arrOfPrimitives: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[i.Vector, JsValue](size,
                                                    primitivesGen.primitive
                                                    )
    } yield JsArray(vector)

  }


  val arr: Gen[JsArray] = Gen.frequency((arrayTypeFreq.str, arrOfStrs),
                                        (arrayTypeFreq.number, arrOfNumbers),
                                        (arrayTypeFreq.bool, arrOfBools),
                                        (arrayTypeFreq.primitive, arrOfPrimitives),
                                        (arrayTypeFreq.obj, arrOfObjs),
                                        (arrayTypeFreq.int, arrOfInts),
                                        (arrayTypeFreq.long, arrOfLongs),
                                        (arrayTypeFreq.double, arrOfDoubles)
                                        )

  val json: Gen[Json[_]] = Gen.oneOf(obj,
                                     arr
                                     )


}
