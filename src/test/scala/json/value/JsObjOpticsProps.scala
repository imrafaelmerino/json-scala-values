package json.value

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import gen.RandomJsObjGen

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