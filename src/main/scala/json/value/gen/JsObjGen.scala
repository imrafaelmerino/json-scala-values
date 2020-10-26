package json.value.gen

import json.value.Preamble._
import json.value.gen.Preamble._
import json.value.{JsObj, JsPath, JsValue}
import org.scalacheck.Gen

import scala.language.implicitConversions

/**
 * Represents a Json object generator.
 *
 */
object JsObjGen
{

  def inserted(gen: Gen[JsObj],
               pairs: (JsPath, Gen[JsValue])*
              ): Gen[JsObj] =
  {
    if pairs.count(pair => pair._1.head.isIndex) > 0
    then throw UnsupportedOperationException("head of a path is an index")
    else insertedPairs(gen,
                       pairs: _*
                       )
  }


  def concat(a   : Gen[JsObj],
             b   : Gen[JsObj],
             rest: Gen[JsObj]*
            ): Gen[JsObj] = concatGens(a,
                                       b,
                                       rest: _*
                                       )


  def apply(pairs: (String, Gen[JsValue])*): Gen[JsObj] =
  {
    @scala.annotation.tailrec
    def objGenRec(acc: Gen[JsObj],
                  seq: Seq[(String, Gen[JsValue])]
                 ): Gen[JsObj] =
    {

      if seq.isEmpty
      then acc
      else
        val (key, gen) = seq.head
        objGenRec(acc.flatMap(o => gen.map(e => o.inserted(JsPath.empty / key,
                                                           e
                                                           )
                                           )
                              ),
                  seq.tail
                  )
    }

    objGenRec(Gen.const(JsObj()),
              pairs
              )
  }

  def fromPairs(pairs: (JsPath, Gen[JsValue])*): Gen[JsObj] =
  {
    if pairs.count(pair => pair._1.head.isIndex) > 0
    then throw UnsupportedOperationException("head of a path is an index")
    else genFromPairs[JsObj](Gen.const(JsObj()),
                             pairs
                             )
  }

}