import value.{JsArray, JsObj, JsPath, JsValue}
import value.Preamble._



val b = JsObj.empty.inserted("a" / "b" / 2, "hi", padWith="")

println(b)

val array = JsArray(JsObj("a" -> 1,
                          "b" -> "hi"),
                    1,
                    "hi",
                    true
                    )

val pairs:LazyList[(JsPath,JsValue)] = array.flatten

pairs.foreach(println)