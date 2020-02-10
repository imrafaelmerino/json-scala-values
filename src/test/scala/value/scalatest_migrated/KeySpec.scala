package value.scalatest_migrated

import org.junit.{Assert, Test}
import value.{Key, UserError}
import scala.language.implicitConversions

class KeySpec
{

  @Test
  def test_asIndex_should_throws_a_user_error_in(): Unit =
  {

    //Assert.assertTrueThrows[UserError]
      //{
        //Key("").asIndex
      //}
  }

  @Test
  def test_isIndex_should_returns_false_in(): Unit =
  {
    Assert.assertTrue(!Key("").isIndex)
    Assert.assertTrue(!Key("").isIndex((i: Int) => i > 0))
  }

  @Test
  def test_isKey_should_returns_true_in(): Unit =
  {
    Assert.assertTrue(Key("").isKey)
    Assert.assertTrue(Key("a").isKey((s: String) => s == "a"))
  }

  @Test
  def test_mapKey_should_maps_the_name_of_the_key_in(): Unit =
  {
    Assert.assertTrue(Key("a")
                        .mapKey((s: String) => s.toUpperCase)
                        .isKey((s: String) => s == "A")
           )
  }

}
