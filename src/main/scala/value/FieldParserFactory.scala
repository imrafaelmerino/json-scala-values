package value

import java.util.function.Function

import com.dslplatform.json.{DslJson, JsonReader, ParsingException}
import com.dslplatform.json.derializers.arrays.{JsArrayDeserializer, JsArrayOfBoolDeserializer, JsArrayOfDecimalDeserializer, JsArrayOfDoubleDeserializer, JsArrayOfIntDeserializer, JsArrayOfIntegralDeserializer, JsArrayOfLongDeserializer, JsArrayOfNumberDeserializer, JsArrayOfObjDeserializer, JsArrayOfStringDeserializer, JsArrayOfValueDeserializer}
import com.dslplatform.json.derializers.types.{JsBoolDeserializer, JsDecimalDeserializer, JsDoubleDeserializer, JsIntDeserializer, JsIntegralDeserializer, JsLongDeserializer, JsNumberDeserializer, JsObjDeserializer, JsStrDeserializer, JsTypeDeserializer, JsValueDeserializer}
import value.spec.{Invalid, Result, Valid}

object FieldParserFactory
{


  type R = JsonReader[_]
  type DesFn = Function[R, JsValue]

  val dslJson = new DslJson()
  val intParser = new JsIntDeserializer
  val longParser = new JsLongDeserializer
  val doubleParser = new JsDoubleDeserializer
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
  val arrayOfDoubleParser = new JsArrayOfDoubleDeserializer(doubleParser)
  val arrayOfDecimalParser = new JsArrayOfDecimalDeserializer(decimalParser)
  val arrayOfIntegralParser = new JsArrayOfIntegralDeserializer(integralParser)
  val arrayOfNumberParser = new JsArrayOfNumberDeserializer(numberParser)
  val arrayOfObjParser = new JsArrayOfObjDeserializer(objParser)
  val arrayOfStrParser = new JsArrayOfStringDeserializer(strParser)
  val arrayOfBoolParser = new JsArrayOfBoolDeserializer(boolParser)

  val newParseException: (R, Invalid) => ParsingException = (reader: R, r: Invalid) => reader.newParseError(r.messages.mkString(","))


  def ofInt(nullable: Boolean): DesFn = getDeserializer(intParser,
                                                        nullable
                                                        )

