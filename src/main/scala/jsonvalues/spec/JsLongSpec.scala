package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.LONG_NOT_FOUND
import jsonvalues.spec.JsValueSpec._
import jsonvalues.{JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}
import ErrorMessages._
import jsonvalues.Implicits._

object JsLongSpec
{
  val long: JsValidator = JsValueValidator((value: JsValue) => if (value.isLong || value.isInt) JsValueOk else JsValueError(LONG_NOT_FOUND(value)))

  def long(minimum: Long,
           maximum   : Long,
           multipleOf: Long = 0
          ): JsValueValidator =
  {
    and(long,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsLong.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(LONG_LOWER_THAN_MINIMUM(value,
                                                                                             minimum
                                                                                             )
                                                                     )
                           if (n > maximum) errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(value,
                                                                                               maximum
                                                                                               )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(value,
                                                                                                                                  multipleOf
                                                                                                                                  )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )

  }

  def longGT(exclusiveMinimum: Long,
             multipleOf      : Long = 0
            ): JsValueValidator =
  {
    and(long,
        longGTE(exclusiveMinimum,
                multipleOf
                ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsLong.value
                           if (n == exclusiveMinimum) JsValueError(LONG_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                                   exclusiveMinimum
                                                                                                   )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def longGTE(minimum: Long,
              multipleOf: Long = 0
             ): JsValueValidator =
  {
    and(long,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsLong.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(LONG_LOWER_THAN_MINIMUM(value,
                                                                                             minimum
                                                                                             )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(value,
                                                                                                                                  multipleOf
                                                                                                                                  )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def longLTE(maximum: Long,
              multipleOf: Long = 0
             ): JsValueValidator =
  {
    and(long,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsLong.value
                           var errors: Seq[String] = Seq.empty
                           if (n > maximum) errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(value,
                                                                                               maximum
                                                                                               )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(value,
                                                                                                                                  multipleOf
                                                                                                                                  )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def longLT(exclusiveMaximum: Long,
             multipleOf      : Long = 0
            ): JsValueValidator =
  {
    and(long,
        longLTE(exclusiveMaximum,
                multipleOf
                ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsLong.value
                           if (n == exclusiveMaximum) JsValueError(LONG_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                                   exclusiveMaximum
                                                                                                   )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def long(condition: Long => Boolean,
           message  : Long => String
          ): JsValueValidator = and(long,
                                    JsValueValidator((value: JsValue) =>
                                                       if (condition.apply(value.asJsLong.value)) JsValueOk else JsValueError(message(value.asJsLong.value))
                                                     )
                                    )
}
