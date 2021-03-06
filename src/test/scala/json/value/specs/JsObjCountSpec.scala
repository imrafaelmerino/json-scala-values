package json.value.specs

import org.scalatest.FlatSpec
import json.value.{JsInt, JsObj, JsPath, JsValue}


class JsObjCountSpec extends FlatSpec
{


  "count pairs in an empty object" should "return zero" in
  {
    val empty = JsObj()
    assert(empty.count((_:(JsPath,JsValue))=>true) == 0)
  }



}
