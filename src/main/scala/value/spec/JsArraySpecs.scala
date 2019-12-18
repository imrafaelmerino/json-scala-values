package value.spec

import java.util.Objects.requireNonNull

import value.{JsArray, JsNumber, JsObj, JsValue}

object JsArraySpecs
{

  val array_of_value = IsArray(elemNullable = false)
  val array_of_value_with_nulls = IsArray()
  val array_of_value_or_null = IsArray(nullable = true,
                                       elemNullable = false
                                       )
  val array_with_nulls_or_null = IsArray(nullable = true)

  val array_of_int = IsArrayOfInt(elemNullable = false)
  val array_of_int_with_nulls = IsArrayOfInt()
  val array_of_int_or_null = IsArrayOfInt(nullable = true,
                                          elemNullable = false
                                          )
  val array_of_int_with_nulls_or_null = IsArrayOfInt(nullable = true)

  val array_of_long = IsArrayOfLong(elemNullable = false)
  val array_of_long_with_nulls = IsArrayOfLong()
  val array_of_long_or_null = IsArrayOfLong(nullable = true,
                                            elemNullable = false
                                            )
  val array_of_long_with_nulls_or_null = IsArrayOfLong(nullable = true)

  val array_of_decimal = IsArrayOfDecimal(elemNullable = false)
  val array_of_decimal_with_nulls = IsArrayOfDecimal()
  val array_of_decimal_or_null = IsArrayOfDecimal(nullable = true,
                                                  elemNullable = false
                                                  )
  val array_of_decimal_with_nulls_or_null = IsArrayOfDecimal(nullable = true)

  val array_of_integral = IsArrayOfIntegral(elemNullable = false)
  val array_of_integral_with_nulls = IsArrayOfIntegral()
  val array_of_integral_with_nulls_or_null = IsArrayOfIntegral(nullable = true)
  val array_of_integral_or_null = IsArrayOfIntegral(nullable = true,
                                                    elemNullable = false
                                                    )

  val array_of_bool = IsArrayOfBool(elemNullable = false)
  val array_of_bool_with_nulls = IsArrayOfBool()
  val array_of_bool_with_nulls_or_null = IsArrayOfBool(nullable = true)
  val array_of_bool_or_null = IsArrayOfBool(nullable = true,
                                            elemNullable = false
                                            )

  val array_of_number = IsArrayOfNumber(elemNullable = false)
  val array_of_number_with_nulls = IsArrayOfNumber()
  val array_of_number_with_nulls_or_null = IsArrayOfNumber(nullable = true
                                                           )
  val array_of_number_or_null = IsArrayOfNumber(nullable = true,
                                                elemNullable = false
                                                )

  val array_of_str = IsArrayOfStr(elemNullable = false)
  val array_of_str_with_nulls = IsArrayOfStr()
  val array_of_str_with_nulls_or_null = IsArrayOfStr(nullable = true)
  val array_of_str_or_null = IsArrayOfStr(nullable = true,
                                          elemNullable = false
                                          )

  val array_of_obj = IsArrayOfObj(elemNullable = false)
  val array_of_obj_with_nulls = IsArrayOfObj()
  val array_of_obj_with_nulls_or_null = IsArrayOfObj(nullable = true)
  val array_of_obj_or_null = IsArrayOfObj(nullable = true,
                                          elemNullable = false
                                          )


  def arrayOfObjSpec(spec        : JsObjSpec,
                     nullable    : Boolean = false,
                     required    : Boolean = true,
                     elemNullable: Boolean = false
                    ) = ArrayOfObjSpec(spec,
                                       nullable,
                                       required,
                                       elemNullable
                                       )

  def arrayOfObjSuchThat(p           : JsArray => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ) = IsArrayOfObjSuchThat(p,
                                                 nullable,
                                                 required,
                                                 elemNullable
                                                 )

  def arrayOfTestedObj(p           : JsObj => Result,
                       nullable    : Boolean = false,
                       required    : Boolean = true,
                       elemNullable: Boolean = false
                      ) = IsArrayOfTestedObj(p,
                                             nullable,
                                             required,
                                             elemNullable
                                             )

  def arrayOfIntSuchThat(p           : JsArray => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ): JsSpec = IsArrayOfIntSuchThat(p,
                                                         requireNonNull(nullable),
                                                         requireNonNull(required),
                                                         requireNonNull(elemNullable)
                                                         )

