package json.value.specs

import org.scalatest.FlatSpec
import json.value.JsObj
import json.value.JsObj.empty
import json.value.Preamble._


class ConcatObjSpec extends FlatSpec
{
  val a = JsObj("a" -> 1,
                "b" -> 2,
                "c" -> true
                )

  "empty concat with empty" should  "return empty" in
  {
    assert((empty concat empty) == empty)
  }

  "empty concat with object" should  "return the object" in
  {
    assert((empty concat a) == a)
    assert((a concat empty) == a)
  }

  "object concat with itself" should  "return the same object" in
  {
    assert((a concat a) == a)
  }

}
