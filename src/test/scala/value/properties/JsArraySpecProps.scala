package value.properties

import value.Preamble._
import org.scalacheck.Prop.forAll
import org.scalacheck.Arbitrary
import value.spec.JsNumberSpecs.int
import value.spec.JsStrSpecs.str
import value.spec.JsArraySpec
import valuegen.JsArrayGen
import valuegen.Preamble._

class JsArraySpecProps extends BasePropSpec
{

  property("operating with JsArraySpecs")
  {
    check(forAll(JsArrayGen(Arbitrary.arbitrary[String],
                          Arbitrary.arbitrary[Int]
                          )
                 )
          {
            arr =>
              arr.validate(JsArraySpec(str) ++ JsArraySpec(int)).isEmpty &&
              arr.validate(JsArraySpec(str) :+ int).isEmpty &&
              arr.validate(str +: JsArraySpec(int)).isEmpty
          }
          )
  }
}
