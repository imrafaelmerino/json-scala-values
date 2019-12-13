package value.spec


object JsLongSpecs
{
  val long: JsSpec = IsLong()
  val long_or_null: JsSpec = IsLong(nullable = true)

  def long(nullable: Boolean,
           required: Boolean
          ) = IsLong(nullable,
                     required
                     )

  def longSuchThat(p: Long => Result,
                   nullable: Boolean = false,
                   required: Boolean = true
                  ): JsSpec = IsLongSuchThat(p,
                                             nullable = nullable,
                                             required = required
                                             )


}
