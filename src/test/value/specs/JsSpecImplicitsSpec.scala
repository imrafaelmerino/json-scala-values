package value.specs

import org.scalatest.FlatSpec
import value.Implicits.{arr2Spec, bigDec2Spec, bigInt2Spec, boolean2Spec, double2Spec, int2Spec, long2Spec, obj2Spec, null2Spec, nothing2Spec, str2Spec}
import value.spec.{JsArraySpec, JsObjSpec}
import value.{JsArray, JsNothing, JsNull, JsObj}

class JsSpecImplicitsSpec extends FlatSpec
{



  "primitive2Spec implicits" should "turn primitive types into JsValueSpec" in
  {
    assert(JsObj().validate(JsObjSpec("a" -> "hi",
                                      "b" -> 1,
                                      "c" -> 1L,
                                      "d" -> JsArraySpec(1.2,
                                                         true,
                                                         false
                                                         ),
                                      "e" -> JsObj(),
                                      "f" -> JsArray(),
                                      "g" -> JsObjSpec("h" -> BigInt(1),
                                                       "i" -> BigDecimal(3.2),
                                                       "j" -> JsNull
                                                       ),
                                      "h" -> JsNothing
                                      )
                            ).nonEmpty
           )
  }

}
