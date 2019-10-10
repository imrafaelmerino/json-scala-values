package json

import json.immutable.{JsArray, JsObj}
import org.scalacheck.{Arbitrary, Gen}

import scala.collection.SortedMap
import scala.collection.immutable.{ListMap, TreeMap}


case class FreqTypeOfValue(str     : Int = 2,
                           number  : Int = 2,
                           bool    : Int = 1,
                           nullFreq: Int = 1,
                           emptyArr: Int = 1,
                           emptyObj: Int = 1
                          )

case class FreqTypeOfNumber(int   : Int = 1,
                            long  : Int = 1,
                            double: Int = 1,
                            bigInt: Int = 1,
                            bigDec: Int = 1
                           )

case class FreqObjPair(value: Int = 5,
                       obj  : Int = 1,
                       arr  : Int = 1
                      )

case class FreqTypeOfArr(number: Int = 10,
                         str   : Int = 10,
                         bool  : Int = 10,
                         value : Int = 10,
                         obj   : Int = 2,
                         arr   : Int = 1,
                         int   : Int = 10,
                         double: Int = 10,
                         long  : Int = 10
                        )


case class JsElemGens(strGen        : Gen[String] = Gen.oneOf("abcdefghijklmnopqrstuvwzyz".split("")),
                      intGen        : Gen[Int] = Arbitrary.arbitrary[Int],
                      longGen       : Gen[Long] = Arbitrary.arbitrary[Long],
                      doubleGen     : Gen[Double] = Arbitrary.arbitrary[Double],
                      floatGen      : Gen[Float] = Arbitrary.arbitrary[Float],
                      boolGen       : Gen[Boolean] = Arbitrary.arbitrary[Boolean],
                      bigIntGen     : Gen[BigInt] = Arbitrary.arbitrary[BigInt],
                      bigDecGen     : Gen[BigDecimal] = Arbitrary.arbitrary[BigDecimal],
                      arrLengthGen  : Gen[Int] = Gen.choose(0,
                                                            10
                                                            ),
                      objSizeGen    : Gen[Int] = Gen.choose(0,
                                                            10
                                                            ),
                      keyGen        : Gen[String] = Gen.oneOf("abcdefghijklmnopqrstuvwzyz".split("")),
                      numberTypeFreq: FreqTypeOfNumber = FreqTypeOfNumber(),
                      valueFreq     : FreqTypeOfValue = FreqTypeOfValue(),
                      arrayTypeFreq : FreqTypeOfArr = FreqTypeOfArr(),
                      objectPairFreq: FreqObjPair = FreqObjPair()
                     )
{

  //  implicit val emptyObjSupplier:()=>Map[String,JsElem]= ()=> TreeMap[String,JsElem]()
  //  implicit val emptyArrSupplier:()=>Seq[JsElem]= ()=> LazyList.empty


  private val jsStrGen: Gen[JsStr] = strGen.map(it => JsStr(it))

  private val jsIntGen: Gen[JsInt] = intGen.map(it => JsInt(it))

  private val jsLongGen: Gen[JsLong] = longGen.map(it => JsLong(it))

  private val jsDoubleGen: Gen[JsDouble] = doubleGen.map(it => JsDouble(it))

  private val jsBigIntGen: Gen[JsBigInt] = bigIntGen.map(it => JsBigInt(it))

  private val jsBoolGen: Gen[JsBool] = boolGen.map(it => JsBool(it))

  private val jsBigDecGen: Gen[JsBigDec] = bigDecGen.map(it => JsBigDec(it))

  private val jsNumberGen: Gen[JsNumber] = Gen.frequency((numberTypeFreq.int, jsIntGen),
                                                         (numberTypeFreq.long, jsLongGen),
                                                         (numberTypeFreq.double, jsDoubleGen),
                                                         (numberTypeFreq.bigDec, jsBigIntGen),
                                                         (numberTypeFreq.bigInt, jsBigDecGen)
                                                         )

  private val jsValueGen: Gen[JsElem] = Gen.frequency((valueFreq.str, jsStrGen),
                                                      (valueFreq.number, jsNumberGen),
                                                      (valueFreq.bool, jsBoolGen),
                                                      (valueFreq.emptyArr, Gen.const(JsObj.NIL)),
                                                      (valueFreq.emptyArr, Gen.const(JsArray.NIL)),
                                                      (valueFreq.nullFreq, Gen.const(JsNull))
                                                      )

  private val pairNameValueGen: Gen[(String, JsElem)] =
  {
    for
      {
      key <- keyGen
      value <- Gen.frequency((objectPairFreq.value, jsValueGen),
                             (objectPairFreq.obj, jsObjGen),
                             (objectPairFreq.arr, jsArrGen),
                             )
    } yield (key, value)
  }

  val jsObjGen: Gen[JsObj] =
  {
    for
      {
      size <- objSizeGen
      pairs <- Gen.containerOfN[Array, (String, JsElem)](size,
                                                         pairNameValueGen
                                                         )
    } yield new JsObj(scala.collection.immutable.HashMap[String, JsElem](pairs: _*))

  }

  private val jsArrNumberGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsNumberGen
                                                 )
    } yield JsArray((vector))
  }

  private val jsArrStrGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsStrGen
                                                 )
    } yield JsArray(vector)
  }

  val jsArrBoolGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsBoolGen
                                                 )
    } yield JsArray(vector)
  }

  private val jsArrIntGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsIntGen
                                                 )
    } yield JsArray((vector))

  }

  private val jsArrDoubleGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsDoubleGen
                                                 )
    } yield JsArray(vector)

  }


  private val jsArrObjGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsObj](size,
                                                jsObjGen
                                                )
    } yield JsArray(vector)

  }

  private val jsArrLongGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsLongGen
                                                 )
    } yield JsArray(vector)

  }

  private val jsArrValueGen: Gen[JsArray] =
  {
    for
      {
      size <- arrLengthGen
      vector <- Gen.containerOfN[Vector, JsElem](size,
                                                 jsValueGen
                                                 )
    } yield JsArray(vector)

  }


  val jsArrGen: Gen[JsArray] = Gen.frequency((arrayTypeFreq.str, jsArrStrGen),
                                             (arrayTypeFreq.number, jsArrNumberGen),
                                             (arrayTypeFreq.bool, jsArrBoolGen),
                                             (arrayTypeFreq.value, jsArrValueGen),
                                             (arrayTypeFreq.obj, jsArrObjGen),
                                             (arrayTypeFreq.int, jsArrIntGen),
                                             (arrayTypeFreq.long, jsArrLongGen),
                                             (arrayTypeFreq.double, jsArrDoubleGen)
                                             )

  val jsonGen: Gen[Json[_]] = Gen.oneOf(jsObjGen,
                                        jsArrGen
                                        )


}
