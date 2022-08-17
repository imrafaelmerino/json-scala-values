package json.value.gen
import org.scalacheck.Gen
import json.value.*
object ChaosGen:
  def apply(pairs: (String, Gen[JsValue])*): Gen[JsObj] = 
    val keys = pairs.map(_._1)
    JsObjGen.apply(pairs: _*)
            .withOptKeys(keys: _*)
            .withNullValues(keys: _*)
