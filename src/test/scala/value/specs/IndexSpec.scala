package value.specs
import org.junit.Test
import org.junit.Assert
import value.{Index, UserError}
import scala.language.implicitConversions

class IndexSpec
{

  @Test
  def test_asKey_should_throws_a_user_error():Unit =
  {
//TODO
//    assertThrows[UserError]
//      {
//        Index(0).asKey
//      }
  }

  @Test
  def test_mapKey_should_throws_a_user_error():Unit =
  {
    //TODO
//    assertThrows[UserError]
//      {
//        Index(0).mapKey((s: String) => s.toLowerCase())
//      }
  }
  @Test
  def test_isKey_should_returns_false():Unit =
  {
    Assert.assertTrue(!Index(0).isKey)
    Assert.assertTrue(!Index(0).isKey((s : String) => s == ""))
  }
  @Test
  def test_isIndex_should_returns_true():Unit =
  {
    Assert.assertTrue(Index(0).isIndex)
    Assert.assertTrue(Index(0).isIndex((i: Int) => i == 0))
  }
}
