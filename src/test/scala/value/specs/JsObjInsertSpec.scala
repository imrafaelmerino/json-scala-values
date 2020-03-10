package value.specs

import org.scalatest.FlatSpec
import value.{JsInt, JsObj, JsValue}
import value.Preamble._


class JsObjInsertSpec extends FlatSpec
{


  "implicit conversion" should "be applied when inserting values in Json objects" in
  {

    val a = JsObj()
    val b = a.insert("a",
                     1
                     )
    assert(b("a") == JsInt(1))

  }
}
