package value

import java.util.function.Function

import com.dslplatform.json.derializers.arrays._
import com.dslplatform.json.derializers.specs.{JsArrayOfObjSpecDeserializer, JsArraySpecDeserializer, JsObjSpecDeserializer, JsObjSpecWithRequiredKeysDeserializer}
import com.dslplatform.json.derializers.types._
import com.dslplatform.json.{JsonReader, ParsingException}
import value.spec.{Invalid, Result}

import scala.collection.immutable.Map
import value.InternalError._
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
  val arrOfValueParser = new JsArrayOfValueDeserializer(valueParser)
  valueParser.setArrayDeserializer(arrOfValueParser)
  valueParser.setObjDeserializer(objParser)
  valueParser.setNumberDeserializer(numberParser)

  val arrOfIntParser = new JsArrayOfIntDeserializer(intParser)
  val arrOfLongParser = new JsArrayOfLongDeserializer(longParser)
  val arrOfDecimalParser = new JsArrayOfDecimalDeserializer(decimalParser)
  val arrOfIntegralParser = new JsArrayOfIntegralDeserializer(integralParser)
  val arrOfNumberParser = new JsArrayOfNumberDeserializer(numberParser)
  val arrOfObjParser = new JsArrayOfObjDeserializer(objParser)
  val arrOfStrParser = new JsArrayOfStringDeserializer(strParser)
  val arrOfBoolParser = new JsArrayOfBoolDeserializer(boolParser)

  val newParseException: (R, Invalid) => ParsingException =
    (reader: R, r: Invalid) => reader.newParseError(r.message)


  def ofInt(nullable: Boolean): ValueParser = getDeserializer(intParser,
                                                              nullable
                                                              )

  def ofIntSuchThat(predicate: Int => Result,
                    nullable : Boolean
                   ): ValueParser =
    if nullable
    then (reader: R) =>
      val value: JsValue = intParser.nullOrValue(reader)
      if value == JsNull
      then value
      else testTypeAndSpec[Int](value => value.isInt,
                                value => value.toJsInt.value,
                                predicate,
                                () => InternalError.integerWasExpected(INT_DESERIALIZER_WRONG_RESULT),
                                result => newParseException(reader,
                                                            result
                                                            )
                                )(value)

    else (reader: R) =>
      val int: JsInt = intParser.value(reader)
      predicate(int.value).fold(int)(i => throw newParseException(reader,
                                                                  i
                                                                  )
                                     )

  def ofArrayOfInt(nullable: Boolean,
                   elemNullable: Boolean
                  ): ValueParser = getDeserializer(arrOfIntParser,
                                                   nullable,
                                                   elemNullable
                                                   )


  def ofArrayOfIntSuchThat(p: JsArray => Result,
                           nullable: Boolean,
                           elemNullable: Boolean
                          ): ValueParser = getDeserializer(arrOfIntParser,
                                                           p,
                                                           nullable,
                                                           elemNullable
                                                           )

  def ofArrayOfIntEachSuchThat(p: Int => Result,
                               nullable: Boolean,
                               elemNullable: Boolean
                              ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfIntParser.nullOrArrayWithNullEachSuchThat(reader, p(_))
    else if nullable && !elemNullable
    then (reader: R) => arrOfIntParser.nullOrArrayEachSuchThat(reader, p(_))
    else if !nullable && elemNullable
    then (reader: R) => arrOfIntParser.arrayWithNullEachSuchThat(reader, p(_))
    else (reader: R) => arrOfIntParser.arrayEachSuchThat(reader, p(_))

  def ofLong(nullable: Boolean): ValueParser = getDeserializer(longParser,
                                                               nullable
                                                               )

  def ofLongSuchThat(predicate: Long => Result,
                     nullable: Boolean
                    ): ValueParser =
    if nullable
    then (reader: R) =>
      val value: JsValue = longParser.nullOrValue(reader)
      if value == JsNull
      then value
      else testTypeAndSpec[Long](value => value.isLong,
                                 value => value.toJsLong.value,
                                 predicate,
                                 () => InternalError.longWasExpected(LONG_DESERIALIZER_WRONG_RESULT) ,
                                 result => newParseException(reader,
                                                             result
                                                             )
                                 )(value)
    else (reader: R) =>
      val long: JsLong = longParser.value(reader)
      (predicate(long.value) fold long)(i => throw newParseException(reader,
                                                                     i
                                                                     )
                                        )

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
    (value: JsValue) =>
      if typeCondition(value)
      then (spec(converter(value)) fold value)(invalid => throw errorSpecSupplier(invalid))
      else throw errorTypeSupplier()

  def ofArrayOfLong(nullable: Boolean,
                    elemNullable: Boolean
                   ): ValueParser = getDeserializer(arrOfLongParser,
                                                    nullable,
                                                    elemNullable
                                                    )

  def ofArrayOfLongEachSuchThat(p: Long => Result,
                                nullable: Boolean,
                                elemNullable: Boolean
                               ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfLongParser.nullOrArrayWithNullEachSuchThat(reader, p(_) )
    else if nullable && !elemNullable
    then (reader: R) => arrOfLongParser.nullOrArrayEachSuchThat(reader, p(_) )
    else if !nullable && elemNullable
    then (reader: R) => arrOfLongParser.arrayWithNullEachSuchThat(reader, p(_) )
    else (reader: R) => arrOfLongParser.arrayEachSuchThat(reader, p(_) )

  def ofArrayOfLongSuchThat(p: JsArray => Result,
                            nullable: Boolean,
                            elemNullable: Boolean
                           ): ValueParser = getDeserializer(arrOfLongParser,
                                                            p,
                                                            nullable,
                                                            elemNullable
                                                            )

  def ofDecimal(nullable: Boolean): ValueParser = getDeserializer(decimalParser,
                                                                  nullable
                                                                  )

  private def getDeserializer(deserializer: JsTypeDeserializer,
                              nullable: Boolean
                             ): ValueParser =
    if nullable
    then (reader: R) => deserializer.nullOrValue(reader)
    else (reader: R) => deserializer.value(reader)

  def ofDecimalSuchThat(predicate: BigDecimal => Result,
                        nullable: Boolean
                       ): ValueParser =
    if nullable
    then (reader: R) =>
      val value: JsValue = decimalParser.nullOrValue(reader)
      if (value == JsNull) value
      else testTypeAndSpec[BigDecimal](value => value.isDecimal,
                                       value => value.toJsBigDec.value,
                                       predicate,
                                       () => InternalError.decimalWasExpected(DECIMAL_DESERIALIZER_WRONG_RESULT),
                                       result => newParseException(reader,
                                                                   result
                                                                   )
                                       )(value)
    else (reader: R) =>
      val decimal: JsBigDec = decimalParser.value(reader)
      predicate(decimal.value).fold(decimal)(i => throw newParseException(reader,
                                                                          i
                                                                          )
                                             )

  def ofArrayOfDecimal(nullable: Boolean,
                       elemNullable: Boolean
                      ): ValueParser = getDeserializer(arrOfDecimalParser,
                                                       nullable,
                                                       elemNullable
                                                       )

  private def getDeserializer(deserializer: JsArrayDeserializer,
                              nullable: Boolean,
                              elemNullable: Boolean
                             ): ValueParser =
    if nullable && elemNullable
    then  (reader: R) => deserializer.nullOrArrayWithNull(reader)
    else if nullable && !elemNullable
    then (reader: R) => deserializer.nullOrArray(reader)
    else if !nullable && elemNullable
    then (reader: R) => deserializer.arrayWithNull(reader)
    else (reader: R) => deserializer.array(reader)

  def ofArrayOfDecimalEachSuchThat(p: BigDecimal => Result,
                                   nullable: Boolean,
                                   elemNullable: Boolean
                                  ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfDecimalParser.nullOrArrayWithNullEachSuchThat(reader, p(_))
    else if nullable && !elemNullable
    then (reader: R) => arrOfDecimalParser.nullOrArrayEachSuchThat(reader, p(_))
    else if !nullable && elemNullable
    then (reader: R) => arrOfDecimalParser.arrayWithNullEachSuchThat(reader, p(_))
    else (reader: R) => arrOfDecimalParser.arrayEachSuchThat(reader, p(_))

  def ofArrayOfDecimalSuchThat(p: JsArray => Result,
                               nullable: Boolean,
                               elemNullable: Boolean
                              ): ValueParser = getDeserializer(arrOfDecimalParser,
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
    if nullable
    then (reader: R) =>
      val value: JsValue = integralParser.nullOrValue(reader)
      if value == JsNull
      then value
      else testTypeAndSpec[BigInt](value => value.isBigInt,
                                   value => value.toJsBigInt.value,
                                   predicate,
                                   () => InternalError.integralWasExpected("JsIntegralDeserializer.nullOrValue didn't return null or JsBigInt as expected."),
                                   result => newParseException(reader,
                                                               result
                                                               )
                                   )(value)
    else (reader: R) =>
      val integral: JsBigInt = integralParser.value(reader)
      predicate(integral.value).fold(integral)(i => throw newParseException(reader,
                                                                            i
                                                                            )
                                               )

  def ofArrayOfIntegral(nullable: Boolean,
                        elemNullable: Boolean
                       ): ValueParser = getDeserializer(arrOfIntegralParser,
                                                        nullable,
                                                        elemNullable
                                                        )

  def ofArrayOfIntegralEachSuchThat(p: BigInt => Result,
                                    nullable: Boolean,
                                    elemNullable: Boolean
                                   ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfIntegralParser.nullOrArrayWithNullEachSuchThat(reader, p(_) )
    else if nullable && !elemNullable
    then (reader: R) => arrOfIntegralParser.nullOrArrayEachSuchThat(reader, p(_) )
    else if !nullable && elemNullable
    then (reader: R) => arrOfIntegralParser.arrayWithNullEachSuchThat(reader, p(_) )
    else (reader: R) => arrOfIntegralParser.arrayEachSuchThat(reader, p(_) )

  def ofArrayOfIntegralSuchThat(p: JsArray => Result,
                                nullable: Boolean,
                                elemNullable: Boolean
                               ): ValueParser = getDeserializer(arrOfIntegralParser,
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
    if nullable
    then (reader: R) =>
      val value: JsValue = numberParser.nullOrValue(reader)
      if value == JsNull
      then value
      else testTypeAndSpec[JsNumber](value => value.isNumber,
                                     value => value.toJsNumber,
                                     predicate,
                                     () => InternalError.numberWasExpected(NUMBER_DESERIALIZER_WRONG_RESULT),
                                     result => newParseException(reader,
                                                                 result
                                                                 )
                                     )(value)

    else (reader: R) =>
      val value: JsNumber = numberParser.value(reader)
      predicate(value).fold(value)(i => throw newParseException(reader,
                                                                i
                                                                )
                                   )

  def ofArrayOfNumber(nullable: Boolean,
                      elemNullable: Boolean
                     ): ValueParser = getDeserializer(arrOfNumberParser,
                                                      nullable,
                                                      elemNullable
                                                      )

  def ofArrayOfNumberEachSuchThat(p: JsNumber => Result,
                                  nullable: Boolean,
                                  elemNullable: Boolean
                                 ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfNumberParser.nullOrArrayWithNullEachSuchThat(reader, p(_))
    else if nullable && !elemNullable
    then (reader: R) => arrOfNumberParser.nullOrArrayEachSuchThat(reader, p(_))
    else if !nullable && elemNullable
    then (reader: R) => arrOfNumberParser.arrayWithNullEachSuchThat(reader, p(_))
    else (reader: R) => arrOfNumberParser.arrayEachSuchThat(reader, p(_))

  def ofArrayOfNumberSuchThat(p: JsArray => Result,
                              nullable: Boolean,
                              elemNullable: Boolean
                             ): ValueParser = getDeserializer(arrOfNumberParser,
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
    if nullable
    then (reader: R) =>
      val value: JsValue = strParser.nullOrValue(reader)
      if value == JsNull
      then value
      else testTypeAndSpec[String](value => value.isStr,
                                   value => value.toJsStr.value,
                                   predicate,
                                   () => InternalError.stringWasExpected(STRING_DESERIALIZER_WRONG_RESULT),
                                   result => newParseException(reader,
                                                               result
                                                               )
                                   )(value)
    else (reader: R) =>
      val string: JsStr = strParser.value(reader)
      predicate(string.value).fold(string)(i => throw newParseException(reader,
                                                                        i
                                                                        )
                                           )

  def ofArrayOfStr(nullable: Boolean,
                   elemNullable: Boolean
                  ): ValueParser = getDeserializer(arrOfStrParser,
                                                   nullable,
                                                   elemNullable
                                                   )

  def ofArrayOfStrEachSuchThat(p: String => Result,
                               nullable: Boolean,
                               elemNullable: Boolean
                              ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfStrParser.nullOrArrayWithNullEachSuchThat(reader, p(_) )
    else if nullable && !elemNullable
    then (reader: R) => arrOfStrParser.nullOrArrayEachSuchThat(reader, p(_) )
    else if !nullable && elemNullable
    then (reader: R) => arrOfStrParser.arrayWithNullEachSuchThat(reader, p(_) )
    else (reader: R) => arrOfStrParser.arrayEachSuchThat(reader, p(_) )

  def ofArrayOfStrSuchThat(p: JsArray => Result,
                           nullable: Boolean,
                           elemNullable: Boolean
                          ): ValueParser = getDeserializer(arrOfStrParser,
                                                           p,
                                                           nullable,
                                                           elemNullable
                                                           )

  def ofBool(nullable: Boolean): ValueParser = getDeserializer(boolParser,
                                                               nullable
                                                               )

  def ofTrue(nullable: Boolean
            ): Function[JsonReader[_], JsValue] =
    if nullable
    then (reader: JsonReader[_]) => boolParser.nullOrTrue(reader)
    else (reader: JsonReader[_]) => boolParser.True(reader)

  def ofFalse(nullable: Boolean
             ): Function[JsonReader[_], JsValue] =
    if nullable
    then (reader: JsonReader[_]) => boolParser.nullOrFalse(reader)
    else (reader: JsonReader[_]) => boolParser.False(reader)

  def ofArrayOfBool(nullable: Boolean,
                    elemNullable: Boolean
                   ): ValueParser = getDeserializer(arrOfBoolParser,
                                                    nullable,
                                                    elemNullable
                                                    )

  def ofArrayOfBoolSuchThat(p: JsArray => Result,
                            nullable: Boolean,
                            elemNullable: Boolean
                           ): ValueParser = getDeserializer(arrOfBoolParser,
                                                            p,
                                                            nullable,
                                                            elemNullable
                                                            )

  def ofValue(): ValueParser = getDeserializer(valueParser,
                                               nullable = true
                                               )

  def ofValueSuchThat(predicate: JsValue => Result
                     ): ValueParser =
    (reader: R) =>
      val value: JsValue = valueParser.nullOrValue(reader)
      if value == JsNull
      then value
      else predicate(value).fold[JsValue](value)(i => throw newParseException(reader,
                                                                              i
                                                                              )
                                                 )

  def ofArrayOfValue(nullable: Boolean,
                     elemNullable: Boolean
                    ): ValueParser = getDeserializer(arrOfValueParser,
                                                     nullable,
                                                     elemNullable
                                                     )

  def ofArrayOfValueEachSuchThat(p: JsValue => Result,
                                 nullable: Boolean,
                                 elemNullable: Boolean
                                ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfValueParser.nullOrArrayWithNullEachSuchThat(reader, p(_))
    else if nullable && !elemNullable
    then (reader: R) => arrOfValueParser.nullOrArrayEachSuchThat(reader, p(_))
    else if !nullable && elemNullable
    then (reader: R) => arrOfValueParser.arrayWithNullEachSuchThat(reader, p(_))
    else (reader: R) => arrOfValueParser.arrayEachSuchThat(reader, p(_))

  def ofArrayOfValueSuchThat(p: JsArray => Result,
                             nullable: Boolean,
                             elemNullable: Boolean
                            ): ValueParser = getDeserializer(arrOfValueParser,
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
    if required.isEmpty
    then
      val deserializer = new JsObjSpecDeserializer(keyDeserializers)
      if nullable
      then deserializer.nullOrValue(reader)
      else deserializer.value(reader)
    else
      val deserializer = new JsObjSpecWithRequiredKeysDeserializer(required,keyDeserializers)
      if nullable
      then deserializer.nullOrValue(reader)
      else deserializer.value(reader)



  def ofArraySpec(keyDeserializers: Vector[ValueParser],
                  nullable: Boolean
                 ): ValueParser =
    if nullable
    then (reader: R) => new JsArraySpecDeserializer(keyDeserializers).nullOrArray(reader)
    else (reader: R) => new JsArraySpecDeserializer(keyDeserializers).array(reader)

  def ofObjSuchThat(predicate: JsObj => Result,
                    nullable: Boolean
                   ): ValueParser =
    if nullable
    then (reader: R) =>
      val value: JsValue = objParser.nullOrValue(reader)
      if value == JsNull
      then value
      else testTypeAndSpec[JsObj](value => value.isObj,
                                  value => value.toJsObj,
                                  predicate,
                                  () => InternalError.objWasExpected(OBJ_DESERIALIZER_WRONG_RESULT) ,
                                  result => newParseException(reader,
                                                              result
                                                              )
                                  )(value)
    else (reader: R) =>
      val value: JsObj = objParser.value(reader)
      predicate(value).fold(value)(i => throw newParseException(reader,
                                                                i
                                                                )
                                   )

  def ofArrayOfObj(nullable: Boolean,
                   elemNullable: Boolean
                  ): ValueParser = getDeserializer(arrOfObjParser,
                                                   nullable,
                                                   elemNullable
                                                   )

  def ofArrayOfObjSpec(required: Vector[String],
                       keyDeserializers: Map[String, ValueParser],
                       nullable: Boolean,
                       elemNullable: Boolean
                      ): ValueParser =
    val deserializer = new JsArrayOfObjSpecDeserializer(
      if required.isEmpty
      then new JsObjSpecDeserializer(keyDeserializers)
      else new JsObjSpecWithRequiredKeysDeserializer(required, keyDeserializers)
      )
    if nullable && elemNullable
    then (reader: R) => deserializer.nullOrArrayWithNull(reader)
    else if nullable && !elemNullable
    then (reader: R) => deserializer.nullOrArray(reader)
    else if !nullable && elemNullable
    then (reader: R) => deserializer.arrayWithNull(reader)
    else (reader: R) => deserializer.array(reader)


  def ofArrayOfObjEachSuchThat(p: JsObj => Result,
                               nullable: Boolean,
                               elemNullable: Boolean
                              ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => arrOfObjParser.nullOrArrayWithNullEachSuchThat(reader,p(_) )
    else if nullable && !elemNullable
    then (reader: R) => arrOfObjParser.nullOrArrayEachSuchThat(reader, p(_) )
    else if !nullable && elemNullable
    then (reader: R) => arrOfObjParser.arrayWithNullEachSuchThat(reader, p(_) )
    else (reader: R) => arrOfObjParser.arrayEachSuchThat(reader, p(_) )

  def ofArrayOfObjSuchThat(p: JsArray => Result,
                           nullable: Boolean,
                           elemNullable: Boolean
                          ): ValueParser = getDeserializer(arrOfObjParser,
                                                           p,
                                                           nullable,
                                                           elemNullable
                                                           )

  private def getDeserializer(deserializer: JsArrayDeserializer,
                              p: JsArray => Result,
                              nullable: Boolean,
                              elemNullable: Boolean
                             ): ValueParser =
    if nullable && elemNullable
    then (reader: R) => deserializer.nullOrArrayWithNullSuchThat(reader, p(_))
    else if nullable && !elemNullable
    then (reader: R) => deserializer.nullOrArraySuchThat(reader, p(_))
    else if !nullable && elemNullable
    then (reader: R) => deserializer.arrayWithNullSuchThat(reader, p(_))
    else (reader: R) => deserializer.arraySuchThat(reader, p(_))

}
