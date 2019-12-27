package value.spec

import java.util.Objects.requireNonNull

import value.{JsArray, JsNumber, JsObj, JsValue}

object JsArraySpecs
{

  val array = IsArray(elemNullable = false)

  def array(nullable: Boolean = false,
            required: Boolean = true,
            elemNullable: Boolean = false
           ) = IsArray(nullable,
                       required,
                       elemNullable
                       )

  val arrayOfInt = IsArrayOfInt(elemNullable = false)


  def arrayOfInt(nullable: Boolean = false,
                 required  : Boolean = true,
                 elemNullable: Boolean = false
                ) = IsArrayOfInt(nullable,
                                 required,
                                 elemNullable
                                 )


  val arrayOfLong = IsArrayOfLong(elemNullable = false)

  def arrayOfLong(nullable: Boolean = false,
                  required: Boolean = true,
                  elemNullable: Boolean = false
                 ) = IsArrayOfLong(nullable,
                                   required,
                                   elemNullable
                                   )

  val arrayOfDecimal = IsArrayOfDecimal(elemNullable = false)


  def arrayOfDecimal(nullable: Boolean = false,
                     required: Boolean = true,
                     elemNullable: Boolean = false
                    ) = IsArrayOfDecimal(nullable,
                                         required,
                                         elemNullable
                                         )

  val arrayOfIntegral = IsArrayOfIntegral(elemNullable = false)

  def arrayOfIntegral(nullable: Boolean = false,
                      required: Boolean = true,
                      elemNullable: Boolean = false
                     ) = IsArrayOfIntegral(nullable,
                                           required,
                                           elemNullable
                                           )

  val arrayOfBool = IsArrayOfBool(elemNullable = false)


  def arrayOfBool(nullable: Boolean = false,
                  required    : Boolean = true,
                  elemNullable: Boolean = false
                 ) = IsArrayOfBool(nullable,
                                   required,
                                   elemNullable
                                   )

  val arrayOfNumber = IsArrayOfNumber(elemNullable = false)


  def arrayOfNumber(nullable: Boolean = false,
                    required: Boolean = true,
                    elemNullable: Boolean = false
                   ) = IsArrayOfNumber(nullable,
                                       required,
                                       elemNullable
                                       )

  val arrayOfStr = IsArrayOfStr(elemNullable = false)

  def arrayOfStr(nullable: Boolean = false,
                 required   : Boolean = true,
                 elemNullable: Boolean = false
                ) = IsArrayOfStr(nullable,
                                 required,
                                 elemNullable
                                 )

  val arrayOfObj = IsArrayOfObj(elemNullable = false)

  def arrayOfObj(nullable: Boolean = false,
                 required: Boolean = true,
                 elemNullable: Boolean = false
                ) = IsArrayOfObj(nullable,
                                 required,
                                 elemNullable
                                 )

  def conforms(spec: JsArraySpec,
               nullable: Boolean = false,
               required: Boolean = true
              ): JsSpec = IsArraySpec(spec,
                                      nullable = nullable,
                                      required = required
                                      )


  def arrayOf(spec: JsObjSpec,
              nullable    : Boolean = false,
              required    : Boolean = true,
              elemNullable: Boolean = false
             ): ArrayOfObjSpec = ArrayOfObjSpec(spec,
                                                nullable,
                                                required,
                                                elemNullable
                                                )

  def arrayOfObjSuchThat(p: JsArray => Result,
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

  def arrayOfIntSuchThat(p: JsArray => Result,
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


  def arrayOfIntegralSuchThat(p: JsArray => Result,
                              nullable    : Boolean = false,
                              required    : Boolean = true,
                              elemNullable: Boolean = false
                             ): JsSpec = IsArrayOfIntegralSuchThat(p,
                                                                   requireNonNull(nullable),
                                                                   requireNonNull(required),
                                                                   requireNonNull(elemNullable)
                                                                   )

  def arrayOfTestedIntegral(p: BigInt => Result,
                            nullable    : Boolean = false,
                            required    : Boolean = true,
                            elemNullable: Boolean = false
                           ): JsSpec = IsArrayOfTestedIntegral(p,
                                                               requireNonNull(nullable),
                                                               requireNonNull(required),
                                                               requireNonNull(elemNullable)
                                                               )

  def arrayOfBoolSuchThat(p: JsArray => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfBoolSuchThat(p,
                                                           requireNonNull(nullable),
                                                           requireNonNull(required),
                                                           requireNonNull(elemNullable)
                                                           )

  def arrayOfStrSuchThat(p: JsArray => Result,
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

  def arrayOfLongSuchThat(p: JsArray => Result,
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

  def arrayOfDecimalSuchThat(p: JsArray => Result,
                             nullable    : Boolean = false,
                             required    : Boolean = true,
                             elemNullable: Boolean = false
                            ): JsSpec = IsArrayOfDecimalSuchThat(p,
                                                                 requireNonNull(nullable),
                                                                 requireNonNull(required),
                                                                 requireNonNull(elemNullable)
                                                                 )

  def arrayOfTestedDecimal(p: BigDecimal => Result,
                           nullable    : Boolean = false,
                           required    : Boolean = true,
                           elemNullable: Boolean = false
                          ): JsSpec = IsArrayOfTestedDecimal(p,
                                                             requireNonNull(nullable),
                                                             requireNonNull(required),
                                                             requireNonNull(elemNullable)
                                                             )

  def arrayOfNumberSuchThat(p: JsArray => Result,
                            nullable    : Boolean = false,
                            required    : Boolean = true,
                            elemNullable: Boolean = false
                           ): JsSpec = IsArrayOfNumberSuchThat(p,
                                                               requireNonNull(nullable),
                                                               requireNonNull(required),
                                                               requireNonNull(elemNullable)
                                                               )

  def arrayOfTestedNumber(p: JsNumber => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfTestedNumber(p,
                                                           requireNonNull(nullable),
                                                           requireNonNull(required),
                                                           requireNonNull(elemNullable)
                                                           )

  def arrayOfTestedValue(p: JsValue => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ): JsSpec = IsArrayOfTestedValue(p,
                                                         nullable,
                                                         required,
                                                         elemNullable
                                                         )

  def arrayOfValueSuchThat(p: JsArray => Result,
                           nullable    : Boolean = false,
                           required    : Boolean = true,
                           elemNullable: Boolean = false
                          ): JsSpec = IsArrayOfValueSuchThat(p,
                                                             nullable,
                                                             required,
                                                             elemNullable
                                                             )

}
