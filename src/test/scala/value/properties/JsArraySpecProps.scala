package value.properties

import valuegen.JsArrayGen
import value.Implicits._
import valuegen.Implicits._
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import value.JsObj
import value.spec.JsArraySpecs._
import value.spec.{JsArraySpec, JsArraySpec_?, JsIntSpecs, JsStringSpecs}

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
              arr.validate(JsArraySpec(JsStringSpecs.string) ++ JsArraySpec(JsIntSpecs.int)).isEmpty &&
              arr.validate(JsArraySpec(JsStringSpecs.string) :+ JsIntSpecs.int).isEmpty &&
              arr.validate(JsStringSpecs.string +: JsArraySpec(JsIntSpecs.int)).isEmpty
          }
          )
  }



  property("array of integers validated against the arrayOfInt and arrayOfNumber specs doesn't return any error.")
  {
    check(forAll(JsArrayGen.ofN(10,
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

    check(forAll(JsArrayGen.ofN(10,
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

    check(forAll(JsArrayGen.ofN(10,
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
