package value.properties

import java.io.ByteArrayInputStream

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
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


  property("pairs from the stream of an object are all inserted in an empty object, producing the same object")
  {
    check(forAll(gen)
          { obj =>
            var acc = JsObj()
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
              obj.flatten.forall(p=> obj.containsPath(p._1))
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
            result == JsObj() || result.flatten.forall(p => p._2 match
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
            obj.flatten.forall(pair => obj.updated(pair._1,
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
            val string = obj.toPrettyString
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
            val mapped = obj.map((_, value) => value.asJsStr.map(string => s"$string?"),
                                 (_, value) => value.isStr
                                 )
            mapped.flatten
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
                                                       (_, value) => value.asJsInt.value,
                                                       _ + _
                                                       )

            val sum: Int = obj.flatten
              .filter((pair: (JsPath, JsValue)) => pair._2.isInt)
              .map((pair: (JsPath, JsValue)) => pair._2.asJsInt.value)
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
              obj.mapKey((path: JsPath, _: JsValue) => path.last.asKey.name + "!")
                .flatten
                .filter((pair: (JsPath, JsValue)) => pair._1.last.isKey)
                .forall((pair: (JsPath, JsValue)) => pair._1.last.isKey(_.endsWith("!"))) &&
              obj.mapKey((key: String) => key + "!")
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
              obj.filterKey((_: JsPath, value: JsValue) => value.isNotNumber)
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
    check(forAll(JsObjGen("a" -> JsObj(),
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
              obj.filterJsObj((_: JsPath, obj: JsObj) => obj.isNotEmpty).flatten
                .filter((pair: (JsPath, JsValue)) => pair._2.isObj)
                .forall((pair: (JsPath, JsValue)) => pair._2.asJsObj.isNotEmpty) &&
              obj.filterJsObj((obj: JsObj) => obj.isNotEmpty).flatten
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
                .flatten
                .forall((pair: (JsPath, JsValue)) => obj.get(pair._1).contains(pair._2))
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
              val paths = obj.flatten.map((pair: (JsPath, JsValue)) => pair._1).reverse
              val result = obj.removedAll(paths)
              result.flatten.forall((pair: (JsPath, JsValue)) => pair._2.asJson.isEmpty)
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
                        if (obj(path) != value) throw new RuntimeException
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
              obj.mapKey((path: JsPath, value: JsValue) =>
                           if (obj(path) != value) throw new RuntimeException
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
                                if (obj(path) != value) throw new RuntimeException
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
              obj.filter((path        : JsPath, value: JsValue) =>
                           if (obj(path) != value) throw new RuntimeException
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
              obj.filterKey((path        : JsPath, value: JsValue) =>
                              if (obj(path) != value) throw new RuntimeException
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
             JsObj.parse(obj.serialize) == Try(obj)
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
              val os = new ByteOutputStream()
              obj.serialize(os).apply()
              os.flush()
              JsObj.parse(os.getBytes) == Try(obj)
          }
          )
  }

  property("getting primitives out of a JsObj")
  {
    val objGen = RandomJsObjGen()
    check(forAll(objGen
                 )
          {
            obj =>
              obj.flatten.forall((p: (JsPath, JsValue)) =>
                                 {
                                   p._2 match
                                   {
                                     case JsBool(value) => obj.bool(p._1).get == value
                                     case JsNull => obj(p._1) == JsNull
                                     case number: JsNumber => number match
                                     {
                                       case JsInt(value) => obj.int(p._1).get == value
                                       case JsDouble(value) => obj.double(p._1).get == value
                                       case JsLong(value) => obj.long(p._1).get == value
                                       case JsBigDec(value) => obj.bigDecimal(p._1).get == value
                                       case JsBigInt(value) => obj.bigInt(p._1).get == value
                                     }
                                     case JsStr(value) => obj.string(p._1).get == value
                                     case json: Json[_] => json match
                                     {
                                       case a: JsArray => obj.array(p._1).get == a
                                       case o: JsObj => obj.obj(p._1).get == o

                                     }
                                     case _ => false
                                   }
                                 }
                                 )
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
              JsObj.parse(string).get == obj &&
              JsObj.parse(string.getBytes).get == obj &&
              JsObj.parse(prettyString).get == obj &&
              JsObj.parse(prettyString.getBytes).get == obj &&
              JsObj.parse(new ByteArrayInputStream(string.getBytes)).get == obj &&
              JsObj.parse(new ByteArrayInputStream(prettyString.getBytes)).get == obj
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
              JsObj.parse(string,
                          parser
                          ) == Try(obj) &&
              JsObj.parse(string.getBytes,
                          parser
                          ) == Try(obj) &&
              JsObj.parse(prettyString,
                          parser
                          ) == Try(obj) &&
              JsObj.parse(prettyString.getBytes,
                          parser
                          ) == Try(obj) &&
              JsObj.parse(new ByteArrayInputStream(string.getBytes),
                          parser
                          ) == Try(obj) &&
              JsObj.parse(new ByteArrayInputStream(prettyString.getBytes),
                          parser
                          ) == Try(obj)
          }
          )
  }
}