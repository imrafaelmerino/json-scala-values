package value.spec

import value.Implicits._

//TODO Poner en  singletons los JsIntSpec, todos menos el generico que se crea un por funcion
object JsBoolSpecs
{
  val boolean: JsSpec = boolean(nullable = false,
                                required = true
                                )

  val booleanOrNull: JsSpec = boolean(nullable = true,
                                      required = true
                                      )

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
