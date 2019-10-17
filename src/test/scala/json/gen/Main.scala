package json.gen

import json.immutable.{JsArray, JsObj}
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

    val b: JsObj = JsObj("@context" -> "http://schema.org",
                         "@type" -> "MusicEvent",
                         "location" -> JsObj("type" -> "MusicVenue",
                                             "name" -> "Chicago Symphony Center",
                                             "address" -> "220 S. Michigan Ave, Chicago, Illinois, USA"
                                             ),
                         "name" -> "Shostakovich Leningrad",
                         "offers" -> JsObj("@type" -> "Offer",
                                           "url" -> "/examples/ticket/12341234",
                                           "price" -> 40,
                                           "priceCurrency" -> "USD",
                                           "availability" -> "http://schema.org/InStock"
                                           ),
                         "performer" -> JsArray(JsObj("@type" -> "MusicGroup",
                                                      "name" -> "Chicago Symphony Orchestra",
                                                      "sameAs" -> JsArray("http://cso.org/",
                                                                          "http://en.wikipedia.org/wiki/Chicago_Symphony_Orchestra"
                                                                          )
                                                      ),
                                                JsObj("@type" -> "Person",
                                                      "image" -> "/examples/jvanzweden_s.jpg",
                                                      "name" -> "Jaap van Zweden",
                                                      "sameAs" -> "http://www.jaapvanzweden.com/"
                                                      )
                                                ),
                         "startDate" -> "2014-05-23T20:00",
                         "workPerformed" -> JsArray(JsObj("@type" -> "CreativeWork",
                                                          "name" -> "Britten Four Sea Interludes and Passacaglia from Peter Grimes",
                                                          "sameAs" -> "http://en.wikipedia.org/wiki/Peter_Grimes"
                                                          ),
                                                    JsObj("@type" -> "CreativeWork",
                                                          "name" -> "Shostakovich Symphony No. 7 (Leningrad)",
                                                          "sameAs" -> "http://en.wikipedia.org/wiki/Symphony_No._7_(Shostakovich)"
                                                          )
                                                    )
                         )


  }


}
