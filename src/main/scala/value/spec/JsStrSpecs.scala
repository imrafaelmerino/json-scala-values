package value.spec

import value.spec.ErrorMessages._

/**
 * Factory of specs to define values as strings
 */
object JsStrSpecs
{

  /**
   * spec to specify that a value is a string
   */
  val str: JsSpec = IsStr()

  /**
   * returns a spec to specify that a value is a string
   * @param nullable if true, null is allowed
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def str(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsStr(nullable,
                           required
                           )

  /**
   * returns a spec to specify that a value is a string that satisfies a predicate
   * @param p the predicate the string has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the value is mandatory
   * @return  a spec
   */
  def strSuchThat(p       : String => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                    ): JsSpec = IsStrSuchThat(p,
                                              nullable = nullable,
                                              required = required
                                              )

  /**
   * returns a spec to restrict a value to a fixed set of strings
   * @param nullable if true, null is allowed
   * @param required if true, the value is mandatory
   * @param constants the set of allowed strings
   * @return a spec
   */
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

  /**
   * returns a spec to restrict a value to a fixed set of strings
   * @param constants the set of allowed strings
   * @return a spec
   */
  def enum(constants: String*
          ): JsSpec = enum(false,
                           true,
                           constants: _*
                           )
}
