package value.specs

import org.scalatest.{Assertions, FlatSpec}
import value.{JsArray, JsArrayParser, JsNull, JsObj, JsObjParser}
import value.spec.JsNumberSpecs._
import value.spec.JsStrSpecs.{str, str_or_null}
import value.spec.{Invalid, JsArraySpec, JsArraySpecs, JsObjSpec, Valid}
import value.spec.JsObjSpecs.conforms
import value.spec.JsSpecs.any
import value.Preamble._
import value.spec.JsArraySpecs.{arrayOf, arrayOfTestedDecimal, arrayOfTestedInt, arrayOfTestedIntegral, arrayOfTestedLong, arrayOfTestedNumber, arrayOfTestedObj, arrayOfTestedStr, arrayOfTestedValue, arrayOfValueSuchThat, array_of_value, array_of_value_or_null, array_of_value_with_nulls}

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

    val spec = arrayOf(JsObjSpec("a" -> str,
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

  "array deserializers" should "return no error" in
  {

    val spec = JsObjSpec(
      "a" -> array_of_value,
      "b" -> array_of_value_or_null,
      "c" -> array_of_value_with_nulls,
      "d" -> arrayOfTestedValue(v => if (v.isNumber) Valid else Invalid("not a number"),
                                elemNullable = true
                                ),
      "e" -> arrayOfTestedValue(v => if (v.isNumber) Valid else Invalid("not a number"),
                                nullable = true
                                ),
      "g" -> arrayOfValueSuchThat(a => if (a.length() == 3) Valid else Invalid("length should be three")),

      "h" -> arrayOfTestedInt(i => if (i % 2 == 0) Valid else Invalid("not even"),
                              nullable = true
                              ),
      "i" -> arrayOfTestedInt(i => if (i % 2 == 0) Valid else Invalid("not even"),
                              elemNullable = true
                              ),
      "j" -> arrayOfTestedInt(i => if (i % 2 == 0) Valid else Invalid("not even")),

      "k" -> arrayOfTestedLong(i => if (i % 2 == 0) Valid else Invalid("not even"),
                               nullable = true
                               ),
      "l" -> arrayOfTestedLong(i => if (i % 2 == 0) Valid else Invalid("not even"),
                               elemNullable = true
                               ),
      "m" -> arrayOfTestedLong(i => if (i % 2 == 0) Valid else Invalid("not even")),

      "n" -> arrayOfTestedStr(i => if (i.startsWith("a")) Valid else Invalid("no start with a"),
                              nullable = true
                              ),
      "o" -> arrayOfTestedStr(i => if (i.startsWith("b")) Valid else Invalid("no start with b"),
                              elemNullable = true
                              ),
      "p" -> arrayOfTestedStr(i => if (i.startsWith("c")) Valid else Invalid("no start with c")),
      //
      "r" -> arrayOfTestedDecimal(i => if (i < 5.5) Valid else Invalid("greater than 5.5"),
                                  nullable = true
                                  ),
      "s" -> arrayOfTestedDecimal(i => if (i > 10.9) Valid else Invalid("lower than 10.9"),
                                  elemNullable = true
                                  ),
      "t" -> arrayOfTestedDecimal(i => if (i.isValidInt) Valid else Invalid("not valid int")),

      "u" -> arrayOfTestedObj(o => if (o.containsKey("a")) Valid else Invalid("not contain a"),
                              nullable = true
                              ),
      "v" -> arrayOfTestedObj(o => if (o.size == 1) Valid else Invalid("more than one key"),
                              elemNullable = true
                              ),
      "w" -> arrayOfTestedObj(o => if (o.size == 1) Valid else Invalid("more than one key")),

      "x" -> arrayOfTestedNumber(i => if (i.isIntegral) Valid else Invalid("not integral"),
                                 nullable = true
                                 ),
      "y" -> arrayOfTestedNumber(i => if (i.isIntegral) Valid else Invalid("not integral"),
                                 elemNullable = true
                                 ),
      "z" -> arrayOfTestedNumber(i => if (i.isIntegral) Valid else Invalid("not integral")),

      "a1" -> arrayOfTestedIntegral(i => if (i == BigInt(1)) Valid else Invalid("not one"),
                                    nullable = true
                                    ),
      "b1" -> arrayOfTestedIntegral(i => if (i == BigInt(1)) Valid else Invalid("not one"),
                                    elemNullable = true
                                    ),
      "c1" -> arrayOfTestedIntegral(i => if (i == BigInt(1)) Valid else Invalid("not one")),

      "a2" -> arrayOf(JsObjSpec("a" -> int,
                                "b" -> str
                                ),
                      nullable = true
                      ),
      "b2" -> arrayOf(JsObjSpec("a" -> int,
                                "b" -> str
                                ),
                      elemNullable = true
                      ),
      "c2" -> arrayOf(JsObjSpec("a" -> int,
                                "b" -> str
                                )
                      ),
      "a3" -> JsArraySpec(str,
                          int,
                          any
                          )
      )

    val o = JsObj(
      "a" -> JsArray("a",
                     1,
                     true,
                     1.5,
                     JsObj.empty,
                     JsArray.empty
                     ),
      "b" -> JsNull,
      "c" -> JsArray("a",
                     1,
                     true,
                     1.5,
                     JsObj.empty,
                     JsArray.empty,
                     JsNull
                     ),
      "d" -> JsArray(1,
                     Long.MaxValue,
                     BigInt(10000000),
                     BigDecimal("1.567"),
                     1.5,
                     JsNull
                     ),
      "e" -> JsNull,
      "g" -> JsArray("a",
                     1,
                     true
                     ),
      "h" -> JsNull,
      "i" -> JsArray(2,
                     4,
                     6,
                     JsNull
                     ),
      "j" -> JsArray(2,
                     4,
                     6
                     ),
      "k" -> JsNull,
      "l" -> JsArray(2,
                     4,
                     6,
                     JsNull
                     ),
      "m" -> JsArray(2,
                     4,
                     6
                     ),
      "n" -> JsNull,
      "o" -> JsArray("bbc",
                     "bd",
                     JsNull
                     ),
      "p" -> JsArray("cbc",
                     "cd",
                     "c3"
                     ),
      "r" -> JsNull,
      "s" -> JsArray(12.3,
                     13.5,
                     15.0,
                     JsNull
                     ),
      "t" -> JsArray(1.0,
                     2.0,
                     3.0
                     ),
      "u" -> JsNull,
      "v" -> JsArray(JsObj("a" -> 1),
                     JsObj("b" -> 2),
                     JsNull,
                     JsNull
                     ),
      "w" -> JsArray(JsObj("a" -> 1),
                     JsObj("b" -> 2)
                     ),

      "x" -> JsNull,
      "y" -> JsArray(2,
                     4,
                     6,
                     JsNull
                     ),
      "z" -> JsArray(2,
                     4,
                     6
                     ),

      "a1" -> JsNull,
      "b1" -> JsArray(BigInt(1),
                      BigInt(1),
                      BigInt(1),
                      JsNull
                      ),
      "c1" -> JsArray(BigInt(1),
                      BigInt(1),
                      BigInt(1)
                      ),

      "a2" -> JsNull,
      "b2" -> JsArray(JsObj("a" -> 1,
                            "b" -> "hi"
                            ),
                      JsNull,
                      JsObj("a" -> 1,
                            "b" -> "hi"
                            ),
                      JsNull
                      ),
      "c2" -> JsArray(JsObj("a" -> 1,
                            "b" -> "hi"
                            ),
                      JsObj("a" -> 1,
                            "b" -> "hi"
                            )
                      ),

      "a3" -> JsArray("a",
                      1,
                      JsObj.empty
                      )


      )

    val parser = JsObjParser(spec)

    val parsed = JsObj.parse(o.toPrettyString,
                             parser
                             )

    assert(parsed == Try(o))


    assert(o.validate(spec).isEmpty)

  }

  "" should "" in
  {

    val spec = JsArraySpec(JsArraySpecs.conforms(JsArraySpec(str)),
                           arrayOf(JsObjSpec("a" -> str,
                                             "b" -> int
                                             )
                                   )
                           )

    val parser = JsArrayParser(spec)

    val array = JsArray(JsArray("a"),
                        JsArray(JsObj("a" -> "hi",
                                      "b" -> 1
                                      )
                                )
                        )

    assert(JsArray.parse(array.toPrettyString,
                         parser
                         ) == Try(array)
           )

  }

  "spec implicits" should "convert primitive types into specs" in
  {
    val spec = JsArraySpec(1,
                           "2",
                           BigDecimal(1.2),
                           Long.MaxValue,
                           BigInt(100),
                           JsObj.empty,
                           JsArray.empty
                           )

    val parser = JsArrayParser(spec)

    val array = JsArray(1,
                        "2",
                        BigDecimal(1.2),
                        Long.MaxValue,
                        BigInt(100),
                        JsObj.empty,
                        JsArray.empty
                        )

    println(array.toPrettyString)
    assert(JsArray.parse(array.toString,
                         parser
                         ) == Try(array)
           )
  }
}
