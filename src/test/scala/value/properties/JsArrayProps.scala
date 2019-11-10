package value.properties

import valuegen.{RandomJsArrayGen, RandomJsObjGen, ValueFreq}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import value.{JsArray, JsNull, JsObj, JsPath, JsValue, Json}

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

  property("adds up every integer number o a Json array")
  {
    val onlyStrAndIntFreq = ValueFreq(long = 0,
                                      int = 10,
                                      bigDec = 0,
                                      bigInt = 0,
                                      double = 0,
                                      bool = 0,
                                      str = 10,
                                      `null` = 0
                                      )
    val strGen = RandomJsArrayGen(objectValueFreq = onlyStrAndIntFreq,
                                  arrayValueFreq = onlyStrAndIntFreq,
                                  objSizeGen = Gen.choose(5,
                                                          10
                                                          ),
                                  arrLengthGen = Gen.choose(5,
                                                            10
                                                            )
                                  )
    check(forAll(strGen)
          { arr =>

            val reduced: Option[Int] = arr.reduceRec[Int]((_, value) => value.isInt,
                                                          (_, value) => value.asJsInt.value,
                                                          _ + _
                                                          )

            val sum: Int = arr.toLazyListRec
              .filter((pair: (JsPath, JsValue)) => pair._2.isInt)
              .map((pair: (JsPath, JsValue)) => pair._2.asJsInt.value)
              .toVector.sum

            if (reduced.isEmpty) sum == 0
            else reduced.contains(sum)

          }
          )
  }

  property("mapping the Keys of every element of a Json array with mapKeyRec")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.mapKeyRec((path: JsPath, _: JsValue) => path.last.asKey.name + "!")
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!")))
          }
          )
  }

  property("mapping into null every primitive element of a Json array with mapRec")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.mapRec((_: JsPath, _: JsValue) => JsNull)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull)
          }
          )
  }

  property("removing every number of a Json array with filterKeyRec")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.filterKeyRec((_: JsPath, value: JsValue) => value.isNotNumber)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every number of a Json array with filterRec")
  {
    check(forAll(RandomJsArrayGen())
          {
            obj =>
              obj.filterRec((_: JsPath, value: JsValue) => value.isNotNumber)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every boolean of a Json array with filterRec")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.filterRec((_: JsPath, value: JsValue) => !value.isBool)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool)
          }
          )
  }

}
