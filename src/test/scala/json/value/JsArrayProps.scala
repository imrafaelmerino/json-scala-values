package json.value

import json.value.gen.RandomJsArrayGen
import org.junit.{Assert, Test}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}

class JsArrayProps
{

  def test(gen: Gen[JsArray],
           prop: Function[JsArray, Boolean],
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

  val gen = RandomJsArrayGen(arrLengthGen = Gen.choose(1,
                                                       10
                                                       ),
                             objSizeGen = Gen.choose(1,
                                                     10
                                                     )
                             )


  @Test
  def pairs_from_the_stream_of_an_array_are_collected_into_an_array_that_its_equal(): Unit =
  {
    test(gen,
         arr =>
         {
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

  @Test
  def an_array_is_a_set_of_path_and_json_pairs(): Unit =
  {
    test(RandomJsArrayGen(arrLengthGen = Gen.choose(1,
                                                    20
                                                    )
                          ),
         x =>
         {
           val pairs = x.flatten
           x == JsArray(pairs.head,
                        pairs.tail: _*
                        )
         }
         )
  }

  @Test
  def inserted_function_is_honest_it_always_inserts_at_the_specified_path(): Unit =
  {
    test(RandomJsArrayGen(),
         x =>
         {
           @scala.annotation.tailrec
           def insertPairs(pairs: LazyList[(JsPath, JsValue)],
                           y: JsArray
                          ): JsArray =
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
                       JsArray.empty
                       ) == x
         }

         )
  }

  @Test
  def apply_function_returns_the_element_located_at_the_specified_path(): Unit =
  {

    test(RandomJsArrayGen(arrLengthGen = Gen.choose(1,
                                                    20
                                                    )
                          ),
         x => x.flatten.forall((p: (JsPath, JsValue)) => x(p._1) == p._2)
         )
  }


  @Test
  def head_tail_returns_the_same_object(): Unit =
  {
    test(RandomJsArrayGen(arrLengthGen = Gen.choose(1,
                                                    20
                                                    )
                          ),
         x => x.tail.prepended(x.head) == x
         )
  }

  @Test
  def init_last_returns_the_same_object(): Unit =
  {
    test(RandomJsArrayGen(arrLengthGen = Gen.choose(1,
                                                    20
                                                    )
                          ),
         x => x.init.appended(x.last) == x
         )
  }

  @Test
  def map_traverses_the_whole_array(): Unit =
  {
    test(RandomJsArrayGen(),
         x => x.mapAll((path: JsPath, value: JsValue) =>
                         if (x(path) != value) throw RuntimeException()
                         else value
                       ) == x

         )
  }

  @Test
  def mapKeys_traverses_the_whole_array(): Unit =
  {
    test(RandomJsArrayGen(),
         x => x.mapAllKeys((path: JsPath, value: JsValue) =>
                             if (x(path) != value) throw RuntimeException()
                             else path.last.asKey.name
                           ) == x
         )

  }

  @Test
  def filter_traverses_the_whole_array(): Unit =
  {
    test(RandomJsArrayGen(),
         x => x.filterAll((path: JsPath, value: JsValue) =>
                            if (x(path) != value) false
                            else true
                          ) == x
         )
  }

  @Test
  def filterKeys_traverses_the_whole_array(): Unit =
  {
    test(RandomJsArrayGen(),
         x => x.filterAllKeys((path: JsPath, value: JsValue) =>
                              {
                                if (x(path) != value) false
                                else true
                              }
                              ) == x
         )
  }


}
