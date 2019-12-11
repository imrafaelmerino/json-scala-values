package value.spec

import java.util.Objects.requireNonNull

import Messages._
import value.{JsArray, JsNull, JsValue}

object JsArraySpecs
{

  val array = IsArray(nullable = false,
                      required = true,
                      eachElemNullable = false
                      )
  val arrayWithNull = IsArray(nullable = false,
                              required = true,
                              eachElemNullable = true
                              )
  val nullOrArray = IsArray(nullable = true,
                            required = true,
                            eachElemNullable = false
                            )

  val nullOrArrayWithNull = IsArray(nullable = true,
                                    required = true,
                                    eachElemNullable = true
                                    )

  val arrayOfInt = IsArrayOfInt(nullable = false,
                                required = true,
                                eachElemNullable = false
                                )
  val arrayOfIntWithNull = IsArrayOfInt(nullable = false,
                                        required = true,
                                        eachElemNullable = true
                                        )
  val nullOrArrayOfIntWithNull = IsArrayOfInt(nullable = true,
                                              required = true,
                                              eachElemNullable = true
                                              )
  val nullOrArrayOfInt = IsArrayOfInt(nullable = true,
                                        required = true,
                                        eachElemNullable = false
                                        )

  val arrayOfLong = IsArrayOfLong(nullable = false,
                                  required = true,
                                  eachElemNullable = false
                                  )
  val arrayOfLongWithNull = IsArrayOfLong(nullable = false,
                                          required = true,
                                          eachElemNullable = true
                                          )
  val nullOrArrayOfLongWithNull = IsArrayOfLong(nullable = true,
                                                required = true,
                                                eachElemNullable = true
                                                )
  val nullOrArrayOfLong = IsArrayOfLong(nullable = true,
                                        required = true,
                                        eachElemNullable = false
                                        )

  val arrayOfDecimal = IsArrayOfDecimal(nullable = false,
                                        required = true,
                                        eachElemNullable = false
                                        )
  val arrayOfDecimalWithNull = IsArrayOfDecimal(nullable = false,
                                                required = true,
                                                eachElemNullable = true
                                                )
  val nullOrArrayOfDecimalWithNull = IsArrayOfDecimal(nullable = true,
                                                      required = true,
                                                      eachElemNullable = true
                                                      )
  val nullOrArrayOfDecimal = IsArrayOfDecimal(nullable = true,
                                              required = true,
                                              eachElemNullable = false
                                              )

  val arrayOfIntegral = IsArrayOfIntegral(nullable = false,
                                          required = true,
                                          eachElemNullable = false
                                          )
  val arrayOfIntegralWithNull = IsArrayOfIntegral(nullable = false,
                                                  required = true,
                                                  eachElemNullable = true
                                                  )
  val nullOrArrayOfIntegralWithNull = IsArrayOfIntegral(nullable = true,
                                                        required = true,
                                                        eachElemNullable = true
                                                        )
  val nullOrArrayOfIntegral = IsArrayOfIntegral(nullable = true,
                                                required = true,
                                                eachElemNullable = false
                                                )

  val arrayOfBool = IsArrayOfBool(nullable = false,
                                  required = true,
                                  eachElemNullable = false
                                  )
  val arrayOfBoolWithNull = IsArrayOfBool(nullable = false,
                                          required = true,
                                          eachElemNullable = true
                                          )
  val nullOrArrayOfBoolWithNull = IsArrayOfBool(nullable = true,
                                                required = true,
                                                eachElemNullable = true
                                                )
  val nullOrArrayOfBool = IsArrayOfBool(nullable = true,
                                        required = true,
                                        eachElemNullable = false
                                        )

  val arrayOfNumber = IsArrayOfNumber(nullable = false,
                                      required = true,
                                      eachElemNullable = false
                                      )
  val arrayOfNumberWithNull = IsArrayOfNumber(nullable = false,
                                              required = true,
                                              eachElemNullable = true
                                              )
  val nullOrArrayOfNumberWithNull = IsArrayOfNumber(nullable = true,
                                                    required = true,
                                                    eachElemNullable = true
                                                    )
  val nullOrArrayOfNumber = IsArrayOfNumber(nullable = true,
                                              required = true,
                                              eachElemNullable = false
                                              )

