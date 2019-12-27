package value.spec

import value.JsValue
import value.spec.ErrorMessages.{NOTHING_FOUND, NULL_FOUND}

object JsSpecs
{
  val any: JsSpec = any(required = true)

  def any(required: Boolean
         ): JsSpec = IsValueSuchThat((value: JsValue) =>
                                     {
                                       if (required && value.isNothing) Invalid(NOTHING_FOUND)
                                       else Valid
                                     }
                                     )


}
