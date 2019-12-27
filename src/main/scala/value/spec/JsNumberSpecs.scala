package value.spec

import value.JsNumber

object JsNumberSpecs
{

  val integral: JsSpec = integral()

  def integral(nullable: Boolean = false,
               required: Boolean = true
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


  def decimal(nullable: Boolean = false,
              required: Boolean = true
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


  val number: JsSpec = number()

  def number(nullable: Boolean = false,
             required: Boolean = true
            ): JsSpec = IsNumber(nullable,
                                 required
                                 )

  def numberSuchThat(p: JsNumber => Result,
                     nullable: Boolean = false,
                     required: Boolean = true
                    ): JsSpec = IsNumberSuchThat(p,
                                                 nullable,
                                                 required
                                                 )

  val int: JsSpec = IsInt()

  def int(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsInt(nullable,
                           required
                           )

  def intSuchThat(p: Int => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                 ): JsSpec = IsIntSuchThat(p,
                                           nullable = nullable,
                                           required = required
                                           )

  val long: JsSpec = IsLong()

  def long(nullable: Boolean = false,
           required: Boolean = true
          ) = IsLong(nullable,
                     required
                     )

  def longSuchThat(p: Long => Result,
                   nullable: Boolean = false,
                   required: Boolean = true
                  ): JsSpec = IsLongSuchThat(p,
                                             nullable = nullable,
                                             required = required
                                             )
}
