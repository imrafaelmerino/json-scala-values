package json.specifications.immutable

import json.ImmutableJsGen
import json.immutable.{JsArray, Json}
import json.specifications.BasePropSpec
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class JsArraySpec extends BasePropSpec
{
  val gen = ImmutableJsGen(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("pairs from the stream of an array are collected into an array that it's equal")
  {
    check(forAll(gen.arr)
          { arr =>
            var acc = json.immutable.JsArray()
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
    check(forAll(gen.arr)
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

    check(forAll(gen.arr)
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

}
