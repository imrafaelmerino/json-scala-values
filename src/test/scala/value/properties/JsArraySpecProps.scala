package value.properties

import valuegen.JsArrGen
import value.Implicits._
import valuegen.Implicits._
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import value.JsObj
import value.spec.JsArraySpecs._
import value.spec.{JsArraySpec, JsIntSpecs, JsStrSpecs}

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
              arr.validate(JsArraySpec(JsStrSpecs.str) ++ JsArraySpec(JsIntSpecs.int)).isEmpty &&
              arr.validate(JsArraySpec(JsStrSpecs.str) :+ JsIntSpecs.int).isEmpty &&
              arr.validate(JsStrSpecs.str +: JsArraySpec(JsIntSpecs.int)).isEmpty
          }
          )
  }
}
