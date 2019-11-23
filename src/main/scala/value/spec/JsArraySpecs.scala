package value.spec

import Messages._
import value.spec.JsValueSpec._
import value.{JsArray, JsValue}
import java.util.Objects.requireNonNull

object JsArraySpecs
{

  val array: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isArr) Valid else Invalid(JS_ARRAY_NOT_FOUND(value)))

  val arrayOfInt: JsValueSpec = and(array,
                                    JsValueSpec((value: JsValue) =>
                                                {
                                                  val arr = value.asJsArray
                                                  if (!arr.seq.forall(e => e.isInt)) Invalid(ARRAY_OF_INT_NOT_FOUND)
                                                  else Valid
                                                }
                                                )
                                    )

  def arrayOfInt(minItems: Int = -1,
                 maxItems: Int = -1,
                 unique  : Boolean = false
                ): JsValueSpec = and(arrayOfInt,
                                     arraySpec(requireNonNull(minItems),
                                               requireNonNull(maxItems),
                                               requireNonNull(unique)
                                               )
                                     )


  val arrayOfString: JsValueSpec = and(array,
                                       JsValueSpec((value: JsValue) =>
                                                     if (value.asJsArray.seq.forall(v => v.isStr)) Valid
                                                     else Invalid(ARRAY_OF_STRING_NOT_FOUND)
                                                   )
                                       )

  def arrayOfString(minItems: Int = -1,
                    maxItems: Int = -1,
                    unique  : Boolean = false
                   ): JsValueSpec = and(arrayOfString,
                                        arraySpec(requireNonNull(minItems),
                                                  requireNonNull(maxItems),
                                                  requireNonNull(unique)
                                                  )
                                        )

  val arrayOfLong: JsValueSpec = and(array,
                                     JsValueSpec((value: JsValue) =>
                                                   if (value.asJsArray.seq.forall(v => v.isLong)) Valid
                                                   else Invalid(ARRAY_OF_LONG_NOT_FOUND)
                                                 )
                                     )

  def arrayOfLong(minItems: Long = -1,
                  maxItems: Long = -1,
                  unique  : Boolean = false
                 ): JsValueSpec = and(arrayOfLong,
                                      arraySpec(requireNonNull(minItems),
                                                requireNonNull(maxItems),
                                                requireNonNull(unique)
                                                )
                                      )


  val arrayOfDecimal: JsValueSpec = and(array,
                                        JsValueSpec((value: JsValue) =>
                                                      if (value.asJsArray.seq.forall(v => v.isDecimal || v.isDouble)) Valid
                                                      else Invalid(ARRAY_OF_DECIMAL_NOT_FOUND)
                                                    )
                                        )

  def arrayOfDecimal(minItems: Int = -1,
                     maxItems: Int = -1,
                     unique  : Boolean = false
                    ): JsValueSpec = and(arrayOfDecimal,
                                         arraySpec(requireNonNull(minItems),
                                                   requireNonNull(maxItems),
                                                   requireNonNull(unique)
                                                   )
                                         )

  val arrayOfNumber: JsValueSpec = and(array,
                                       JsValueSpec((value: JsValue) =>
                                                     if (value.asJsArray.seq.forall(v => v.isNumber)) Valid
                                                     else Invalid(ARRAY_OF_NUMBER_NOT_FOUND)
                                                   )
                                       )

  def arrayOfNumber(minItems: Int = -1,
                    maxItems: Int = -1,
                    unique  : Boolean = false
                   ): JsValueSpec = and(arrayOfNumber,
                                        arraySpec(requireNonNull(minItems),
                                                  requireNonNull(maxItems),
                                                  requireNonNull(unique)
                                                  )
                                        )

  def arrayOf(validator: JsValueSpec,
              message  : String
             ): JsValueSpec =
  {
    requireNonNull(message)
    requireNonNull(validator)
    and(array,
        JsValueSpec((value: JsValue) =>
                      if (
                        value.asJsArray.seq.forall(v => validator.f(v) match
                        {
                          case Valid => true
                          case Invalid(_) => false
                        }
                                                   )
                      ) Valid else Invalid(message)
                    )
        )

  }


  val arrayOfIntegral: JsValueSpec = and(array,
                                         JsValueSpec((value: JsValue) =>
                                                     {
                                                       val arr = value.asJsArray
                                                       if (!arr.seq.forall(e => e.isIntegral)) Invalid(ARRAY_OF_INTEGRAL_NOT_FOUND)
                                                       else Valid
                                                     }
                                                     )
                                         )

  def arrayOfIntegral(minItems: Int = -1,
                      maxItems: Int = -1,
                      unique  : Boolean = false
                     ): JsValueSpec = and(arrayOfIntegral,
                                          arraySpec(requireNonNull(minItems),
                                                    requireNonNull(maxItems),
                                                    requireNonNull(unique)
                                                    )
                                          )


  def array(condition: JsArray => Boolean,
            message  : JsArray => String
           ): JsValueSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    and(array,
        JsValueSpec((value                        : JsValue) =>
                      if (condition.apply(value.asJsArray)) Valid
                      else Invalid(message(value.asJsArray))
                    )
        )
  }

  private def arraySpec(minItems: Int,
                        maxItems: Int,
                        unique: Boolean
                       ): JsValueSpec =
  {
    JsValueSpec((value: JsValue) =>
                {
                  val arr = value.asJsArray
                  var errors: Seq[String] = Seq.empty
                  val length = arr.length()
                  if (minItems != -1 && length < minItems) errors = errors :+ INT_ARRAY_OF_LENGTH_LOWER_THAN_MINIMUM(length,
                                                                                                                     minItems
                                                                                                                     )
                  if (maxItems != -1 && length > maxItems) errors = errors :+ INT_ARRAY_OF_LENGTH_GREATER_THAN_MAXIMUM(length,
                                                                                                                       maxItems
                                                                                                                       )
                  if (unique && arr.seq.distinct.length != arr.seq.length) errors = errors :+ ARRAY_WITH_DUPLICATES
                  if (errors.isEmpty) Valid
                  else Invalid(errors)
                }
                )
  }

  private def arraySpec(minItems: Long,
                        maxItems: Long,
                        unique  : Boolean
                       ): JsValueSpec =
  {
    JsValueSpec((value: JsValue) =>
                {
                  val arr = value.asJsArray
                  var errors: Seq[String] = Seq.empty
                  val length = arr.length()
                  if (minItems != -1 && length < minItems) errors = errors :+ LONG_ARRAY_OF_LENGTH_LOWER_THAN_MINIMUM(length,
                                                                                                                      minItems
                                                                                                                      )
                  if (maxItems != -1 && length > maxItems) errors = errors :+ LONG_ARRAY_OF_LENGTH_GREATER_THAN_MAXIMUM(length,
                                                                                                                        maxItems
                                                                                                                        )
                  if (unique && arr.seq.distinct.length != arr.seq.length) errors = errors :+ ARRAY_WITH_DUPLICATES
                  if (errors.isEmpty) Valid
                  else Invalid(errors)
                }
                )
  }
}
