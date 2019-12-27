package value.spec

import value.spec.ErrorMessages._

object JsStrSpecs
{

  val str: JsSpec = IsStr()

  def str(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsStr(nullable,
                           required
                           )

  def strSuchThat(p       : String => Result,
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
