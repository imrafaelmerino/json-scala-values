package value.spec

import value.JsNumber

object JsNumberSpecs
{

  val integral: JsSpec = integral(nullable = false,
                                  required = true
                                  )

  val integral_or_null: JsSpec = integral(nullable = true,
                                          required = true
                                          )

  def integral(nullable: Boolean,
               required: Boolean
              ): JsSpec = IsIntegral(nullable,
                                     required
                                     )

  def integralSuchThat(p: BigInt => Result,
                       nullable: Boolean = false,
                       required: Boolean = true,
                      ): JsSpec = IsIntegralSuchThat(p,
                                                     nullable,
                                                     required
                                                     )

  val decimal: JsSpec = decimal(nullable = false,
                                required = true
                                )

  val decimal_or_null: JsSpec = decimal(nullable = true,
                                        required = true
                                        )

  def decimal(nullable: Boolean,
              required: Boolean
             ): JsSpec = IsDecimal(nullable,
                                   required
                                   )

  def decimalSuchThat(p: BigDecimal => Result,
                      nullable: Boolean = false,
                      required: Boolean = true,
                     ): JsSpec = IsDecimalSuchThat(p,
                                                   nullable,
                                                   required
                                                   )


  val number: JsSpec = number(nullable = false,
                              required = true
                              )

  val number_or_null: JsSpec = number(nullable = true,
                                      required = true
                                      )

  def number(nullable: Boolean,
             required: Boolean
            ): JsSpec = IsNumber(nullable,
                                 required
                                 )

  def numberSuchThat(p: JsNumber => Result,
                     nullable: Boolean,
                     required: Boolean
                    ): JsSpec = IsNumberSuchThat(p,
                                                 nullable,
                                                 required
                                                 )
}
