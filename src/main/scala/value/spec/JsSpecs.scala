package value.spec

import value.JsValue
import value.spec.ErrorMessages.{NOTHING_FOUND, NULL_FOUND}

/**
 *  Factory of specs
 */
object JsSpecs
{

  /**
   * spec that any value conforms
   */
  val any: JsSpec = any(required = true)

  /**
   * returns a spec that any value conforms
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def any(required: Boolean
         ): JsSpec = IsValueSuchThat((value: JsValue) =>
                                     {
                                       if (required && value.isNothing) Invalid(NOTHING_FOUND)
                                       else Valid
                                     }
                                     )


}
