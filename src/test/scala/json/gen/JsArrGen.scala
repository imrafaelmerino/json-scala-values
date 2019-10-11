package json.gen

import json.immutable.JsArray
import json.{JsElem, JsPath}
import org.scalacheck.Gen

object JsArrGen
{

  def pairs(pairs: (JsPath, Gen[JsElem])*): Gen[JsArray] =
  {

    JsGen.json(Gen.const(JsArray.NIL),
               pairs: _*
               )
  }

  def apply(seq: collection.Seq[Gen[JsElem]]): Gen[JsArray] =
  {
    @scala.annotation.tailrec
    def arrGenRec(acc: Gen[JsArray],
                  gens: collection.Seq[Gen[JsElem]]
                 ): Gen[JsArray] =
    {

      if (gens.isEmpty) acc
      else arrGenRec(acc.flatMap(a => gens.head.map(e => a.appended(e))),
                     gens.tail
                     )

    }

    arrGenRec(Gen.const(JsArray.NIL),
              seq
              )
  }
}
