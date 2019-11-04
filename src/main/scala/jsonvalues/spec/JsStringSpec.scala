package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.{STRING_DOESNT_MATCH_PATTERN, STRING_NOT_FOUND, STRING_NOT_IN_ENUM, STRING_OF_LENGTH_GREATER_THAN_MAXIMUM, STRING_OF_LENGTH_LOWER_THAN_MINIMUM}
import jsonvalues.{JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}
import jsonvalues.spec.JsValueSpec._
import jsonvalues.Implicits._

import scala.collection.immutable
import scala.util.matching.Regex

object JsStringSpec
{
  val string: JsValidator = JsValueValidator((value: JsValue) => if (value.isStr) JsValueOk else JsValueError(STRING_NOT_FOUND(value)))

  def string(minLength: Int = -1,
             maxLength: Int = -1
            ): JsValueValidator =
  {

    and(string,
        JsValueValidator((value: JsValue) =>
                         {
                           val str = value.asJsStr.value
                           var errorMessages: immutable.Seq[String] = immutable.Vector.empty
                           if (minLength != -1 && str.length < minLength)
                             errorMessages = errorMessages.appended(STRING_OF_LENGTH_LOWER_THAN_MINIMUM(str.length,
                                                                                                        minLength
                                                                                                        )
                                                                    )
                           if (maxLength != -1 && str.length > maxLength)
                             errorMessages = errorMessages.appended(STRING_OF_LENGTH_GREATER_THAN_MAXIMUM(str.length,
                                                                                                          maxLength
                                                                                                          )
                                                                    )

                           if (errorMessages.isEmpty) JsValueOk
                           else JsValueError(errorMessages)
                         }
                         )
        )

  }

  def string(pattern: Regex): JsValueValidator =
  {

    and(string,
        JsValueValidator(
          (value: JsValue) =>
          {
            val str = value.asJsStr.value
            if (!pattern.matches(str))
              JsValueError(STRING_DOESNT_MATCH_PATTERN(str,
                                                       pattern.pattern.pattern()
                                                       )
                           )
            else JsValueOk

          }
          )
        )

  }

  def string(minLength: Int,
             maxLength: Int,
             pattern: Regex
            ): JsValueValidator =
  {

    and(string,
        string(minLength = minLength,
               maxLength = maxLength
               ),
        string(pattern)
        )
  }

  def string(condition: String => Boolean,
             message  : JsValue => String
            ): JsValidator = and(string,
                                 JsValueValidator((value: JsValue) =>
                                                    if (condition.apply(value.asJsStr.value)) JsValueOk else JsValueError(message(value))
                                                  )
                                 )

  def enum(constants: String*): JsValidator = and(string,
                                                  JsValueValidator((value: JsValue) =>
                                                                   {
                                                                     val constant = value.asJsStr.value
                                                                     if (constants.contains(constant)) JsValueOk else JsValueError(STRING_NOT_IN_ENUM(constant,
                                                                                                                                                      constants
                                                                                                                                                      )
                                                                                                                                   )
                                                                   }
                                                                   )
                                                  )
}
