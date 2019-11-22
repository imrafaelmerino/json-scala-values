package value.spec

import Messages._
import value.spec.JsValueSpec._
import value.{JsArray, JsValue}

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
                 unique: Boolean = false
                ): JsValueSpec = and(arrayOfInt,
                                     arraySpec(minItems,
                                               maxItems,
                                               unique
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
                    unique: Boolean = false
                   ): JsValueSpec = and(arrayOfString,
                                        arraySpec(minItems,
                                                  maxItems,
                                                  unique
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
                  unique: Boolean = false
                 ): JsValueSpec = and(arrayOfLong,
                                      arraySpec(minItems,
                                                maxItems,
                                                unique
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
                     unique: Boolean = false
                    ): JsValueSpec = and(arrayOfDecimal,
                                         arraySpec(minItems,
                                                   maxItems,
                                                   unique
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
                    unique: Boolean = false
                   ): JsValueSpec = and(arrayOfNumber,
                                        arraySpec(minItems,
                                                  maxItems,
                                                  unique
                                                  )
                                        )

  def arrayOf(validator: JsValueSpec,
              message: String
             ): JsValueSpec =
  {
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
                      unique: Boolean = false
                     ): JsValueSpec = and(arrayOfIntegral,
                                          arraySpec(minItems,
                                                    maxItems,
                                                    unique
                                                    )
                                          )


  def array(condition: JsArray => Boolean,
            message: JsArray => String
           ): JsValueSpec = and(array,
                                JsValueSpec((value: JsValue) =>
                                              if (condition.apply(value.asJsArray)) Valid
                                              else Invalid(message(value.asJsArray))
                                            )
                                )

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
                        unique: Boolean
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
