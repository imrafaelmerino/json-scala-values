package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.{DECIMAL_NUMBER_NOT_FOUND, INTEGRAL_NUMBER_NOT_FOUND, NUMBER_NOT_FOUND}
import jsonvalues.spec.JsValueSpec._
import jsonvalues.{JsNumber, JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}
import ErrorMessages._
import jsonvalues.Implicits._


object JsNumberSpec
{

  val integral: JsValidator = JsValueValidator((value: JsValue) => if (value.isInt || value.isLong || value.isBigInt) JsValueOk else JsValueError(INTEGRAL_NUMBER_NOT_FOUND(value)))

  val decimal: JsValidator = JsValueValidator((value: JsValue) => if (value.isDouble || value.isBigDec) JsValueOk else JsValueError(DECIMAL_NUMBER_NOT_FOUND(value)))

  val number: JsValidator = JsValueValidator((value: JsValue) => if (value.isNumber) JsValueOk else JsValueError(NUMBER_NOT_FOUND(value)))

  def number(condition: JsNumber => Boolean,
             message: String
            ): JsValidator = and(number,
                                 JsValueValidator((value: JsValue) =>
                                                    if (condition.apply(value.asJsNumber)) JsValueOk else JsValueError(message)
                                                  )
                                 )

  def decimal(minimum: BigDecimal,
              maximum: BigDecimal,
              multipleOf: BigDecimal = 0
             ): JsValueValidator =
  {
    and(decimal,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigDec.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(DECIMAL_LOWER_THAN_MINIMUM(value,
                                                                                                minimum
                                                                                                )
                                                                     )
                           if (n > maximum) errors = errors.appended(DECIMAL_GREATER_THAN_MAXIMUM(value,
                                                                                                  maximum
                                                                                                  )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(value,
                                                                                                                        multipleOf
                                                                                                                        )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )

  }

  def decimalGT(exclusiveMinimum: BigDecimal,
                multipleOf: BigDecimal = 0
               ): JsValueValidator =
  {
    and(decimal,
        decimalGTE(exclusiveMinimum,
                   multipleOf
                   ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigDec.value
                           if (n == exclusiveMinimum) JsValueError(DECIMAL_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                                      exclusiveMinimum
                                                                                                      )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def decimalGTE(minimum: BigDecimal,
                 multipleOf: BigDecimal = 0
                ): JsValueValidator =
  {
    and(decimal,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigDec.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(DECIMAL_LOWER_THAN_MINIMUM(value,
                                                                                                minimum
                                                                                                )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(value,
                                                                                                                        multipleOf
                                                                                                                        )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def decimalLTE(maximum: BigDecimal,
                 multipleOf: BigDecimal = 0
                ): JsValueValidator =
  {
    and(decimal,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigDec.value
                           var errors: Seq[String] = Seq.empty
                           if (n > maximum) errors = errors.appended(DECIMAL_GREATER_THAN_MAXIMUM(value,
                                                                                                  maximum
                                                                                                  )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(DECIMAL_NOT_MULTIPLE_OF(value,
                                                                                                                        multipleOf
                                                                                                                        )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def decimalLT(exclusiveMaximum: BigDecimal,
                multipleOf: BigDecimal = 0
               ): JsValueValidator =
  {
    and(decimal,
        decimalLTE(exclusiveMaximum,
                   multipleOf
                   ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigDec.value
                           if (n == exclusiveMaximum) JsValueError(DECIMAL_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                                      exclusiveMaximum
                                                                                                      )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def decimal(condition: BigDecimal => Boolean,
              errorMessage: String
             ): JsValidator = and(decimal,
                                  JsValueValidator((value: JsValue) =>
                                                     if (condition.apply(value.asJsLong.value)) JsValueOk else JsValueError(errorMessage)
                                                   )
                                  )


  def integral(minimum: BigInt,
               maximum: BigInt,
               multipleOf: BigInt = 0
              ): JsValueValidator =
  {
    and(integral,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigInt.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(INTEGRAL_LOWER_THAN_MINIMUM(value,
                                                                                                 minimum
                                                                                                 )
                                                                     )
                           if (n > maximum) errors = errors.appended(INTEGRAL_GREATER_THAN_MAXIMUM(value,
                                                                                                   maximum
                                                                                                   )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(value,
                                                                                                                         multipleOf
                                                                                                                         )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )

  }

  def integralGT(exclusiveMinimum: BigInt,
                 multipleOf: BigInt = 0
                ): JsValueValidator =
  {
    and(integral,
        integralGTE(exclusiveMinimum,
                    multipleOf
                    ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigInt.value
                           if (n == exclusiveMinimum) JsValueError(INTEGRAL_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                                       exclusiveMinimum
                                                                                                       )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def integralGTE(minimum: BigInt,
                  multipleOf: BigInt = 0
                 ): JsValueValidator =
  {
    and(integral,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigInt.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(INTEGRAL_LOWER_THAN_MINIMUM(value,
                                                                                                 minimum
                                                                                                 )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(value,
                                                                                                                         multipleOf
                                                                                                                         )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def integralLTE(maximum: BigInt,
                  multipleOf: BigInt = 0
                 ): JsValueValidator =
  {
    and(integral,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigInt.value
                           var errors: Seq[String] = Seq.empty
                           if (n > maximum) errors = errors.appended(INTEGRAL_GREATER_THAN_MAXIMUM(value,
                                                                                                   maximum
                                                                                                   )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(INTEGRAL_NOT_MULTIPLE_OF(value,
                                                                                                                         multipleOf
                                                                                                                         )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def integralLT(exclusiveMaximum: BigInt,
                 multipleOf: BigInt = 0
                ): JsValueValidator =
  {
    and(integral,
        integralLTE(exclusiveMaximum,
                    multipleOf
                    ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsBigInt.value
                           if (n == exclusiveMaximum) JsValueError(INTEGRAL_GREATER_OR_EQUAL_THAN_EXCLUSIVE_MAXIMUM(value,
                                                                                                                    exclusiveMaximum
                                                                                                                    )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }


  def integral(condition: BigInt => Boolean,
               message: String
              ): JsValidator = and(integral,
                                   JsValueValidator((value: JsValue) =>
                                                      if (condition.apply(value.asJsBigInt.value)) JsValueOk else JsValueError(message)
                                                    )
                                   )

}
