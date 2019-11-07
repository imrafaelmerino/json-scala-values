package jsonvalues.spec

import jsonvalues.Implicits._
import jsonvalues.spec.ErrorMessages.JS_ARRAY_NOT_FOUND
import jsonvalues.spec.JsValueSpec._
import jsonvalues.{JsArray, JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidationResult, JsValueValidator}
import ErrorMessages._

object JsArraySpec
{

  val array: JsValueValidator = JsValueValidator((value: JsValue) => if (value.isArr) JsValueOk else JsValueError(JS_ARRAY_NOT_FOUND(value)))

  val arrayOfInt: JsValueValidator = and(array,
                                         JsValueValidator((value: JsValue) =>
                                                          {
                                                            val arr = value.asJsArray
                                                            if (!arr.seq.forall(e => e.isInt)) JsValueError(ARRAY_OF_INT_NOT_FOUND)
                                                            else JsValueOk
                                                          }
                                                          )
                                         )

  def arrayOfInt(minItems: Int = -1,
                 maxItems: Int = -1,
                 unique  : Boolean = false
                ): JsValueValidator = and(arrayOfInt,
                                          arraySpec(minItems,
                                                    maxItems,
                                                    unique
                                                    )
                                          )


  val arrayOfString: JsValueValidator = and(array,
                                            JsValueValidator((value: JsValue) =>
                                                               if (value.asJsArray.seq.forall(v => v.isStr)) JsValueOk
                                                               else JsValueError(ARRAY_OF_STRING_NOT_FOUND)
                                                             )
                                            )

  def arrayOfString(minItems: Int = -1,
                    maxItems: Int = -1,
                    unique  : Boolean = false
                   ): JsValueValidator = and(arrayOfString,
                                             arraySpec(minItems,
                                                       maxItems,
                                                       unique
                                                       )
                                             )

  val arrayOfLong: JsValueValidator = and(array,
                                          JsValueValidator((value: JsValue) =>
                                                             if (value.asJsArray.seq.forall(v => v.isLong)) JsValueOk
                                                             else JsValueError(ARRAY_OF_LONG_NOT_FOUND)
                                                           )
                                          )

  def arrayOfLong(minItems: Long = -1,
                  maxItems: Long = -1,
                  unique  : Boolean = false
                 ): JsValueValidator = and(arrayOfLong,
                                           arraySpec(minItems,
                                                     maxItems,
                                                     unique
                                                     )
                                           )


  val arrayOfDecimal: JsValueValidator = and(array,
                                             JsValueValidator((value: JsValue) =>
                                                                if (value.asJsArray.seq.forall(v => v.isDecimal || v.isDouble)) JsValueOk
                                                                else JsValueError(ARRAY_OF_DECIMAL_NOT_FOUND)
                                                              )
                                             )

  def arrayOfDecimal(minItems: Int = -1,
                     maxItems: Int = -1,
                     unique  : Boolean = false
                    ): JsValueValidator = and(arrayOfDecimal,
                                              arraySpec(minItems,
                                                        maxItems,
                                                        unique
                                                        )
                                              )

  val arrayOfNumber: JsValueValidator = and(array,
                                            JsValueValidator((value: JsValue) =>
                                                               if (value.asJsArray.seq.forall(v => v.isNumber)) JsValueOk
                                                               else JsValueError(ARRAY_OF_NUMBER_NOT_FOUND)
                                                             )
                                            )

  def arrayOfNumber(minItems: Int = -1,
                    maxItems: Int = -1,
                    unique  : Boolean = false
                   ): JsValueValidator = and(arrayOfNumber,
                                             arraySpec(minItems,
                                                       maxItems,
                                                       unique
                                                       )
                                             )

  def arrayOf(validator: JsValueValidator,
              message  : String
             ): JsValueValidator =
  {
    and(array,
        JsValueValidator((value: JsValue) =>
                           if (
                             value.asJsArray.seq.forall(v => validator.f(v) match
                             {
                               case JsValueOk => true
                               case JsValueError(_) => false
                             }
                                                        )
                           ) JsValueOk else JsValueError(message)
                         )
        )

  }


  def arrayOf(validator: JsValueValidator,
              minItems : Int = -1,
              maxItems : Int = -1,
              unique   : Boolean = false
             ): JsValueValidator = and(array,
                                       JsValueValidator((value: JsValue) =>
                                                        {
                                                          val arr = value.asJsArray

                                                          @scala.annotation.tailrec
                                                          def apply0(array: JsArray): JsValueValidationResult =
                                                          {
                                                            if (array.isEmpty) JsValueOk
                                                            else
                                                            {
                                                              val head: JsValue = array.head
                                                              val result = validator.f(head)
                                                              result match
                                                              {
                                                                case JsValueOk => apply0(array.tail)
                                                                case error: JsValueError => error
                                                              }
                                                            }
                                                          }

                                                          apply0(arr)

                                                        }
                                                        ),
                                       arraySpec(minItems,
                                                 maxItems,
                                                 unique
                                                 )
                                       )

  val arrayOfIntegral: JsValueValidator = and(array,
                                              JsValueValidator((value: JsValue) =>
                                                               {
                                                                 val arr = value.asJsArray
                                                                 if (!arr.seq.forall(e => e.isIntegral)) JsValueError(ARRAY_OF_INTEGRAL_NOT_FOUND)
                                                                 else JsValueOk
                                                               }
                                                               )
                                              )

  def arrayOfIntegral(minItems: Int = -1,
                      maxItems: Int = -1,
                      unique  : Boolean = false
                     ): JsValueValidator = and(arrayOfIntegral,
                                               arraySpec(minItems,
                                                         maxItems,
                                                         unique
                                                         )
                                               )


  def array(condition: JsArray => Boolean,
            message  : JsArray => String
           ): JsValueValidator = and(array,
                                     JsValueValidator((value: JsValue) =>
                                                        if (condition.apply(value.asJsArray)) JsValueOk
                                                        else JsValueError(message(value.asJsArray))
                                                      )
                                     )

  private def arraySpec(minItems: Int,
                        maxItems: Int,
                        unique  : Boolean
                       ): JsValueValidator =
  {
    JsValueValidator((value: JsValue) =>
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
                       if (errors.isEmpty) JsValueOk
                       else JsValueError(errors)
                     }
                     )
  }

  private def arraySpec(minItems: Long,
                        maxItems: Long,
                        unique  : Boolean
                       ): JsValueValidator =
  {
    JsValueValidator((value: JsValue) =>
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
                       if (errors.isEmpty) JsValueOk
                       else JsValueError(errors)
                     }
                     )
  }
}
