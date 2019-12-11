package value.spec

import scala.collection.immutable
import value.Implicits._
import Messages._
import value.JsObj

object JsObjSpecs
{

  //TODO OBJECT CON SPEC PARA ASI PODER DEFINIR SI ES OPTIONAL, REQUIRED ETC Y A LA VEZ
  //PODER UTILIZAR SPEC
  val obj: JsSpec = IsObj()
  val nullOrObj: JsSpec = IsObj(nullable = true)

  def obj(minKeys: Int = -1,
          maxKeys          : Int = -1,
          required         : Seq[String] = Seq.empty,
          dependentRequired: Seq[(String, Seq[String])] = Seq.empty
         ): JsSpec =
  {
    IsObjSuchThat((o: JsObj) =>
                  {
                    var errorMessages: immutable.Seq[String] = immutable.Vector.empty
                    val size = o.keys.size
                    if (minKeys != -1 && size < minKeys)
                      errorMessages = errorMessages.appended(OBJECT_NUMBER_KEYS_LOWER_THAN_MINIMUM(size,
                                                                                                   minKeys
                                                                                                   )
                                                             )
                    if (maxKeys != -1 && size > maxKeys)
                      errorMessages = errorMessages.appended(OBJECT_NUMBER_KEYS_GREATER_THAN_MAXIMUM(size,
                                                                                                     maxKeys
                                                                                                     )
                                                             )
                    if (required.nonEmpty)
                    {
                      for (requiredKey <- required) if (!o.containsPath(requiredKey))
                        errorMessages = errorMessages.appended(OBJECT_KEY_NOT_FOUND(requiredKey))
                    }
                    if (dependentRequired.nonEmpty)
                    {
                      for ((key, dependent) <- dependentRequired)
                      {
                        if (o.containsPath(key))
                        {
                          for (dependentRequiredKey <- dependent) if (!o.containsPath(dependentRequiredKey))
                            errorMessages = errorMessages.appended(OBJECT_DEPENDANT_KEY_NOT_FOUND(key,
                                                                                                  dependentRequiredKey
                                                                                                  )
                                                                   )
                        }
                      }
                    }

                    if (errorMessages.isEmpty) Valid
                    else Invalid(errorMessages)
                  }
                  )
  }

  def obj(condition: JsObj => Boolean,
          message  : JsObj => String
         ): JsSpec = IsObjSuchThat((o: JsObj) =>
                                     if (condition.apply(o))
                                       Valid
                                     else Invalid(message(o))
                                   )


}
