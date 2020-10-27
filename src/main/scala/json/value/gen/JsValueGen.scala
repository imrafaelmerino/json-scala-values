package json.value.gen

import json.value.{JsBool, JsNull, JsStr, JsValue}
import org.scalacheck.{Arbitrary, Gen}

object JsValueGen
{

  def apply(): Gen[JsValue] = Gen.oneOf(JsObjGen(),
                                        JsArrayGen(),
                                        JsNumberGen(),
                                        Arbitrary.arbitrary[String].map(it => JsStr(it)),
                                        Gen.oneOf(false,
                                                  true
                                                  ).map(it => JsBool(it)),
                                        Gen.const(JsNull)
                                        )

}
