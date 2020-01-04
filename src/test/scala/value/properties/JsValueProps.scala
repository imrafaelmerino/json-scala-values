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
              Try(nothing.asJsArray).isFailure &&
              Try(nothing.asJsBigDec).isFailure &&
              Try(nothing.asJsBigInt).isFailure &&
              Try(nothing.asJsBool).isFailure &&
              Try(nothing.asJsInt).isFailure &&
              Try(nothing.asJsLong).isFailure &&
              Try(nothing.asJsNull).isFailure &&
              Try(nothing.asJsNumber).isFailure &&
              Try(nothing.asJsObj).isFailure &&
              Try(nothing.asJsDouble).isFailure &&
              Try(nothing.asJsStr).isFailure
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

              jsInt.asJsLong == jsInt &&
              jsInt.asJsBigInt == jsInt &&
              jsInt.asJsBigDec == jsInt &&
              jsInt.asJsDouble == jsInt &&
              jsInt.asJsLong.hashCode == jsInt.hashCode &&
              jsInt.asJsBigInt.hashCode == jsInt.hashCode &&
              jsInt.asJsBigDec.hashCode == jsInt.hashCode &&
              jsInt.asJsDouble.hashCode == jsInt.hashCode

          }
          )
  }


  property("JsLong can be converted into any other JsNumber but JsInt and JsDouble")
  {
    check(forAll(Arbitrary.arbitrary[Long])
          {
            i =>
              val jsLong = JsLong(i)
              jsLong.asJsBigInt == jsLong &&
              jsLong.asJsBigDec == jsLong &&
              jsLong.asJsBigInt.hashCode == jsLong.hashCode &&
              jsLong.asJsBigDec.hashCode == jsLong.hashCode
          }
          )
  }

  property("JsDouble can be converted into JsBigDec")
  {
    check(forAll(Arbitrary.arbitrary[Double])
          {
            i =>
              val jsDouble = JsDouble(i)
              jsDouble.asJsBigDec == jsDouble &&
              jsDouble.asJsBigDec.hashCode == jsDouble.hashCode
          }
          )
  }

  property("JsBigInt can be converted into JsBigDec")
  {
    check(forAll(Arbitrary.arbitrary[BigInt])
          {
            i =>
              val jsBigInt = JsBigInt(i)
              jsBigInt.asJsBigDec == jsBigInt &&
              jsBigInt.asJsBigDec.hashCode == jsBigInt.hashCode
          }
          )
  }
}

