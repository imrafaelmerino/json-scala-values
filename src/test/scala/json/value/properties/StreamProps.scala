package json.value.properties

import json.value.gen.{RandomJsArrayGen, RandomJsObjGen}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import json.value.{JsPath, JsValue}
class StreamProps extends BasePropSpec
{
  val obGen = RandomJsObjGen(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )

  val arrGen = RandomJsArrayGen(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("an array and its stream have the same head")
  {
    check(forAll(arrGen)
          { arr =>
            val head: (JsPath, JsValue) = arr.flatten.head
            arr(head._1) == head._2
          }
          )
  }



  property("every pair of the stream of an object is found using its apply method")
  {
    check(forAll(obGen)
          { obj =>
            obj.flatten.forall((pair: (JsPath, JsValue)) => obj(pair._1) == pair._2)
          }
          )
  }

  property("every pair of the stream of an array is found using its apply method")
  {
    check(forAll(arrGen)
          { arr =>
            arr.flatten.forall((pair: (JsPath, JsValue)) => arr(pair._1) == pair._2)
          }
          )
  }
}
