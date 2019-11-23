package value.spec

import java.util.Objects.requireNonNull

import value.Implicits._
import Messages._
import JsValueSpec.and
import value.JsValue

object JsIntSpecs
{

  val int: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isInt) Valid else Invalid(INT_NOT_FOUND(value)))

  def int(minimum: Int,
          maximum   : Int,
          multipleOf: Int = 0
         ): JsValueSpec =
  {
    and(int,
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsInt.value
                      var errors: Seq[String] = Seq.empty
                      if (n < requireNonNull(minimum)) errors = errors.appended(INT_LOWER_THAN_MINIMUM(value,
                                                                                                       minimum
                                                                                                       )
                                                                                )
                      if (n > requireNonNull(maximum)) errors = errors.appended(INT_GREATER_THAN_MAXIMUM(value,
                                                                                                         maximum
                                                                                                         )
                                                                                )
                      if (requireNonNull(multipleOf) != 0 && n % multipleOf != 0)
                        errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(value,
                                                                            multipleOf
                                                                            )
                                                 )
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )

  }

  def intGT(exclusiveMinimum: Int,
            multipleOf      : Int = 0
           ): JsValueSpec =
  {
    and(int,
        intGTE(requireNonNull(exclusiveMinimum),
               requireNonNull(multipleOf)
               ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsInt.value
                      if (n == exclusiveMinimum) Invalid(INT_EQUAL_TO_EXCLUSIVE_MINIMUM(value,
                                                                                        exclusiveMinimum
                                                                                        )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def intGTE(minimum   : Int,
             multipleOf: Int = 0
            ): JsValueSpec =
  {
    and(int,
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsInt.value
                      var errors: Seq[String] = Seq.empty
                      if (n < requireNonNull(minimum)) errors = errors.appended(INT_LOWER_THAN_MINIMUM(value,
                                                                                                       minimum
                                                                                                       )
                                                                                )
                      if (requireNonNull(multipleOf) != 0 && n % multipleOf != 0)
                        errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(value,
                                                                            multipleOf
                                                                            )
                                                 )
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def intLTE(maximum   : Int,
             multipleOf: Int = 0
            ): JsValueSpec =
  {
    and(int,
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsInt.value
                      var errors: Seq[String] = Seq.empty
                      if (n > requireNonNull(maximum)) errors = errors.appended(INT_GREATER_THAN_MAXIMUM(value,
                                                                                                         maximum
                                                                                                         )
                                                                                )
                      if (requireNonNull(multipleOf) != 0 && n % multipleOf != 0) errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(value,
                                                                                                                                      multipleOf
                                                                                                                                      )
                                                                                                           )
                      if (errors.isEmpty) Valid
                      else Invalid(errors)
                    }
                    )
        )
  }

  def intLT(exclusiveMaximum: Int,
            multipleOf      : Int = 0
           ): JsValueSpec =
  {
    and(int,
        intLTE(requireNonNull(exclusiveMaximum),
               requireNonNull(multipleOf)
               ),
        JsValueSpec((value: JsValue) =>
                    {
                      val n = value.asJsInt.value
                      if (n == exclusiveMaximum) Invalid(INT_EQUAL_TO_EXCLUSIVE_MAXIMUM(value,
                                                                                        exclusiveMaximum
                                                                                        )
                                                         )
                      else Valid
                    }
                    )
        )
  }

  def int(condition: Int => Boolean,
          message  : Int => String
         ): JsValueSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    and(int,
        JsValueSpec((value                      : JsValue) =>
                      if (condition.apply(value.asJsInt.value)) Valid else Invalid(message(value.asJsInt.value))
                    )
        )
  }


}
