package value.spec

import value.Implicits._

//TODO Poner en  singletons los JsIntSpec, todos menos el generico que se crea un por funcion
object JsBoolSpecs
{
  val boolean: JsSpec = boolean(nullable = false,
                                optional = false
                                )

  val booleanOrNull: JsSpec = boolean(nullable = true,
                                      optional = false
                                      )

  def TRUE(nullable: Boolean = false,
           optional: Boolean = false
          ): JsSpec = IsTrue(nullable,
                             optional
                             )

  def FALSE(nullable: Boolean = false,
            optional: Boolean = false
           ): JsSpec = IsFalse(nullable,
                               optional
                               )

  def boolean(nullable: Boolean,
              optional: Boolean
             ): JsSpec = IsBool(nullable,
                                optional
                                )

}
