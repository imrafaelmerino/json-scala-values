package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.{BOOLEAN_NOT_FOUND, FALSE_NOT_FOUND, TRUE_NOT_FOUND}
import jsonvalues.{JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}

object JsBoolSpec
{

  val TRUE: JsValueValidator = JsValueValidator((value: JsValue) =>
                                                  if (value.isBool && value.asJsBool.value)
                                                    JsValueOk else JsValueError(TRUE_NOT_FOUND(value))
                                                )
  val FALSE: JsValueValidator = JsValueValidator((value: JsValue) =>
                                                   if (value.isBool && !value.asJsBool.value) JsValueOk
                                                   else JsValueError(FALSE_NOT_FOUND(value))
                                                 )
  val boolean: JsValueValidator = JsValueValidator((value: JsValue) =>
                                                     if (value.isBool) JsValueOk
                                                     else JsValueError(BOOLEAN_NOT_FOUND(value))
                                                   )

}
