package json.value

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import gen.RandomJsArrayGen

class JsArrayOpticsProps extends Properties("JsArrayOptics")
{

  property("getting values out of a JsArray using accessors") = forAll(RandomJsArrayGen())
  {
    (arr:JsArray) =>
      arr.flatten.forall((p: (JsPath, JsValue)) =>
                         {
                           arr(p._1) == p._2
                         }
                         )

  }

}