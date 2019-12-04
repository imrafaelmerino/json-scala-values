package value.spec

import java.util.Objects.requireNonNull

import Messages._
import value.{JsArray, JsNull, JsValue}

object JsArraySpecs
{

  val array = IsArray(nullable = false,
                      optional = false,
                      eachElemNullable = false)
  val arrayOfInt = IsArrayOfInt(nullable = false,
                                optional = false,
                                eachElemNullable = false
                                )
  val arrayOfLong = IsArrayOfLong(nullable = false,
                                  optional = false,
                                  eachElemNullable = false)
  val arrayOfDecimal = IsArrayOfDecimal(nullable = false,
                                        optional = false,
                                        eachElemNullable = false)
  val arrayOfIntegral = IsArrayOfIntegral(nullable = false,
                                          optional = false,
                                          eachElemNullable = false)
  val arrayOfBool = IsArrayOfBool(nullable = false,
                                  optional = false,
                                  eachElemNullable = false)
  val arrayOfNumber = IsArrayOfNumber(nullable = false,
                                      optional = false,
                                      eachElemNullable = false)
  val arrayOfStr = IsArrayOfStr(nullable = false,
                                optional = false,
                                eachElemNullable = false)
  val arrayOfObj = IsArrayOfObj(nullable = false,
                                optional = false,
                                eachElemNullable = false)


  def array(nullable: Boolean = false,
            optional: Boolean = false
           ): JsSpec = IsArray(nullable,
                               optional
                               )


  def arrayOfInt(minItems    : Long = -1,
                 maxItems    : Long = -1,
                 unique      : Boolean = false,
                 elemNullable: Boolean = false,
                 nullable    : Boolean = false,
                 optional    : Boolean = false
                ): JsSpec = IsArrayOfIntSuchThat(arraySpec(requireNonNull(minItems),
                                                           requireNonNull(maxItems),
                                                           requireNonNull(unique)
                                                           ),
                                                 requireNonNull(nullable),
                                                 requireNonNull(optional),
                                                 requireNonNull(elemNullable)
                                                 )


  def arrayOfString(minItems    : Long = -1,
                    maxItems    : Long = -1,
                    unique      : Boolean = false,
                    elemNullable: Boolean = false,
                    nullable    : Boolean = false,
                    optional    : Boolean = false
                   ): JsSpec = IsArrayOfStrSuchThat(arraySpec(requireNonNull(minItems),
                                                              requireNonNull(maxItems),
                                                              requireNonNull(unique)
                                                              ),
                                                    requireNonNull(nullable),
                                                    requireNonNull(optional),
                                                    requireNonNull(elemNullable)
                                                    )

  def arrayOfLong(minItems    : Long = -1,
                  maxItems    : Long = -1,
                  unique      : Boolean = false,
                  elemNullable: Boolean = false,
                  nullable    : Boolean = false,
                  optional    : Boolean = false
                 ): JsSpec = IsArrayOfLongSuchThat(arraySpec(requireNonNull(minItems),
                                                             requireNonNull(maxItems),
                                                             requireNonNull(unique)
                                                             ),
                                                   requireNonNull(nullable),
                                                   requireNonNull(optional),
                                                   requireNonNull(elemNullable)
                                                   )

  def arrayOfDecimal(minItems    : Long = -1,
                     maxItems    : Long = -1,
                     unique      : Boolean = false,
                     elemNullable: Boolean = false,
                     nullable    : Boolean = false,
                     optional    : Boolean = false
                    ): JsSpec = IsArrayOfDecimalSuchThat(arraySpec(requireNonNull(minItems),
                                                                   requireNonNull(maxItems),
                                                                   requireNonNull(unique)
                                                                   ),
                                                         requireNonNull(nullable),
                                                         requireNonNull(optional),
                                                         requireNonNull(elemNullable)
                                                         )

  def arrayOfNumber(minItems    : Long = -1,
                    maxItems    : Long = -1,
                    unique      : Boolean = false,
                    elemNullable: Boolean = false,
                    nullable    : Boolean = false,
                    optional    : Boolean = false
                   ): JsSpec = IsArrayOfNumberSuchThat(arraySpec(requireNonNull(minItems),
                                                                 requireNonNull(maxItems),
                                                                 requireNonNull(unique)
                                                                 ),
                                                       requireNonNull(nullable),
                                                       requireNonNull(optional),
                                                       requireNonNull(elemNullable)
                                                       )

  def arrayOf(validator: JsValue => Boolean,
              message  : String
             ): JsSpec = arrayOf(validator,
                                 message,
                                 nullable = false,
                                 optional = false
                                 )

  def arrayOf(predicate: JsValue => Boolean,
              message  : String,
              nullable : Boolean,
              optional : Boolean
             ): JsSpec =
  {
    requireNonNull(message)
    requireNonNull(predicate)
    IsArrayEachSuchThat((value: JsValue) => if (predicate(value)) Valid else Invalid(message),
                        nullable,
                        optional
                        )

  }

  def arrayOfIntegral(minItems    : Long = -1,
                      maxItems    : Long = -1,
                      unique      : Boolean = false,
                      elemNullable: Boolean = false,
                      nullable    : Boolean = false,
                      optional    : Boolean = false
                     ): JsSpec = IsArrayOfIntegralSuchThat(arraySpec(requireNonNull(minItems),
                                                                     requireNonNull(maxItems),
                                                                     requireNonNull(unique)
                                                                     ),
                                                           requireNonNull(nullable),
                                                           requireNonNull(optional),
                                                           requireNonNull(elemNullable)
                                                           )


  def array(condition: JsArray => Boolean,
            message  : JsArray => String
           ): JsSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    IsArraySuchThat((array: JsArray) =>
                      if (condition.apply(array)) Valid
                      else Invalid(message(array))
                    )
  }


  //TODO PASAR EL UNIQUE A OBJECTSPEC PARA NO ITERAR DOS VECES
  private def arraySpec(minItems: Long,
                        maxItems: Long,
                        unique  : Boolean
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
      if (unique && arr.seq.distinct.length != arr.seq.length)
        errors = errors :+ ARRAY_WITH_DUPLICATES
      if (errors.isEmpty) Valid
      else Invalid(errors)
    }

  }
}
