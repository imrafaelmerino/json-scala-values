package json.value.gen

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import json.value.{JsBool, JsNull, JsNumber, JsStr, Json}

object JsValueGenSpecification extends Properties("JsValueGen")
{

  property("random json json.value") = forAll(JsValueGen() )
  {
    case o: JsBool => o.isBool
    case n: JsNull.type => n.isNull
    case n: JsNumber => n.isNumber
    case s: JsStr => s.isStr
    case json: Json[_] => json.isJson
    case _ => false
  }
}
