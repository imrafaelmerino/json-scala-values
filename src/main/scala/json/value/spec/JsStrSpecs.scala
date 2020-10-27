package json.value.spec
import java.util.Objects.requireNonNull

import json.value.spec.ValidationMessages._

/**
 * Factory of specs to define values as strings
 */
object JsStrSpecs
{

  /**
   * spec to specify that a json.value is a string
   */
  val str: JsSpec = IsStr()

  /**
   * returns a spec to specify that a json.value is a string
   *
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def str(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsStr(nullable,
                           required
                           )

  /**
   * returns a spec to specify that a json.value is a string that satisfies a predicate
   *
   * @param p        the predicate the string has to satisfy
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def strSuchThat(p       : String => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                 ): JsSpec = IsStrSuchThat(requireNonNull(p),
                                           nullable = nullable,
                                           required = required
                                           )

  /**
   * returns a spec to restrict a json.value to a fixed set of strings
   *
   * @param nullable  if true, null is allowed
   * @param required  if true, the json.value is mandatory
   * @param constants the set of allowed strings
   * @return a spec
   */
  def consts(nullable : Boolean,
             required : Boolean,
             constants: String*
            ): JsSpec = IsStrSuchThat((str: String) =>
                                      {
                                        if (requireNonNull(constants).contains(str))
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
   * returns a spec to restrict a json.value to a fixed set of strings
   *
   * @param constants the set of allowed strings
   * @return a spec
   */
  def consts(constants: String*
            ): JsSpec = consts(false,
                               true,
                               constants: _*
                               )
}
