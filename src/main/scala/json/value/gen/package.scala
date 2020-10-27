package json.value

import json.value._
import json.value.gen.Preamble._
import org.scalacheck.{Arbitrary, Gen}

package object gen
{

  private[gen] def inserted[T <: Json[T]](gen: Gen[T],
                             pairs: (JsPath, Gen[JsValue])*
                            ): Gen[T] =
  {

    @scala.annotation.tailrec
    def apply0(acc: Gen[T],
               headGen: (JsPath, Gen[JsValue]),
               tailGens: Seq[(JsPath, Gen[JsValue])]
              ): Gen[T] =
    {
      val a = for
        {
        head <- headGen._2
        obj <- acc
      } yield obj.inserted(headGen._1,
                           head
                           )
      if (tailGens.isEmpty) a
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

  @scala.annotation.tailrec
  private[gen] def concat[T <: Json[T]](a: Gen[T],
                                        b   : Gen[T],
                                        rest: Gen[T]*
                                       ): Gen[T] =
  {
    def c = concatTwo(a,
                      b
                      )

    if (rest.isEmpty) c
    else concat(c,
                rest.head,
                rest.tail: _*
                )

  }

  private[gen] def concatTwo[T <: Json[T]](a: Gen[T],
                                           b: Gen[T]
                                          ): Gen[T] = b.flatMap(it => json.value.gen.inserted(a,
                                                                                              it.flatten.map(it => (it._1, Gen.const(it._2))): _*
                                                                                              )
                                                                )

  @scala.annotation.tailrec
  def genFromPairs[T <: Json[T]](acc: Gen[T],
                                 pairs: Seq[(JsPath, Gen[JsValue])]
                                ): Gen[T] =
  {
    if (pairs.isEmpty) acc
    else genFromPairs(acc.flatMap(o => for
      {
      a <- pairs.head._2
    } yield o.inserted(pairs.head._1,
                       a
                       )
                                  ),
                      pairs.tail
                      )
  }

  val ALPHABET: Seq[String] = "abcdefghijklmnopqrstuvwzyz".split("").toIndexedSeq

  final case class ValueFreq(obj: Int = 1,
                             arr   : Int = 1,
                             str   : Int = 5,
                             int   : Int = 5,
                             long  : Int = 5,
                             double: Int = 5,
                             bigInt: Int = 5,
                             bigDec: Int = 5,
                             bool  : Int = 5,
                             `null`: Int = 5
                            )


  final case class PrimitiveGen(strGen: Gen[String] = Gen.oneOf(ALPHABET),
                                intGen   : Gen[Int] = Arbitrary.arbitrary[Int],
                                longGen: Gen[Long] = Arbitrary.arbitrary[Long],
                                doubleGen: Gen[Double] = Arbitrary.arbitrary[Double],
                                floatGen: Gen[Float] = Arbitrary.arbitrary[Float],
                                boolGen: Gen[Boolean] = Arbitrary.arbitrary[Boolean],
                                bigIntGen: Gen[BigInt] = Arbitrary.arbitrary[BigInt],
                                bigDecGen: Gen[BigDecimal] = Arbitrary.arbitrary[BigDecimal]
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
