package json.value.properties

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import json.value.{JsObj, _}
import json.value.gen.RandomJsObjGen

class JsObjOpticsProps extends Properties("JsObjOptics")
{
  property("accessors") = forAll(RandomJsObjGen())
  {
    (obj:JsObj) =>
      obj.flatten.forall((p: (JsPath, JsValue)) =>
                         {
                           obj(p._1) == p._2
                         }
                         )
  }


}