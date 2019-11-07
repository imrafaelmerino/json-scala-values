package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.{JS_OBJ_NOT_FOUND, OBJECT_DEPENDANT_KEY_NOT_FOUND, OBJECT_KEY_NOT_FOUND, OBJECT_NUMBER_KEYS_GREATER_THAN_MAXIMUM, OBJECT_NUMBER_KEYS_LOWER_THAN_MINIMUM}
import jsonvalues.spec.JsValueSpec._
import jsonvalues.{JsObj, JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}
import jsonvalues.Implicits._

import scala.collection.immutable

object JsObjSpec
{

  val obj: JsValueValidator = JsValueValidator((value: JsValue) => if (value.isObj) JsValueOk else JsValueError(JS_OBJ_NOT_FOUND(value)))

  def obj(minKeys: Int = -1,
          maxKeys: Int = -1,
          required         : Seq[String] = Seq.empty,
          dependentRequired: Seq[(String, Seq[String])] = Seq.empty
         ): JsValueValidator =
  {
    and(obj,
        JsValueValidator((value: JsValue) =>
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

                           if (errorMessages.isEmpty) JsValueOk
                           else JsValueError(errorMessages)
                         }
                         )
        )
  }

  def obj(condition: JsObj => Boolean,
          message  : JsObj => String
         ): JsValueValidator = and(obj,
                                   JsValueValidator((value: JsValue) =>
                                                      if (condition.apply(value.asJsObj))
                                                        JsValueOk
                                                      else JsValueError(message(value.asJsObj))
                                                    )
                                   )


}
