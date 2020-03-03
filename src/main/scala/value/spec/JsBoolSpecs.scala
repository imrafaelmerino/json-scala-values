package value.spec

/**
 * Factory of specs to define values as booleans
 */
object JsBoolSpecs
  /**
   * spec to specify that a value is a boolean
   */
  val bool: JsSpec = IsBool()

  /**
   * spec to specify that a value is true
   */
  val isTrue: JsSpec = isTrue()
  /**
   * spec to specify that a value is false
   */
  val isFalse: JsSpec = isFalse()

  /**
   * returns a spec to specify that a value is true
   *
   * @param nullable if true, null is allowed
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def isTrue(nullable: Boolean = false,
             required: Boolean = true
            ): JsSpec = IsTrue(nullable,
                               required
                               )

  /**
   * returns a spec to specify that a value is false
   *
   * @param nullable if true, null is allowed
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def isFalse(nullable: Boolean = false,
              required: Boolean = true
             ): JsSpec = IsFalse(nullable,
                                 required
                                 )

  /**
   * returns a spec to specify that a value is a boolean
   * @param nullable if true, null is allowed
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def bool(nullable: Boolean = false,
           required: Boolean = true
          ): JsSpec = IsBool(nullable,
                             required
                             )

