package jsonvalues

import org.scalacheck.Gen

case class FreqField(freqIndex: Int = 1,
                     freqKey  : Int = 1
                    )


case class JsPathGens(sizeGen     : Gen[Int] = Gen.choose(1,
                                                          10
                                                          ),
                      keyGen      : Gen[String] = Gen.oneOf("abcdefghijklmnopqrstuvwzyz".split("")),
                      indexGen    : Gen[Int] = Gen.choose(0,
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

  val pathGen = for
    {
    length <- sizeGen
    arr <- Gen.containerOfN[Vector, Position](length,
                                              fieldGen
                                              )
  } yield JsPath(arr)


  val objPathGen = for
    {
    length <- sizeGen
    key <- keyGen
    arr <- Gen.containerOfN[Vector, Position](length,
                                              fieldGen
                                              )
  } yield key +: JsPath(arr)

  val arrPathGen = for
    {
    length <- sizeGen
    index <- indexGen
    arr <- Gen.containerOfN[Vector, Position](length,
                                              fieldGen
                                              )
  } yield index +: JsPath(arr)


}
