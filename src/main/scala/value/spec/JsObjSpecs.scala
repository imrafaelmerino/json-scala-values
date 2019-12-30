package value.spec

import value.JsObj

/**
 * Factory of specs to define values as Json objects
 */
object JsObjSpecs
{

  /**
   * spec to specify that a value is a Json object
   */
  val obj: JsSpec = IsObj()

  /**
   * returns a spec to specify that a value is a Json object
   *
   * @param nullable if true, null is allowed
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def obj(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsObj(nullable,
                           required
                           )

  /**
   * return a spec to specify that a value is a Json object that conforms the specified spec
   * @param spec the specified Json object spec
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the value is mandatory
   * @return a spec
   */
  def conforms(spec: JsObjSpec,
               nullable: Boolean = false,
               required: Boolean = true
              ): JsSpec = IsObjSpec(spec,
                                    nullable,
                                    required
                                    )

  /**
   * returns a spec to specify that a value is a Json object that satisfies a predicate
   * @param p the predicate the Json object has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the value is mandatory
   * @return  a spec
   */
  def objSuchThat(p: JsObj => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                 ): JsSpec = IsObjSuchThat(p,
                                           nullable,
                                           required
                                           )
}
