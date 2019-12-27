package value.properties

import value.Preamble._
import org.scalacheck.Prop.{True, forAll}
import org.scalacheck.Arbitrary
import value.{JsArray, JsArrayParser, Parser}
import value.spec.JsNumberSpecs.int
import value.spec.JsStrSpecs.str
import value.spec.{JsArraySpec, JsArraySpecs}
import valuegen.JsArrayGen
import valuegen.Preamble._

import scala.util.Try

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
              JsArray.parse(arr.toPrettyString,
                            parser
                            ) == Try(arr) && arr.validate(spec).isValid

          }
          )
  }
}
