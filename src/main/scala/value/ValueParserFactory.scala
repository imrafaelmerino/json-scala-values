package value

import java.util.function.Function

import com.dslplatform.json.derializers.arrays._
import com.dslplatform.json.derializers.specs.{JsArrayOfObjSpecDeserializer, JsArraySpecDeserializer, JsObjSpecDeserializer, JsObjSpecWithRequiredKeysDeserializer}
import com.dslplatform.json.derializers.types._
import com.dslplatform.json.{JsonReader, ParsingException}
import value.spec.{Invalid, Result}

import scala.collection.immutable.Map

private[value] object ValueParserFactory
{


  type R = JsonReader[_]
  type ValueParser = Function[R, JsValue]

  val intParser = new JsIntDeserializer
  val longParser = new JsLongDeserializer
  val integralParser = new JsIntegralDeserializer
  val boolParser = new JsBoolDeserializer
  val decimalParser = new JsDecimalDeserializer
  val strParser = new JsStrDeserializer
  val numberParser = new JsNumberDeserializer

  val valueParser = new JsValueDeserializer
  val objParser = new JsObjDeserializer(valueParser)
  val arrayOfValueParser = new JsArrayOfValueDeserializer(valueParser)
  valueParser.setArrayDeserializer(arrayOfValueParser)
  valueParser.setObjDeserializer(objParser)
  valueParser.setNumberDeserializer(numberParser)

  val arrayOfIntParser = new JsArrayOfIntDeserializer(intParser)
  val arrayOfLongParser = new JsArrayOfLongDeserializer(longParser)
  val arrayOfDecimalParser = new JsArrayOfDecimalDeserializer(decimalParser)
  val arrayOfIntegralParser = new JsArrayOfIntegralDeserializer(integralParser)
  val arrayOfNumberParser = new JsArrayOfNumberDeserializer(numberParser)
  val arrayOfObjParser = new JsArrayOfObjDeserializer(objParser)
  val arrayOfStrParser = new JsArrayOfStringDeserializer(strParser)
  val arrayOfBoolParser = new JsArrayOfBoolDeserializer(boolParser)

  val newParseException: (R, Invalid) => ParsingException = (reader: R, r: Invalid) => reader.newParseError(r.message)


  def ofInt(nullable: Boolean): ValueParser = getDeserializer(intParser,
                                                              nullable
                                                              )

  def ofIntSuchThat(predicate: Int => Result,
                    nullable : Boolean
                   ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value: JsValue = intParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[Int](value => value.isInt,
                                                           value => value.toJsInt.value,
                                                           predicate,
                                                           () => InternalError.integerWasExpected("JsIntDeserializer.nullOrValue didn't return neither null or a Int as expected."),
                                                           result => newParseException(reader,
                                                                                       result
                                                                                       )
                                                           )(value)

    }
    else (reader: R) =>
    {
      val int = intParser.value(reader)
      predicate(int.value).fold(int)(i => throw newParseException(reader,
                                                                  i
                                                                  )
                                     )
    }
  }

  def ofArrayOfInt(nullable: Boolean,
                   elemNullable: Boolean
                  ): ValueParser = getDeserializer(arrayOfIntParser,
                                                   nullable,
                                                   elemNullable
                                                   )


  def ofArrayOfIntSuchThat(p: JsArray => Result,
                           nullable    : Boolean,
                           elemNullable: Boolean
                          ): ValueParser = getDeserializer(arrayOfIntParser,
                                                           p,
                                                           nullable,
                                                           elemNullable
                                                           )

  def ofArrayOfIntEachSuchThat(p: Int => Result,
                               nullable    : Boolean,
                               elemNullable: Boolean
                              ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) => arrayOfIntParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                  (value: Int) => p(value)
                                                                                                  )
    else if (nullable && !elemNullable) (reader: R) => arrayOfIntParser.nullOrArrayEachSuchThat(reader,
                                                                                                (value: Int) => p(value)
                                                                                                )
    else if (!nullable && elemNullable) (reader: R) => arrayOfIntParser.arrayWithNullEachSuchThat(reader,
                                                                                                  (value: Int) => p(value)
                                                                                                  )
    else (reader: R) => arrayOfIntParser.arrayEachSuchThat(reader,
                                                           (value: Int) => p(value)
                                                           )
  }

  def ofLong(nullable: Boolean): ValueParser = getDeserializer(longParser,
                                                               nullable
                                                               )

  def ofLongSuchThat(predicate: Long => Result,
                     nullable : Boolean
                    ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value = longParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[Long](value => value.isLong,
                                                            value => value.toJsLong.value,
                                                            predicate,
                                                            () => InternalError.longWasExpected("JsLongDeserializer.nullOrValue didn't return neither null or a JsLong as expected."),
                                                            result => newParseException(reader,
                                                                                        result
                                                                                        )
                                                            )(value)
    }
    else (reader: R) =>
    {
      val long = longParser.value(reader)
      predicate(long.value).fold(long)(i => throw newParseException(reader,
                                                                    i
                                                                    )
                                       )
    }
  }

  def ofArrayOfLong(nullable: Boolean,
                    elemNullable: Boolean
                   ): ValueParser = getDeserializer(arrayOfLongParser,
                                                    nullable,
                                                    elemNullable
                                                    )

  def ofArrayOfLongEachSuchThat(p: Long => Result,
                                nullable    : Boolean,
                                elemNullable: Boolean
                               ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) =>
      arrayOfLongParser.nullOrArrayWithNullEachSuchThat(reader,
                                                        (value: Long) => p(value)
                                                        )
    else if (nullable && !elemNullable) (reader: R) =>
      arrayOfLongParser.nullOrArrayEachSuchThat(reader,
                                                (value: Long) => p(value)
                                                )
    else if (!nullable && elemNullable) (reader: R) =>
      arrayOfLongParser.arrayWithNullEachSuchThat(reader,
                                                  (value: Long) => p(value)
                                                  )
    else (reader: R) => arrayOfLongParser.arrayEachSuchThat(reader,
                                                            (value: Long) => p(value)
                                                            )
  }

  def ofArrayOfLongSuchThat(p: JsArray => Result,
                            nullable    : Boolean,
                            elemNullable: Boolean
                           ): ValueParser = getDeserializer(arrayOfLongParser,
                                                            p,
                                                            nullable,
                                                            elemNullable
                                                            )


  def ofDecimal(nullable: Boolean): ValueParser = getDeserializer(decimalParser,
                                                                  nullable
                                                                  )

  def ofDecimalSuchThat(predicate: BigDecimal => Result,
                        nullable : Boolean
                       ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value = decimalParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[BigDecimal](value => value.isDecimal,
                                                                  value => value.toJsBigDec.value,
                                                                  predicate,
                                                                  () => InternalError.decimalWasExpected("JsDecimalDeserializer.nullOrValue didn't return neither null or a JsBigDec as expected."),
                                                                  result => newParseException(reader,
                                                                                              result
                                                                                              )
                                                                  )(value)
    }
    else (reader: R) =>
    {
      val decimal = decimalParser.value(reader)
      predicate(decimal.value).fold(decimal)(i => throw newParseException(reader,
                                                                          i
                                                                          )
                                             )
    }
  }

  def ofArrayOfDecimal(nullable: Boolean,
                       elemNullable: Boolean
                      ): ValueParser = getDeserializer(arrayOfDecimalParser,
                                                       nullable,
                                                       elemNullable
                                                       )

  def ofArrayOfDecimalEachSuchThat(p: BigDecimal => Result,
                                   nullable    : Boolean,
                                   elemNullable: Boolean
                                  ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) =>
      arrayOfDecimalParser.nullOrArrayWithNullEachSuchThat(reader,
                                                           (value: java.math.BigDecimal) => p(value)
                                                           )
    else if (nullable && !elemNullable) (reader: R) =>
      arrayOfDecimalParser.nullOrArrayEachSuchThat(reader,
                                                   (value: java.math.BigDecimal) => p(value)
                                                   )
    else if (!nullable && elemNullable) (reader: R) =>
      arrayOfDecimalParser.arrayWithNullEachSuchThat(reader,
                                                     (value: java.math.BigDecimal) => p(value)
                                                     )
    else (reader: R) =>
      arrayOfDecimalParser.arrayEachSuchThat(reader,
                                             (value: java.math.BigDecimal) => p(value)
                                             )
  }

  def ofArrayOfDecimalSuchThat(p: JsArray => Result,
                               nullable    : Boolean,
                               elemNullable: Boolean
                              ): ValueParser = getDeserializer(arrayOfDecimalParser,
                                                               p,
                                                               nullable,
                                                               elemNullable
                                                               )


  def ofIntegral(nullable: Boolean): ValueParser = getDeserializer(integralParser,
                                                                   nullable
                                                                   )

  def ofIntegralSuchThat(predicate: BigInt => Result,
                         nullable: Boolean
                        ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value = integralParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[BigInt](value => value.isBigInt,
                                                              value => value.toJsBigInt.value,
                                                              predicate,
                                                              () => InternalError.integralWasExpected("JsIntegralDeserializer.nullOrValue didn't return neither null or a JsBigInt as expected."),
                                                              result => newParseException(reader,
                                                                                          result
                                                                                          )
                                                              )(value)
    }
    else (reader: R) =>
    {
      val integral = integralParser.value(reader)
      predicate(integral.value).fold(integral)(i => throw newParseException(reader,
                                                                            i
                                                                            )
                                               )
    }
  }

  def ofArrayOfIntegral(nullable: Boolean,
                        elemNullable: Boolean
                       ): ValueParser = getDeserializer(arrayOfIntegralParser,
                                                        nullable,
                                                        elemNullable
                                                        )

  def ofArrayOfIntegralEachSuchThat(p: BigInt => Result,
                                    nullable: Boolean,
                                    elemNullable: Boolean
                                   ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) =>
      arrayOfIntegralParser.nullOrArrayWithNullEachSuchThat(reader,
                                                            (value: java.math.BigInteger) => p(value)
                                                            )
    else if (nullable && !elemNullable) (reader: R) =>
      arrayOfIntegralParser.nullOrArrayEachSuchThat(reader,
                                                    (value: java.math.BigInteger) => p(value)
                                                    )
    else if (!nullable && elemNullable) (reader: R) =>
      arrayOfIntegralParser.arrayWithNullEachSuchThat(reader,
                                                      (value: java.math.BigInteger) => p(value)
                                                      )
    else (reader: R) =>
      arrayOfIntegralParser.arrayEachSuchThat(reader,
                                              (value: java.math.BigInteger) => p(value)
                                              )
  }

  def ofArrayOfIntegralSuchThat(p: JsArray => Result,
                                nullable: Boolean,
                                elemNullable: Boolean
                               ): ValueParser = getDeserializer(arrayOfIntegralParser,
                                                                p,
                                                                nullable,
                                                                elemNullable
                                                                )

  def ofNumber(nullable: Boolean): ValueParser = getDeserializer(numberParser,
                                                                 nullable
                                                                 )

  def ofNumberSuchThat(predicate: JsNumber => Result,
                       nullable: Boolean
                      ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value = numberParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[JsNumber](value => value.isNumber,
                                                                value => value.toJsNumber,
                                                                predicate,
                                                                () => InternalError.numberWasExpected("JsNumberDeserializer.nullOrValue didn't return neither null or a JsNumber as expected."),
                                                                result => newParseException(reader,
                                                                                            result
                                                                                            )
                                                                )(value)

    }
    else (reader: R) =>
    {
      val value = numberParser.value(reader)
      predicate(value).fold(value)(i => throw newParseException(reader,
                                                                i
                                                                )
                                   )
    }
  }

  def ofArrayOfNumber(nullable: Boolean,
                      elemNullable: Boolean
                     ): ValueParser = getDeserializer(arrayOfNumberParser,
                                                      nullable,
                                                      elemNullable
                                                      )

  def ofArrayOfNumberEachSuchThat(p: JsNumber => Result,
                                  nullable: Boolean,
                                  elemNullable: Boolean
                                 ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) =>
      arrayOfNumberParser.nullOrArrayWithNullEachSuchThat(reader,
                                                          (value: JsNumber) => p(value)
                                                          )
    else if (nullable && !elemNullable) (reader: R) =>
      arrayOfNumberParser.nullOrArrayEachSuchThat(reader,
                                                  (value: JsNumber) => p(value)
                                                  )
    else if (!nullable && elemNullable) (reader: R) =>
      arrayOfNumberParser.arrayWithNullEachSuchThat(reader,
                                                    (value: JsNumber) => p(value)
                                                    )
    else (reader: R) =>
      arrayOfNumberParser.arrayEachSuchThat(reader,
                                            (value: JsNumber) => p(value)
                                            )
  }

  def ofArrayOfNumberSuchThat(p: JsArray => Result,
                              nullable: Boolean,
                              elemNullable: Boolean
                             ): ValueParser = getDeserializer(arrayOfNumberParser,
                                                              p,
                                                              nullable,
                                                              elemNullable
                                                              )

  def ofStr(nullable: Boolean): ValueParser = getDeserializer(strParser,
                                                              nullable
                                                              )

  def ofStrSuchThat(predicate: String => Result,
                    nullable: Boolean
                   ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value = strParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[String](value => value.isStr,
                                                              value => value.toJsStr.value,
                                                              predicate,
                                                              () => InternalError.stringWasExpected("JsStrDeserializer.nullOrValue didn't return neither null or a JsStr as expected."),
                                                              result => newParseException(reader,
                                                                                          result
                                                                                          )
                                                              )(value)
    }
    else (reader: R) =>
    {
      val string = strParser.value(reader)
      predicate(string.value).fold(string)(i => throw newParseException(reader,
                                                                        i
                                                                        )
                                           )
    }
  }

  def ofArrayOfStr(nullable: Boolean,
                   elemNullable: Boolean
                  ): ValueParser = getDeserializer(arrayOfStrParser,
                                                   nullable,
                                                   elemNullable
                                                   )

  def ofArrayOfStrEachSuchThat(p: String => Result,
                               nullable: Boolean,
                               elemNullable: Boolean
                              ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) =>
      arrayOfStrParser.nullOrArrayWithNullEachSuchThat(reader,
                                                       (value: String) => p(value)
                                                       )
    else if (nullable && !elemNullable) (reader: R) =>
      arrayOfStrParser.nullOrArrayEachSuchThat(reader,
                                               (value: String) => p(value)
                                               )
    else if (!nullable && elemNullable) (reader: R) =>
      arrayOfStrParser.arrayWithNullEachSuchThat(reader,
                                                 (value: String) => p(value)
                                                 )
    else (reader: R) =>
      arrayOfStrParser.arrayEachSuchThat(reader,
                                         (value: String) => p(value)
                                         )
  }

  def ofArrayOfStrSuchThat(p: JsArray => Result,
                           nullable: Boolean,
                           elemNullable: Boolean
                          ): ValueParser = getDeserializer(arrayOfStrParser,
                                                           p,
                                                           nullable,
                                                           elemNullable
                                                           )

  def ofBool(nullable: Boolean): ValueParser = getDeserializer(boolParser,
                                                               nullable
                                                               )

  def ofTrue(nullable: Boolean
            ): Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => boolParser.nullOrTrue(reader)
    else (reader: JsonReader[_]) => boolParser.True(reader)

  def ofFalse(nullable: Boolean
             ): Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => boolParser.nullOrFalse(reader)
    else (reader: JsonReader[_]) => boolParser.False(reader)

  def ofArrayOfBool(nullable: Boolean,
                    elemNullable: Boolean
                   ): ValueParser = getDeserializer(arrayOfBoolParser,
                                                    nullable,
                                                    elemNullable
                                                    )

  def ofArrayOfBoolSuchThat(p: JsArray => Result,
                            nullable: Boolean,
                            elemNullable: Boolean
                           ): ValueParser = getDeserializer(arrayOfBoolParser,
                                                            p,
                                                            nullable,
                                                            elemNullable
                                                            )

  def ofValue(): ValueParser = getDeserializer(valueParser,
                                               nullable = true
                                               )

  def ofValueSuchThat(predicate: JsValue => Result
                     ): ValueParser =
  {
    (reader: R) =>
    {
      val value = valueParser.nullOrValue(reader)
      if (value == JsNull) value
      else predicate(value).fold[JsValue](value)(i => throw newParseException(reader,
                                                                              i
                                                                              )
                                                 )
    }
  }

  def ofArrayOfValue(nullable: Boolean,
                     elemNullable: Boolean
                    ): ValueParser = getDeserializer(arrayOfValueParser,
                                                     nullable,
                                                     elemNullable
                                                     )

  def ofArrayOfValueEachSuchThat(p: JsValue => Result,
                                 nullable: Boolean,
                                 elemNullable: Boolean
                                ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) =>
      arrayOfValueParser.nullOrArrayWithNullEachSuchThat(reader,
                                                         (value: JsValue) => p(value)
                                                         )
    else if (nullable && !elemNullable) (reader: R) =>
      arrayOfValueParser.nullOrArrayEachSuchThat(reader,
                                                 (value: JsValue) => p(value)
                                                 )
    else if (!nullable && elemNullable) (reader: R) =>
      arrayOfValueParser.arrayWithNullEachSuchThat(reader,
                                                   (value: JsValue) => p(value)
                                                   )
    else (reader: R) => arrayOfValueParser.arrayEachSuchThat(reader,
                                                             (value: JsValue) => p(value)
                                                             )
  }

  def ofArrayOfValueSuchThat(p: JsArray => Result,
                             nullable: Boolean,
                             elemNullable: Boolean
                            ): ValueParser = getDeserializer(arrayOfValueParser,
                                                             p,
                                                             nullable,
                                                             elemNullable
                                                             )

  def ofObj(nullable: Boolean): ValueParser = getDeserializer(objParser,
                                                              nullable
                                                              )

  def ofObjSpec(required: Vector[String],
                keyDeserializers: Map[String, ValueParser],
                nullable: Boolean = false
               ): ValueParser = (reader: R) =>
    if (required.isEmpty)
    {
      val deserializer = new JsObjSpecDeserializer(keyDeserializers
                                                   )
      if (nullable) deserializer.nullOrValue(reader) else deserializer.value(reader)
    }
    else
    {
      val deserializer = new JsObjSpecWithRequiredKeysDeserializer(required,
                                                                   keyDeserializers
                                                                   )
      if (nullable) deserializer.nullOrValue(reader) else deserializer.value(reader)


    }


  def ofArraySpec(keyDeserializers: Vector[ValueParser],
                  nullable: Boolean
                 ): ValueParser =
    if (nullable) (reader: R) => new JsArraySpecDeserializer(keyDeserializers).nullOrArray(reader)
    else (reader: R) => new JsArraySpecDeserializer(keyDeserializers).array(reader)


  def ofObjSuchThat(predicate: JsObj => Result,
                    nullable: Boolean
                   ): ValueParser =
  {

    if (nullable) (reader: R) =>
    {
      val value = objParser.nullOrValue(reader)
      if (value == JsNull) value else testTypeAndSpec[JsObj](value => value.isObj,
                                                             value => value.toJsObj,
                                                             predicate,
                                                             () => InternalError.objWasExpected("JsObjDeserializer.nullOrValue didn't return wither null or a JsObj as expected."),
                                                             result => newParseException(reader,
                                                                                         result
                                                                                         )
                                                             )(value)
    }
    else (reader: R) =>
    {
      val value = objParser.value(reader)
      predicate(value).fold(value)(i => throw newParseException(reader,
                                                                i
                                                                )
                                   )
    }
  }

  def ofArrayOfObj(nullable: Boolean,
                   elemNullable: Boolean
                  ): ValueParser = getDeserializer(arrayOfObjParser,
                                                   nullable,
                                                   elemNullable
                                                   )

  def ofArrayOfObjSpec(required: Vector[String],
                       keyDeserializers: Map[String, ValueParser],
                       nullable: Boolean,
                       elemNullable: Boolean
                      ): ValueParser =
  {

    val deserializer = new JsArrayOfObjSpecDeserializer(
      if (required.isEmpty) new JsObjSpecDeserializer(keyDeserializers
                                                      )
      else new JsObjSpecWithRequiredKeysDeserializer(required,
                                                     keyDeserializers
                                                     )
      )
    if (nullable && elemNullable)
      (reader: R) => deserializer.nullOrArrayWithNull(reader)
    else if (nullable && !elemNullable)
      (reader: R) => deserializer.nullOrArray(reader)
    else if (!nullable && elemNullable)
      (reader: R) => deserializer.arrayWithNull(reader)
    else
      (reader: R) => deserializer.array(reader)

  }

  def ofArrayOfObjEachSuchThat(p: JsObj => Result,
                               nullable: Boolean,
                               elemNullable: Boolean
                              ): ValueParser =
  {
    if (nullable && elemNullable) (reader: R) => arrayOfObjParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                  (value: JsObj) => p(value)
                                                                                                  )
    else if (nullable && !elemNullable) (reader: R) => arrayOfObjParser.nullOrArrayEachSuchThat(reader,
                                                                                                (value: JsObj) => p(value)
                                                                                                )
    else if (!nullable && elemNullable) (reader: R) => arrayOfObjParser.arrayWithNullEachSuchThat(reader,
                                                                                                  (value: JsObj) => p(value)
                                                                                                  )
    else (reader: R) => arrayOfObjParser.arrayEachSuchThat(reader,
                                                           (value: JsObj) => p(value)
                                                           )
  }

  def ofArrayOfObjSuchThat(p: JsArray => Result,
                           nullable: Boolean,
                           elemNullable: Boolean
                          ): ValueParser = getDeserializer(arrayOfObjParser,
                                                           p,
                                                           nullable,
                                                           elemNullable
                                                           )

  private def getDeserializer(deserializer: JsTypeDeserializer,
                              nullable: Boolean
                             ): ValueParser =
  {
    if (nullable)
      (reader: R) => deserializer.nullOrValue(reader)
    else
      (reader: R) => deserializer.value(reader)
  }

  private def getDeserializer(deserializer: JsArrayDeserializer,
                              nullable: Boolean,
                              elemNullable: Boolean
                             ): ValueParser =
  {
    if (nullable && elemNullable)
      (reader: R) => deserializer.nullOrArrayWithNull(reader)
    else if (nullable && !elemNullable)
      (reader: R) => deserializer.nullOrArray(reader)
    else if (!nullable && elemNullable)
      (reader: R) => deserializer.arrayWithNull(reader)
    else (reader: R) => deserializer.array(reader)
  }

  private def getDeserializer(deserializer: JsArrayDeserializer,
                              p: JsArray => Result,
                              nullable: Boolean,
                              elemNullable: Boolean
                             ): ValueParser =
  {
    if (nullable && elemNullable)
      (reader: R) => deserializer.nullOrArrayWithNullSuchThat(reader,
                                                              (arr: JsArray) => p(arr)
                                                              )
    else if (nullable && !elemNullable)
      (reader: R) => deserializer.nullOrArraySuchThat(reader,
                                                      (arr: JsArray) => p(arr)
                                                      )
    else if (!nullable && elemNullable)
      (reader: R) => deserializer.arrayWithNullSuchThat(reader,
                                                        (arr: JsArray) => p(arr)
                                                        )
    else (reader: R) => deserializer.arraySuchThat(reader,
                                                   (arr: JsArray) => p(arr)
                                                   )
  }

  /**
   *
   * @param typeCondition     condition to check if the value has the expected type
   * @param converter         function to convert the value to the expected type
   * @param spec              the specification that the value has to conform
   * @param errorTypeSupplier if the value doesn't have the expected type,
   *                          the error produced by this supplier is thrown. It's considered an internal error
   *                          because if this happened, it would be because a development error
   * @param errorSpecSupplier function that returns the error throw if the value doesnt expect the given spec
   * @tparam R the type of the value (primitive type or JsObj or JsArray or Json)
   * @return a function to test that a value has the expected type and conforms a given spec
   */
  def testTypeAndSpec[R](typeCondition: JsValue => Boolean,
                         converter: JsValue => R,
                         spec: R => Result,
                         errorTypeSupplier: () => InternalError,
                         errorSpecSupplier: Invalid => ParsingException
                        ): JsValue => JsValue =
  {
    (value: JsValue) =>
    {
      if (typeCondition(value))
        spec(converter(value)).fold(value)(invalid => throw errorSpecSupplier(invalid))
      else throw errorTypeSupplier()
    }
  }

}
