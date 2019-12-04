package value.spec

import Messages._

import scala.util.matching.Regex
import value.Implicits._

import scala.collection.immutable

//TODO Poner en  singletons los JsStringSpec, todos menos el generico que se crea un por funcion

object JsStringSpecs
{

  val string: JsSpec = string(nullable = false,
                              optional = false
                              )

  def string(nullable : Boolean,
             optional : Boolean
            ): JsSpec = IsStr(nullable,
                              optional
                              )

  def string(minLength: Int,
             maxLength: Int
            ): JsSpec = string(minLength, maxLength, nullable = false, optional = false)

  def string(minLength: Int,
             maxLength: Int,
             nullable : Boolean,
             optional : Boolean
            ): JsSpec =
  {

    IsStrSuchThat((str: String) =>
                  {
                    var errorMessages: collection.immutable.Seq[String] = immutable.Vector.empty
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
                  },
                  nullable,
                  optional

                  )

  }

  def string(pattern: Regex): JsSpec = string(pattern,
                                              nullable = false,
                                              optional = false
                                              )

  def string(pattern                : Regex,
             nullable: Boolean,
             optional               : Boolean
            ): JsSpec =
  {
    IsStrSuchThat((str: String) =>
                  {
                    if (!pattern.matches(str))
                      Invalid(STRING_DOESNT_MATCH_PATTERN(str,
                                                          pattern.pattern.pattern()
                                                          )
                              )
                    else Valid

                  },
                  nullable,
                  optional

                  )
  }

  def string(minLength: Int,
             maxLength: Int,
             pattern  : Regex,
             nullable : Boolean = false,
             optional : Boolean = false
            ): JsSpec =
  {

    IsStrSuchThat((str: String) =>
                  {
                    var errorMessages: collection.immutable.Seq[String] = immutable.Vector.empty
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
                  },
                  nullable,
                  optional

                  )
  }

  def string(condition: String => Boolean,
             message  : String => String,
             nullable : Boolean,
             optional : Boolean
            ): JsSpec = IsStrSuchThat((str: String) =>
                                        if (condition.apply(str)) Valid
                                        else Invalid(message(str)),
                                      nullable,
                                      optional
                                      )

  def enum(nullable: Boolean,
           optional  : Boolean,
           constants : String*
          ): JsSpec = IsStrSuchThat((str                        : String) =>
                                    {
                                      if (constants.contains(str))
                                        Valid
                                      else Invalid(STRING_NOT_IN_ENUM(str,
                                                                      constants
                                                                      )
                                                   )
                                    },
                                    nullable,
                                    optional
                                    )

  def enum(constants: String*
          ): JsSpec = enum(false,
                           false,
                           constants: _*
                           )
}
