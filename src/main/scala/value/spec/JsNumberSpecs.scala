package value.spec

import Messages._
import value.spec.JsValueSpec.and
import value.{JsNumber, JsValue}

object JsNumberSpecs
{

  val integral: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isInt || value.isLong || value.isBigInt) Valid else Invalid(INTEGRAL_NUMBER_NOT_FOUND(value)))

  val decimal: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isDouble || value.isBigDec) Valid else Invalid(DECIMAL_NUMBER_NOT_FOUND(value)))

  val number: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isNumber) Valid else Invalid(NUMBER_NOT_FOUND(value)))

  def number(condition: JsNumber => Boolean,
             message: String
            ): JsSpec = and(number,
                            JsValueSpec((value: JsValue) =>
                                          if (condition.apply(value.asJsNumber)) Valid else Invalid(message)
                                        )
                            )

  def decimal(minimum: BigDecimal,
              maximum: BigDecimal,
              multipleOf: BigDecimal = 0
             ): JsValueSpec =
  {
    and(decimal,
        JsValueSpec((value: JsValue) =>
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
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )

  }

  def decimalGT(exclusiveMinimum: BigDecimal,
                multipleOf: BigDecimal = 0
               ): JsValueSpec =
  {
    and(decimal,
        decimalGTE(exclusiveMinimum,
                   multipleOf
                   ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsBigDec.value
                      if (n == exclusiveMinimum) Invalid(DECIMAL_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                            exclusiveMinimum
                                                                                            )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def decimalGTE(minimum: BigDecimal,
                 multipleOf: BigDecimal = 0
                ): JsValueSpec =
  {
    and(decimal,
        JsValueSpec((value: JsValue) =>
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
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def decimalLTE(maximum: BigDecimal,
                 multipleOf: BigDecimal = 0
                ): JsValueSpec =
  {
    and(decimal,
        JsValueSpec((value: JsValue) =>
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
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def decimalLT(exclusiveMaximum: BigDecimal,
                multipleOf: BigDecimal = 0
               ): JsValueSpec =
  {
    and(decimal,
        decimalLTE(exclusiveMaximum,
                   multipleOf
                   ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsBigDec.value
                      if (n == exclusiveMaximum) Invalid(DECIMAL_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                            exclusiveMaximum
                                                                                            )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def decimal(condition: BigDecimal => Boolean,
              errorMessage: BigDecimal => String
             ): JsValueSpec = and(decimal,
                                  JsValueSpec((value: JsValue) =>
                                                if (condition.apply(value.asJsBigDec.value)) Valid
                                                else Invalid(errorMessage(value.asJsBigDec.value))
                                              )
                                  )


  def integral(minimum: BigInt,
               maximum: BigInt,
               multipleOf: BigInt = 0
              ): JsValueSpec =
  {
    and(integral,
        JsValueSpec((value: JsValue) =>
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
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )

  }

  def integralGT(exclusiveMinimum: BigInt,
                 multipleOf: BigInt = 0
                ): JsValueSpec =
  {
    and(integral,
        integralGTE(exclusiveMinimum,
                    multipleOf
                    ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsBigInt.value
                      if (n == exclusiveMinimum) Invalid(INTEGRAL_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                             exclusiveMinimum
                                                                                             )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def integralGTE(minimum: BigInt,
                  multipleOf: BigInt = 0
                 ): JsValueSpec =
  {
    and(integral,
        JsValueSpec((value: JsValue) =>
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
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def integralLTE(maximum: BigInt,
                  multipleOf: BigInt = 0
                 ): JsValueSpec =
  {
    and(integral,
        JsValueSpec((value: JsValue) =>
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
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def integralLT(exclusiveMaximum: BigInt,
                 multipleOf: BigInt = 0
                ): JsValueSpec =
  {
    and(integral,
        integralLTE(exclusiveMaximum,
                    multipleOf
                    ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsBigInt.value
                      if (n == exclusiveMaximum) Invalid(INTEGRAL_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                             exclusiveMaximum
                                                                                             )
                                                         )
                      else Valid
                    }
                    )
        )
  }


  def integral(condition: BigInt => Boolean,
               message: BigInt => String
              ): JsValueSpec = and(integral,
                                   JsValueSpec((value: JsValue) =>
                                                 if (condition.apply(value.asJsBigInt.value)) Valid
                                                 else Invalid(message(value.asJsBigInt.value))
                                               )
                                   )

}
