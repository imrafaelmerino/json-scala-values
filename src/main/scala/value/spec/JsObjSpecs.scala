package value.spec

import value.JsObj

object JsObjSpecs
{

  val obj: JsSpec = IsObj()

  def obj(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsObj(nullable,
                           required
                           )

  def conforms(spec: JsObjSpec,
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
