package jsonvalues.gen

import jsonvalues.{JsObj, JsValue}
import org.scalacheck.Gen
object JsObjGen
{
  def pairs(pairs: JsPairGen*): Gen[JsObj] =
  {
    JsGen.json(Gen.const(JsObj()),
               pairs: _*
               )
  }

  def apply(map: collection.Map[String, Gen[JsValue]]): Gen[JsObj] =
  {
    @scala.annotation.tailrec
    def objGenRec(acc: Gen[JsObj],
                  gens: collection.Map[String, Gen[JsValue]]
                 ): Gen[JsObj] =
    {
      if (gens.isEmpty) acc
      else
      {
        val (key, gen) = gens.head
        objGenRec(acc.flatMap(o => gen.map(e => o.updated(key,
                                                          e
                                                          )
                                           )
                              ),
                  gens.tail
                  )
      }
    }

    objGenRec(Gen.const(JsObj()),
              map
              )
  }
}
