package value.specs

import scala.language.implicitConversions
import org.junit.{Assert, Test}
import value.Preamble._
import value._

class JsValueSpec
{

  @Test
  def test_isXXX_functions_should_evaluate_the_predicate_or_return_false_in(): Unit =
  {

    val jsInt = JsInt(1)
    val jsLong = JsLong(1)
    val jsStr = JsStr("hi")
    val jsObj = JsObj("a" -> 1)
    val jsArr = JsArray("a")
    val jsDouble = JsDouble(1.5)
    val jsDecimal = JsBigDec(1.5)
    val jsBigInt = JsBigInt(1)

    Assert.assertTrue(jsDouble.isDouble)
    Assert.assertTrue(jsDouble.isDouble(i => i == 1.5))

    Assert.assertTrue(jsDecimal.isDecimal)
    Assert.assertTrue(jsDecimal.isDecimal(i => i == 1.5))

    Assert.assertTrue(jsBigInt.isIntegral)
    Assert.assertTrue(jsBigInt.isIntegral(i => i == BigInt(1)))

    Assert.assertTrue(jsInt.isInt)
    Assert.assertTrue(jsInt.isInt(i => i == 1))

    Assert.assertTrue(jsLong.isLong)
    Assert.assertTrue(jsLong.isLong(i => i == 1))

    Assert.assertTrue(jsStr.isStr)
    Assert.assertTrue(jsStr.isStr(i => i.startsWith("h")))

    Assert.assertTrue(jsStr.isNotJson)

    Assert.assertTrue(jsObj.isObj)
    Assert.assertTrue(jsObj.isObj(i => i.size == 1))

    Assert.assertTrue(jsObj.isJson)
    Assert.assertTrue(jsObj.isJson(i => i.size == 1))

    Assert.assertTrue(jsArr.isArr)
    Assert.assertTrue(jsArr.isArr(i => i.size == 1))

  }

  @Test
  def test_TRUE_and_FALSE_should_be_the_only_instances_of_JsBool_in(): Unit =
  {

    Assert.assertTrue(JsBool(true) == TRUE)
    Assert.assertTrue(JsBool(false) == FALSE)
  }

  @Test
  def test_JsStr_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {

    val str = JsStr("a")
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsArray
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsBigDec
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsBigInt
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsBool
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsDouble
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsInt
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsLong
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsNull
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsNumber
    //  }
    //Assert.assertTrueThrows[UserError]
    //  {
    //    str.toJsObj
    //  }
  }

  @Test
  def test_JsStr_isXXX_methods_should_return_false_in(): Unit =
  {

    val str = JsStr("a")
    Assert.assertTrue(!str.isArr)
    Assert.assertTrue(!str.isBigDec)
    Assert.assertTrue(!str.isDecimal)
    Assert.assertTrue(!str.isBigInt)
    Assert.assertTrue(!str.isIntegral)
    Assert.assertTrue(!str.isBool)
    Assert.assertTrue(!str.isDouble)
    Assert.assertTrue(!str.isInt)
    Assert.assertTrue(!str.isLong)
    Assert.assertTrue(!str.isNull)
    Assert.assertTrue(!str.isNumber)
    Assert.assertTrue(!str.isObj)
  }


  @Test
  def test_toJsLong_should__should_tobey_widening_primitive_conversion_rules_in(): Unit =
  {
    Assert.assertTrue(JsLong(1L) == JsInt(1).toJsLong)
  }

  @Test
  def test_JsLong_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {

    val long = JsLong(1)
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        long.toJsArray
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        long.toJsBool
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        long.toJsInt
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        long.toJsNull
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        long.toJsObj
    //      }
  }

  @Test
  def test_JsLong_isXXX_methods_should_return_false_in(): Unit =
  {

    val long = JsLong(1L)
    Assert.assertTrue(!long.isArr)
    Assert.assertTrue(!long.isBigDec)
    Assert.assertTrue(!long.isDecimal)
    Assert.assertTrue(!long.isBigInt)
    Assert.assertTrue(!long.isBool)
    Assert.assertTrue(!long.isDouble)
    Assert.assertTrue(!long.isInt)
    Assert.assertTrue(!long.isNull)
    Assert.assertTrue(!long.isObj)
  }


