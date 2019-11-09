package value.properties

import valuegen.RandomJsArrayGen
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import value.{JsArray, Json}

class JsArrayProps extends BasePropSpec
{
  val gen = RandomJsArrayGen(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("pairs from the stream of an array are collected into an array that it's equal")
  {
    check(forAll(gen)
          { arr =>
            var acc = JsArray()
            arr.toLazyListRec.foreach(p =>
                                      {
                                        acc = acc.inserted(p._1,
                                                           p._2
                                                           )
                                      }
                                      )
            acc == arr && acc.hashCode() == arr.hashCode()
          }
          )
  }

  property("removing a path from an array returns a different array")
  {
    check(forAll(gen)
          { arr =>
            arr.toLazyListRec.forall(p =>
                                     {
                                       arr.removed(p._1) != arr
                                     }
                                     )
          }
          )
  }

  property("removing by path all the elements of an array returns the empty array or an array with only empty Jsons")
  {

    check(forAll(gen)
          { arr =>
            val result: JsArray = arr.removedAll(arr.toLazyListRec.map(p => p._1).reverse)
            result == JsArray() || result.toLazyListRec.forall(p => p._2 match
            {
              case o: Json[_] => o.isEmpty
              case _ => false
            }
                                                               )
          }
          )
  }

  property("given a json array, parsing its toString representation returns the same array")
  {
    check(forAll(gen)
          { arr =>
            val parsed: JsArray = JsArray.parse(arr.toString).get
            parsed == arr && arr.hashCode() == parsed.hashCode()
          }
          )
  }

}
