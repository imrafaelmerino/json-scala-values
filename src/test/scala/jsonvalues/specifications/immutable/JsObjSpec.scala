package jsonvalues.specifications.immutable

import jsonvalues.{ImmutableJsGen, JsInt, JsNull, JsObj, Json}
import jsonvalues.immutable.Json
import jsonvalues.specifications.BasePropSpec
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import jsonvalues.JsPath./
import jsonvalues.Implicits._

class JsObjSpec extends BasePropSpec
{
  val gen = ImmutableJsGen(

    arrLengthGen = Gen.choose(1,
                              10
                              ),
    objSizeGen = Gen.choose(1,
                            10
                            )
    )


  property("pairs from the stream of an object are all inserted in an empty object, producing the same object")
  {
    check(forAll(gen.obj)
          { obj =>
            var acc = jsonvalues.immutable.JsObj()
            obj.toLazyListRec.foreach(p =>
                                      {
                                        acc = acc.inserted(p)
                                      }
                                      )
            acc == obj && acc.hashCode() == obj.hashCode()
          }
          )
  }

  property("removing by path an existing element returns a different object")
  {
    check(forAll(gen.obj)
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

    check(forAll(gen.obj)
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
    check(forAll(gen.obj)
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
    check(forAll(gen.obj)
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
    check(forAll(gen.obj)
          { obj =>
            val parsed: JsObj = JsObj.parse(obj.toString).get
            parsed == obj && obj.hashCode() == parsed.hashCode()
          }
          )
  }
}
