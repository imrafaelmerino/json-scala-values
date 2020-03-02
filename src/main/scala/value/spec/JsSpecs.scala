package value.spec
import java.util.Objects.requireNonNull
import value.JsValue

/**
 * Factory of specs
 */
object JsSpecs

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

  /**returns a spec that conforms any value that is evaluated to true on the predicate.
   * When the type is not specified by the spec, positive numbers are parsed as Long by default,
   * which has to be taken into account in order to define any condition.
   * @param p the predicate
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def anySuchThat(p: JsValue => Result,
                  required: Boolean = true
                 ): JsSpec = IsValueSuchThat(requireNonNull(p),
                                             required = required
                                             )


