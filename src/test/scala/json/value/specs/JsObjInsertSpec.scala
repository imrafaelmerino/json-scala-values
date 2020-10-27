package json.value.specs

import org.scalatest.FlatSpec
import json.value.{JsInt, JsObj, JsValue}
import json.value.Preamble._


class JsObjInsertSpec extends FlatSpec
{


  "implicit conversion" should "be applied when inserting values in Json objects" in
  {

    val a = JsObj()
    val b = a.inserted("a",
                       1
                       )
    assert(b("a") == JsInt(1))

  }
}