  def arrayOfTestedInt(p           : Int => Result,
                       nullable    : Boolean = false,
                       required    : Boolean = true,
                       elemNullable: Boolean = false
                      ): JsSpec = IsArrayOfTestedInt(p,
                                                     requireNonNull(nullable),
                                                     requireNonNull(required),
                                                     requireNonNull(elemNullable)
                                                     )


  def arrayOfIntegralSuchThat(p           : JsArray => Result,
                              nullable    : Boolean = false,
                              required    : Boolean = true,
                              elemNullable: Boolean = false
                             ): JsSpec = IsArrayOfIntegralSuchThat(p,
                                                                   requireNonNull(nullable),
                                                                   requireNonNull(required),
                                                                   requireNonNull(elemNullable)
                                                                   )

  def arrayOfTestedIntegral(p           : BigInt => Result,
                            nullable    : Boolean = false,
                            required    : Boolean = true,
                            elemNullable: Boolean = false
                           ): JsSpec = IsArrayOfTestedIntegral(p,
                                                               requireNonNull(nullable),
                                                               requireNonNull(required),
                                                               requireNonNull(elemNullable)
                                                               )

  def arrayOfBoolSuchThat(p           : JsArray => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfBoolSuchThat(p,
                                                           requireNonNull(nullable),
                                                           requireNonNull(required),
                                                           requireNonNull(elemNullable)
                                                           )

  def arrayOfStrSuchThat(p           : JsArray => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ): JsSpec = IsArrayOfStrSuchThat(p,
                                                         requireNonNull(nullable),
                                                         requireNonNull(required),
                                                         requireNonNull(elemNullable)
                                                         )

  def arrayOfTestedStr(p           : String => Result,
                       nullable    : Boolean = false,
                       required    : Boolean = true,
                       elemNullable: Boolean = false
                      ): JsSpec = IsArrayOfTestedStr(p,
                                                     requireNonNull(nullable),
                                                     requireNonNull(required),
                                                     requireNonNull(elemNullable)
                                                     )

  def arrayOfLongSuchThat(p           : JsArray => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfLongSuchThat(p,
                                                           requireNonNull(nullable),
                                                           requireNonNull(required),
                                                           requireNonNull(elemNullable)
                                                           )

  def arrayOfTestedLong(p           : Long => Result,
                        nullable    : Boolean = false,
                        required    : Boolean = true,
                        elemNullable: Boolean = false
                       ): JsSpec = IsArrayOfTestedLong(p,
                                                       requireNonNull(nullable),
                                                       requireNonNull(required),
                                                       requireNonNull(elemNullable)
                                                       )

  def arrayOfDecimalSuchThat(p           : JsArray => Result,
                             nullable    : Boolean = false,
                             required    : Boolean = true,
                             elemNullable: Boolean = false
                            ): JsSpec = IsArrayOfDecimalSuchThat(p,
                                                                 requireNonNull(nullable),
                                                                 requireNonNull(required),
                                                                 requireNonNull(elemNullable)
                                                                 )

  def arrayOfTestedDecimal(p           : BigDecimal => Result,
                           nullable    : Boolean = false,
                           required    : Boolean = true,
                           elemNullable: Boolean = false
                          ): JsSpec = IsArrayOfTestedDecimal(p,
                                                             requireNonNull(nullable),
                                                             requireNonNull(required),
                                                             requireNonNull(elemNullable)
                                                             )

  def arrayOfNumberSuchThat(p           : JsArray => Result,
                            nullable    : Boolean = false,
                            required    : Boolean = true,
                            elemNullable: Boolean = false
                           ): JsSpec = IsArrayOfNumberSuchThat(p,
                                                               requireNonNull(nullable),
                                                               requireNonNull(required),
                                                               requireNonNull(elemNullable)
                                                               )

  def arrayOfTestedNumber(p           : JsNumber => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfTestedNumber(p,
                                                           requireNonNull(nullable),
                                                           requireNonNull(required),
                                                           requireNonNull(elemNullable)
                                                           )

  def arrayOfTestedValue(p       : JsValue => Result,
                         nullable: Boolean = false,
                         required: Boolean = true
                        ): JsSpec = IsArrayOfTestedValue(p,
                                                         nullable,
                                                         required
                                                         )

  def arrayOfValueSuchThat(p: JsArray => Result,
                           nullable: Boolean = false,
                           required: Boolean = true
                          ): JsSpec = IsArrayOfValueSuchThat(p,
                                                             nullable,
                                                             required
                                                             )

}
