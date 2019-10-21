package jsonvalues.gen

import jsonvalues.{JsArray, JsPath, JsValue}
import org.scalacheck.Gen

object JsArrGen
{

  def pairs(pairs: (JsPath, Gen[JsValue])*): Gen[JsArray] =
  {

    JsGen.json(Gen.const(JsArray()),
               pairs: _*
               )
  }

  def apply(seq: collection.Seq[Gen[JsValue]]): Gen[JsArray] =
  {
    @scala.annotation.tailrec
    def arrGenRec(acc: Gen[JsArray],
                  gens: collection.Seq[Gen[JsValue]]
                 ): Gen[JsArray] =
    {

      if (gens.isEmpty) acc
      else arrGenRec(acc.flatMap(a => gens.head.map(e => a.appended(e))),
                     gens.tail
                     )

    }

    arrGenRec(Gen.const(JsArray()),
              seq
              )
  }
}
