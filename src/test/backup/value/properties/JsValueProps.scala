package value.properties

import org.scalacheck.{Arbitrary, Gen}
import valuegen.{RandomJsArrayGen, RandomJsGen, RandomJsObjGen}
import org.scalacheck.Prop.forAll
import value.{JsBigInt, JsDouble, JsInt, JsLong, JsNothing, JsPath, JsValue, Json}

import scala.util.Try

class JsValueProps extends BasePropSpec
{
  val gen: Gen[Json[_]] = Gen.oneOf(RandomJsObjGen(),
                                    RandomJsArrayGen()
                                    )
  property("asJsStr throws an user error when called on a non string value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair                 : (JsPath, JsValue)) => !pair._2.isStr)
                .forall((pair                                                             : (JsPath, JsValue)) => Try(pair._2.asJsStr).isFailure)
          }
          )
  }

  property("asJsObj throws an user error when called on a non object")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isObj)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsObj).isFailure)
          }
          )
  }

  property("asJsArray throws an user error when called on a non array")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isArr)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsArray).isFailure)
          }
          )
  }

  property("asJsInt throws an user error when called on a non integer value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isInt)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsInt).isFailure)
          }
          )
  }

  property("asJsLong throws an user error when called on a non long nor integer value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isLong && !pair._2.isInt)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsLong).isFailure)
          }
          )
  }

  property("asJsDouble throws an user error when called on a non double nor int nor long value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isDouble && !pair._2.isInt && !pair._2.isLong)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsDouble).isFailure)
          }
          )
  }

  property("asJsBigDec throws an user error when called on a non numerical value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isNumber)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsBigDec).isFailure)
          }
          )
  }

  property("asJsBigInt throws an user error when called on a non integral value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isIntegral)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsBigInt).isFailure)
          }
          )
  }

  property("asJsBool throws an user error when called on a non boolean value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isBool)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsBool).isFailure)
          }
          )
  }

  property("asJsNull throws an user error when called on a non null value")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isNull)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsNull).isFailure)
          }
          )
  }


  property("asJsNumber never throws an error when it's called on numbers")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => pair._2.isNumber)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsNumber).isSuccess)
          }
          )
  }


  property("isJsNothing is always false when it's called on any value but JsNothing")
  {
    check(forAll(gen)
          {
            obj =>
              obj.toLazyList
                .forall((pair: (JsPath, JsValue)) => !pair._2.isNothing)
          }
          )
  }

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

