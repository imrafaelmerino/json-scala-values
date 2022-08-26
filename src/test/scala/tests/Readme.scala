package tests
import json.value.JsPath.root
import json.value.*
import json.value.Conversions.given
import json.value.gen.Conversions.given
import json.value.spec.*
import json.value.gen.*
import json.value.spec.parser.JsObjSpecParser
import org.scalacheck.*
import java.time.Instant

import scala.language.implicitConversions
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.math.BigInteger

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

    streetAddressOpt.replace("Elm's Street")(JsObj("address" -> JsObj.empty)) should be(JsObj("address" -> JsObj("street" -> JsStr("Elm's Street"))))

    longitudeAddressOpt.replace(0)(JsObj.empty) should be(JsObj.empty)

    longitudeAddressOpt.replace(1.0)(JsObj.empty) should be(JsObj.empty)

    longitudeAddressOpt.replace(1.0)(JsObj("address" -> JsObj("coordinates" -> JsArray(0.0, 0.0)))) should be(JsObj("address" -> JsObj("coordinates" -> JsArray(1.0, 0.0))))

    longitudeAddressOpt.replace(1.0).andThen(latitudeAddressOpt.replace(2.0))(JsObj("address" -> JsObj("coordinates" -> JsArray.empty))) should be(JsObj("address" -> JsObj("coordinates" -> JsArray(1.0, 2.0))))

  }

  "defining the same json with and without implicit conversions" should "return the same json" in {
    val a = JsObj(name -> JsStr("Rafael"), languages -> JsArray("Java", "Scala", "Kotlin"), age -> JsInt(1), address -> JsObj(street -> JsStr("Elm Street"), coordinates -> JsArray(3.32, 40.4)))

    val b = JsObj(name -> "Rafael", languages -> JsArray("Java", "Scala", "Kotlin"), age -> 1, address -> JsObj(street -> "Elm Street", coordinates -> JsArray(3.32, 40.4)))

    a should be(b)
  }

  "defining a spec and a generator" should "generate valid jsons" in {
    val spec = JsObjSpec(name -> IsStr, languages -> IsArrayOf(IsStr), age -> IsInt, address -> JsObjSpec(street -> IsStr, coordinates -> IsTuple(IsNumber, IsNumber))).withOptKeys(address)

    val gen = JsObjGen(name -> Gen.alphaStr, languages -> JsArrayGen.of(Gen.oneOf("scala", "java", "kotlin")).distinct, age -> Arbitrary.arbitrary[Int], address -> JsObjGen(street -> Gen.asciiStr, coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal], Arbitrary.arbitrary[BigDecimal]))).withOptKeys(address)


    Gen.infiniteStream(gen).sample.get.take(10).forall(it => spec.validateAll(it).isEmpty) should be(true)

  }

  "defining a chaos generator and calling the partition method" should "return two generators" in {
    val spec: JsObjSpec = JsObjSpec(name -> IsStr, languages -> IsArrayOf(IsStr), age -> IsInt, address -> JsObjSpec(street -> IsStr, coordinates -> IsTuple(IsNumber, IsNumber)))

    val parser: JsObjSpecParser = spec.parser


    val gen = JsObjGen(name -> Gen.alphaStr, languages -> JsArrayGen.of(Gen.oneOf("scala", "java", "kotlin")).distinct, age -> Arbitrary.arbitrary[Int], address -> JsObjGen(street -> Gen.asciiStr, coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal], Arbitrary.arbitrary[BigDecimal])).withOptKeys(coordinates, street).withNullValues(coordinates, street)).withOptKeys(name, languages, age, address).withNullValues(name, languages, age, address)


    val (validGen, invalidGen) = gen.partition(spec)

    Gen.infiniteStream(validGen).sample.get.take(10).forall(it => parser.parse(it.serialize()) == it) should be(true)

    Gen.infiniteStream(invalidGen).sample.get.take(10).forall(it =>
      try
        val o = parser.parse(it.serialize())
        throw RuntimeException(o.toString)
      catch _ => true) should be(true)

    Gen.infiniteStream(validGen).sample.get.take(10).forall(it => spec.validateAll(it).isEmpty) should be(true)

    Gen.infiniteStream(invalidGen).sample.get.take(10).forall(it => spec.validateAll(it).nonEmpty) should be(true)
  }

  "validateAll example" should "returns all the errors" in {
    val json = JsObj("a" -> 1, "b" -> "hi", "c" -> JsArray(JsObj("d" -> "bye", "e" -> 1)))

    val spec = JsObjSpec("a" -> IsStr, "b" -> IsInt, "c" -> IsArrayOf(JsObjSpec("d" -> IsInstant, "e" -> IsBool)))

    val errors: LazyList[(JsPath, Invalid)] = spec.validateAll(json)
    errors.foreach(println)

    errors.size should be(4)

    val result: Result = spec.validate(json)
    result match
      case Valid => println("valid json!")
      case Invalid(value, error) => println(s"the value $value doesn conform the spec: $error")

  }

  "different object representing the same json" should "be equals and have same hashcode" in {
    val xs = JsObj("a" -> JsInt(1000), "b" -> JsBigDec(BigDecimal.valueOf(100_000_000_000_000L)), "c" -> JsInstant(Instant.parse("2022-05-25T14:27:37.353Z")))

    val ys = JsObj("b" -> JsBigInt(BigInteger.valueOf(100_000_000_000_000L)), "a" -> JsLong(1000L), "c" -> JsStr("2022-05-25T14:27:37.353Z"))


    xs.equals(ys) should be(true)
    xs.hashCode() should be(ys.hashCode())

    val json:Json[?] = JsObj.empty
    json
      .mapKeys(_.toLowerCase)
      .map(JsStr.prism.modify(_.trim))
      .filter(_.noneNull)
      .filterKeys(!_.startsWith("$"))

    val spec = IsTuple(IsStr,
      IsInt,
      JsObjSpec("a" -> IsStr),
      IsStr.nullable,
      IsArrayOf(IsInt)
    )

    val toLowerCase:String => String = _.toLowerCase



    json mapKeys toLowerCase

    json map JsStr.prism.modify(_.trim)

    val isNotNull:JsPrimitive => Boolean = _.noneNull

    json filter isNotNull


    val obj = JsObj("a" -> 1, "b" -> JsArray(1, "m", JsObj("c" -> true, "d" -> JsObj.empty)))

    obj.flatten.foreach(println) // all the pairs are consumed


  }

}