  val arrayOfStr = IsArrayOfStr(nullable = false,
                                required = true,
                                eachElemNullable = false
                                )
  val arrayOfStrWithNull = IsArrayOfStr(nullable = false,
                                        required = true,
                                        eachElemNullable = true
                                        )
  val nullOrArrayOfStrWithNull = IsArrayOfStr(nullable = true,
                                              required = true,
                                              eachElemNullable = true
                                              )
  val nullOrArrayOfStr = IsArrayOfStr(nullable = true,
                                      required = true,
                                      eachElemNullable = false
                                      )

  val arrayOfObj = IsArrayOfObj(nullable = false,
                                required = true,
                                eachElemNullable = false
                                )

  val arrayOfObjWithNull = IsArrayOfObj(nullable = false,
                                        required = true,
                                        eachElemNullable = true
                                        )
  val nullOrArrayOfObjWithNull = IsArrayOfObj(nullable = true,
                                              required = true,
                                              eachElemNullable = true
                                              )
  val nullOrArrayOfObj = IsArrayOfObj(nullable = true,
                                      required = true,
                                      eachElemNullable = false
                                      )

  def nullOrArrayOfObj(spec: JsObjSpec
                      ) = ArrayOfObjSpec(spec,
                                         nullable = true,
                                         required = true,
                                         eachElemNullable = false
                                         )

  def nullOrArrayOfObjWithNull(spec: JsObjSpec
                              ) = ArrayOfObjSpec(spec,
                                                 nullable = true,
                                                 required = true,
                                                 eachElemNullable = true
                                                 )

  def arrayOfObj(spec: JsObjSpec,
                 nullable: Boolean = false,
                 required: Boolean = false,
                 elemNullable: Boolean = false
                ) = ArrayOfObjSpec(spec,
                                   nullable,
                                   required,
                                   elemNullable
                                   )

  def arrayOfInt(minItems    : Long = -1,
                 maxItems    : Long = -1,
                 unique      : Boolean = false,
                 elemNullable: Boolean = false,
                 nullable: Boolean = false,
                 required: Boolean = false
                ): JsSpec = IsArrayOfIntSuchThat(arraySpec(requireNonNull(minItems),
                                                           requireNonNull(maxItems),
                                                           requireNonNull(unique)
                                                           ),
                                                 requireNonNull(nullable),
                                                 requireNonNull(required),
                                                 requireNonNull(elemNullable)
                                                 )

  def arrayOfBool(minItems   : Long = -1,
                  maxItems   : Long = -1,
                  unique: Boolean = false,
                  elemNullable: Boolean = false,
                  nullable   : Boolean = false,
                  required: Boolean = false
                 ): JsSpec = IsArrayOfBoolSuchThat(arraySpec(requireNonNull(minItems),
                                                             requireNonNull(maxItems),
                                                             requireNonNull(unique)
                                                             ),
                                                   requireNonNull(nullable),
                                                   requireNonNull(required),
                                                   requireNonNull(elemNullable)
                                                   )

  def arrayOfStr(minItems    : Long = -1,
                 maxItems    : Long = -1,
                 unique      : Boolean = false,
                 elemNullable: Boolean = false,
                 nullable    : Boolean = false,
                 required    : Boolean = true
                ): JsSpec = IsArrayOfStrSuchThat(arraySpec(requireNonNull(minItems),
                                                           requireNonNull(maxItems),
                                                           requireNonNull(unique)
                                                           ),
                                                 requireNonNull(nullable),
                                                 requireNonNull(required),
                                                 requireNonNull(elemNullable)
                                                 )

