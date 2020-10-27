package json.value.specs

import org.scalatest.FlatSpec
import json.value.{JsArray, JsNull}
import json.value.JsArray.empty
import json.value.Preamble._


class ConcatArraySpec extends FlatSpec
{
  val a: JsArray = JsArray(1,
                           2,
                           true,
                           JsNull
                           )

  "concat empty arrays" should "return an empty array" in {
    assert((empty concat empty) == empty)
  }
  "concat empty array with other" should "return the other" in {
    assert((empty concat a) == a)
    assert((a concat empty) == a)
  }
  "concat array with itself" should "return the same array" in {
    assert((a concat a) == a)
  }

  "concat multisets" should "return the same array" in {
    assert(a.concat(a,JsArray.TYPE.MULTISET)==a.appendedAll(a))
  }

  "concat sets" should "return the same array" in {
    assert(a.concat(a,JsArray.TYPE.SET)==a)
  }

  "concat list" should "return the same array" in {
    assert(a.concat(a,JsArray.TYPE.LIST)==a)
  }
}
