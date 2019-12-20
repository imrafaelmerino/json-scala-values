package value.spec

import value.JsValue
import value.spec.ErrorMessages.{NOTHING_FOUND, NULL_FOUND}

object JsSpecs
{
  val any: JsSpec = IsValue()

  def any(nullable: Boolean,
          required: Boolean
         ): JsSpec = IsValueSuchThat((value: JsValue) =>
                                     {
                                       if (!nullable && value.isNull) Invalid(NULL_FOUND)
                                       if (required && value.isNothing) Invalid(NOTHING_FOUND)
                                       Valid
                                     }
                                     )
}
