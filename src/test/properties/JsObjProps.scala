package value.properties

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import valuegen.{JsArrayGen, JsObjGen, RandomJsArrayGen, RandomJsObjGen, ValueFreq}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Prop.forAll
import value.Preamble._
import value.spec.JsStrSpecs.str
import value.spec.{JsArraySpecs, JsNumberSpecs, JsObjSpec}
import value.{JsObj, _}
import valuegen.Preamble._

import scala.util.Try


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
  property("object.keys and map.keys return the same result")
  {
    check(forAll(RandomJsObjGen())
          {
            a =>
              a.keys == a.map.keys
          }
          )
  }
  property("object from a set of path/value pairs")
  {
    check(forAll(RandomJsObjGen())
          {
            a =>
              val flatten = a.flatten
              if (flatten.isEmpty) true
              else a == JsObj(flatten: _*)
          }
          )
  }
  property("pairs from the stream of an object are all inserted in an empty object, producing the same object")
  {
    check(forAll(gen)
          { obj =>
            var acc = JsObj.empty
            obj.flatten.foreach(p =>
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

  property("count JsNothing returns 0")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              val a = obj.count((p: (JsPath, JsValue)) => p._2 == JsNothing)
              a == 0
          }
          )
  }

  property("contains path")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              obj.flatten.forall(p => obj.containsPath(p._1))
          }
          )
  }

  property("exists JsNothing returns false")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen.suchThat(obj => obj.isNotEmpty)
                 )
          {
            obj =>
              !obj.exists((p: (JsPath, JsValue)) => p._2 == JsNothing)
          }
          )
  }

  property("removing by path an existing element returns a different object")
  {
    check(forAll(gen)
          { obj =>
            obj.flatten.forall(p =>
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
            val result: JsObj = obj.removedAll(obj.flatten.map(p => p._1).reverse)
            result == JsObj.empty || result.flatten.forall(p => p._2 match
            {
              case o: Json[_] => o.isEmpty
              case _ => false
            }
                                                       )
          }
          )
  }

  property("given a json object, parsing its toString representation returns the same object")
  {
    check(forAll(gen)
          { obj =>
            val string = obj.toPrettyString
            JsObjParser.parse(string).exists(it => it == obj && it.hashCode() == obj.hashCode())
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
            val mapped = obj.map((_, value) => value.toJsStr.map(string => s"$string?"),
                                 (_, value) => value.isStr
                                 )
            mapped.flatten
              .filter((pair: (JsPath, JsValue)) => pair._2.isStr)
              .forall((pair: (JsPath, JsValue)) => pair._2.toJsStr.value.endsWith("?"))
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
            val filtered = obj.filter((_, value) => !value.isStr
                                      )
            filtered.flatten
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

            val reduced: Option[Int] = obj.reduce[Int]((_, value) => value.isInt,
                                                       (_, value) => value.toJsInt.value,
                                                       _ + _
                                                       )

            val sum: Int = obj.flatten
              .filter((pair: (JsPath, JsValue)) => pair._2.isInt)
              .map((pair: (JsPath, JsValue)) => pair._2.toJsInt.value)
              .toVector.sum

            if (reduced.isEmpty) sum == 0
            else reduced.contains(sum)

          }
          )
  }

  property("mapping the Keys of every element of a Json object with mapKey")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.mapKeys((path: JsPath, _: JsValue) => path.last.asKey.name + "!")
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!"))) &&
              obj.mapKeys((key: String) => key + "!")
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!")))
          }
          )
  }


  property("mapping into null every primitive element of a Json object with map")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.map((_: JsPath, _: JsValue) => JsNull)
                .flatten
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull) &&
              obj.map((_: JsValue) => JsNull)
                .flatten
                .filter((pair: (JsPath, JsValue)) => !pair._2.isJson)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNull)
          }
          )
  }


  property("removing every number of a Json object with filterKey")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.filterKeys((_: JsPath, value: JsValue) => value.isNotNumber)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._2.isNotNumber)
          }
          )
  }


  property("removing every number of a Json with filter")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.filter((_: JsPath, value: JsValue) => value.isNotNumber)
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

  property("removing every boolean of a Json with filter")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.filter((_: JsPath, value: JsValue) => !value.isBool)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool) &&
              obj.filter((value: JsValue) => !value.isBool)
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => !pair._2.isBool)
          }
          )
  }


  property("removing every empty of a Json with filter")
  {
    check(forAll(JsObjGen("a" -> JsObj.empty,
                          "b" -> JsArray("a",
                                         JsObj.empty,
                                         JsObj("a" -> 1,
                                               "b" -> "hi",
                                               "c" -> JsObj.empty
                                               )
                                         ),
                          "c" -> JsObj("d" -> JsObj.empty,
                                       "e" -> 1
                                       ),
                          "d" -> true,
                          "e" -> JsArray(JsObj.empty,
                                         JsObj.empty
                                         )
                          )
                 )
          {
            obj =>
              obj.filterJsObj((_: JsPath, obj: JsObj) => obj.isNotEmpty).flatten
                .filter((pair: (JsPath, JsValue)) => pair._2.isObj)
                .forall((pair: (JsPath, JsValue)) => pair._2.toJsObj.isNotEmpty) &&
              obj.filterJsObj((obj: JsObj) => obj.isNotEmpty).flatten
                .filter((pair: (JsPath, JsValue)) => pair._2.isObj)
                .forall((pair: (JsPath, JsValue)) => pair._2.toJsObj.isNotEmpty)
          }
          )
  }


  property("get the value of an object by path")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj
                .flatten
                .forall((pair: (JsPath, JsValue)) => obj(pair._1) == pair._2)
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
              val paths = obj.flatten.map((pair: (JsPath, JsValue)) => pair._1).reverse
              val result = obj.removedAll(paths)
              result.flatten.forall((pair: (JsPath, JsValue)) => pair._2.toJson.isEmpty)
          }
          )
  }


  property("map traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              obj.map((path: JsPath, value: JsValue) =>
                        if (obj(path) != value) throw  RuntimeException()
                        else value
                      ) == obj
          }
          )
  }

  property("mapKey traverses all the elements and passed every pair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              obj.mapKeys((path: JsPath, value: JsValue) =>
                           if (obj(path) != value) throw  RuntimeException()
                           else path.last.asKey.name
                          ) == obj
          }
          )
  }

  property("filterJsObj traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              obj.filterJsObj((path: JsPath, value: JsValue) =>
                                if (obj(path) != value) throw  RuntimeException()
                                else true
                              ) == obj
          }
          )
  }
  property("filter traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              obj.filter((path: JsPath, value: JsValue) =>
                           if (obj(path) != value) throw  RuntimeException()
                           else true
                         ) == obj
          }
          )
  }

  property("filterKey traverses all the elements and passed every jspair of the Json to the function")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              obj.filterKeys((path: JsPath, value: JsValue) =>
                              if (obj(path) != value) throw  RuntimeException()
                              else true
                             ) == obj
          }
          )
  }

  property("serialize obj into bytes")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              JsObjParser.parse(obj.serialize) == Right(obj)
          }
          )
  }


  property("serialize obj into output stream")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              val os = ByteArrayOutputStream()
              obj.serialize(os).apply()
              os.flush()
              JsObjParser.parse(os.toByteArray) == Right(obj)
          }
          )
  }

  property("parsers without spec")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              val string = obj.toString
              val prettyString = obj.toPrettyString

              JsObjParser.parse(string).contains(obj) &&
              JsObjParser.parse(string.getBytes).contains(obj) &&
              JsObjParser.parse(prettyString).contains(obj) &&
              JsObjParser.parse(prettyString.getBytes).contains(obj) &&
              JsObjParser.parse(ByteArrayInputStream(string.getBytes)) == Try(obj) &&
              JsObjParser.parse(ByteArrayInputStream(prettyString.getBytes)) == Try(obj)
          }
          )
  }
  property("parsers with a spec")
  {
    val objGen = JsObjGen("a" -> Gen.asciiPrintableStr,
                          "b" -> Gen.numStr,
                          "c" -> Arbitrary.arbitrary[Int],
                          "d" -> JsArrayGen.of(Gen.asciiPrintableStr),
                          "e" -> JsArrayGen.of(Arbitrary.arbitrary[BigDecimal])
                          )

    val spec = JsObjSpec("a" -> str,
                         "b" -> str,
                         "c" -> JsNumberSpecs.int,
                         "d" -> JsArraySpecs.arrayOfStr,
                         "e" -> JsArraySpecs.arrayOfDecimal,
                         )

    val parser = JsObjParser(spec)

    check(forAll(objGen
                 )
          {
            obj =>

              val string = obj.toString
              val prettyString = obj.toPrettyString
              parser.parse(string) == Right(obj) &&
              parser.parse(string.getBytes
                           ) == Right(obj) &&
              parser.parse(prettyString
                           ) == Right(obj) &&
              parser.parse(prettyString.getBytes
                           ) == Right(obj) &&
              parser.parse(ByteArrayInputStream(string.getBytes)

                           ) == Try(obj) &&
              parser.parse(ByteArrayInputStream(prettyString.getBytes)

                           ) == Try(obj)
          }
          )
  }
}
