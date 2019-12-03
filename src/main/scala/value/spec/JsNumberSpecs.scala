package value.spec

import Messages._
import value.JsNumber
import value.Implicits._

object JsNumberSpecs
{

  val integral: JsSpec = IsIntegral()

  val decimal: JsSpec = IsDecimal()

  val number: JsSpec = IsNumber()

  def number(condition: JsNumber => Boolean,
             message  : String
            ): JsSpec = IsNumberSuchThat((n: JsNumber) =>
                                           if (condition.apply(n)) Valid else Invalid(message)
                                         )

  def decimal(minimum: BigDecimal,
              maximum   : BigDecimal,
              multipleOf: BigDecimal = 0
             ): JsSpec =
  {
    IsDecimalSuchThat((n: BigDecimal) =>
                      {
                        var errors: Seq[String] = Seq.empty
                        if (n < minimum) errors = errors.appended(DECIMAL_LOWER_THAN_MINIMUM(n,
                                                                                             minimum
                                                                                             )
                                                                  )
                        if (n > maximum) errors = errors.appended(DECIMAL_GREATER_THAN_MAXIMUM(n,
                                                                                               maximum
                                                                                               )
                                                                  )
                        if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(n,
                                                                                                                     multipleOf
                                                                                                                     )
                                                                                             )
                        if (errors.isEmpty) Valid
                        else Invalid(errors)
                      }
                      )

  }

  def decimalGT(exclusiveMinimum: BigDecimal,
                multipleOf      : BigDecimal = 0
               ): JsSpec =
  {
    IsDecimalSuchThat((n: BigDecimal) =>
                      {
                        var errors: Seq[String] = Seq.empty
                        if (n < exclusiveMinimum) errors =
                          errors.appended(DECIMAL_LOWER_THAN_MINIMUM(n,
                                                                     exclusiveMinimum
                                                                     )
                                          )
                        if (multipleOf != 0 && n % multipleOf != 0)
                          errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(n,
                                                                           multipleOf
                                                                           )
                                                   )

                        if (n == exclusiveMinimum)
                          errors = errors.appended(DECIMAL_EQUAL_TO_EXCLUSIVE_MINIMUM(n,
                                                                                      exclusiveMinimum
                                                                                      )
                                                   )
                        if (errors.isEmpty) Valid
                        else Invalid(errors)
                      }

                      )
  }

  def decimalGTE(minimum   : BigDecimal,
                 multipleOf: BigDecimal = 0
                ): JsSpec =
  {
    IsDecimalSuchThat((n: BigDecimal) =>
                      {
                        var errors: Seq[String] = Seq.empty
                        if (n < minimum)
                          errors = errors.appended(DECIMAL_LOWER_THAN_MINIMUM(n,
                                                                              minimum
                                                                              )
                                                   )
                        if (multipleOf != 0 && n % multipleOf != 0)
                          errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(n,
                                                                           multipleOf
                                                                           )
                                                   )
                        if (errors.isEmpty) Valid
                        else Invalid(errors)
                      }

                      )
  }

  def decimalLTE(maximum   : BigDecimal,
                 multipleOf: BigDecimal = 0
                ): JsSpec =
  {
    IsDecimalSuchThat((n: BigDecimal) =>
                      {
                        var errors: Seq[String] = Seq.empty
                        if (n > maximum)
                          errors = errors.appended(DECIMAL_GREATER_THAN_MAXIMUM(n,
                                                                                maximum
                                                                                )
                                                   )
                        if (multipleOf != 0 && n % multipleOf != 0)
                          errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(n,
                                                                           multipleOf
                                                                           )
                                                   )
                        if (errors.isEmpty) Valid
                        else Invalid(errors)
                      }
                      )
  }

  def decimalLT(exclusiveMaximum: BigDecimal,
                multipleOf      : BigDecimal = 0
               ): JsSpec =
  {
    IsDecimalSuchThat((n: BigDecimal) =>
                      {
                        var errors: Seq[String] = Seq.empty
                        if (n > exclusiveMaximum)
                          errors = errors.appended(DECIMAL_GREATER_THAN_MAXIMUM(n,
                                                                                exclusiveMaximum
                                                                                )
                                                   )
                        if (multipleOf != 0 && n % multipleOf != 0)
                          errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(n,
                                                                           multipleOf
                                                                           )
                                                   )

                        if (n == exclusiveMaximum)
                          errors = errors.appended(DECIMAL_EQUAL_TO_EXCLUSIVE_MAXIMUM(exclusiveMaximum,
                                                                                      exclusiveMaximum
                                                                                      )
                                                   )
                        if (errors.isEmpty) Valid
                        else Invalid(errors)
                      }
                      )
  }

  def decimal(condition   : BigDecimal => Boolean,
              errorMessage: BigDecimal => String
             ): JsSpec = IsDecimalSuchThat((value: BigDecimal) =>
                                             if (condition.apply(value)) Valid
                                             else Invalid(errorMessage(value))
                                           )


  def integral(minimum: BigInt,
               maximum: BigInt,
               multipleOf: BigInt = 0
              ): JsSpec =
  {
    IsIntegralSuchThat((n: BigInt) =>
                       {
                         var errors: Seq[String] = Seq.empty
                         if (n < minimum)
                           errors = errors.appended(INTEGRAL_LOWER_THAN_MINIMUM(n,
                                                                                minimum
                                                                                )
                                                    )
                         if (n > maximum)
                           errors = errors.appended(INTEGRAL_GREATER_THAN_MAXIMUM(n,
                                                                                  maximum
                                                                                  )
                                                    )
                         if (multipleOf != 0 && n % multipleOf != 0)
                           errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(n,
                                                                             multipleOf
                                                                             )
                                                    )
                         if (errors.isEmpty) Valid
                         else Invalid(errors)
                       }
                       )

  }

  def integralGT(exclusiveMinimum: BigInt,
                 multipleOf      : BigInt = 0
                ): JsSpec =
  {
    IsIntegralSuchThat((n: BigInt) =>
                       {
                         var errors: Seq[String] = Seq.empty
                         if (n < exclusiveMinimum)
                           errors = errors.appended(INTEGRAL_LOWER_THAN_MINIMUM(n,
                                                                                exclusiveMinimum
                                                                                )
                                                    )
                         if (multipleOf != 0 && n % multipleOf != 0)
                           errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(n,
                                                                             multipleOf
                                                                             )
                                                    )

                         if (n == exclusiveMinimum)
                           errors = errors.appended(INTEGRAL_EQUAL_TO_EXCLUSIVE_MINIMUM(n,
                                                                                        exclusiveMinimum
                                                                                        )
                                                    )
                         if (errors.isEmpty) Valid
                         else Invalid(errors)
                       }
                       )
  }

  def integralGTE(minimum   : BigInt,
                  multipleOf: BigInt = 0
                 ): JsSpec =
  {
    IsIntegralSuchThat((n: BigInt) =>
                       {
                         var errors: Seq[String] = Seq.empty
                         if (n < minimum)
                           errors = errors.appended(INTEGRAL_LOWER_THAN_MINIMUM(n,
                                                                                minimum
                                                                                )
                                                    )
                         if (multipleOf != 0 && n % multipleOf != 0)
                           errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(n,
                                                                             multipleOf
                                                                             )
                                                    )
                         if (errors.isEmpty) Valid
                         else Invalid(errors)
                       }

                       )
  }

  def integralLTE(maximum   : BigInt,
                  multipleOf: BigInt = 0
                 ): JsSpec =
  {
    IsIntegralSuchThat((n: BigInt) =>
                       {
                         var errors: Seq[String] = Seq.empty
                         if (n > maximum)
                           errors = errors.appended(INTEGRAL_GREATER_THAN_MAXIMUM(n,
                                                                                  maximum
                                                                                  )
                                                    )
                         if (multipleOf != 0 && n % multipleOf != 0)
                           errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(n,
                                                                             multipleOf
                                                                             )
                                                    )
                         if (errors.isEmpty) Valid
                         else Invalid(errors)
                       }

                       )
  }

  def integralLT(exclusiveMaximum: BigInt,
                 multipleOf      : BigInt = 0
                ): JsSpec =
  {
    IsIntegralSuchThat((n: BigInt) =>
                       {
                         var errors: Seq[String] = Seq.empty
                         if (n > exclusiveMaximum)
                           errors = errors.appended(INTEGRAL_GREATER_THAN_MAXIMUM(n,
                                                                                  exclusiveMaximum
                                                                                  )
                                                    )
                         if (multipleOf != 0 && n % multipleOf != 0)
                           errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(n,
                                                                             multipleOf
                                                                             )
                                                    )

                         if (n == exclusiveMaximum)
                           errors = errors.appended(INTEGRAL_EQUAL_TO_EXCLUSIVE_MAXIMUM(exclusiveMaximum,
                                                                                        exclusiveMaximum
                                                                                        )
                                                    )
                         if (errors.isEmpty) Valid
                         else Invalid(errors)
                       }

                       )
  }

  def integral(condition: BigInt => Boolean,
               message  : BigInt => String
              ): JsSpec = IsIntegralSuchThat((n: BigInt) =>
                                               if (condition.apply(n)) Valid
                                               else Invalid(message(n))
                                             )

}
