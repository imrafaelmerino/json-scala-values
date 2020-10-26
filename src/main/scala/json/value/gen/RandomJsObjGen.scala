package json.value.gen

import json.value.JsObj
import org.scalacheck.Gen
import json.value.gen.Preamble.{_, given _}
object RandomJsObjGen
{
  def apply(objPrimitiveGen: PrimitiveGen = PrimitiveGen(),
            arrPrimitiveGen: PrimitiveGen = PrimitiveGen(),
            arrLengthGen   : Gen[Int] = Gen.choose(0, 10 ),
            objSizeGen     : Gen[Int] = Gen.choose(0, 10 ),
            keyGen         : Gen[String] = Gen.oneOf(ALPHABET),
            arrValueFreq   : ValueFreq = ValueFreq(),
            objValueFreq   : ValueFreq = ValueFreq()
           ): Gen[JsObj] =
    RandomJsGen(objPrimitiveGen,
                arrPrimitiveGen,
                arrLengthGen,
                objSizeGen,
                keyGen,
                arrValueFreq,
                objValueFreq
                ).obj

}
