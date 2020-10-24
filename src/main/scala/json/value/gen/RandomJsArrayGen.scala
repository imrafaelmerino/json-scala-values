package json.value.gen

import json.value.Preamble._
import json.value.JsArray
import json.value.gen.Preamble._
import org.scalacheck.Gen
object RandomJsArrayGen
  def apply(objectPrimitiveGen: PrimitiveGen = PrimitiveGen(),
            arrayPrimitiveGen: PrimitiveGen = PrimitiveGen(),
            arrLengthGen: Gen[Int] = Gen.choose(0,
                                                 10
                                                 ),
            objSizeGen: Gen[Int] = Gen.choose(0,
                                                 10
                                                 ),
            keyGen            : Gen[String] = Gen.oneOf(ALPHABET),
            arrayValueFreq    : ValueFreq = ValueFreq(),
            objectValueFreq   : ValueFreq = ValueFreq()
           ): Gen[JsArray] =
    RandomJsGen(objectPrimitiveGen,
                arrayPrimitiveGen,
                arrLengthGen,
                objSizeGen,
                keyGen,
                arrayValueFreq,
                objectValueFreq
                ).arr


