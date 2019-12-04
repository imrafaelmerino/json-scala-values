package value.specs

import org.scalatest.FlatSpec
import value.Implicits._
import value.spec.JsArraySpecs.arrayOfInt
import value.spec.JsBoolSpecs.boolean
import value.spec.JsIntSpecs.int
import value.spec.JsNumberSpecs.{decimal, number}
import value.spec.JsStringSpecs.string
import value.spec.{JsArraySpec, JsArraySpecs, JsBoolSpecs, JsIntSpecs, JsNumberSpecs, JsObjSpec, JsStringSpecs}
import value.{JsArray, JsBool, JsInt, JsObj, JsObjParser, Parser}


class ObjParserSpec extends FlatSpec
{


  "implicit conversion" should "be applied when inserting values in Json objects" in
  {

    val obj = JsObj("a" -> "a",
                    "b" -> 1,
                    "c" -> true,
                    "d" -> 1.4,
                    "e" -> Long.MaxValue,
                    "f" -> JsArray(1,2,3,4),
                    "g" -> null
                    )

    val spec = JsObjSpec("a" -> string,
                         "b" -> int,
                         "c" -> boolean,
                         "d" -> decimal,
                         "e" -> number,
                         "f" -> arrayOfInt,
                         "g" -> arrayOfInt
                         )

    println(obj)
    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    println(obj1)
    assert(obj == obj1)


  }
}
