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

  val nameLens = JsObj.lens.str(name)
  val ageLens = JsObj.lens.int(age)
  val lanLens = JsObj.lens.array(languages)
  val addressOpt = JsObj.optional.obj(address)
  val streetLens = JsObj.lens.str(street)
  val coordinatesLens = JsObj.lens.array(coordinates)
  val latitudeLens = coordinatesLens.andThen(JsArray.lens.double(1))
  val longitudeLens = coordinatesLens.andThen(JsArray.lens.double(0))


  val streetAddressOpt = addressOpt.andThen(streetLens)
  val coordinatesAddressOpt = addressOpt.andThen(coordinatesLens)
  val latitudeAddressOpt = addressOpt.andThen(latitudeLens)
  val longitudeAddressOpt = addressOpt.andThen(longitudeLens)


  "creating functions with optics" should "never fail" in {


    val xs = nameLens.replace("rafa")(JsObj.empty)
    nameLens.get(xs) should be("rafa")

    streetAddressOpt.replace("Elm's Street")(JsObj.empty) should be(JsObj.empty)

    streetAddressOpt.replace("Elm's Street")(JsObj("address"->JsObj.empty)) should be(JsObj("address"->JsObj("street"->JsStr("Elm's Street"))))

    longitudeAddressOpt.replace(0)(JsObj.empty) should be(JsObj.empty)

    longitudeAddressOpt.replace(1.0)(JsObj.empty) should be(JsObj.empty)

    longitudeAddressOpt.replace(1.0)(JsObj("address"->JsObj("coordinates"->JsArray(0.0,0.0)))) should be(JsObj("address"->JsObj("coordinates"->JsArray(1.0,0.0))))

    longitudeAddressOpt.replace(1.0).andThen(latitudeAddressOpt.replace(2.0))(JsObj("address"->JsObj("coordinates"->JsArray.empty))) should be(JsObj("address"->JsObj("coordinates"->JsArray(1.0,2.0))))

  }

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
                                     coordinates -> IsTuple(IsNumber, IsNumber))
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
                                     coordinates -> IsTuple(IsNumber,IsNumber)
                                    )
               )

    val parser:JsObjSpecParser = spec.parser


    val gen =
        JsObjGen(name -> Gen.alphaStr,
                 languages -> JsArrayGen.of(Gen.oneOf("scala","java","kotlin")).distinct,
                 age -> Arbitrary.arbitrary[Int],
                 address -> JsObjGen(street -> Gen.asciiStr,
                                     coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal],
                                                             Arbitrary.arbitrary[BigDecimal]
                                                            )
                                      )
                   .withOptKeys(coordinates, street)
                   .withNullValues(coordinates, street)
                )
          .withOptKeys(name,languages,age,address)
          .withNullValues(name,languages,age,address)


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
