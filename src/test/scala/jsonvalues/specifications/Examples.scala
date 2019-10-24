package jsonvalues.specifications

import jsonvalues.Implicits._
import jsonvalues.{JsArray, JsObj, JsPath}
import JsPath._
object Examples
{

  val a: JsObj = JsObj("@context" -> "http://schema.org",
                       "@type" -> "MusicEvent",
                       "location" / "type" -> "MusicVenue",
                       "location" / "name" -> "Chicago Symphony Center",
                       "/location/address" -> "220 S. Michigan Ave, Chicago, Illinois, USA",
                       "/name" -> "Shostakovich Leningrad",
                       "/offers/@type" -> "Offer",
                       "/offers/url" -> "/examples/ticket/12341234",
                       "/offers/price" -> 40,
                       "/offers/priceCurrency" -> "USD",
                       "/offers/availability" -> "http://schema.org/InStock",
                       "/performers/0/@type" -> "MusicGroup",
                       "/performers/0/name" -> "Chicago Symphony Orchestra",
                       "/performers/0/sameAs/0" -> "http://cso.org/",
                       "/performers/0/sameAs/1" -> "http://en.wikipedia.org/wiki/Chicago_Symphony_Orchestra",
                       "/performers/1/@type" -> "Person",
                       "/performers/1/image" -> "/examples/jvanzweden_s.jpg",
                       "/performers/1/name" -> "Jaap van Zweden",
                       "/performers/1/sameAs/0" -> "http://www.jaapvanzweden.com/",
                       "/startDate" -> "2014-05-23T20:00",
                       "/workPerformed/0/@type" -> "CreativeWork",
                       "/workPerformed/0/name" -> "Britten Four Sea Interludes and Passacaglia from Peter Grimes",
                       "/workPerformed/0/sameAs" -> "http://en.wikipedia.org/wiki/Peter_Grimes",
                       "/workPerformed/1/@type" -> "CreativeWork",
                       "/workPerformed/1/name" -> "Shostakovich Symphony No. 7 (Leningrad)",
                       "/workPerformed/1/sameAs" -> "http://en.wikipedia.org/wiki/Symphony_No._7_(Shostakovich)"
                       )


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
