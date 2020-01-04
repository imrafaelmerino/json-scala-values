package value.properties

import java.io.ByteArrayInputStream

import valuegen.Preamble._
import valuegen.{JsArrayGen, RandomJsArrayGen, ValueFreq}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Prop.forAll
import value.spec.JsStrSpecs.str
import value.spec.{JsArraySpec, JsBoolSpecs, JsNumberSpecs, JsStrSpecs}
import value.{JsArray, JsArrayParser, JsBool, JsNothing, JsNull, JsObj, JsPath, JsValue, Json, Key}

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
            val result: JsArray = arr.removedAll(arr.flatten.map(p => p._1).reverse)
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
            val parsed: JsArray = JsArray.parse(arr.toPrettyString).get
            parsed == arr && arr.hashCode() == parsed.hashCode()
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

            val reduced: Option[Int] = arr.reduce[Int]((_, value) => value.isInt,
                                                       (_, value) => value.asJsInt.value,
                                                       _ + _
                                                       )

            val sum: Int = arr.flatten
              .filter((pair: (JsPath, JsValue)) => pair._2.isInt)
              .map((pair: (JsPath, JsValue)) => pair._2.asJsInt.value)
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
              arr.mapKey((path: JsPath, _: JsValue) => path.last.asKey.name + "!")
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!"))) &&
              arr.mapKey((key: String) => key + "!")
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
              arr.map((_   : JsPath, _: JsValue) => JsNull)
                .flatten
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull) &&
              arr.map((_: JsValue) => JsNull)
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
              arr.filterKey((_   : JsPath, value: JsValue) => value.isNotNumber)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }

  property("removing every number of a Json array with filter")
  {
    check(forAll(RandomJsArrayGen())
          {
            obj =>
              obj.filter((_   : JsPath, value: JsValue) => value.isNotNumber)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber) &&
              obj.filter((value: JsValue) => value.isNotNumber)
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
              arr.filter((_: JsPath, value: JsValue) => !value.isBool)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool) &&
              arr.filter((value: JsValue) => !value.isBool)
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
                .forall((pair: (JsPath, JsValue)) => obj.get(pair._1).contains(pair._2))
          }
          )
  }

  property("- operator removes the specified value")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.flatten.forall((pair: (JsPath, JsValue)) =>
                                   arr - pair._1 != arr &&
                                   arr - pair._1 == arr.removed(pair._1)
                                 )
          }
          )
  }

  property("head + tail returns the same object")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen.suchThat(a => a.isNotEmpty)
                 )
          {
            arr =>
              arr.head +: arr.tail == arr &&
              arr.tail.prepended(arr.head) == arr
          }
          )
  }

  property("last + init returns the same object")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen.suchThat(a => a.isNotEmpty)
                 )
          {
            arr =>
              arr.init.appended(arr.last) == arr &&
              arr.init :+ arr.last == arr
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
              val result = arr -- paths
              val result1 = arr.removedAll(paths)
              result.flatten.forall((pair: (JsPath, JsValue)) => pair._2.asJson.isEmpty) &&
              result1.flatten.forall((pair: (JsPath, JsValue)) => pair._2.asJson.isEmpty) &&
              result == result1
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

  property("map traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.map((path: JsPath, value: JsValue) =>
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
              arr.filter((path   : JsPath, value: JsValue) =>
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
              arr.filterKey((path   : JsPath, value: JsValue) =>
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
              arr.mapKey((path: JsPath, value: JsValue) =>
                           if (arr(path) != value) throw new RuntimeException
                           else path.last.asKey.name
                         ) == arr
          }
          )
  }

  property("filterJsObj traverses all the elements and passed every jspair of the Json to the function")
  {
    val arrGen = RandomJsArrayGen()
    check(forAll(arrGen
                 )
          {
            arr =>
              arr.filterJsObj((path                  : JsPath, value: JsObj) =>
                                if (arr(path) != value) throw new RuntimeException
                                else true
                              ) == arr
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
              JsArray.parse(string).get == arr &&
              JsArray.parse(string.getBytes).get == arr &&
              JsArray.parse(prettyString).get == arr &&
              JsArray.parse(prettyString.getBytes).get == arr &&
              JsArray.parse(new ByteArrayInputStream(string.getBytes)).get == arr &&
              JsArray.parse(new ByteArrayInputStream(prettyString.getBytes)).get == arr
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
              JsArray.parse(string,
                            parser
                            ) == Try(arr) &&
              JsArray.parse(string.getBytes,
                            parser
                            ) == Try(arr) &&
              JsArray.parse(prettyString,
                            parser
                            ) == Try(arr) &&
              JsArray.parse(prettyString.getBytes,
                            parser
                            ) == Try(arr) &&
              JsArray.parse(new ByteArrayInputStream(string.getBytes),
                            parser
                            ) == Try(arr) &&
              JsArray.parse(new ByteArrayInputStream(prettyString.getBytes),
                            parser
                            ) == Try(arr)
          }
          )
  }
}
