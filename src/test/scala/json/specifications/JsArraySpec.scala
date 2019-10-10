package json.specifications

import json.JsElemGens
import json.immutable.{JsArray, JsObj, Json}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class JsArraySpec extends BasePropSpec
{
  val gen = JsElemGens(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("pairs from the stream of an array are collected into an array that it's equal")
  {
    check(forAll(gen.jsArrGen)
          { arr =>
            println("Gen: " + arr)
            var acc = json.immutable.JsArray.NIL
            arr.toLazyListRec.foreach(p =>
                                      {
                                        println("JsPair: " + p)
                                        acc = acc.inserted(p._1,
                                                           p._2
                                                           )

                                      }
                                      )
            println("Result: " + acc)
            acc == arr && acc.hashCode() == arr.hashCode()
          }
          )
  }

  property("removing a path from an array returns a different array")
  {
    check(forAll(gen.jsArrGen)
          { arr =>
            println("Gen: " + arr)
            arr.toLazyListRec.forall(p =>
                                     {
                                       println("JsPair: " + p)
                                       arr.removed(p._1) != arr
                                     }
                                     )
          }
          )
  }

  property("removing by path all the elements of an array returns the empty array or an array with only empty Jsons")
  {

    check(forAll(gen.jsArrGen)
          { arr =>
            println("Gen: "+arr)
            val result:JsArray = arr.removedAll(arr.toLazyListRec.map(p => p._1).reverse)
            println("Result: "+result)
            result == JsArray.NIL || result.toLazyListRec.forall(p=> p._2 match{
              case o:Json[_] => o.isEmpty
              case _ => false
            })
          }
          )
  }

}
