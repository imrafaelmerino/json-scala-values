package json.value.gen

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import json.value.Preamble._
import json.value.{JsArray, JsNothing, JsNull, JsObj}

class JsNumberGenSpec extends BasePropSpec
{

  property("random json number")
  {
    check(forAll(JsNumberGen()
                 )
          { n =>
            n.isNumber
          }
          )
  }
}
