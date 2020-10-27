package json.value.specs

import org.scalatest.{Assertions, FlatSpec}
import json.value.{JsArray, JsArrayParser, JsNull, JsObj, JsObjParser, spec}
import json.value.spec.JsNumberSpecs._
import json.value.spec.JsStrSpecs.{str, strSuchThat}
import json.value.spec.{Invalid, JsArraySpec, JsArraySpecs, JsObjSpec, Valid}
import json.value.spec.JsObjSpecs.conforms
import json.value.spec.JsSpecs.any
import json.value.Preamble._
import json.value.spec.Preamble._
import json.value.spec.JsArraySpecs.{array, arrayOf, arrayOfTestedDecimal, arrayOfTestedInt, arrayOfTestedIntegral, arrayOfTestedLong, arrayOfTestedNumber, arrayOfTestedObj, arrayOfTestedStr, arrayOfTestedValue, arraySuchThat}
import json.value.spec.JsBoolSpecs.bool

class ArrayParserSpec extends FlatSpec
{
  "array spec" should "return no error" in
  {

    val spec = JsArraySpec(JsObjSpec("a" -> long(nullable = true)),
                           int,
                           str(nullable = true),
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

    Assertions.assert(parser.parse(array.toPrettyString
                                   ) == Right(array)
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

    Assertions.assert(parser.parse(array.toPrettyString

                                   ) == Right(array)
                      )
  }

  "array deserializers" should "return no error" in
  {

    val spec = JsObjSpec(
      "a" -> array,
      "b" -> array(nullable = true),
      "c" -> array(elemNullable = true),
      "d" -> arrayOfTestedValue(v => if (v.isNumber) Valid else Invalid("not a number"),
                                elemNullable = true
                                ),
      "e" -> arrayOfTestedValue(v => if (v.isNumber) Valid else Invalid("not a number"),
                                nullable = true
                                ),
      "g" -> arraySuchThat(a => if (a.length() == 3) Valid else Invalid("length should be three")),

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

    val o = JsObj("a" -> JsArray("a",
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

    val parsed = parser.parse(o.toPrettyString
                              )

    assert(parsed == Right(o))

    assert(o.validate(spec).isEmpty)

  }

  "array specs" should "return no error" in
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

    assert(parser.parse(array.toPrettyString

                        ) == Right(array)
           )

  }

  "spec implicits" should "convert primitive types into specs" in
  {
    val spec = JsArraySpec(1,
                           "2",
                           1.2d,
                           BigDecimal(1.2),
                           Long.MaxValue,
                           BigInt(100),
                           JsObj.empty,
                           JsArray.empty
                           )

    val parser = JsArrayParser(spec)

    val array = JsArray(1,
                        "2",
                        1.2d,
                        BigDecimal(1.2),
                        Long.MaxValue,
                        BigInt(100),
                        JsObj.empty,
                        JsArray.empty
                        )

    assert(parser.parse(array.toString

                        ) == Right(array)
           )
  }

  "all the elements in a tuple" should "be mandatory" in
  {

    def parser = JsArrayParser(JsArraySpec(str,
                                           int,
                                           bool
                                           )
                               )

    val either = parser.parse("[\"a\",true]")

    assert(either.isLeft)

  }

  "suchThat predicates" should "test the parsed json.value" in
  {

    def parser = JsArrayParser(JsArraySpec(
      intSuchThat(i => if (i > 0) Valid else Invalid("must be greater than 0")),
      strSuchThat(s => if (s.startsWith("a")) Valid else Invalid("must start with a"))
      )
                               )

    val either = parser.parse("[1,\"a\"]")

    assert(either.isRight)


  }
}
