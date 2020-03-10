package properties

import org.scalacheck.Gen
import value.{Index, JsPath, Key, Position}

case class FreqField(freqIndex: Int = 1,
                     freqKey  : Int = 1
                    )


case class JsPathGens(sizeGen: Gen[Int] = Gen.choose(1,
                                                     10
                                                     ),
                      keyGen: Gen[String] = Gen.oneOf("abcdefghijklmnopqrstuvwzyz".split("").toIndexedSeq),
                      indexGen: Gen[Int] = Gen.choose(0,
                                                      10
                                                      ),
                      freqFieldGen: FreqField = FreqField()
                     )
{

  private val indexFieldGen = indexGen.map(it => Index(it))

  private val keyFieldGen = keyGen.map(it => Key(it))

  private val fieldGen = Gen.frequency((freqFieldGen.freqIndex, indexFieldGen),
                                       (freqFieldGen.freqKey, keyFieldGen)
                                       )

  val pathGen: Gen[JsPath] = for
    {
    length <- sizeGen
    arr <- Gen.containerOfN[Vector, Position](length,
                                              fieldGen
                                              )
  } yield JsPath(arr)


  val objPathGen: Gen[JsPath] = for
    {
    length <- sizeGen
    key <- keyGen
    arr <- Gen.containerOfN[Vector, Position](length,
                                              fieldGen
                                              )
  } yield value.JsPath(arr) \ key

  val arrPathGen: Gen[JsPath] = for
    {
    length <- sizeGen
    index <- indexGen
    arr <- Gen.containerOfN[Vector, Position](length,
                                              fieldGen
                                              )
  } yield value.JsPath(arr) \ index


}
