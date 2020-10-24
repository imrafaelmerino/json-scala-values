package json.value.gen

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import json.value.Preamble.{given}
import json.value.{JsArray, JsNothing, JsNull, JsObj}
import json.value.gen.Preamble.{given}

object JsNumberGenSpecification extends Properties("JsNumberGen")
{
  property("random json number") = forAll(JsNumberGen() ) { n => n.isNumber }
}

