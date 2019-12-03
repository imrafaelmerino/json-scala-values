package value.spec

import Messages._
import scala.util.matching.Regex
import value.Implicits._

//TODO Poner en  singletons los JsStringSpec, todos menos el generico que se crea un por funcion

object JsStringSpecs
{
  val string: JsSpec = IsStr()

  def string(minLength: Int = -1,
             maxLength: Int = -1
            ): JsSpec =
  {

    IsStrSuchThat((str: String) =>
                  {
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

  }

  def string(pattern: Regex): JsSpec =
  {
    IsStrSuchThat((str: String) =>
                  {
                    if (!pattern.matches(str))
                      Invalid(STRING_DOESNT_MATCH_PATTERN(str,
                                                          pattern.pattern.pattern()
                                                          )
                              )
                    else Valid

                  }

                  )
  }

  def string(minLength: Int,
             maxLength: Int,
             pattern  : Regex
            ): JsSpec =
  {

    IsStrSuchThat((str: String) =>
                  {
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

                    if (!pattern.matches(str))
                      errorMessages = errorMessages.appended(STRING_DOESNT_MATCH_PATTERN(str,
                                                                                         pattern.pattern.pattern()
                                                                                         )
                                                             )
                    if (errorMessages.isEmpty) Valid
                    else Invalid(errorMessages)
                  }

                  )
  }

  def string(condition: String => Boolean,
             message  : String => String
            ): JsSpec = IsStrSuchThat((str: String) =>
                                        if (condition.apply(str)) Valid
                                        else Invalid(message(str))
                                      )

  def enum(constants: String*): JsSpec = IsStrSuchThat((str: String) =>
                                                       {
                                                         if (constants.contains(str))
                                                           Valid
                                                         else Invalid(STRING_NOT_IN_ENUM(str,
                                                                                         constants
                                                                                         )
                                                                      )
                                                       }
                                                       )
}
