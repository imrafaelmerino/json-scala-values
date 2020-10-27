package json.value.properties

import json.value.Preamble._
import org.scalacheck.Prop.forAll
import org.scalacheck.Arbitrary
import json.value.JsArrayParser
import json.value.spec.JsNumberSpecs.int
import json.value.spec.JsStrSpecs.str
import json.value.spec.{JsArraySpec, JsArraySpecs, Valid}
import json.value.gen.JsArrayGen
import json.value.gen.Preamble._

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
