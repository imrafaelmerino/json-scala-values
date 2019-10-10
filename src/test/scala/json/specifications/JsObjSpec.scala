package json.specifications

import json.immutable.{JsObj, Json}
import json.{JsElem, JsElemGens, JsNothing, JsPair, JsPath}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class JsObjSpec extends BasePropSpec
{
  val gen = JsElemGens(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("pairs from the stream of an object are collected into an object that it's equals")
  {
    check(forAll(gen.jsObjGen)
          { obj =>
            println("Gen: " + obj)
            var acc = json.immutable.JsObj.NIL
            obj.toLazyListRec.foreach(p =>
                                      {
                                        println("JsPair: " + p)
                                        acc = acc.inserted(p._1,
                                                           p._2
                                                           )
                                      }
                                      )
            println("Result: " + acc)
            acc == obj && acc.hashCode() == obj.hashCode()
          }
          )
  }

  property("removing by path an existing element returns a different object")
  {
    check(forAll(gen.jsObjGen)
          { obj =>
            println("Gen: " + obj)
            obj.toLazyListRec.forall(p =>
                                     {
                                       println("JsPair: " + p)
                                       obj.removed(p._1) != obj
                                     }
                                     )
          }
          )
  }


  property("removing by path all the elements of an object returns the empty object or an object with only empty Jsons")
  {

    check(forAll(gen.jsObjGen)
          { obj =>
            println("Gen: "+obj)
            val result:JsObj = obj.removedAll(obj.toLazyListRec.map(p => p._1).reverse)
            println("Result: "+result)
            result == JsObj.NIL || result.toLazyListRec.forall(p=> p._2 match{
              case o:Json[_] => o.isEmpty
              case _ => false
            })
          }
          )
  }


}
