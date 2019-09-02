package jsonvalues.specifications

import jsonvalues.{FreqTypeOfArr, FreqTypeOfValue, JsElem, JsElemGens, JsPair, JsPath}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class StreamSpec extends BasePropSpec
{
  val gen = JsElemGens(

    arrayTypeFreq = FreqTypeOfArr(
                                  obj = 0,
                                  arr = 0,
                                  value = 0
                                  ),
    arrLengthGen = Gen.choose(1,
                              10
                              )
    )

  val genObj = JsElemGens(valueFreq = FreqTypeOfValue(obj = 0,
                                                      arr = 0,
                                                      ),
                          objSizeGen = Gen.choose(1,
                                                  10
                                                  )
                          )

  property("stream of array")
  {
    check(forAll(gen.jsArrGen)
          { arr =>
            val head = arr.stream().head

            arr.get(head._1) == head._2

          }
          )
  }

  property("stream of obj")
  {
    check(forAll(genObj.jsObjGen)
          { obj =>
            print(obj)
            val head = obj.stream().head

            obj.get(head._1) == head._2

          }
          )
  }

  property("stream_ of obj")
  {
    check(forAll(gen.jsObjGen)
          { obj =>
            print(obj)
            val exist_? = (pair:JsPair) => obj.get(pair._1) == pair._2
            obj.stream_().forall(exist_?)
          }
          )
  }
}
