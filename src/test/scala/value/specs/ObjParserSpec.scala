package value.specs

import org.scalatest.FlatSpec
import value.Implicits._
import value.spec.JsArraySpecs._
import value.spec.JsBoolSpecs.boolean
import value.spec.JsIntSpecs.int
import value.spec.JsNumberSpecs.{decimal, number}
import value.spec.JsObjSpec
import value.spec.JsStringSpecs.string
import value.spec.JsNumberSpecs.integral
import value.spec.JsLongSpecs.long
import value.{JsArray, JsNull, JsObj, JsObjParser}


class ObjParserSpec extends FlatSpec
{

  "parsing primitives types specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("a" -> "a",
                    "b" -> 1,
                    "c" -> true,
                    "d" -> 1.4,
                    "e" -> Long.MaxValue,
                    "f" -> BigDecimal(1.5),
                    "g" -> 100L,
                    "h" -> BigInt(1000)

                    )

    val spec = JsObjSpec("a" -> string,
                         "b" -> int,
                         "c" -> boolean,
                         "d" -> decimal,
                         "e" -> number,
                         "f" -> decimal,
                         "g" -> long,
                         "h" -> integral
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of integers specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1,
                                   2,
                                   3,
                                   4
                                   ),
                    "g" -> null,
                    "h" -> JsArray(1,
                                   2,
                                   3,
                                   JsNull
                                   ), //no vale null, ver si se puede converison
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfInt,
                         "g" -> nullOrArrayOfInt,
                         "h" -> arrayOfIntWithNull,
                         "i" -> nullOrArrayOfIntWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of longs specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1L,
                                   2L,
                                   3L,
                                   4L
                                   ),
                    "g" -> null,
                    "h" -> JsArray(1L,
                                   2L,
                                   3L,
                                   JsNull
                                   ), //no vale null, ver si se puede converison
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfLong,
                         "g" -> nullOrArrayOfLong,
                         "h" -> arrayOfLongWithNull,
                         "i" -> nullOrArrayOfLongWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of bigdec specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(BigDecimal(1.2),
                                   BigDecimal(1.5),
                                   BigDecimal(1.12),
                                   BigDecimal(BigInt(100000000))
                                   ),
                    "g" -> null,
                    "h" -> JsArray(BigDecimal(1.2),
                                   BigDecimal(1.2),
                                   BigDecimal(1.2),
                                   JsNull
                                   ), //no vale null, ver si se puede converison
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfDecimal,
                         "g" -> nullOrArrayOfDecimal,
                         "h" -> arrayOfDecimalWithNull,
                         "i" -> nullOrArrayOfDecimalWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of strings specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray("a",
                                   "b",
                                   "c",
                                   "d"
                                   ),
                    "g" -> null,
                    "h" -> JsArray("a",
                                   "hi",
                                   JsNull,
                                   "hi"
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfStr,
                         "g" -> nullOrArrayOfStr,
                         "h" -> arrayOfStrWithNull,
                         "i" -> nullOrArrayOfStrWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of booleans specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(true,
                                   false,
                                   false,
                                   true
                                   ),
                    "g" -> null,
                    "h" -> JsArray(true,
                                   true,
                                   JsNull,
                                   false
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfBool,
                         "g" -> nullOrArrayOfBool,
                         "h" -> arrayOfBoolWithNull,
                         "i" -> nullOrArrayOfBoolWithNull

                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of numbers specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1L,
                                   2.5,
                                   BigInt("100000000000000000"),
                                   BigDecimal(10.5),
                                   3
                                   ),
                    "g" -> null,
                    "h" -> JsArray(1,
                                   Long.MaxValue,
                                   JsNull,
                                   10.5
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfNumber,
                         "g" -> nullOrArrayOfNumber,
                         "h" -> arrayOfNumberWithNull,
                         "i" -> nullOrArrayOfNumberWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of integral numbers specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1L,
                                   2,
                                   BigInt("100000000000000000"),
                                   BigDecimal(10),
                                   3
                                   ),
                    "g" -> null,
                    "h" -> JsArray(10,
                                   Long.MaxValue,
                                   JsNull,
                                   10000L
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfIntegral,
                         "g" -> nullOrArrayOfIntegral,
                         "h" -> arrayOfIntegralWithNull,
                         "i" -> nullOrArrayOfIntegralWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing nested objects specs of primitives types" should "parse the string into the same json object" in
  {

    val obj = JsObj("a" -> "a",
                    "b" -> 1,
                    "c" -> true,
                    "d" -> 1.4,
                    "e" -> Long.MaxValue,
                    "f" -> BigDecimal(1.5),
                    "g" -> 100L,
                    "h" -> BigInt(1000),
                    "i" -> JsObj("a" -> 1,
                                 "b" -> "hi",
                                 "c" -> JsObj("a" -> true,
                                              "d" -> "hi",
                                              "e" -> JsArray(1,
                                                             2,
                                                             3,
                                                             4
                                                             )
                                              ),

                                 ),
                    "j" -> JsArray(JsObj("a" -> 1,
                                         "b" -> "a"
                                         ),
                                   JsObj("a" -> 2,
                                         "b" -> "b"
                                         )
                                   )

                    )

    val spec = JsObjSpec("a" -> string,
                         "b" -> int,
                         "c" -> boolean,
                         "d" -> decimal,
                         "e" -> number,
                         "f" -> decimal,
                         "g" -> long,
                         "h" -> integral,
                         "i" -> JsObjSpec("a" -> int,
                                          "b" -> string,
                                          "c" -> JsObjSpec("a" -> boolean,
                                                           "d" -> string,
                                                           "e" -> arrayOfInt
                                                           )
                                          ),
                         "j" -> arrayOfObj(JsObjSpec("a" -> int,
                                                     "b" -> string
                                                     )
                                           )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing array of values specs" should "parse the string into the same json object" in
  {

    val obj = JsObj("a" -> JsArray("a",
                                   1,
                                   true,
                                   2.5
                                   ),
                    "b" -> JsArray("a",
                                   1,
                                   true,
                                   2.5,
                                   JsNull
                                   ),
                    "c" -> null,
                    "d" -> JsArray(JsNull,
                                   1,
                                   2,
                                   "hi"
                                   )
                    )

    val spec = JsObjSpec("a" -> array,
                         "b" -> arrayWithNull,
                         "c" -> nullOrArray,
                         "d" -> nullOrArrayWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing array of string specs" should "parse the string into the same json object" in
  {

    val obj = JsObj("a" -> JsArray("a",
                                   "b",
                                   "c",
                                   "d"
                                   ),
                    "b" -> JsArray("a",
                                   "b",
                                   JsNull,
                                   "d",
                                   JsNull
                                   ),
                    "c" -> null,
                    "d" -> JsArray("a",
                                   "b",
                                   JsNull,
                                   "d",
                                   JsNull
                                   ),
                    )

    val spec = JsObjSpec("a" -> arrayOfStr,
                         "b" -> arrayOfStrWithNull,
                         "c" -> nullOrArrayOfStr,
                         "d" -> nullOrArrayOfStrWithNull
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }
}
