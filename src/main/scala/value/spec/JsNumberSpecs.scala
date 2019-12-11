package value.spec

import Messages._
import value.JsNumber
import value.Implicits._

object JsNumberSpecs
{

  val integral: JsSpec = integral(nullable = false,
                                  required = true
                                  )

  val nullOrIntegral: JsSpec = integral(nullable = true,
                                        required = true
                                        )

  def integral(nullable: Boolean,
               required: Boolean
              ): JsSpec = IsIntegral(nullable,
                                     required
                                     )

  val decimal: JsSpec = decimal(nullable = false,
                                required = true
                                )

  val nullOrDecimal: JsSpec = decimal(nullable = true,
                                      required = true
                                      )

  def decimal(nullable: Boolean,
              required: Boolean
             ): JsSpec = IsDecimal(nullable,
                                   required
                                   )

  val number: JsSpec = number(nullable = false,
                              required = true
                              )

  val nullOrNumber: JsSpec = number(nullable = true,
                                    required = true
                                    )

  def number(nullable: Boolean,
             required: Boolean
            ): JsSpec = IsNumber(nullable,
                                 required
                                 )

  def number(condition: JsNumber => Boolean,
             message  : String,
             nullable : Boolean,
             required : Boolean
            ): JsSpec = IsNumberSuchThat((n: JsNumber) =>
                                           if (condition.apply(n)) Valid else Invalid(message),
                                         nullable,
                                         required
                                         )

  def decimal(minimum   : BigDecimal,
              maximum   : BigDecimal,
              multipleOf: BigDecimal = 0,
              nullable  : Boolean = false,
              required  : Boolean = true
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
                      },
                      nullable,
                      required
                      )

  }

  def decimalGT(exclusiveMinimum: BigDecimal,
                multipleOf      : BigDecimal = 0,
                nullable        : Boolean = false,
                required        : Boolean = true
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
                      },
                      nullable,
                      required

                      )
  }

  def decimalGTE(minimum: BigDecimal,
                 multipleOf: BigDecimal = 0,
                 nullable  : Boolean = false,
                 required  : Boolean = true
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
                      },
                      nullable,
                      required
                      )
  }

  def decimalLTE(maximum: BigDecimal,
                 multipleOf: BigDecimal = 0,
                 nullable  : Boolean = false,
                 required  : Boolean = true
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
                      },
                      nullable,
                      required
                      )
  }

  def decimalLT(exclusiveMaximum: BigDecimal,
                multipleOf      : BigDecimal = 0,
                nullable        : Boolean = false,
                required        : Boolean = true
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
                      },
                      nullable,
                      required
                      )
  }

  def decimal(condition: BigDecimal => Boolean,
              errorMessage: BigDecimal => String,
              nullable    : Boolean,
              required    : Boolean
             ): JsSpec = IsDecimalSuchThat((value: BigDecimal) =>
                                             if (condition.apply(value)) Valid
                                             else Invalid(errorMessage(value)),
                                           nullable,
                                           required
                                           )


  def integral(minimum   : BigInt,
               maximum   : BigInt,
               multipleOf: BigInt = 0,
               nullable  : Boolean = false,
               required  : Boolean = true
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
                       },
                       nullable,
                       required
                       )

  }

  def integralGT(exclusiveMinimum: BigInt,
                 multipleOf      : BigInt = 0,
                 nullable        : Boolean = false,
                 required        : Boolean = true
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
                       },
                       nullable,
                       required
                       )
  }

  def integralGTE(minimum: BigInt,
                  multipleOf: BigInt = 0,
                  nullable  : Boolean = false,
                  required  : Boolean = true
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
                       },
                       nullable,
                       required
                       )
  }

  def integralLTE(maximum: BigInt,
                  multipleOf: BigInt = 0,
                  nullable  : Boolean = false,
                  required  : Boolean = true
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
                       },
                       nullable,
                       required
                       )
  }

  def integralLT(exclusiveMaximum: BigInt,
                 multipleOf      : BigInt = 0,
                 nullable        : Boolean = false,
                 required        : Boolean = true
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
                       },
                       nullable,
                       required

                       )
  }

  def integral(condition: BigInt => Boolean,
               message  : BigInt => String,
               nullable : Boolean,
               required : Boolean
              ): JsSpec = IsIntegralSuchThat((n: BigInt) =>
                                               if (condition.apply(n)) Valid
                                               else Invalid(message(n)),
                                             nullable,
                                             required
                                             )

}
