package jsonvalues.specifications

import jsonvalues.Implicits._
import jsonvalues.spec.JsArraySpec._
import jsonvaluesgen.Implicits._
import jsonvaluesgen.JsArrGen
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}

class JsArrayValidationSpec extends BasePropSpec
{

  property("array of integers validated against the arrayOfInt and arrayOfNumber specs doesn't return any error.")
  {

    check(forAll(JsArrGen.ofN(10,
                              choose(1,
                                     20
                                     )
                              )
                 )
          {
            arr =>
              arr.validate(arrayOfInt).isEmpty &&
              arr.validate(arrayOfNumber).isEmpty
          }
          )
  }

  property("array of strings validated against the arrayOfString specs doesn't return any error.")
  {

    check(forAll(JsArrGen.ofN(10,
                              Gen.alphaStr
                              )
                 )
          {
            arr => arr.validate(arrayOfString).isEmpty
          }
          )
  }


  property("array of decimals validated against the arrayOfDecimal and arrayOfNumber specs doesn't return any error.")
  {

    check(forAll(JsArrGen.ofN(10,
                              Arbitrary.arbitrary[Double]
                              )
                 )
          {
            arr =>
              arr.validate(arrayOfNumber).isEmpty &&
              arr.validate(arrayOfDecimal).isEmpty
          }
          )
  }
}
