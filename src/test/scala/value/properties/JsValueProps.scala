package value.properties

import org.scalacheck.{Arbitrary, Gen}
import valuegen.{RandomJsArrayGen, RandomJsObjGen}
import org.scalacheck.Prop.forAll
import value.{JsBigInt, JsDouble, JsInt, JsLong, JsNothing, JsPath, JsValue, Json}

import scala.util.Try

class JsValueProps extends BasePropSpec
{
  val gen: Gen[Json[_]] = Gen.oneOf(RandomJsObjGen(),
                                    RandomJsArrayGen()
                                    )

  property("JsNothing can't be converted into any other value")
  {
    check(forAll(Gen.const(JsNothing))
          {
            nothing =>
              Try(nothing.toJsArray).isFailure &&
              Try(nothing.toJsBigDec).isFailure &&
              Try(nothing.toJsBigInt).isFailure &&
              Try(nothing.toJsBool).isFailure &&
              Try(nothing.toJsInt).isFailure &&
              Try(nothing.toJsLong).isFailure &&
              Try(nothing.toJsNull).isFailure &&
              Try(nothing.toJsNumber).isFailure &&
              Try(nothing.toJsObj).isFailure &&
              Try(nothing.toJsDouble).isFailure &&
              Try(nothing.toJsStr).isFailure
          }
          )
  }

  property("JsNothing is not equals to any other value")
  {
    check(forAll(Gen.const(JsNothing))
          {
            nothing =>
              !nothing.isArr &&
              !nothing.isBigDec &&
              !nothing.isBigInt &&
              !nothing.isBigDec &&
              !nothing.isBool &&
              !nothing.isDouble &&
              !nothing.isInt &&
              !nothing.isLong &&
              !nothing.isNull &&
              !nothing.isNumber &&
              !nothing.isObj &&
              !nothing.isStr
          }
          )
  }


  property("JsInt can be converted into any other JsNumber")
  {
    check(forAll(Arbitrary.arbitrary[Int])
          {
            i =>
              val jsInt = JsInt(i)

              jsInt.toJsLong == jsInt &&
              jsInt.toJsBigInt == jsInt &&
              jsInt.toJsBigDec == jsInt &&
              jsInt.toJsDouble == jsInt &&
              jsInt.toJsLong.hashCode == jsInt.hashCode &&
              jsInt.toJsBigInt.hashCode == jsInt.hashCode &&
              jsInt.toJsBigDec.hashCode == jsInt.hashCode &&
              jsInt.toJsDouble.hashCode == jsInt.hashCode

          }
          )
  }


  property("JsLong can be converted into any other JsNumber but JsInt and JsDouble")
  {
    check(forAll(Arbitrary.arbitrary[Long])
          {
            i =>
              val jsLong = JsLong(i)
              jsLong.toJsBigInt == jsLong &&
              jsLong.toJsBigDec == jsLong &&
              jsLong.toJsBigInt.hashCode == jsLong.hashCode &&
              jsLong.toJsBigDec.hashCode == jsLong.hashCode
          }
          )
  }

  property("JsDouble can be converted into JsBigDec")
  {
    check(forAll(Arbitrary.arbitrary[Double])
          {
            i =>
              val jsDouble = JsDouble(i)
              jsDouble.toJsBigDec == jsDouble &&
              jsDouble.toJsBigDec.hashCode == jsDouble.hashCode
          }
          )
  }

  property("JsBigInt can be converted into JsBigDec")
  {
    check(forAll(Arbitrary.arbitrary[BigInt])
          {
            i =>
              val jsBigInt = JsBigInt(i)
              jsBigInt.toJsBigDec == jsBigInt &&
              jsBigInt.toJsBigDec.hashCode == jsBigInt.hashCode
          }
          )
  }
}

