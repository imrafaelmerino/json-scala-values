package json.specifications

import json.{JsElem, JsElemGens, JsPair, JsPath}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class StreamSpec extends BasePropSpec
{
  val gen = JsElemGens(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("an array and its stream have the same head")
  {
    check(forAll(gen.jsArrGen)
          { arr =>
            val head: (JsPath, JsElem) = arr.toLazyList.head
            arr(head._1) == head._2
          }
          )
  }

  property("an object and its stream have the same head")
  {
    check(forAll(gen.jsObjGen)
          { obj =>
            val head = obj.toLazyList.head
            obj(head._1) == head._2
          }
          )
  }

  property("every pair of the stream of an object is found using its apply method")
  {
    check(forAll(gen.jsObjGen)
          { obj =>
            obj.toLazyListRec.forall((pair: JsPair) => obj(pair._1) == pair._2)
          }
          )
  }

  property("every pair of the stream of an array is found using its apply method")
  {
    check(forAll(gen.jsArrGen)
          { arr =>
            arr.toLazyListRec.forall((pair: JsPair) => arr(pair._1) == pair._2)
          }
          )
  }
}
