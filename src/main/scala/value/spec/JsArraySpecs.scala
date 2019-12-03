package value.spec

import java.util.Objects.requireNonNull

import Messages._
import value.{JsArray, JsNull, JsValue}

object JsArraySpecs
{

  val array: JsSpec = IsArray()

  val arrayOfInt: JsSpec = IsArrayOfInt()

  def arrayOfInt(minItems: Long = -1,
                 maxItems: Long = -1,
                 unique  : Boolean = false,
                 nullable: Boolean = false
                ): JsSpec = IsArrayOfIntSuchThat(arraySpec(requireNonNull(minItems),
                                                           requireNonNull(maxItems),
                                                           requireNonNull(unique),
                                                           requireNonNull(nullable)
                                                           )
                                                 )


  val arrayOfString: JsSpec = IsArrayOfStr()

  def arrayOfString(minItems: Long = -1,
                    maxItems: Long = -1,
                    unique: Boolean = false,
                    nullable : Boolean = false
                   ): JsSpec = IsArrayOfStrSuchThat(arraySpec(requireNonNull(minItems),
                                                              requireNonNull(maxItems),
                                                              requireNonNull(unique),
                                                              requireNonNull(nullable)
                                                              )
                                                    )

  val arrayOfLong: JsSpec = IsArrayOfLong()

  def arrayOfLong(minItems: Long = -1,
                  maxItems: Long = -1,
                  unique: Boolean = false,
                  nullable: Boolean = false
                 ): JsSpec = IsArrayOfLongSuchThat(arraySpec(requireNonNull(minItems),
                                                             requireNonNull(maxItems),
                                                             requireNonNull(unique),
                                                             requireNonNull(nullable)
                                                             )
                                                   )


  val arrayOfDecimal: JsSpec = IsArrayOfDecimal()

  def arrayOfDecimal(minItems: Long = -1,
                     maxItems: Long = -1,
                     unique: Boolean = false,
                     nullable : Boolean = false
                    ): JsSpec = IsArrayOfDecimalSuchThat(arraySpec(requireNonNull(minItems),
                                                                   requireNonNull(maxItems),
                                                                   requireNonNull(unique),
                                                                   requireNonNull(nullable)
                                                                   )
                                                         )

  val arrayOfNumber: JsSpec = IsArrayOfNumber()


  def arrayOfNumber(minItems: Long = -1,
                    maxItems: Long = -1,
                    unique: Boolean = false,
                    nullable: Boolean = false
                   ): JsSpec = IsArrayOfNumberSuchThat(arraySpec(requireNonNull(minItems),
                                                                 requireNonNull(maxItems),
                                                                 requireNonNull(unique),
                                                                 requireNonNull(nullable)
                                                                 )
                                                       )

  def arrayOf(validator: JsValue => Boolean,
              message  : String
             ): JsSpec =
  {
    requireNonNull(message)
    requireNonNull(validator)
    IsArrayEachSuchThat((value: JsValue) => if (validator(value)) Valid else Invalid(message))

  }


  val arrayOfIntegral: JsSpec = IsArrayOfIntegral()

  def arrayOfIntegral(minItems: Long = -1,
                      maxItems: Long = -1,
                      unique: Boolean = false,
                      nullable: Boolean = false
                     ): JsSpec = IsArrayOfIntegralSuchThat(arraySpec(requireNonNull(minItems),
                                                                     requireNonNull(maxItems),
                                                                     requireNonNull(unique),
                                                                     requireNonNull(nullable)
                                                                     )
                                                           )


  def array(condition: JsArray => Boolean,
            message: JsArray => String,
            nullable : Boolean = false
           ): JsSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    IsArraySuchThat((array: JsArray) =>
                      if (condition.apply(array)) Valid
                      else Invalid(message(array))
                    )
  }


  private def arraySpec(minItems: Long,
                        maxItems: Long,
                        unique: Boolean,
                        nullable: Boolean
                       ): JsArray => Result =
  {

    arr: JsArray =>
    {
      var errors: Seq[String] = Seq.empty
      val length = arr.length()
      if (minItems != -1 && length < minItems)
        errors = errors :+ LONG_ARRAY_OF_LENGTH_LOWER_THAN_MINIMUM(length,
                                                                   minItems
                                                                   )
      if (maxItems != -1 && length > maxItems)
        errors = errors :+ LONG_ARRAY_OF_LENGTH_GREATER_THAN_MAXIMUM(length,
                                                                     maxItems
                                                                     )
      if (!nullable && arr.seq.exists(_.equals(JsNull)))
        errors = errors :+ ARRAY_WITH_NULL
      if (unique && arr.seq.distinct.length != arr.seq.length)
        errors = errors :+ ARRAY_WITH_DUPLICATES
      if (errors.isEmpty) Valid
      else Invalid(errors)
    }

  }
}
