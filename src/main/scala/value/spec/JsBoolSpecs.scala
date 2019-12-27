package value.spec

object JsBoolSpecs
{
  val bool: JsSpec = IsBool()

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

  def bool(nullable: Boolean = false,
              required: Boolean = true
             ): JsSpec = IsBool(nullable,
                                required
                                )

}
