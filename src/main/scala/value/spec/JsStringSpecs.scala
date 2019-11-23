package value.spec

import java.util.Objects.requireNonNull

import Messages._

import scala.util.matching.Regex
import value.spec.JsValueSpec._
import value.JsValue


object JsStringSpecs
{
  val string: JsValueSpec = JsValueSpec((value: JsValue) =>
                                          if (value.isStr) Valid
                                          else Invalid(STRING_NOT_FOUND(value))
                                        )

  def string(minLength: Int = -1,
             maxLength: Int = -1
            ): JsValueSpec =
  {

    and(string,
        JsValueSpec((value: JsValue) =>
                    {
                      val str = value.asJsStr.value
                      var errorMessages: collection.immutable.Seq[String] = collection.immutable.Vector.empty
                      if (minLength != -1 && str.length < minLength)
                        errorMessages = errorMessages.appended(STRING_OF_LENGTH_LOWER_THAN_MINIMUM(str,
                                                                                                   minLength
                                                                                                   )
                                                               )
                      if (maxLength != -1 && str.length > maxLength)
                        errorMessages = errorMessages.appended(STRING_OF_LENGTH_GREATER_THAN_MAXIMUM(str,
                                                                                                     maxLength
                                                                                                     )
                                                               )

                      if (errorMessages.isEmpty) Valid
                      else Invalid(errorMessages)
                    }
                    )
        )

  }

  def string(pattern: Regex): JsValueSpec =
  {

    and(string,
        JsValueSpec(
          (value: JsValue) =>
          {
            val str = value.asJsStr.value
            if (!requireNonNull(pattern).matches(str))
              Invalid(STRING_DOESNT_MATCH_PATTERN(str,
                                                  pattern.pattern.pattern()
                                                  )
                      )
            else Valid

          }
          )
        )

  }

  def string(minLength: Int,
             maxLength: Int,
             pattern  : Regex
            ): JsValueSpec =
  {

    and(string,
        string(minLength = requireNonNull(minLength),
               maxLength = requireNonNull(maxLength)
               ),
        string(requireNonNull(pattern))
        )
  }

  def string(condition: String => Boolean,
             message  : String => String
            ): JsValueSpec =
  {
    requireNonNull(condition)
    requireNonNull(message)
    and(string,
        JsValueSpec((value                         : JsValue) =>
                      if (condition.apply(value.asJsStr.value)) Valid
                      else Invalid(message(value.asJsStr.value))
                    )
        )
  }

  def enum(constants: String*): JsValueSpec = and(string,
                                                  JsValueSpec((value: JsValue) =>
                                                              {
                                                                val constant = value.asJsStr.value
                                                                if (requireNonNull(constants).contains(constant))
                                                                  Valid else Invalid(STRING_NOT_IN_ENUM(constant,
                                                                                                        constants
                                                                                                        )
                                                                                     )
                                                              }
                                                              )
                                                  )
}
