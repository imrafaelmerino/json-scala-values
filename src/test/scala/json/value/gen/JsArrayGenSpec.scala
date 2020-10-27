package json.value.gen

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import json.value.JsNothing
import json.value.Preamble._
import json.value.gen.Preamble._


class JsArrayGenSpec extends BasePropSpec
{
  property("random array generator produces json arrays")
  {
    check(forAll(JsArrayGen(Gen.choose(1,
                                       100
                                       ),
                            JsArrayGen(Gen.oneOf(1,
                                                 2,
                                                 3,
                                                 JsNothing
                                                 ),
                                       Gen.alphaNumStr
                                       ),
                            JsObjGen("d" -> Gen.choose(1,
                                                       10
                                                       )
                                     )
                            )
                 )
          { arr =>
            arr.isArr
          }
          )
  }

  property("array from a string generator and random length")
  {
    check(forAll(JsArrayGen.of(Gen.alphaNumStr))
          {
            arr =>
              arr.isArr && (arr.size == 0 || arr.flatten.forall(p => p._2.isStr))
          }
          )
  }

  property("five-element array from a string generator")
  {
    check(forAll(JsArrayGen.ofN(5,
                                Gen.alphaNumStr
                                )
                 )
          {
            arr =>
              arr.isArr && arr.size == 5
          }
          )
  }

  property("none-empty generator produces none-empty arrays")
  {
    check(forAll(JsArrayGen.noneEmptyOf(Gen.alphaNumStr))
          {
            arr =>
              arr.isArr && arr.size > 0
          }
          )
  }

  property("adding pairs to JsArray generators")
  {

    check(forAll(JsArrayGen.inserted(JsArrayGen(1,
                                                2,
                                                JsObjGen("d" -> JsArrayGen(Gen.choose(0,
                                                                                      10
                                                                                      ),
                                                                           Gen.alphaStr
                                                                           )
                                                         )
                                                ),
                                     (3 / "e" / 0, Gen.choose(10,
                                                              20
                                                              )
                                     ),
                                     (3 / "e" / 1, Gen.alphaStr
                                     )
                                     )
                 )
          {
            (arr) =>
              arr(3 / "e" / 0).isInt && arr(3 / "e" / 1).isStr
          }
          )
  }

}