  def ofIntSuchThat(predicate: Int => Result,
                    nullable : Boolean
                   ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = intParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsInt.value)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsint = intParser.value(reader)
      predicate(jsint.value).orExceptionIfInvalid(jsint,
                                                  newParseException(reader,
                                                                    _
                                                                    )
                                                  )
    }
  }

  def ofArrayOfInt(nullable        : Boolean,
                   eachElemNullable: Boolean
                  ): DesFn = getDeserializer(arrayOfIntParser,
                                             nullable,
                                             eachElemNullable
                                             )


  def ofArrayOfIntSuchThat(p               : JsArray => Result,
                           nullable        : Boolean,
                           eachElemNullable: Boolean
                          ): DesFn = getDeserializer(arrayOfIntParser,
                                                     p,
                                                     nullable,
                                                     eachElemNullable
                                                     )

  def ofArrayOfIntEachSuchThat(p               : Int => Result,
                               nullable        : Boolean,
                               eachElemNullable: Boolean
                              ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfIntParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                      (value: Int) => p(value)
                                                                                                      )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfIntParser.nullOrArrayEachSuchThat(reader,
                                                                                                    (value: Int) => p(value)
                                                                                                    )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfIntParser.arrayWithNullEachSuchThat(reader,
                                                                                                      (value: Int) => p(value)
                                                                                                      )
    else (reader: R) => arrayOfIntParser.arrayEachSuchThat(reader,
                                                           (value: Int) => p(value)
                                                           )
  }

  def ofLong(nullable: Boolean): DesFn = getDeserializer(longParser,
                                                         nullable
                                                         )

  def ofLongSuchThat(predicate: Long => Result,
                     nullable : Boolean
                    ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = longParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsLong.value)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jslong = longParser.value(reader)
      predicate(jslong.value).orExceptionIfInvalid(jslong,
                                                   newParseException(reader,
                                                                     _
                                                                     )
                                                   )
    }
  }

  def ofArrayOfLong(nullable        : Boolean,
                    eachElemNullable: Boolean
                   ): DesFn = getDeserializer(arrayOfLongParser,
                                              nullable,
                                              eachElemNullable
                                              )

  def ofArrayOfLongEachSuchThat(p               : Long => Result,
                                nullable        : Boolean,
                                eachElemNullable: Boolean
                               ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfLongParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                       (value: Long) => p(value)
                                                                                                       )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfLongParser.nullOrArrayEachSuchThat(reader,
                                                                                                     (value: Long) => p(value)
                                                                                                     )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfLongParser.arrayWithNullEachSuchThat(reader,
                                                                                                       (value: Long) => p(value)
                                                                                                       )
    else (reader: R) => arrayOfLongParser.arrayEachSuchThat(reader,
                                                            (value: Long) => p(value)
                                                            )
  }

  def ofArrayOfLongSuchThat(p               : JsArray => Result,
                            nullable        : Boolean,
                            eachElemNullable: Boolean
                           ): DesFn = getDeserializer(arrayOfLongParser,
                                                      p,
                                                      nullable,
                                                      eachElemNullable
                                                      )

  def ofDouble(nullable: Boolean): DesFn = getDeserializer(doubleParser,
                                                           nullable
                                                           )

  def ofDoubleSuchThat(predicate: Double => Result,
                       nullable : Boolean
                      ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = doubleParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsDouble.value)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsdouble = doubleParser.value(reader)
      predicate(jsdouble.value).orExceptionIfInvalid(jsdouble,
                                                     newParseException(reader,
                                                                       _
                                                                       )
                                                     )
    }
  }

  def ofArrayOfDouble(nullable        : Boolean,
                      eachElemNullable: Boolean
                     ): DesFn = getDeserializer(arrayOfDoubleParser,
                                                nullable,
                                                eachElemNullable
                                                )

  def ofArrayOfDoubleEachSuchThat(p               : Double => Result,
                                  nullable        : Boolean,
                                  eachElemNullable: Boolean
                                 ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfDoubleParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                         (value: Double) => p(value)
                                                                                                         )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfDoubleParser.nullOrArrayEachSuchThat(reader,
                                                                                                       (value: Double) => p(value)
                                                                                                       )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfDoubleParser.arrayWithNullEachSuchThat(reader,
                                                                                                         (value: Double) => p(value)
                                                                                                         )
    else (reader: R) => arrayOfDoubleParser.arrayEachSuchThat(reader,
                                                              (value: Double) => p(value)
                                                              )
  }

  def ofArrayOfDoubleSuchThat(p               : JsArray => Result,
                              nullable        : Boolean,
                              eachElemNullable: Boolean
                             ): DesFn = getDeserializer(arrayOfDoubleParser,
                                                        p,
                                                        nullable,
                                                        eachElemNullable
                                                        )

  def ofDecimal(nullable: Boolean): DesFn = getDeserializer(decimalParser,
                                                            nullable
                                                            )

  def ofDecimalSuchThat(predicate: BigDecimal => Result,
                        nullable : Boolean
                       ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = decimalParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsBigDec.value)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsdecimal = decimalParser.value(reader)
      predicate(jsdecimal.value).orExceptionIfInvalid(jsdecimal,
                                                      newParseException(reader,
                                                                        _
                                                                        )
                                                      )
    }
  }

  def ofArrayOfDecimal(nullable        : Boolean,
                       eachElemNullable: Boolean
                      ): DesFn = getDeserializer(arrayOfDecimalParser,
                                                 nullable,
                                                 eachElemNullable
                                                 )

  def ofArrayOfDecimalEachSuchThat(p               : BigDecimal => Result,
                                   nullable        : Boolean,
                                   eachElemNullable: Boolean
                                  ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfDecimalParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                          (value: java.math.BigDecimal) => p(value)
                                                                                                          )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfDecimalParser.nullOrArrayEachSuchThat(reader,
                                                                                                        (value: java.math.BigDecimal) => p(value)
                                                                                                        )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfDecimalParser.arrayWithNullEachSuchThat(reader,
                                                                                                          (value: java.math.BigDecimal) => p(value)
                                                                                                          )
    else (reader: R) => arrayOfDecimalParser.arrayEachSuchThat(reader,
                                                               (value: java.math.BigDecimal) => p(value)
                                                               )
  }

  def ofArrayOfDecimalSuchThat(p               : JsArray => Result,
                               nullable        : Boolean,
                               eachElemNullable: Boolean
                              ): DesFn = getDeserializer(arrayOfDecimalParser,
                                                         p,
                                                         nullable,
                                                         eachElemNullable
                                                         )


  def ofIntegral(nullable: Boolean): DesFn = getDeserializer(integralParser,
                                                             nullable
                                                             )

  def ofIntegralSuchThat(predicate: BigInt => Result,
                         nullable : Boolean
                        ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = integralParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsBigInt.value)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsbigint = integralParser.value(reader)
      predicate(jsbigint.value).orExceptionIfInvalid(jsbigint,
                                                     newParseException(reader,
                                                                       _
                                                                       )
                                                     )
    }
  }

  def ofArrayOfIntegral(nullable        : Boolean,
                        eachElemNullable: Boolean
                       ): DesFn = getDeserializer(arrayOfIntegralParser,
                                                  nullable,
                                                  eachElemNullable
                                                  )

  def ofArrayOfIntegralEachSuchThat(p               : BigInt => Result,
                                    nullable        : Boolean,
                                    eachElemNullable: Boolean
                                   ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfIntegralParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                           (value: java.math.BigInteger) => p(value)
                                                                                                           )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfIntegralParser.nullOrArrayEachSuchThat(reader,
                                                                                                         (value: java.math.BigInteger) => p(value)
                                                                                                         )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfIntegralParser.arrayWithNullEachSuchThat(reader,
                                                                                                           (value: java.math.BigInteger) => p(value)
                                                                                                           )
    else (reader: R) => arrayOfIntegralParser.arrayEachSuchThat(reader,
                                                                (value: java.math.BigInteger) => p(value)
                                                                )
  }

  def ofArrayOfIntegralSuchThat(p               : JsArray => Result,
                                nullable        : Boolean,
                                eachElemNullable: Boolean
                               ): DesFn = getDeserializer(arrayOfIntegralParser,
                                                          p,
                                                          nullable,
                                                          eachElemNullable
                                                          )

  def ofNumber(nullable: Boolean): DesFn = getDeserializer(numberParser,
                                                           nullable
                                                           )

  def ofNumberSuchThat(predicate: JsNumber => Result,
                       nullable : Boolean
                      ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = numberParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsNumber)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsnumber = numberParser.value(reader)
      predicate(jsnumber).orExceptionIfInvalid(jsnumber,
                                               newParseException(reader,
                                                                 _
                                                                 )
                                               )
    }
  }

  def ofArrayOfNumber(nullable        : Boolean,
                      eachElemNullable: Boolean
                     ): DesFn = getDeserializer(arrayOfNumberParser,
                                                nullable,
                                                eachElemNullable
                                                )

  def ofArrayOfNumberEachSuchThat(p               : JsNumber => Result,
                                  nullable        : Boolean,
                                  eachElemNullable: Boolean
                                 ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfNumberParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                         (value: JsNumber) => p(value)
                                                                                                         )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfNumberParser.nullOrArrayEachSuchThat(reader,
                                                                                                       (value: JsNumber) => p(value)
                                                                                                       )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfNumberParser.arrayWithNullEachSuchThat(reader,
                                                                                                         (value: JsNumber) => p(value)
                                                                                                         )
    else (reader: R) => arrayOfNumberParser.arrayEachSuchThat(reader,
                                                              (value: JsNumber) => p(value)
                                                              )
  }

  def ofArrayOfNumberSuchThat(p               : JsArray => Result,
                              nullable        : Boolean,
                              eachElemNullable: Boolean
                             ): DesFn = getDeserializer(arrayOfNumberParser,
                                                        p,
                                                        nullable,
                                                        eachElemNullable
                                                        )

  def ofStr(nullable: Boolean): DesFn = getDeserializer(strParser,
                                                        nullable
                                                        )

  def ofStrSuchThat(predicate: String => Result,
                    nullable : Boolean
                   ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = strParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsStr.value)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsstring = strParser.value(reader)
      predicate(jsstring.value).orExceptionIfInvalid(jsstring,
                                                     newParseException(reader,
                                                                       _
                                                                       )
                                                     )
    }
  }

  def ofArrayOfStr(nullable        : Boolean,
                   eachElemNullable: Boolean
                  ): DesFn = getDeserializer(arrayOfStrParser,
                                             nullable,
                                             eachElemNullable
                                             )

  def ofArrayOfStrEachSuchThat(p               : String => Result,
                               nullable        : Boolean,
                               eachElemNullable: Boolean
                              ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfStrParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                      (value: String) => p(value)
                                                                                                      )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfStrParser.nullOrArrayEachSuchThat(reader,
                                                                                                    (value: String) => p(value)
                                                                                                    )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfStrParser.arrayWithNullEachSuchThat(reader,
                                                                                                      (value: String) => p(value)
                                                                                                      )
    else (reader: R) => arrayOfStrParser.arrayEachSuchThat(reader,
                                                           (value: String) => p(value)
                                                           )
  }

  def ofArrayOfStrSuchThat(p               : JsArray => Result,
                           nullable        : Boolean,
                           eachElemNullable: Boolean
                          ): DesFn = getDeserializer(arrayOfStrParser,
                                                     p,
                                                     nullable,
                                                     eachElemNullable
                                                     )

  def ofBool(nullable: Boolean): DesFn = getDeserializer(boolParser,
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

  def ofArrayOfBool(nullable        : Boolean,
                    eachElemNullable: Boolean
                   ): DesFn = getDeserializer(arrayOfBoolParser,
                                              nullable,
                                              eachElemNullable
                                              )

  def ofArrayOfBoolSuchThat(p: JsArray => Result,
                            nullable: Boolean,
                            eachElemNullable: Boolean
                           ): DesFn = getDeserializer(arrayOfBoolParser,
                                                      p,
                                                      nullable,
                                                      eachElemNullable
                                                      )

  def ofValue(nullable: Boolean): DesFn = getDeserializer(valueParser,
                                                          nullable
                                                          )

  def ofValueSuchThat(predicate: JsValue => Result,
                      nullable    : Boolean
                     ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = valueParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsvalue = integralParser.value(reader)
      predicate(jsvalue).orExceptionIfInvalid(jsvalue,
                                              newParseException(reader,
                                                                _
                                                                )
                                              )
    }
  }

  def ofArrayOfValue(nullable: Boolean,
                     eachElemNullable: Boolean
                    ): DesFn = getDeserializer(arrayOfValueParser,
                                               nullable,
                                               eachElemNullable
                                               )

  def ofArrayOfValueEachSuchThat(p: JsValue => Result,
                                 nullable: Boolean,
                                 eachElemNullable: Boolean
                                ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfValueParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                        (value: JsValue) => p(value)
                                                                                                        )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfValueParser.nullOrArrayEachSuchThat(reader,
                                                                                                      (value: JsValue) => p(value)
                                                                                                      )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfValueParser.arrayWithNullEachSuchThat(reader,
                                                                                                        (value: JsValue) => p(value)
                                                                                                        )
    else (reader: R) => arrayOfValueParser.arrayEachSuchThat(reader,
                                                             (value: JsValue) => p(value)
                                                             )
  }

  def ofArrayOfValueSuchThat(p: JsArray => Result,
                             nullable: Boolean,
                             eachElemNullable: Boolean
                            ): DesFn = getDeserializer(arrayOfValueParser,
                                                       p,
                                                       nullable,
                                                       eachElemNullable
                                                       )

  def ofObj(nullable: Boolean): DesFn = getDeserializer(objParser,
                                                        nullable
                                                        )

  def ofObjSuchThat(predicate     : JsObj => Result,
                    nullable      : Boolean
                   ): DesFn =
  {

    if (nullable) (reader: R) =>
    {
      val jsval = objParser.nullOrValue(reader)
      jsval.mapIfNotNull[Result](Valid,
                                 v => predicate(v.asJsObj)
                                 ).orExceptionIfInvalid(jsval,
                                                        newParseException(reader,
                                                                          _
                                                                          )
                                                        )
    }
    else (reader: R) =>
    {
      val jsobject = objParser.value(reader)
      predicate(jsobject).orExceptionIfInvalid(jsobject,
                                               newParseException(reader,
                                                                 _
                                                                 )
                                               )
    }
  }

  def ofArrayOfObj(nullable  : Boolean,
                   eachElemNullable: Boolean
                  ): DesFn = getDeserializer(arrayOfObjParser,
                                             nullable,
                                             eachElemNullable
                                             )

  def ofArrayOfObjEachSuchThat(p: JsObj => Result,
                               nullable: Boolean,
                               eachElemNullable: Boolean
                              ): DesFn =
  {
    if (nullable && eachElemNullable) (reader: R) => arrayOfObjParser.nullOrArrayWithNullEachSuchThat(reader,
                                                                                                      (value: JsObj) => p(value)
                                                                                                      )
    else if (nullable && !eachElemNullable) (reader: R) => arrayOfObjParser.nullOrArrayEachSuchThat(reader,
                                                                                                    (value: JsObj) => p(value)
                                                                                                    )
    else if (!nullable && eachElemNullable) (reader: R) => arrayOfObjParser.arrayWithNullEachSuchThat(reader,
                                                                                                      (value: JsObj) => p(value)
                                                                                                      )
    else (reader: R) => arrayOfObjParser.arrayEachSuchThat(reader,
                                                           (value: JsObj) => p(value)
                                                           )
  }

  def ofArrayOfObjSuchThat(p  : JsArray => Result,
                           nullable: Boolean,
                           eachElemNullable: Boolean
                          ): DesFn = getDeserializer(arrayOfObjParser,
                                                     p,
                                                     nullable,
                                                     eachElemNullable
                                                     )


  def ofArray(nullable: Boolean,
              eachElemNullable: Boolean
             ): DesFn = ???


  private def getDeserializer(deserializer: JsTypeDeserializer,
                              nullable: Boolean
                             ): DesFn =
  {
    if (nullable)
      (reader: R) => deserializer.nullOrValue(reader)
    else
      (reader: R) => deserializer.value(reader)
  }

  private def getDeserializer(deserializer: JsArrayDeserializer,
                              nullable       : Boolean,
                              eachElemNullable: Boolean
                             ): DesFn =
  {
    if (nullable && eachElemNullable)
      (reader: R) => deserializer.nullOrArrayWithNull(reader)
    else if (nullable && !eachElemNullable)
      (reader: R) => deserializer.nullOrArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: R) => deserializer.arrayWithNull(reader)
    else (reader: R) => deserializer.array(reader)
  }

  private def getDeserializer(deserializer: JsArrayDeserializer,
                              p: JsArray => Result,
                              nullable               : Boolean,
                              eachElemNullable       : Boolean
                             ): DesFn =
  {
    if (nullable && eachElemNullable)
      (reader: R) => deserializer.nullOrArrayWithNullSuchThat(reader,
                                                              (arr                 : JsArray) => p(arr)
                                                              )
    else if (nullable && !eachElemNullable)
      (reader: R) => deserializer.nullOrArraySuchThat(reader,
                                                      (arr                 : JsArray) => p(arr)
                                                      )
    else if (!nullable && eachElemNullable)
      (reader: R) => deserializer.arrayWithNullSuchThat(reader,
                                                        (arr                 : JsArray) => p(arr)
                                                        )
    else (reader: R) => deserializer.arraySuchThat(reader,
                                                   (arr                 : JsArray) => p(arr)
                                                   )
  }


}