  @Test
  def test_JsInt_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {

    val int = JsInt(1)
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        int.toJsArray
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        int.toJsBool
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        int.toJsNull
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        int.toJsObj
    //      }
  }

  @Test
  def test_JsInt_isXXX_methods_should_return_false_in(): Unit =
  {

    val int = JsInt(1)
    Assert.assertTrue(!int.isArr)
    Assert.assertTrue(!int.isBigDec)
    Assert.assertTrue(!int.isDecimal)
    Assert.assertTrue(!int.isBigInt)
    Assert.assertTrue(!int.isBool)
    Assert.assertTrue(!int.isDouble)
    Assert.assertTrue(!int.isLong)
    Assert.assertTrue(!int.isNull)
    Assert.assertTrue(!int.isObj)
  }

  @Test
  def test_JsNull_isXXX_methods_should_return_false_in(): Unit =
  {

    Assert.assertTrue(!JsNull.isArr)
    Assert.assertTrue(!JsNull.isBigDec)
    Assert.assertTrue(!JsNull.isDecimal)
    Assert.assertTrue(!JsNull.isBigInt)
    Assert.assertTrue(!JsNull.isIntegral)
    Assert.assertTrue(!JsNull.isBool)
    Assert.assertTrue(!JsNull.isDouble)
    Assert.assertTrue(!JsNull.isInt)
    Assert.assertTrue(!JsNull.isLong)
    Assert.assertTrue(!JsNull.isNumber)
    Assert.assertTrue(!JsNull.isObj)
    Assert.assertTrue(!JsNull.isStr)
  }

  @Test
  def test_JsNothing_isXXX_methods_should_return_false_in(): Unit =
  {

    Assert.assertTrue(!JsNothing.isArr)
    Assert.assertTrue(!JsNothing.isBigDec)
    Assert.assertTrue(!JsNothing.isDecimal)
    Assert.assertTrue(!JsNothing.isBigInt)
    Assert.assertTrue(!JsNothing.isIntegral)
    Assert.assertTrue(!JsNothing.isBool)
    Assert.assertTrue(!JsNothing.isDouble)
    Assert.assertTrue(!JsNothing.isInt)
    Assert.assertTrue(!JsNothing.isLong)
    Assert.assertTrue(!JsNothing.isNull)
    Assert.assertTrue(!JsNothing.isNumber)
    Assert.assertTrue(!JsNothing.isObj)
    Assert.assertTrue(!JsNothing.isStr)
  }

