package value.spec

import value.JsValue
import value.spec.ErrorMessages.{NOTHING_FOUND, NULL_FOUND}

/**
 * Factory of specs
 */
object JsSpecs
{

  /**
   * spec that any value conforms
   */
  val any: JsSpec = any(required = true)

  /**
   * returns a spec that any value conforms
   *
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def any(required: Boolean
         ): JsSpec = IsValue(required = required)

  /**
   * returns a spec that conforms any value that is evaluated to true in the predicate
   * @param p the predicate
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def anySuchThat(p: JsValue => Result,
                  required: Boolean = true
                 ): JsSpec = IsValueSuchThat(p,
                                             required = required
                                             )


}
