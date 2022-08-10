package tests

import json.value.*
import json.value.gen.Conversions.given
import json.value.gen.*
import json.value.spec.IsStr
import org.scalacheck.Gen
import org.scalatest.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*
import scala.language.implicitConversions
import scala.collection.mutable
import scala.annotation.nowarn

//scala check works with Stream instead of LazyList
@nowarn
class JsObjGenTests extends AnyFlatSpec with should.Matchers {

  "all optional fields combinations" should "be generated with the same probability" in {

    val a = JsObjGen("a" -> Gen.alphaStr, "b" -> Gen.alphaStr)
    val b = JsObjGen("c" -> Gen.alphaStr, "d" -> Gen.alphaStr)
    val c = a concat b withOptKeys  ("a", "b", "c", "d")

    val count = mutable.Map[String,Long]().withDefaultValue(0L)

    val stream = Gen.infiniteStream(c).sample.get

    val times = 1_000_00

    stream.take(times).foreach(o => {
      val key = o.keys.mkString("")
      count.update(key,count(key)+1)
    }
    )

    val expected = times/count.size
    val maxError = 5*expected/100
    

    count.forall((_, occurrences)=>{
      val x = (occurrences - expected).abs
      x <= maxError
    }) should be(true)

  }

  "all nullable fields combinations" should "be generated with the same probability" in {
    val a = JsObjGen("a" -> Gen.alphaStr, "b" -> Gen.alphaStr)
    val b = JsObjGen("c" -> Gen.alphaStr, "d" -> Gen.alphaStr)

    val c = a concat b withNullValues ("a", "b", "c", "d")

    val count = mutable.Map[String, Long]().withDefaultValue(0L)

    val stream = Gen.infiniteStream(c).sample.get

    val times = 1_000_00

    stream.take(times).foreach(o => {
      val key = o.filter(_==JsNull).keys.mkString("")
      count update(key, count(key) + 1)
    })
    
    val expected = times / count.size
    val maxError = 5 * expected / 100

    count.forall((_, occurrences) => {
      val x = (occurrences - expected).abs
      x <= maxError
    }) should be(true)

  }
  



}
