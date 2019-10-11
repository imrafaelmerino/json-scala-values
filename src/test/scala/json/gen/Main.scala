package json.gen

import json.immutable.JsObj
import org.scalacheck.Gen

object Main
{

  def main(args: Array[String]): Unit =
  {

//    val gen: Gen[JsElem] = Gen.oneOf(JsStr("hie"),
//                                     JsStr("bye")
//                                     )
//
//    val objGen = for
//      {
//
//      e <- PairGen("a" / "b",
//                   gen
//                   )
//      f <- PairGen("a" / "c",
//                   gen
//                   )
//    } yield JsObj(e,
//                  f
//                  )
//
//    println(objGen.sample.get)
//
//
//    val objGen1 = for
//      {
//
//      a <- PairGen.frequency((1, ("a" / "b" / 0, gen)),
//                             (5, ("a" / "c" / 1, gen))
//                             )
//    } yield JsObj(a)
//
//    println(objGen1.sample.get)
//
//
//    val objGen2 = JsObjGen(("a" / "b", gen),
//                           ("a" / "c", gen)
//                           )
//
//    println(objGen2.sample.get)

//    val arrGen2 = JsGen.array((0 / "b", gen),
//                              (1 / "c", gen)
//                              )
//
//    println(arrGen2.sample.get)

    val c: Gen[JsObj] = JsObjGen(Map("name" -> Gen.alphaNumStr,
                                     "surname" -> Gen.oneOf(List("Rafael",
                                                                 "Philip"
                                                                 )
                                                            ),
                                     "age" -> Gen.choose(1,
                                                         4
                                                         ),
                                     "mails" -> JsArrGen(Vector(Gen.const(1))
                                                         )
                                     )
                                 )

    println(c.sample.get)


  }


}
