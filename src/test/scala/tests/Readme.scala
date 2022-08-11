package tests

import json.value.*
import json.value.Conversions.given
import json.value.gen.Conversions.given
import json.value.spec.*
import json.value.gen.*
import json.value.spec.parser.JsObjSpecParser
import org.scalacheck.*

import scala.language.implicitConversions
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class Readme extends AnyFlatSpec with should.Matchers {

  val name = "name"
  val languages = "languages"
  val age = "age"
  val address = "address"
  val street = "street"
  val coordinates = "coordinates"

  "defining the same json with and without implicit conversions" should "return the same json" in {

    val a = JsObj(name -> JsStr("Rafael"),
                  languages -> JsArray("Java" , "Scala" , "Kotlin"),
                  age -> JsInt(1) ,
                  address -> JsObj(street -> JsStr("Elm Street"),
                                   coordinates -> JsArray(3.32 ,40.4)))

    val b = JsObj(name -> "Rafael",
                  languages -> JsArray("Java", "Scala", "Kotlin"),
                  age -> 1,
                  address -> JsObj(street -> "Elm Street",
                                   coordinates -> JsArray(3.32, 40.4)))

    a should be(b)
  }


  "defining a spec and a generator" should "generate valid jsons" in {
    val spec =
      JsObjSpec(name -> IsStr,
                languages -> IsArrayOf(IsStr),
                age -> IsInt,
                address -> JsObjSpec(street -> IsStr,
                                     coordinates -> IsTuple(IsDec, IsDec))
                ).withOptKeys(address)

    val gen =
      JsObjGen(name -> Gen.alphaStr,
               languages -> JsArrayGen.of(Gen.oneOf("scala", "java", "kotlin")).distinct,
               age -> Arbitrary.arbitrary[Int],
               address -> JsObjGen(street -> Gen.asciiStr,
                                   coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal],
                                                           Arbitrary.arbitrary[BigDecimal]))
               ).withOptKeys(address)


    Gen.infiniteStream(gen).sample.get.take(100).forall(it => spec.validateAll(it).isEmpty) should be(true)

  }

  "defining a chaos generator and calling the partition method" should "return two generators" in {
    val spec:JsObjSpec =
      JsObjSpec(name ->  IsStr,
                languages -> IsArrayOf(IsStr),
                age -> IsInt,
                address -> JsObjSpec(street -> IsStr,
                                     coordinates -> IsTuple(IsDec,IsDec)
                                    )
               )

    val parser:JsObjSpecParser = spec.parser


    val gen =
        ChaosGen(name -> Gen.alphaStr,
                 languages -> JsArrayGen.of(Gen.oneOf("scala","java","kotlin")).distinct,
                 age -> Arbitrary.arbitrary[Int],
                 address -> ChaosGen(street -> Gen.asciiStr,
                                     coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal],
                                                             Arbitrary.arbitrary[BigDecimal]
                                                            )
                                      )
                )


    val (validGen, invalidGen) = gen.partition(spec)

    Gen.infiniteStream(validGen)
       .sample.get.take(1000)
       .forall(it => parser.parse(it.serialize()) == it) should be(true)

    Gen.infiniteStream(invalidGen)
      .sample.get.take(1000)
      .forall(it =>
        try
          val o = parser.parse(it.serialize())
          throw RuntimeException(o.toString)
        catch _ => true
      ) should be(true)

    Gen.infiniteStream(validGen)
       .sample.get.take(1000)
       .forall(it => spec.validateAll(it).isEmpty) should be(true)

    Gen.infiniteStream(invalidGen)
       .sample.get.take(1000)
       .forall(it => spec.validateAll(it).nonEmpty) should be(true)
  }

}
