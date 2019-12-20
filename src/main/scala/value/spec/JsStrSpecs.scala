package value.spec

import value.spec.ErrorMessages._

object JsStrSpecs
{

  val str: JsSpec = IsStr()

  val str_or_null: JsSpec = IsStr(nullable = true)

  def str(nullable: Boolean,
          required: Boolean
         ): JsSpec = IsStr(nullable,
                           required
                           )

  def stringSuchThat(p: String => Result,
                     nullable: Boolean = false,
                     required: Boolean = true
                    ): JsSpec = IsStrSuchThat(p,
                                              nullable = nullable,
                                              required = required
                                              )

  def enum(nullable: Boolean,
           required: Boolean,
           constants: String*
          ): JsSpec = IsStrSuchThat((str: String) =>
                                    {
                                      if (constants.contains(str))
                                        Valid
                                      else Invalid(STRING_NOT_IN_ENUM(str,
                                                                      constants
                                                                      )
                                                   )
                                    },
                                    nullable,
                                    required
                                    )

  def enum(constants: String*
          ): JsSpec = enum(false,
                           true,
                           constants: _*
                           )
}
