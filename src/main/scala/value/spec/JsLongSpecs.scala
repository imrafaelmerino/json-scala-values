package value.spec

import java.util.Objects.requireNonNull

import Messages.LONG_NOT_FOUND
import Messages._
import value.Implicits._
import JsValueSpec.and
import value.JsValue

object JsLongSpecs
{
  val long: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isLong || value.isInt) Valid else Invalid(LONG_NOT_FOUND(value)))

  def long(minimum: Long,
           maximum: Long,
           multipleOf: Long = 0
          ): JsValueSpec =
  {
    and(long,
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsLong.value
                      var errors: Seq[String] = Seq.empty
                      if (n < requireNonNull(minimum)) errors = errors.appended(LONG_LOWER_THAN_MINIMUM(value,
                                                                                                        minimum
                                                                                                        )
                                                                                )
                      if (n > requireNonNull(maximum)) errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(value,
                                                                                                          maximum
                                                                                                          )
                                                                                )
                      if (requireNonNull(multipleOf) != 0 && n % multipleOf != 0) errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(value,
                                                                                                                                             multipleOf
                                                                                                                                             )
                                                                                                           )
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )

  }

  def longGT(exclusiveMinimum: Long,
             multipleOf      : Long = 0
            ): JsValueSpec =
  {
    and(long,
        longGTE(requireNonNull(exclusiveMinimum),
                requireNonNull(multipleOf)
                ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsLong.value
                      if (n == exclusiveMinimum) Invalid(LONG_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                         exclusiveMinimum
                                                                                         )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def longGTE(minimum   : Long,
              multipleOf: Long = 0
             ): JsValueSpec =
  {
    and(long,
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsLong.value
                      var errors: Seq[String] = Seq.empty
                      if (n < requireNonNull(minimum)) errors = errors.appended(LONG_LOWER_THAN_MINIMUM(value,
                                                                                                        minimum
                                                                                                        )
                                                                                )
                      if (requireNonNull(multipleOf) != 0 && n % multipleOf != 0) errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(value,
                                                                                                                                             multipleOf
                                                                                                                                             )
                                                                                                           )
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def longLTE(maximum: Long,
              multipleOf: Long = 0
             ): JsValueSpec =
  {
    and(long,
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsLong.value
                      var errors: Seq[String] = Seq.empty
                      if (n > requireNonNull(maximum)) errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(value,
                                                                                                          maximum
                                                                                                          )
                                                                                )
                      if (requireNonNull(multipleOf) != 0 && n % multipleOf != 0) errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(value,
                                                                                                                                             multipleOf
                                                                                                                                             )
                                                                                                           )
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def longLT(exclusiveMaximum: Long,
             multipleOf      : Long = 0
            ): JsValueSpec =
  {
    and(long,
        longLTE(requireNonNull(exclusiveMaximum),
                requireNonNull(multipleOf)
                ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsLong.value
                      if (n == exclusiveMaximum) Invalid(LONG_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                         exclusiveMaximum
                                                                                         )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def long(condition: Long => Boolean,
           message  : Long => String
          ): JsValueSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    and(long,
        JsValueSpec((value                       : JsValue) =>
                      if (condition.apply(value.asJsLong.value)) Valid else Invalid(message(value.asJsLong.value))
                    )
        )

  }
}
