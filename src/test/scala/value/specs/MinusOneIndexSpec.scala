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
    assert(o("a" / -1) == JsInt(3))
    assert(o("a" / -1) == JsInt(3))
    assert(o("b" / -1) == JsNothing)
    assert(o("b" / -1) == JsNothing)
    assert(o("b" / 0) == JsNothing)
    assert(o("b" / -1) == JsNothing)
  }

  "insert value at index -1" should "replace the last element or append if the array is empty" in
  {

    val o = JsObj("a" -> JsArray(1,
                                 2,
                                 3
                                 ),
                  "b" -> JsArray.empty
                  )


    val a = o.insert("a" / -1,
                     5
                     ).insert("b" / -1,
                              0
                              )
    assert(a("a" / -1) == JsInt(5)
           )
    assert(a("a" / 2) == JsInt(5)
           )
    assert(a("a" / 0) == JsInt(1)
           )
    assert(a("a" / 1) == JsInt(2)
           )
    assert(a("b" / 0) == JsInt(0)
           )
    assert(a("b" / -1) == JsInt(0)
           )
  }

}
