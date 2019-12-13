package value.spec

object JsBoolSpecs
{
  val bool: JsSpec = IsBool()

  val bool_or_null: JsSpec = IsBool(nullable = true)

  def TRUE(nullable: Boolean = false,
           required: Boolean = false
          ): JsSpec = IsTrue(nullable,
                             required
                             )

  def FALSE(nullable: Boolean = false,
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
