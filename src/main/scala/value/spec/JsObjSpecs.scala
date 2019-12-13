package value.spec

import value.JsObj

object JsObjSpecs
{

  val obj: JsSpec = IsObj()
  val obj_or_null: JsSpec = IsObj(nullable = true)

  def obj(nullable: Boolean,
          required: Boolean
         ): JsSpec = IsObj(nullable,
                           required
                           )

  def objSpec(spec: JsObjSpec,
              nullable: Boolean = false,
              required: Boolean = true
             ): JsSpec = IsObjSpec(spec,
                                   nullable,
                                   required
                                   )

  def objSuchThat(p: JsObj => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                 ): JsSpec = IsObjSuchThat(p,
                                           nullable,
                                           required
                                           )
}
