package value.specs

import org.scalatest.FlatSpec
import value.Preamble.{int2JsPath, str2JsPath}
import value.{Index, Key}


class JsPathSpec extends FlatSpec
{

  "appending path2 to path1" should "return a path which prefix is path1 and suffix is path2" in
  {

    val path1 = "a" / "b"
    val path2 = "c" / "d"

    assert((path1 / path2).head == Key("a"))
    assert((path1 / path2).last == Key("d"))
    assert((path1 / path2).length == 4)

    val path3 = 0 / "a" / 0
    val path4 = "c" / "b" / 1

    assert((path3 / path4).head == Index(0))
    assert((path3 / path4).last == Index(1))
    assert((path3 / path4).length == 6)
  }

  "prepending path2 to path1" should "return a path which prefix is path2 and suffix is path1" in
  {

    val path1 = "a" / "b"
    val path2 = "c" / "d"
    assert((path1 \ path2).head == Key("c"))
    assert((path1 \ path2).last == Key("b"))
    assert((path1 \ path2).length == 4)

    val path3 = 0 / "a" / 0
    val path4 = "c" / "b" / 1

    assert((path3 \ path4).head == Key("c"))
    assert((path3 \ path4).last == Index(0))
    assert((path3 \ path4).length == 6)
  }

}
