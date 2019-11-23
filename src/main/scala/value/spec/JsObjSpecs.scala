package value.spec

import value.spec.JsValueSpec._
import scala.collection.immutable
import value.Implicits._
import Messages._
import value.{JsObj, JsValue}

object JsObjSpecs
{

  val obj: JsValueSpec = JsValueSpec((value: JsValue) => if (value.isObj) Valid else Invalid(JS_OBJ_NOT_FOUND(value)))

  def obj(minKeys          : Int = -1,
          maxKeys          : Int = -1,
          required         : Seq[String] = Seq.empty,
          dependentRequired: Seq[(String, Seq[String])] = Seq.empty
         ): JsValueSpec =
  {
    and(obj,
        JsValueSpec((value: JsValue) =>
                    {
                      val o = value.asJsObj
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
                        for (requiredKey <- required) if (!o.contains(requiredKey))
                          errorMessages = errorMessages.appended(OBJECT_KEY_NOT_FOUND(requiredKey))
                      }
                      if (dependentRequired.nonEmpty)
                      {
                        for ((key, dependent) <- dependentRequired)
                        {
                          if (o.contains(key))
                          {
                            for (dependentRequiredKey <- dependent) if (!o.contains(dependentRequiredKey))
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
        )
  }

  def obj(condition: JsObj => Boolean,
          message  : JsObj => String
         ): JsValueSpec = and(obj,
                              JsValueSpec((value: JsValue) =>
                                            if (condition.apply(value.asJsObj))
                                              Valid
                                            else Invalid(message(value.asJsObj))
                                          )
                              )


}
