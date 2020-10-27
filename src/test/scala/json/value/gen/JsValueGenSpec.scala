package json.value.gen

import org.scalacheck.Prop.forAll
import json.value._

class JsValueGenSpec extends BasePropSpec
{

  property("random json value")
  {
    check(forAll(JsValueGen()
                 )
          {
            case o: JsBool => o.isBool
            case n: JsNull.type => n.isNull
            case n: JsNumber => n.isNumber
            case s: JsStr => s.isStr
            case json: Json[_] => json.isJson
            case _ => false
          }
          )
  }
}
