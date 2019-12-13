package value.spec

object JsIntSpecs
{

  val int: JsSpec = IsInt()

  val int_or_null: JsSpec = IsInt(nullable = true)

  def int(nullable: Boolean,
          required: Boolean
         ): JsSpec = IsInt(nullable,
                           required
                           )

  def intSuchThat(p: Int => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                 ): JsSpec = IsIntSuchThat(p,
                                           nullable = nullable,
                                           required = required
                                           )

}
