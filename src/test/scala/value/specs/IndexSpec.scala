package value.specs

import org.scalatest.FlatSpec
import value.{Index, UserError}

class IndexSpec extends FlatSpec
{

  "asKey" should "throws a user error" in
  {

    assertThrows[UserError]
      {
        Index(0).asKey
      }
  }

  "mapKey" should "throws a user error" in
  {
    assertThrows[UserError]
      {
        Index(0).mapKey((s: String) => s.toLowerCase())
      }
  }

  "isKey" should "returns false" in
  {
    assert(!Index(0).isKey)
    assert(!Index(0).isKey((s : String) => s == ""))
  }

  "isIndex" should "returns true" in
  {
    assert(Index(0).isIndex)
    assert(Index(0).isIndex((i: Int) => i == 0))
  }
}
