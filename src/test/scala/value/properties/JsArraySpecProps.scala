package value.properties

import valuegen.JsArrGen
import value.Preamble._
import valuegen.Implicits._
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import value.spec.JsNumberSpecs.int
import value.spec.JsStrSpecs.str
import value.spec.JsArraySpec

class JsArraySpecProps extends BasePropSpec
{

  property("operating with JsArraySpecs")
  {
    check(forAll(JsArrGen(Arbitrary.arbitrary[String],
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
