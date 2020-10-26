package json.value.gen

import json.value._
import org.scalacheck.{Arbitrary, Gen}

import scala.Conversion
import scala.language.implicitConversions

object Preamble
{

  implicit def ? (prob: Int, gen: Gen[JsValue]): Gen[JsValue]
  =
  {
    if prob < 0 || prob > 100
    then throw IllegalArgumentException("prob must be [0,100]")
    Gen.frequency((prob, gen),
                  (100 - prob, JsNothing)
                  )
  }

  def ? (gen: Gen[JsValue]): Gen[JsValue]
  = ? (50, gen)

  given Conversion[Int, Gen[JsValue]] = i => Gen.const(JsInt(i))

  given Conversion[Boolean, Gen[JsValue]] = i => Gen.const(JsBool(i))

  given Conversion[Long, Gen[JsValue]] = i => Gen.const(JsLong(i))

  given Conversion[String, Gen[JsValue]] = i => Gen.const(JsStr(i))

  given Conversion[Double, Gen[JsValue]] = i => Gen.const(JsDouble(i))

  given Conversion[BigInt, Gen[JsValue]] = i => Gen.const(JsBigInt(i))

  given Conversion[BigDecimal, Gen[JsValue]] = i => Gen.const(JsBigDec(i))

  given Conversion[JsObj, Gen[JsValue]] = Gen.const(_)

  given Conversion[JsArray, Gen[JsValue]] = Gen.const(_)

  given Conversion[JsNull.type, Gen[JsValue]] = Gen.const(_)

  given strGenToJsStrGen as Conversion[Gen[String], Gen[JsStr]] = gen => gen.map(s => JsStr(s))

  given intGenToJsIntGen as Conversion[Gen[Int], Gen[JsInt]] = gen => gen.map(s => JsInt(s))

  given longGenToJsLongGen as Conversion[Gen[Long], Gen[JsLong]] = gen => gen.map(s => JsLong(s))

  given bigIntGenToJsBigIntGen as Conversion[Gen[BigInt], Gen[JsBigInt]] = gen => gen.map(s => JsBigInt(s))

  given doubleGenToJsDoubleGen as Conversion[Gen[Double], Gen[JsDouble]] = gen => gen.map(s => JsDouble(s))

  given bigDecGenToJsBigDecGen as Conversion[Gen[BigDecimal], Gen[JsBigDec]] = gen => gen.map(s => JsBigDec(s))

  given boolGenToJsBoolGen as Conversion[Gen[Boolean], Gen[JsBool]] = gen => gen.map(s => JsBool(s))

  @scala.annotation.tailrec
  private[gen] def genFromPairs[T <: Json[T]](acc: Gen[T],
                                              pairs     : Seq[(JsPath, Gen[JsValue])]
                                             ): Gen[T] =
  {
    if pairs.isEmpty
    then acc
    else genFromPairs(acc.flatMap(o =>
                                  {
                                    for{
                                      a <- pairs.head._2
                                    } yield o.inserted(JsPath.empty / pairs.head._1,
                                                       a
                                                       )
                                  }
                                  ),
                      pairs.tail
                      )
  }


  private[gen] def concatTwo[T <: Json[T]](a: Gen[T],
                                           b     : Gen[T]
                                               ): Gen[T] =
  {
    b.flatMap(it => insertedPairs(a,
                                  it.flatten.map(it => (it._1, Gen.const(it._2))): _*
                                  )
              )
  }

  @scala.annotation.tailrec
  private[gen] def concatGens[T <: Json[T]](a: Gen[T],
                                            b        : Gen[T],
                                            rest     : Gen[T]*
                                           ): Gen[T] =
  {
    val c = concatTwo(a, b)
    if rest.isEmpty
    then c
    else concatGens(c,
                    rest.head,
                    rest.tail: _*
                    )
  }

  private[gen] def insertedPairs[T <: Json[T]](gen: Gen[T],
                                               pairs     : (JsPath, Gen[JsValue])*
                                                   ): Gen[T] =
  {
    @scala.annotation.tailrec
    def apply0(acc     : Gen[T],
               headGen : (JsPath, Gen[JsValue]),
               tailGens: Seq[(JsPath, Gen[JsValue])]
              ): Gen[T] =
    {
      val a = for{
        head <- headGen._2
        obj <- acc
      } yield obj.inserted(headGen._1,
                           head
                           )
      if tailGens.isEmpty
      then a
      else apply0(a,
                  tailGens.head,
                  tailGens.tail
                  )
    }

    apply0(gen,
           pairs.head,
           pairs.tail
           )
  }

  private[gen] val ALPHABET: Seq[String] = "abcdefghijklmnopqrstuvwzyz".split("").toIndexedSeq

  private[gen] final case class ValueFreq(obj: Int = 1,
                                          arr: Int = 1,
                                          str: Int = 5,
                                          int: Int = 5,
                                          long: Int = 5,
                                          double: Int = 5,
                                          bigInt: Int = 5,
                                          bigDec: Int = 5,
                                          bool: Int = 5,
                                          `null`     : Int = 5
                                              )

  private[gen] final case class PrimitiveGen(strGen: Gen[String] = Gen.oneOf(ALPHABET),
                                             intGen: Gen[Int] = Arbitrary.arbitrary[Int],
                                             longGen: Gen[Long] = Arbitrary.arbitrary[Long],
                                             doubleGen: Gen[Double] = Arbitrary.arbitrary[Double],
                                             floatGen: Gen[Float] = Arbitrary.arbitrary[Float],
                                             boolGen: Gen[Boolean] = Arbitrary.arbitrary[Boolean],
                                             bigIntGen: Gen[BigInt] = Arbitrary.arbitrary[BigInt],
                                             bigDecGen     : Gen[BigDecimal] = Arbitrary.arbitrary[BigDecimal]
                                                 )
  {
    val str: Gen[JsStr] = strGen.map(it => JsStr(it))
    val int: Gen[JsInt] = intGen.map(it => JsInt(it))
    val long: Gen[JsLong] = longGen.map(it => JsLong(it))
    val double: Gen[JsDouble] = doubleGen.map(it => JsDouble(it))
    val bigInt: Gen[JsBigInt] = bigIntGen.map(it => JsBigInt(it))
    val bool: Gen[JsBool] = boolGen.map(it => JsBool(it))
    val bigDec: Gen[JsBigDec] = bigDecGen.map(it => JsBigDec(it))
  }

}
