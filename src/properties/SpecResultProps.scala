package value.properties
import org.scalacheck.Prop.forAll
import value.Preamble._
import value.spec.JsStrSpecs.str
import value.spec.{Invalid, JsObjSpec, Valid}
import valuegen.JsObjGen

class SpecResultProps extends BasePropSpec
{

  property("operating with Result ADTs(II)")
  {
    check(forAll(JsObjGen("a" -> 1,
                          "b" -> 2
                          )
                 )
          {
            obj =>
              val spec1 = JsObjSpec("a" -> str)
              val spec2 = JsObjSpec("b" -> str)

              val result:Invalid = obj.validate(spec1 ++ spec2)(0)._2

              (result == result) && (Valid == Valid) &&
              result.isInvalid && Valid.isValid  && !Valid.isInvalid &&
              result != Valid && !result.isValid
          }
          )
  }

}
