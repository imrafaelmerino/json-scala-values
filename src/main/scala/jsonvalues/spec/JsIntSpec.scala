package jsonvalues.spec

import jsonvalues.{JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}
import JsValueSpec._
import jsonvalues.Implicits._
import jsonvalues.spec.ErrorMessages._

object JsIntSpec
{

  val int: JsValidator = JsValueValidator((value: JsValue) => if (value.isInt) JsValueOk else JsValueError(INT_NOT_FOUND(value)))

  def int(minimum: Int,
          maximum: Int,
          multipleOf: Int = 0
         ): JsValueValidator =
  {
    and(int,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsInt.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(INT_LOWER_THAN_MINIMUM(value,
                                                                                            minimum
                                                                                            )
                                                                     )
                           if (n > maximum) errors = errors.appended(INT_GREATER_THAN_MAXIMUM(value,
                                                                                              maximum
                                                                                              )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0)
                             errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(value,
                                                                                 multipleOf
                                                                                 )
                                                      )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )

  }

  def intGT(exclusiveMinimum: Int,
            multipleOf      : Int = 0
           ): JsValueValidator =
  {
    and(int,
        intGTE(exclusiveMinimum,
               multipleOf
               ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsInt.value
                           if (n == exclusiveMinimum) JsValueError(INT_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                                  exclusiveMinimum
                                                                                                  )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def intGTE(minimum: Int,
             multipleOf: Int = 0
            ): JsValueValidator =
  {
    and(int,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsInt.value
                           var errors: Seq[String] = Seq.empty
                           if (n < minimum) errors = errors.appended(INT_LOWER_THAN_MINIMUM(value,
                                                                                            minimum
                                                                                            )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0)
                             errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(value,
                                                                                 multipleOf
                                                                                 )
                                                      )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def intLTE(maximum: Int,
             multipleOf: Int = 0
            ): JsValueValidator =
  {
    and(int,
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsInt.value
                           var errors: Seq[String] = Seq.empty
                           if (n > maximum) errors = errors.appended(INT_GREATER_THAN_MAXIMUM(value,
                                                                                              maximum
                                                                                              )
                                                                     )
                           if (multipleOf != 0 && n % multipleOf != 0) errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(value,
                                                                                                                           multipleOf
                                                                                                                           )
                                                                                                )
                           if (errors.isEmpty) JsValueOk
                           else JsValueError(errors)
                         }
                         )
        )
  }

  def intLT(exclusiveMaximum: Int,
            multipleOf: Int = 0
           ): JsValueValidator =
  {
    and(int,
        intLTE(exclusiveMaximum,
               multipleOf
               ),
        JsValueValidator((value: JsValue) =>
                         {
                           val n = value.asJsInt.value
                           if (n == exclusiveMaximum) JsValueError(INT_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                                  exclusiveMaximum
                                                                                                  )
                                                                   )
                           else JsValueOk
                         }
                         )
        )
  }

  def int(condition: Int => Boolean,
          message  : Int => String
         ): JsValueValidator = and(int,
                              JsValueValidator((value: JsValue) =>
                                                 if (condition.apply(value.asJsInt.value)) JsValueOk else JsValueError(message(value.asJsInt.value))
                                               )
                              )


}
