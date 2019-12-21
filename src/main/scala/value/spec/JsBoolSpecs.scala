package value.spec

object JsBoolSpecs
{
  val bool: JsSpec = IsBool()

  val bool_or_null: JsSpec = IsBool(nullable = true)

  val isTrue: JsSpec = isTrue()
  val isFalse: JsSpec = isFalse()

  def isTrue(nullable: Boolean = false,
             required: Boolean = true
            ): JsSpec = IsTrue(nullable,
                               required
                               )

  def isFalse(nullable: Boolean = false,
              required: Boolean = true
             ): JsSpec = IsFalse(nullable,
                                 required
                                 )

  def boolean(nullable: Boolean,
              required: Boolean
             ): JsSpec = IsBool(nullable,
                                required
                                )

}
