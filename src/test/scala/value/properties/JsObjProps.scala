package value.properties

import valuegen.{RandomJsObjGen, ValueFreq}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import value.Implicits._
import value._


class JsObjProps extends BasePropSpec
{
  val gen = RandomJsObjGen(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("pairs from the stream of an object are all inserted in an empty object, producing the same object")
  {
    check(forAll(gen)
          { obj =>
            var acc = JsObj()
            obj.toLazyListRec.foreach(p =>
                                      {
                                        acc = acc.inserted(p._1,
                                                           p._2
                                                           )
                                      }
                                      )
            acc == obj && acc.hashCode() == obj.hashCode()
          }
          )
  }

  property("removing by path an existing element returns a different object")
  {
    check(forAll(gen)
          { obj =>
            obj.toLazyListRec.forall(p =>
                                     {
                                       obj.removed(p._1) != obj
                                     }
                                     )
          }
          )
  }


  property("removing by path all the elements of an object returns the empty object or an object with only empty Jsons")
  {

    check(forAll(gen)
          { obj =>
            val result: JsObj = obj.removedAll(obj.toLazyListRec.map(p => p._1).reverse)
            result == JsObj() || result.toLazyListRec.forall(p => p._2 match
            {
              case o: Json[_] => o.isEmpty
              case _ => false
            }
                                                             )
          }
          )
  }

  property("updated with JsNull the value of a key of an object")
  {
    check(forAll(gen)
          { obj =>
            obj.keys.forall(key => obj.updated(key,
                                               JsNull
                                               )(key) == JsNull
                            )

          }
          )
  }

  property("updated with JsNull the value of a path of an object")
  {
    check(forAll(gen)
          { obj =>
            obj.toLazyListRec.forall(pair => obj.updated(pair._1,
                                                         JsNull
                                                         )(pair._1) == JsNull
                                     )
          }
          )
  }

  property("updated function doesn't create a new container if the parent of an element doesn't exist")
  {
    check(forAll(Gen.const(JsObj("a" -> 1,
                                 "b" -> JsArray(JsObj("c" -> 2),
                                                JsArray(1,
                                                        true,
                                                        "hi"
                                                        )
                                                )
                                 )
                           )
                 )
          { obj =>

            obj.updated("b" / 0 / "d",
                        1
                        )("b" / 0 / "d") == JsInt(1) &&
            obj.updated("b" / 0 / "d" / 0,
                        1
                        ) == obj &&
            obj.updated("b" / 1 / 1,
                        2
                        )("b" / 1 / 1) == JsInt(2)
          }
          )
  }

  property("given a json object, parsing its toString representation returns the same object")
  {
    check(forAll(gen)
          { obj =>
            val string = obj.toString
            val parsed: JsObj = JsObj.parse(string).get
            parsed == obj && obj.hashCode() == parsed.hashCode()
          }
          )
  }


  property("adds a question mark at the end of every string")
  {
    val onlyStrAndIntFreq = ValueFreq(long = 0,
                                      int = 10,
                                      bigDec = 0,
                                      bigInt = 0,
                                      double = 0,
                                      bool = 0,
                                      str = 10
                                      )
    val strGen = RandomJsObjGen(objectValueFreq = onlyStrAndIntFreq,
                                arrayValueFreq = onlyStrAndIntFreq
                                )
    check(forAll(strGen)
          { obj =>
            val mapped = obj.mapRec((_, value) => value.asJsStr.map(string => s"$string?"),
                                    (_, value) => value.isStr
                                    )
            mapped.toLazyListRec
              .filter((pair: (JsPath, JsValue)) => pair._2.isStr)
              .forall((pair: (JsPath, JsValue)) => pair._2.asJsStr.value.endsWith("?"))
          }
          )
  }

  property("filters strings")
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
    val strGen = RandomJsObjGen(objectValueFreq = onlyStrAndIntFreq,
                                arrayValueFreq = onlyStrAndIntFreq
                                )
    check(forAll(strGen)
          { obj =>
            val filtered = obj.filterRec((_, value) => !value.isStr
                                         )
            filtered.toLazyListRec
              .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
              .forall((pair: (JsPath, JsValue)) => pair._2.isInt)
          }
          )
  }

