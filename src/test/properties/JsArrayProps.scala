package properties

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import value.spec.JsStrSpecs.str
import value.spec.{JsArraySpec, JsBoolSpecs, JsNumberSpecs}
import value._

import scala.util.Try

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
            var acc = JsArray.empty
            arr.flatten.foreach(p =>
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
            arr.flatten.forall(p =>
                               {
                                 arr.remove(p._1) != arr
                               }
                               )
          }
          )
  }

  property("removing by path all the elements of an array returns the empty array or an array with only empty Jsons")
  {

    check(forAll(gen)
          { arr =>
            val result: JsArray = arr.removeAll(arr.flatten.map(p => p._1).reverse)
            result == JsArray.empty || result.flatten.forall(p => p._2 match
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
            JsArrayParser
              .parse(arr.toPrettyString)
              .exists(it => it == arr && arr.hashCode() == it.hashCode())
          }
          )
  }

  property("adds up every integer number of a Json array")
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

            val reduced: Option[Int] = arr.reduceAll[Int]((_, value) => value.isInt,
                                                          (_, value) => value.toJsInt.value,
                                                          _ + _
                                                          )


            val sum: Int = arr.flatten
              .filter((pair: (JsPath, JsValue)) => pair._2.isInt)
              .map((pair: (JsPath, JsValue)) => pair._2.toJsInt.value)
              .toVector.sum

            if (reduced.isEmpty) sum == 0
            else reduced.contains(sum)

          }
          )
  }


  property("mapping the Keys of every element of a Json array with mapKey")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.mapAllKeys((path: JsPath, _: JsValue) => path.last.asKey.name + "!")
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!"))) &&
              arr.mapAllKeys((key: String) => key + "!")
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!")))
          }
          )
  }

  property("mapping into null every primitive element of a Json array with map")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.mapAll((_: JsPath, _: JsValue) => JsNull)
                .flatten
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull) &&
              arr.mapAll((_: JsValue) => JsNull)
                .flatten
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull)
          }
          )
  }


  property("removing every number of a Json array with filterKey")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.filterAllKeys((_: JsPath, value: JsValue) => value.isNotNumber)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every key that starts with a")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              !arr.filterAllKeys((key: String) => !key.startsWith("a"))
                .flatten.exists((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.startsWith("a")))
          }
          )
  }

  property("array from a set of path/value pairs")
  {
    check(forAll(RandomJsArrayGen())
          {
            a =>
              val flatten = a.flatten
              if (flatten.isEmpty) true
              else
              {
                a == JsArray(flatten.head,
                             flatten.tail: _*
                             )

              }
          }
          )
  }

  property("removing every number of a Json array with filter")
  {
    check(forAll(RandomJsArrayGen())
          {
            obj =>
              obj.filterAll((_: JsPath, value: JsValue) => value.isNotNumber)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber) &&
              obj.filterAll((value: JsValue) => value.isNotNumber)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every boolean of a Json array with filter")
  {
    check(forAll(RandomJsArrayGen())
          {
            arr =>
              arr.filterAll((_: JsPath, value: JsValue) => !value.isBool)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool) &&
              arr.filterAll((value: JsValue) => !value.isBool)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool)
          }
          )
  }

  property("get the value of an array by path")
  {
    check(forAll(RandomJsArrayGen())
          {
            obj =>
              obj
                .flatten
                .forall((pair: (JsPath, JsValue)) => obj(pair._1) == pair._2)
          }
          )
  }

  property("last + init returns the same array")
  {
    val arrayGen = RandomJsArrayGen()
    check(forAll(arrayGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            arr =>
              arr.init.appended(arr.last,
                                ) == arr
          }
          )
  }

  property("head + tail returns the same array")
  {
    val arrayGen = RandomJsArrayGen()
    check(forAll(arrayGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            arr =>
              arr.tail.prepended(arr.head,
                                 ) == arr
          }
          )
  }


  property("removes all values of a Json array by path, returning a Json array with only empty Jsons")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen.suchThat(arr => arr.isNotEmpty)
                 )
          {
            arr =>
              val paths = arr.flatten.map((pair: (JsPath, JsValue)) => pair._1).reverse
              val result = arr.removeAll(paths)
              result.flatten.forall((pair: (JsPath, JsValue)) => pair._2.toJson.isEmpty)
          }
          )
  }

  property("count JsNothing returns 0")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen.suchThat(arr => arr.isNotEmpty)
                 )
          {
            arr =>
              val a = arr.count((p: (JsPath, JsValue)) => p._2 == JsNothing)
              a == 0
          }
          )
  }

  property("exists JsNothing returns false")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen.suchThat(arr => arr.isNotEmpty)
                 )
          {
            arr =>
              !arr.exists((p: (JsPath, JsValue)) => p._2 == JsNothing)
          }
          )
  }

  property("contains path")
  {
    val arrayGen = RandomJsArrayGen()
    check(forAll(arrayGen.suchThat(arr => arr.isNotEmpty)
                 )
          {
            arr =>
              arr.flatten.forall(p => arr.containsPath(p._1))
          }
          )
  }

  property("map traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.mapAll((path: JsPath, value: JsValue) =>
                        if (arr(path) != value) throw new RuntimeException else value
                      ) == arr

          }
          )
  }

  property("filter traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.filterAll((path: JsPath, value: JsValue) =>
                           if (arr(path) != value) throw new RuntimeException else true
                         ) == arr

          }
          )
  }

  property("filterKey traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.filterAllKeys((path: JsPath, value: JsValue) =>
                               if (arr(path) != value) throw new RuntimeException else true
                             ) == arr

          }
          )
  }

  property("mapKey traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.mapAllKeys((path: JsPath, value: JsValue) =>
                               if (arr(path) != value) throw new RuntimeException
                               else path.last.asKey.name
                             ) == arr
          }
          )
  }

  property("filterAllJsObj traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.filterAllJsObj((path: JsPath, value: JsObj) =>
                                   if (arr(path) != value) throw new RuntimeException
                                   else true
                                 ) == arr
          }
          )
  }

  property("filterAllJsObj to remove all empty json object")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.filterAllJsObj((_: JsPath, value: JsObj) => value.isNotEmpty
                                 ) == arr.filterAllJsObj((value: JsObj) => value.isNotEmpty
                                                         )
          }
          )
  }

  property("serialize array into bytes")
  {
    val arrayGen = RandomJsArrayGen()
    check(forAll(arrayGen
                 )
          {
            arr =>
              JsArrayParser.parse(arr.serialize) == Right(arr)
          }
          )
  }


  property("serialize array into output stream")
  {
    val arrayGen = RandomJsArrayGen()
    check(forAll(arrayGen
                 )
          {
            arr =>
              val os = new ByteArrayOutputStream()
              arr.serialize(os).apply()
              os.flush()
              JsArrayParser.parse(os.toByteArray) == Right(arr)
          }
          )
  }

  property("array parsers without spec")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              val string = arr.toString
              val prettyString = arr.toPrettyString

              JsArrayParser
                .parse(string)
                .exists(it => it == arr) &&
              JsArrayParser
                .parse(string.getBytes)
                .exists(it => it == arr) &&
              JsArrayParser
                .parse(prettyString)
                .exists(it => it == arr) &&
              JsArrayParser
                .parse(prettyString.getBytes)
                .exists(it => it == arr) &&
              JsArrayParser.parse(new ByteArrayInputStream(string.getBytes)).get == arr &&
              JsArrayParser.parse(new ByteArrayInputStream(prettyString.getBytes)).get == arr
          }
          )
  }

  property("array parsers with spec")
  {
    val arrGen = JsArrayGen(Gen.alphaStr,
                            Arbitrary.arbitrary[Int],
                            Arbitrary.arbitrary[Boolean]
                            )

    val spec = JsArraySpec(str,
                           JsNumberSpecs.int,
                           JsBoolSpecs.bool
                           )

    val parser = JsArrayParser(spec)
    check(forAll(arrGen
                 )
          {
            arr =>
              val string = arr.toString
              val prettyString = arr.toPrettyString
              parser.parse(string
                           ) == Right(arr) &&
              parser.parse(string.getBytes

                           ) == Right(arr) &&
              parser.parse(prettyString
                           ) == Right(arr) &&
              parser.parse(prettyString.getBytes

                           ) == Right(arr) &&
              parser.parse(new ByteArrayInputStream(string.getBytes),

                           ) == Try(arr) &&
              parser.parse(new ByteArrayInputStream(prettyString.getBytes),

                           ) == Try(arr)
          }
          )
  }
}
