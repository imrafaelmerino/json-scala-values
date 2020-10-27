package json.value

import json.value.JsObjParser
import json.value.Preamble.{_, given _}
import json.value.gen.JsArrayGen.noneEmptyOf
import json.value.gen.Preamble.{_, given _}
import json.value.gen.{JsArrayGen, JsObjGen, RandomJsObjGen}
import json.value.spec.JsArraySpecs._
import json.value.spec.JsBoolSpecs.bool
import json.value.spec.JsNumberSpecs._
import json.value.spec.JsObjSpecs.objSuchThat
import json.value.spec.JsSpecs.any
import json.value.spec.JsStrSpecs._
import json.value.spec.Preamble.{_, given _}
import json.value.spec.{Invalid, JsArraySpec, JsObjSpec, Valid}
import org.junit.{Assert, Test}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}

import scala.language.implicitConversions

class JsObjProps
{


  def test(gen  : Gen[JsObj],
           prop : Function[JsObj, Boolean],
           times: Int = 1000
          ): Unit =
  {

    for (n <- 1 to times)
    {
      val json = gen.sample.get
      val success = prop(json)
      if (!success) println("Test failed with Json: \n" + json.toPrettyString)
      Assert.assertTrue(success);
    }

  }


  @Test
  def if_two_object_are_equals_they_have_the_same_hashcode(): Unit =
  {
    test(
      RandomJsObjGen(),
      x =>
      {
        val x = RandomJsObjGen().sample.get
        val either = JsObjParser.parse(x.toString)
        either.contains(x) && either.exists(_.hashCode() == x.hashCode())
      }
      )
  }

  @Test
  def an_object_is_a_set_of_path_and_json_value_pairs(): Unit =
  {
    test(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                20
                                                )
                        ),
         x => x == JsObj(x.flatten: _*)
         )
  }

  @Test
  def inserted_function_is_honest_and_always_inserts_at_the_specified_path(): Unit =
  {
    test(RandomJsObjGen(),
         x =>
         {
           @scala.annotation.tailrec
           def insertPairs(pairs  : LazyList[(JsPath, JsValue)],
                           y: JsObj
                          ): JsObj =
           {
             if (pairs.isEmpty) y else
             {
               val head = pairs.head
               insertPairs(pairs.tail,
                           y.inserted(head._1,
                                      head._2
                                      )
                           )
             }
           }

           insertPairs(x.flatten,
                       JsObj.empty
                       ) == x
         }
         )
  }

  @Test
  def apply_function_returns_the_element_located_at_the_specified_path(): Unit =
  {
    test(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                20
                                                )
                        ),
         x => x.flatten.forall((p: (JsPath, JsValue)) => x(p._1) == p._2)
         )
  }


  @Test
  def head_and_tail_returns_the_same_object(): Unit =
  {
    test(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                20
                                                )
                        ),
         x => x.tail.inserted(x.head._1,
                              x.head._2
                              ) == x
         )
  }


  @Test
  def init_last_returns_the_same_object(): Unit =
  {
    test(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                20
                                                )
                        ),
         x => x.init.inserted(x.last._1,
                              x.last._2
                              ) == x
         )
  }


  @Test
  def array_spec(): Unit =
  {
    val gen = JsObjGen("b" -> noneEmptyOf(Arbitrary.arbitrary[Long]),
                       "c" -> noneEmptyOf(Arbitrary.arbitrary[Int]),
                       "d" -> noneEmptyOf(Arbitrary.arbitrary[BigInt]),
                       "e" -> noneEmptyOf(Arbitrary.arbitrary[BigDecimal]),
                       "f" -> JsArrayGen(Gen.choose[Int](1,
                                                         10
                                                         ),
                                         Arbitrary.arbitrary[Boolean],
                                         Gen.oneOf("red",
                                                   "blue",
                                                   "pink",
                                                   "yellow"
                                                   ),
                                         RandomJsObjGen(),
                                         JsObjGen("h" -> "i",
                                                  "j" -> true,
                                                  "m" -> 1
                                                  )
                                         ),
                       "n" -> JsArray(1,
                                      2,
                                      3
                                      ),
                       "s" -> JsArrayGen(BigDecimal(1.5),
                                         BigInt(10),
                                         1.5
                                         ),
                       "t" -> JsArray(1,
                                      1.5,
                                      2L
                                      )
                       )
    test(gen,
         x => x.validate(JsObjSpec("b" -> arrayOfLongSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                   "c" -> arrayOfIntSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                   "d" -> arrayOfIntegralSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                   "e" -> arrayOfDecimalSuchThat((a: JsArray) => if (a.size > 0) Valid else Invalid("")),
                                   "f" -> JsArraySpec(intSuchThat((i: Int) => if (i < 11 && i > 0) Valid else Invalid("")),
                                                      bool,
                                                      strSuchThat((s: String) => if (s.length > 2 || s.length < 7) Valid else Invalid("length not in [3,6]")),
                                                      any,
                                                      objSuchThat((o: JsObj) => if (o.containsKey("h") && o.size == 3) Valid else Invalid(""))
                                                      ),
                                   "n" -> arraySuchThat((array: JsArray) => if (array.length() == 3) Valid else Invalid("")),
                                   "s" -> JsArraySpec(decimalSuchThat((bd: BigDecimal) => if (bd < 5) Valid else Invalid("greater than 5")),
                                                      integralSuchThat((bd: BigInt) => if (bd > 5) Valid else Invalid("lower than 5")),
                                                      decimalSuchThat((bd: BigDecimal) => if (bd > 0) Valid else Invalid("lower than zero"))
                                                      ),
                                   "t" -> arrayOfNumberSuchThat((a: JsArray) => if (a.length() > 1 && a.length() < 5) Valid else Invalid(""))
                                   )
                         ).isEmpty
         )
  }


  @Test
  def map_traverses_the_whole_object(): Unit =
  {
    test(RandomJsObjGen(),
         x => x.mapAll((path: JsPath, value: JsValue) =>
                         if (x(path) != value) throw RuntimeException()
                         else value
                       ) == x
         )
  }


  @Test
  def mapKeys_traverses_the_whole_object(): Unit =
  {
    test(RandomJsObjGen(),
         x => x.mapAllKeys((path: JsPath, value: JsValue) =>
                             if (x(path) != value) throw RuntimeException()
                             else path.last.asKey.name
                           ) == x
         )
  }

  @Test
  def filter_traverses_the_whole_object(): Unit =
  {

    test(RandomJsObjGen(),
         x => x.filterAll((path: JsPath, value: JsValue) =>
                            if (x(path) != value) false
                            else true
                          ) == x
         )
  }

  @Test
  def filterKeys_traverses_the_whole_object(): Unit =
  {
    test(RandomJsObjGen(),
         x => x.filterAllKeys((path: JsPath, value: JsValue) =>
                                if (x(path) != value) false
                                else true
                              ) == x
         )

  }

  @Test
  def obj_satisfies_spec(): Unit =
  {
    val json = JsObjGen("a" -> Arbitrary.arbitrary[String],
                        "b" -> Arbitrary.arbitrary[Int],
                        "c" -> Gen.oneOf(true,
                                         false
                                         )
                        )

    test(json,
         x => x.validate(JsObjSpec("a" -> str,
                                   "b" -> int,
                                   "c" -> bool
                                   )
                         ).isEmpty
         )

  }

  @Test
  def accessors(): Unit =
  {
    test(RandomJsObjGen(),
         obj => obj.flatten.forall((p: (JsPath, JsValue)) =>
                                   {
                                     obj(p._1) == p._2
                                   }
                                   )
         )
  }

}