  @Test
  def test_JsNull_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJson
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsArray
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsBigDec
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsBigInt
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsBool
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsDouble
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsInt
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsLong
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsNumber
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsObj
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsNull.toJsStr
    //      }

  }

  @Test
  def test_JsDouble_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {
    val d = JsDouble(1.2)
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsArray
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJson
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsBigInt
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsBool
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsInt
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsLong
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsObj
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        d.toJsStr
    //      }

  }

  @Test
  def test_JsNothing_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {

    //Assert.assertTrueThrows[UserError]
    //{
    //JsNothing.toJsArray
    //}
    //Assert.assertTrueThrows[UserError]
    //{
    //JsNothing.toJsBigDec
    //}
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsBigInt
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsBool
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsDouble
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsInt
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsLong
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsNull
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsNumber
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsObj
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     JsNothing.toJsStr
    //   }

  }


  @Test
  def test_JsBool_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {

    val bool = JsBool(true)
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsArray
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsBigDec
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsBigInt
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsDouble
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsInt
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsLong
    //     }

    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsNumber
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsObj
    //     }
    //   Assert.assertTrueThrows[UserError]
    //     {
    //       bool.toJsStr
    //     }

  }

  @Test
  def test_JsBool_isXXX_methods_should_return_false_in(): Unit =
  {
    val bool = JsBool(true)

    Assert.assertTrue(!bool.isArr)
    Assert.assertTrue(!bool.isBigDec)
    Assert.assertTrue(!bool.isDecimal)
    Assert.assertTrue(!bool.isBigInt)
    Assert.assertTrue(!bool.isIntegral)
    Assert.assertTrue(!bool.isDouble)
    Assert.assertTrue(!bool.isInt)
    Assert.assertTrue(!bool.isLong)
    Assert.assertTrue(!bool.isNull)
    Assert.assertTrue(!bool.isNumber)
    Assert.assertTrue(!bool.isObj)
    Assert.assertTrue(!bool.isStr)
  }

  @Test
  def test_toJsDouble_should__should_tobey_widening_primitive_conversion_rules_in(): Unit =
  {
    Assert.assertTrue(JsDouble(1L) == JsInt(1).toJsDouble)
    Assert.assertTrue(JsDouble(1L) == JsLong(1L).toJsDouble)
    Assert.assertTrue(JsDouble(1L) == JsDouble(1L).toJsDouble)
  }

  @Test
  def test_toJsBigInt_should__should_tobey_widening_primitive_conversion_rules_in(): Unit =
  {
    Assert.assertTrue(JsBigInt(1L) == JsInt(1).toJsBigInt)
    Assert.assertTrue(JsBigInt(1L) == JsLong(1L).toJsBigInt)
    Assert.assertTrue(JsBigInt(1L) == JsBigInt(1L).toJsBigInt)
  }

  @Test
  def test_toJsBigDec_should__should_tobey_widening_primitive_conversion_rules_in(): Unit =
  {
    Assert.assertTrue(JsBigDec(1) == JsInt(1).toJsBigDec)
    Assert.assertTrue(JsBigDec(1L) == JsLong(1L).toJsBigDec)
    Assert.assertTrue(JsBigDec(1L) == JsBigInt(1L).toJsBigDec)
    Assert.assertTrue(JsBigDec(1L) == JsDouble(1L).toJsBigDec)
    Assert.assertTrue(JsBigDec(1L) == JsBigDec(1L).toJsBigDec)
  }


  @Test
  def test_JsDouble_isXXX_methods_should_return_false_in(): Unit =
  {
    val double = JsDouble(1.2)

    Assert.assertTrue(!double.isArr)
    Assert.assertTrue(!double.isBigDec)
    Assert.assertTrue(!double.isBigInt)
    Assert.assertTrue(!double.isIntegral)
    Assert.assertTrue(!double.isBool)
    Assert.assertTrue(!double.isInt)
    Assert.assertTrue(!double.isLong)
    Assert.assertTrue(!double.isNull)
    Assert.assertTrue(!double.isObj)
    Assert.assertTrue(!double.isStr)
  }

  @Test
  def test_JsBigDec_isXXX_methods_should_return_false_in(): Unit =
  {
    val bd = JsBigDec(1.2)

    Assert.assertTrue(!bd.isArr)
    Assert.assertTrue(!bd.isDouble)
    Assert.assertTrue(!bd.isBigInt)
    Assert.assertTrue(!bd.isIntegral)
    Assert.assertTrue(!bd.isBool)
    Assert.assertTrue(!bd.isInt)
    Assert.assertTrue(!bd.isLong)
    Assert.assertTrue(!bd.isNull)
    Assert.assertTrue(!bd.isObj)
    Assert.assertTrue(!bd.isStr)
  }

  @Test
  def test_JsBigInt_isXXX_methods_should_return_false_in(): Unit =
  {
    val bi = JsBigInt(Long.MaxValue)

    Assert.assertTrue(!bi.isArr)
    Assert.assertTrue(!bi.isDouble)
    Assert.assertTrue(!bi.isBigDec)
    Assert.assertTrue(!bi.isBool)
    Assert.assertTrue(!bi.isInt)
    Assert.assertTrue(!bi.isLong)
    Assert.assertTrue(!bi.isNull)
    Assert.assertTrue(!bi.isObj)
    Assert.assertTrue(!bi.isStr)
  }

  @Test
  def test_JsBigDec_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {
    val bd = BigDecimal(1.5)
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsArray
    //   }

    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsBigInt
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsBool
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsDouble
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsInt
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsLong
    //   }

    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsObj
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsStr
    //   }
    // Assert.assertTrueThrows[UserError]
    //   {
    //     bd.toJsNull
    //   }

  }

  @Test
  def test_JsBigInt_toXXX_methods_should_throw_an_UserError_in(): Unit =
  {
    val bi = JsBigDec(1)
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsArray
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsBool
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsDouble
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsInt
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsLong
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsObj
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsStr
    //      }
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        bi.toJsNull
    //      }

  }
}
