package value.specs

import org.scalatest.{Assertions, FlatSpec}
import value.{JsArray, JsArrayParser, JsNull, JsObj}
import value.spec.JsNumberSpecs._
import value.spec.JsStrSpecs.{str, str_or_null}
import value.spec.{ArrayOfObjSpec, JsArraySpec, JsArraySpecs, JsObjSpec, JsSpecs}
import value.Preamble._
import value.spec.JsObjSpecs.conforms
import value.spec.JsSpecs.any

import scala.util.Try

class ArrayParserSpec extends FlatSpec
{


  "array spec" should "return no error" in
  {

    val spec = JsArraySpec(JsObjSpec("a" -> long_or_null),
                           int,
                           str_or_null,
                           conforms(JsObjSpec("a" -> str),
                                    nullable = true
                                    ),
                           any
                           )

    val parser = JsArrayParser(spec)

    val array = JsArray(JsObj("a" -> null),
                        1,
                        JsNull,
                        JsNull,
                        "hi"
                        )

    Assertions.assert(array.validate(spec).isEmpty)

    Assertions.assert(JsArray.parse(array.toPrettyString,
                                    parser
                                    ) == Try(array)
                      )


  }

  "array of object spec" should "return no error" in
  {

    val spec = JsArraySpecs.arrayOf(JsObjSpec("a" -> str,
                                              "b" -> long
                                              ),
                                    elemNullable = true,
                                    nullable = true
                                    )

    val parser = JsArrayParser(spec)


    val array = JsArray(JsObj("a" -> "hi",
                              "b" -> 1
                              ),
                        JsNull
                        )

   Assertions.assert(array.validate(spec).isEmpty)

    Assertions.assert(JsArray.parse(array.toPrettyString,
                                    parser
                                    ) == Try(array)
                      )
  }

}
