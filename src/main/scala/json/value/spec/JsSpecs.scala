package json.value.spec
import java.util.Objects.requireNonNull
import json.value.JsValue

/**
 * Factory of specs
 */
object JsSpecs
{

  /**
   * spec that any json.value conforms
   */
  val any: JsSpec = any(required = true)

  /**
   * returns a spec that any json.value conforms
   *
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def any(required: Boolean
         ): JsSpec = IsValue(required = required)

  /**returns a spec that conforms any json.value that is evaluated to true on the predicate.
   * When the type is not specified by the spec, positive numbers are parsed as Long by default,
   * which has to be taken into account in order to define any condition.
   * @param p the predicate
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def anySuchThat(p: JsValue => Result,
                  required: Boolean = true
                 ): JsSpec = IsValueSuchThat(requireNonNull(p),
                                             required = required
                                             )


}
