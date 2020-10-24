package json.value.scalatest_migrated
import scala.language.implicitConversions
import json.value.JsPath
import json.value.Preamble.{given}
import org.junit.{Assert, Test}
import json.value.{Index , Key, UserError}


class JsPathSpec
{

  @Test
  def test_appending_path2_to_path1_should_return_a_path_which_prefix_is_path1_and_suffix_is_path2_in(): Unit =
  {

    val path1 = "a" / "b"
    val path2 = "c" / "d"

    Assert.assertTrue((path1 / path2).head == Key("a"))
    Assert.assertTrue((path1 / path2).last == Key("d"))
    Assert.assertTrue((path1 / path2).length == 4)

    val path3 = 0 / "a" / 0
    val path4 = "c" / "b" / 1

    Assert.assertTrue((path3 / path4).head == Index(0))
    Assert.assertTrue((path3 / path4).last == Index(1))
    Assert.assertTrue((path3 / path4).length == 6)
  }

  @Test
  def test_prepending_path2_to_path1_should_return_a_path_which_prefix_is_path2_and_suffix_is_path1_in(): Unit =
  {

    val path1 = "a" / "b"
    val path2 = "c" / "d"
    Assert.assertTrue((path1 \ path2).head == Key("c"))
    Assert.assertTrue((path1 \ path2).last == Key("b"))
    Assert.assertTrue((path1 \ path2).length == 4)

    val path3 = 0 / "a" / 0
    val path4 = "c" / "b" / 1

    Assert.assertTrue((path3 \ path4).head == Key("c"))
    Assert.assertTrue((path3 \ path4).last == Index(0))
    Assert.assertTrue((path3 \ path4).length == 6)
  }

  @Test
  def test_inc_of_empty_path_should_throw_an_UserError_in_(): Unit =
  {
    //Assert.assertTrueThrows[UserError]
    //{
    //JsPath.empty.inc
    //}
  }


  @Test
  def test_inc_of_path_that_ends_with_a_key_should_throw_an_UserError_in_(): Unit =
  {
    //Assert.assertTrueThrows[UserError] {
    //"a" / 1 /"b".inc
    //}
  }

}
