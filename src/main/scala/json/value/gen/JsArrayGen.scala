package json.value.gen

import json.value.Preamble._
import json.value.gen.Preamble._
import json.value.{JsArray, JsPath, JsValue}
import org.scalacheck.Gen

import scala.collection.immutable

object JsArrayGen
{
  def inserted(gen: Gen[JsArray],
               pairs: (JsPath, Gen[JsValue])*
              ): Gen[JsArray] =
  {
    if pairs.count(pair => pair._1.head.isKey) > 0
    then throw UnsupportedOperationException("head of a path is a key")
    insertedPairs(gen,
                  pairs: _*
                  )
  }


  def of(gen: Gen[JsValue]): Gen[JsArray] = Gen.containerOf[immutable.Seq, JsValue](gen).map(v => JsArray(v))

  def noneEmptyOf(gen: Gen[JsValue]): Gen[JsArray] = Gen.nonEmptyContainerOf[immutable.Seq, JsValue](gen).map(v => JsArray(v))

  def ofN(n  : Int,
          gen: Gen[JsValue]
         ): Gen[JsArray] =
  {
    if n == 0
    then Gen.const(JsArray())
    else Gen.containerOfN[immutable.Seq, JsValue](n,
                                                  gen
                                                  ).map(v => JsArray(v))
  }

  def apply(seq: Gen[JsValue]*): Gen[JsArray] =
  {
    @scala.annotation.tailrec
    def arrGenRec(acc : Gen[JsArray],
                  gens: collection.Seq[Gen[JsValue]]
                 ): Gen[JsArray] =
    {
      if gens.isEmpty
      then acc
      else arrGenRec(acc.flatMap(a => gens.head.map(e => a.appended(e))),
                     gens.tail
                     )
    }

    arrGenRec(Gen.const(JsArray()),
              seq
              )
  }

  def fromPairs(pairs: (JsPath, Gen[JsValue])*): Gen[JsArray] =
  {
    if pairs.count(pair => pair._1.head.isKey) > 0
    then throw UnsupportedOperationException("head of a path is a key")
    else genFromPairs[JsArray](Gen.const(JsArray()),
                               pairs
                               )
  }
}