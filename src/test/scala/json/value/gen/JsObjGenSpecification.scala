package json.value.gen

import scala.language.implicitConversions
import json.value.{JsArray, JsNothing, JsNull, JsObj}
import json.value.gen.Preamble.{_,given}
import json.value.Preamble.{given}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}

object JsObjGenSpecification extends Properties("JsObjGen")
{
  property("random array generator produces json arrays")
    = forAll(JsArrayGen(Gen.choose[Int](1,
                                        100
                                        ),
                        JsArrayGen(Gen.oneOf(1,
                                             2,
                                             3,
                                             JsNothing
                                             ),
                                   Gen.alphaNumStr
                                   ),
                        JsObjGen("d" -> Gen.choose[Int](1,
                                                        10
                                                        )
                                 )
                        )
             )
  { arr => arr.isArr }

  property("array from a string generator and random length")
    = forAll(JsArrayGen.of(Gen.alphaNumStr))
  {
    arr => arr.isArr && (arr.size == 0 || arr.flatten.forall(p => p._2.isStr))
  }

  property("five-element array from a string generator")
    = forAll(JsArrayGen.ofN(5,
                            Gen.alphaNumStr
                            )
             )
  {
    arr => arr.isArr && arr.size == 5
  }

  property("none-empty generator produces none-empty arrays")
    = forAll(JsArrayGen.noneEmptyOf(Gen.alphaNumStr))
  {
    arr => arr.isArr && arr.size > 0
  }

  property("adding pairs to JsArray generators")
    = forAll(JsArrayGen.inserted(JsArrayGen(1,
                                            2,
                                            JsObjGen("d" -> JsArrayGen(Gen.choose[Int](0,
                                                                                  10
                                                                                  ),
                                                                       Gen.alphaStr
                                                                       )
                                                     )
                                            ),
                                 (3 / "e" / 0, Gen.choose[Int](10,
                                                          20
                                                          )
                                 ),
                                 (3 / "e" / 1, Gen.alphaStr
                                 )
                                 )
             )
  {
    (arr: JsArray) => arr(3 / "e" / 0).isInt && arr(3 / "e" / 1).isStr
  }
}
