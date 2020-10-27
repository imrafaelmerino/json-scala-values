package json.value.gen

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class RandomJsGenSpec extends BasePropSpec
{


  property("random object generator produces json objects")
  {
    check(forAll(RandomJsObjGen())
          { json =>
            json.isObj
          }
          )
  }


  property("random object generator configured to produce only strings")
  {
    val freqs = ValueFreq(int = 0,
                          long = 0,
                          double = 0,
                          bigDec = 0,
                          bigInt = 0,
                          `null` = 0,
                          bool = 0,
                          str = 10
                          )
    check(forAll(RandomJsObjGen(arrValueFreq = freqs,
                                objValueFreq = freqs
                                )
                 )
          { obj =>
            obj.isObj && obj.flatten.filterNot(pair => pair._2.isJson).forall(pair => pair._2.isStr)
          }
          )
  }

  property("random object generator configured to produce only numbers")
  {
    val freqs = ValueFreq(`null` = 0,
                          bool = 0,
                          str = 0
                          )
    check(forAll(RandomJsObjGen(arrValueFreq = freqs,
                                objValueFreq = freqs
                                )
                 )
          { obj =>
            obj.isObj && obj.flatten.filterNot(pair => pair._2.isJson).forall(pair => pair._2.isNumber)
          }
          )
  }

  property("random object generator configured to produce only integers")
  {
    val freqs = ValueFreq(`null` = 0,
                          double = 0,
                          long = 0,
                          bigInt = 0,
                          bigDec = 0,
                          bool = 0,
                          str = 0,
                          int = 10
                          )
    check(forAll(RandomJsObjGen(arrValueFreq = freqs,
                                objValueFreq = freqs
                                )
                 )
          { obj =>
            obj.isObj && obj.flatten.filterNot(pair => pair._2.isJson).forall(pair => pair._2.isInt)
          }
          )
  }


  property("random array generator produces json arrays")
  {
    check(forAll(RandomJsArrayGen())
          { json =>
            json.isArr
          }
          )
  }


}