  property("adds up every integer number o a Json object")
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
    val strGen = RandomJsObjGen(objectValueFreq = onlyStrAndIntFreq,
                                arrayValueFreq = onlyStrAndIntFreq,
                                objSizeGen = Gen.choose(5,
                                                        10
                                                        ),
                                arrLengthGen = Gen.choose(5,
                                                          10
                                                          )
                                )
    check(forAll(strGen)
          { obj =>

            val reduced: Option[Int] = obj.reduceRec[Int]((_, value) => value.isInt,
                                                          (_, value) => value.asJsInt.value,
                                                          _ + _
                                                          )

            val sum: Int = obj.toLazyListRec
              .filter((pair: (JsPath, JsValue)) => pair._2.isInt)
              .map((pair: (JsPath, JsValue)) => pair._2.asJsInt.value)
              .toVector.sum

            if (reduced.isEmpty) sum == 0
            else reduced.contains(sum)

          }
          )
  }

  property("mapping the Keys of every element of a Json object with mapKeyRec")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.mapKeyRec((path: JsPath, _: JsValue) => path.last.asKey.name + "!")
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!")))
          }
          )
  }

  property("mapping into null every primitive element of a Json object with mapRec")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.mapRec((_: JsPath, _: JsValue) => JsNull)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull)
          }
          )
  }

  property("removing every number of a Json object with filterKeyRec")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.filterKeyRec((_: JsPath, value: JsValue) => value.isNotNumber)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every number of a Json with filterRec")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.filterRec((_: JsPath, value: JsValue) => value.isNotNumber)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every boolean of a Json with filterRec")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.filterRec((_: JsPath, value: JsValue) => !value.isBool)
                .toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool)
          }
          )
  }


  property("removing every empty of a Json with filterRec")
  {
    check(forAll(JsObj("a" -> JsObj(),
                       "b" -> JsArray("a",
                                      JsObj(),
                                      JsObj("a" -> 1,
                                            "b" -> "hi",
                                            "c" -> JsObj()
                                            )
                                      ),
                       "c" -> JsObj("d" -> JsObj(),
                                    "e" -> 1
                                    ),
                       "d" -> true,
                       "e" -> JsArray(JsObj(),
                                      JsObj()
                                      )
                       )
                 )
          {
            obj =>
              obj.filterJsObjRec((_: JsPath, obj: JsObj) => obj.isNotEmpty).toLazyListRec
                .filter((pair: (JsPath, JsValue)) => pair._2.isObj)
                .forall((pair: (JsPath, JsValue)) => pair._2.asJsObj.isNotEmpty)
          }
          )
  }


  property("get the value of an object by path")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj
                .toLazyListRec
                .forall((pair: (JsPath, JsValue)) => obj.get(pair._1).contains(pair._2))
          }
          )
  }


  property("+! operator always inserts the specified value")
  {
    val pathGen = JsPathGens().objPathGen
    val objGen = RandomJsObjGen()
    val valueGen = RandomJsObjGen()
    check(forAll(objGen,
                 pathGen,
                 valueGen
                 )
          {
            (obj, path, valueToBeInserted) =>

              valueToBeInserted.toLazyListRec.forall((pair: (JsPath, JsValue)) =>
                                                     {
                                                       val result = obj +! (path, pair._2)
                                                       result(path) == pair._2
                                                     }
                                                     )


          }
          )
  }

  property("- operator removes the specified value")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj => obj.toLazyListRec.forall((pair: (JsPath, JsValue)) => obj - pair._1 != obj && obj - pair._1 == obj.removed(pair._1))
          }
          )
  }

  property("head + tail returns the same object")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              obj.tail + (obj.head._1, obj.head._2) == obj &&
              obj.tail.updated(obj.head._1,
                               obj.head._2
                               ) == obj &&
              obj.tail.inserted(obj.head._1,
                                obj.head._2
                                ) == obj
          }
          )
  }

  property("last + init returns the same object")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              obj.init + (obj.last._1, obj.last._2) == obj &&
              obj.init.updated(obj.last._1,
                               obj.last._2
                               ) == obj &&
              obj.init.inserted(obj.last._1,
                                obj.last._2
                                ) == obj
          }
          )
  }


  property("removes all values of a Json object by path, returning a Json object with only empty Jsons")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              val paths = obj.toLazyListRec.map((pair: (JsPath, JsValue)) => pair._1).reverse
              val result = obj -- paths
              val result1 = obj.removedAll(paths)
              result.toLazyListRec.forall((pair: (JsPath, JsValue)) => pair._2.asJson.isEmpty) &&
              result1.toLazyListRec.forall((pair: (JsPath, JsValue)) => pair._2.asJson.isEmpty) &&
              result == result1
          }
          )
  }

  property("count head returns one")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              val a = obj.count((p: (JsPath, JsValue)) => p._1 == JsPath(Vector(Key(obj.head._1))))
              a == 1
          }
          )
  }

  property("countRec and count JsNothing returns 0")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              val a = obj.countRec((p: (JsPath, JsValue)) => p._2 == JsNothing)
              val b = obj.count((p: (JsPath, JsValue)) => p._2 == JsNothing)
              a == 0 && b == 0
          }
          )
  }

  property("mapRec traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj => obj.mapRec((path: JsPath, value: JsValue) => if (obj(path) != value) throw new RuntimeException else value) == obj
          }
          )
  }

  property("mapKeyRec traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj => obj.mapKeyRec((path: JsPath, value: JsValue) => if (obj(path) != value) throw new RuntimeException else path.last.asKey.name) == obj

          }
          )
  }

  property("filterJsObjRec traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj => obj.filterJsObjRec((path: JsPath, value: JsValue) => if (obj(path) != value) throw new RuntimeException else true) == obj
          }
          )
  }
  property("filterRec traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj => obj.filterRec((path: JsPath, value: JsValue) => if (obj(path) != value) throw new RuntimeException else true) == obj
          }
          )
  }

  property("filterKeyRec traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj => obj.filterKeyRec((path: JsPath, value: JsValue) => if (obj(path) != value) throw new RuntimeException else true) == obj
          }
          )
  }


}