  def arrayOfLong(minItems    : Long = -1,
                  maxItems    : Long = -1,
                  unique: Boolean = false,
                  elemNullable: Boolean = false,
                  nullable    : Boolean = false,
                  required    : Boolean = true
                 ): JsSpec = IsArrayOfLongSuchThat(arraySpec(requireNonNull(minItems),
                                                             requireNonNull(maxItems),
                                                             requireNonNull(unique)
                                                             ),
                                                   requireNonNull(nullable),
                                                   requireNonNull(required),
                                                   requireNonNull(elemNullable)
                                                   )

  def arrayOfDecimal(minItems    : Long = -1,
                     maxItems    : Long = -1,
                     unique: Boolean = false,
                     elemNullable: Boolean = false,
                     nullable    : Boolean = false,
                     required    : Boolean = true
                    ): JsSpec = IsArrayOfDecimalSuchThat(arraySpec(requireNonNull(minItems),
                                                                   requireNonNull(maxItems),
                                                                   requireNonNull(unique)
                                                                   ),
                                                         requireNonNull(nullable),
                                                         requireNonNull(required),
                                                         requireNonNull(elemNullable)
                                                         )

  def arrayOfNumber(minItems    : Long = -1,
                    maxItems    : Long = -1,
                    unique      : Boolean = false,
                    elemNullable: Boolean = false,
                    nullable: Boolean = false,
                    required: Boolean = false
                   ): JsSpec = IsArrayOfNumberSuchThat(arraySpec(requireNonNull(minItems),
                                                                 requireNonNull(maxItems),
                                                                 requireNonNull(unique)
                                                                 ),
                                                       requireNonNull(nullable),
                                                       requireNonNull(required),
                                                       requireNonNull(elemNullable)
                                                       )

  def arrayOf(validator: JsValue => Boolean,
              message  : String
             ): JsSpec = arrayOf(validator,
                                 message,
                                 nullable = false,
                                 required = true
                                 )

  def arrayOf(predicate: JsValue => Boolean,
              message  : String,
              nullable : Boolean,
              required : Boolean
             ): JsSpec =
  {
    requireNonNull(message)
    requireNonNull(predicate)
    IsArrayEachSuchThat((value: JsValue) => if (predicate(value)) Valid else Invalid(message),
                        nullable,
                        required
                        )

  }

  def arrayOfIntegral(minItems    : Long = -1,
                      maxItems    : Long = -1,
                      unique      : Boolean = false,
                      elemNullable: Boolean = false,
                      nullable: Boolean = false,
                      required: Boolean = true
                     ): JsSpec = IsArrayOfIntegralSuchThat(arraySpec(requireNonNull(minItems),
                                                                     requireNonNull(maxItems),
                                                                     requireNonNull(unique)
                                                                     ),
                                                           requireNonNull(nullable),
                                                           requireNonNull(required),
                                                           requireNonNull(elemNullable)
                                                           )


  def array(condition: JsArray => Boolean,
            message  : JsArray => String
           ): JsSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    IsArraySuchThat((array: JsArray) =>
                      if (condition.apply(array)) Valid
                      else Invalid(message(array))
                    )
  }


  //TODO PASAR EL UNIQUE A OBJECTSPEC PARA NO ITERAR DOS VECES
  private def arraySpec(minItems: Long,
                        maxItems: Long,
                        unique  : Boolean
                       ): JsArray => Result =
  {

    arr: JsArray =>
    {
      var errors: Seq[String] = Seq.empty
      val length = arr.length()
      if (minItems != -1 && length < minItems)
        errors = errors :+ LONG_ARRAY_OF_LENGTH_LOWER_THAN_MINIMUM(length,
                                                                   minItems
                                                                   )
      if (maxItems != -1 && length > maxItems)
        errors = errors :+ LONG_ARRAY_OF_LENGTH_GREATER_THAN_MAXIMUM(length,
                                                                     maxItems
                                                                     )
      if (unique && arr.seq.distinct.length != arr.seq.length)
        errors = errors :+ ARRAY_WITH_DUPLICATES
      if (errors.isEmpty) Valid
      else Invalid(errors)
    }

  }
}
