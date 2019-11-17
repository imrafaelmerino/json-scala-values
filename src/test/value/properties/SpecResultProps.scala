package value.properties
import org.scalacheck.Prop.forAll
import valuegen.Implicits._
import value.Implicits._
import value.spec.JsStringSpecs.string
import value.spec.{Invalid, JsIntSpecs, JsObjSpec, JsStringSpecs, Valid}
import valuegen.JsObjGen

class SpecResultProps extends BasePropSpec
{

  property("operating with Result ADTs")
  {
    check(forAll(JsObjGen("a" -> 1,
                          "b" -> 2
                          )
                 )
          {
            obj =>
              val spec1 = JsObjSpec("a" -> string)
              val spec2 = JsObjSpec("b" -> string)

              val result1 = obj.validate(spec1)(0)._2
              val result2 = obj.validate(spec2)(0)._2


              val aggregated = (Valid + Valid) + result1 + result2 + Valid

              aggregated match
              {
                case Valid => false
                case Invalid(messages) => messages.length == 2
              }

          }
          )
  }

  property("operating with Result ADTs(II)")
  {
    check(forAll(JsObjGen("a" -> 1,
                          "b" -> 2
                          )
                 )
          {
            obj =>
              val spec1 = JsObjSpec("a" -> string)
              val spec2 = JsObjSpec("b" -> string)

              val result:Invalid = obj.validate(spec1 ++ spec2)(0)._2

              (result == result) && (Valid == Valid) &&
              result.isInvalid && Valid.isValid  && !Valid.isInvalid &&
              result != Valid && !result.isValid
          }
          )
  }

}
