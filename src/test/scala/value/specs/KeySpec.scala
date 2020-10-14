package value.specs

import org.scalatest.FlatSpec
import value.{Key, UserError}

class KeySpec extends FlatSpec
{

  "asIndex" should "throws a user error" in
  {

    assertThrows[UserError]
      {
        Key("").asIndex
      }
  }

  "isIndex" should "returns false" in
  {
    assert(!Key("").isIndex)
    assert(!Key("").isIndex((i: Int) => i > 0))
  }

  "isKey" should "returns true" in
  {
    assert(Key("").isKey)
    assert(Key("a").isKey((s: String) => s == "a"))
  }

  "mapKey" should "maps the name of the key" in
  {
    assert(Key("a")
             .mapKey((s: String) => s.toUpperCase)
             .isKey((s: String) => s == "A")
           )
  }

}
