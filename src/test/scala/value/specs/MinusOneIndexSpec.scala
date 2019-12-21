package value.specs

import org.scalatest.FlatSpec
import value.{JsArray, JsInt, JsNothing, JsObj}
import value.Preamble._

class MinusOneIndexSpec extends FlatSpec
{

  "getting value located at index -1" should "return the last element" in
  {


    val o = JsObj("a" -> JsArray(1,
                                 2,
                                 3
                                 ),
                  "b" -> JsArray.empty
                  )

    assert(o("a" / -1) == JsInt(3))
    assert(o.int("a" / -1) == Some(3))
    assert(o.get("a" / -1) == Some(JsInt(3)))
    assert(o("b" / -1) == JsNothing)
    assert(o.int("b" / -1) == Option.empty)
    assert(o.int("b" / 0) == Option.empty)
    assert(o.get("b" / -1) == Option.empty)
  }

  "insert value at index -1" should "replace the last element or append if the array is empty" in
  {

    val o = JsObj("a" -> JsArray(1,
                                 2,
                                 3
                                 ),
                  "b" -> JsArray.empty
                  )


    val a = o.inserted("a" / -1,
                       5
                       ).inserted("b" / -1,
                                  0
                                  )
    assert(a.int("a" / -1) == Some(5)
           )
    assert(a.int("a" / 2) == Some(5)
           )
    assert(a.int("a" / 0) == Some(1)
           )
    assert(a.int("a" / 1) == Some(2)
           )
    assert(a.int("b" / 0) == Some(0)
           )
    assert(a.int("b" / -1) == Some(0)
           )
  }

  "update value at index -1" should "replace the last element of a none empty array" in
  {

    val o = JsObj("a" -> JsArray(1,
                                 2,
                                 3
                                 ),
                  "b" -> JsArray.empty
                  )


    val a = o.updated("a" / -1,
                       5
                       )
    assert(a.int("a" / -1) == Some(5)
           )
    assert(a.int("a" / 2) == Some(5)
           )
    assert(a.int("a" / 0) == Some(1)
           )
    assert(a.int("a" / 1) == Some(2)
           )

  }

}
