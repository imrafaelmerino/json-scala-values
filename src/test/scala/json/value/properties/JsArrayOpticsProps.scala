package json.value.properties

import json.value.{JsObj, _}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import json.value.gen.RandomJsArrayGen

class JsArrayOpticsProps extends Properties("JsArrayOptics")
{

  property("getting values out of a JsArray using accessors") = forAll(RandomJsArrayGen()
                                                                         )
  {
    (arr:JsArray) =>
      arr.flatten.forall((p: (JsPath, JsValue)) =>
                         {
                           arr(p._1) == p._2
                         }
                         )

  }

}