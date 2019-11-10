package value.properties

import valuegen.{RandomJsGen, RandomJsObjGen}
import org.scalacheck.Prop.forAll
import value.{JsPath, JsValue}

import scala.util.Try

class JsValueProps extends BasePropSpec
{
  property("asJsStr throws an user error when called on a non string value")
  {
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.toLazyList
                .filter((pair                 : (JsPath, JsValue)) => !pair._2.isStr)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsStr).isFailure)
          }
          )
  }

  property("asJsObj throws an user error when called on a non object")
  {
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
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
    check(forAll(RandomJsObjGen())
          {
            obj =>
              obj.toLazyList
                .filter((pair: (JsPath, JsValue)) => !pair._2.isNull)
                .forall((pair: (JsPath, JsValue)) => Try(pair._2.asJsNull).isFailure)
          }
          )
  }

}
