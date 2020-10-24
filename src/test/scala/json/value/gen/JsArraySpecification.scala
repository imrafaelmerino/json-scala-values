package json.value.gen

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll
import scala.language.implicitConversions
import json.value.gen.Preamble.{_,given}
object JsArraySpecification extends Properties("JsArrayGen")
{

  property("random object generator produces json objects") = forAll(RandomJsObjGen()) { json => json.isObj }

  val onlyStrFreqs = ValueFreq(int = 0,
                               long = 0,
                               double = 0,
                               bigDec = 0,
                               bigInt = 0,
                               `null` = 0,
                               bool = 0,
                               str = 10
                               )
  property("random object generator configured to produce only strings")
    = forAll(RandomJsObjGen(arrValueFreq = onlyStrFreqs,
                            objValueFreq = onlyStrFreqs
                            )
             )
  {
    obj => obj.isObj && obj.flatten.filterNot(pair => pair._2.isJson)
                                   .forall(pair => pair._2.isStr)
  }

  val onlyNumbersFreqs = ValueFreq(`null` = 0,
                                   bool = 0,
                                   str = 0
                                   )
  property("random object generator configured to produce only numbers")
    = forAll(RandomJsObjGen(arrValueFreq = onlyNumbersFreqs,
                            objValueFreq = onlyNumbersFreqs
                            )
             )
  {
    obj => obj.isObj && obj.flatten.filterNot(pair => pair._2.isJson)
                                   .forall(pair => pair._2.isNumber)

  }

  val onlyIntFreqs = ValueFreq(`null` = 0,
                               double = 0,
                               long = 0,
                               bigInt = 0,
                               bigDec = 0,
                               bool = 0,
                               str = 0,
                               int = 10
                               )
  property("random object generator configured to produce only integers")
    = forAll(RandomJsObjGen(arrValueFreq = onlyIntFreqs, objValueFreq = onlyIntFreqs ) )
  {
    obj => obj.isObj && obj.flatten.filterNot(pair => pair._2.isJson)
                                   .forall(pair => pair._2.isInt)

  }


  property("random array generator produces json arrays")
    = forAll(RandomJsArrayGen()) { json => json.isArr }


}
