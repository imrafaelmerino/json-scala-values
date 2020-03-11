package value.properties

import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import value.JsArrayParser
import value.spec.JsNumberSpecs.int
import value.spec.JsStrSpecs.str
import value.spec.{JsArraySpec, JsArraySpecs, Valid}
import valuegen.{JsArrayGen, JsObjGen}
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


  property("parsing array of  decimals")
  {
    check(forAll(JsArrayGen.of(Arbitrary.arbitrary[BigDecimal]
                               )
                 )
          {
            arr =>
              val spec = JsArraySpecs.arrayOfDecimal
              val parser = JsArrayParser(spec)
              parser.parse(arr.toPrettyString
                           ) == Right(arr) && (arr.validate(spec) == Valid)

          }
          )
  }
}
