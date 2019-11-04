package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.{BOOLEAN_NOT_FOUND, FALSE_NOT_FOUND, TRUE_NOT_FOUND}
import jsonvalues.{JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}

object JsBoolSpec
{

  val TRUE: JsValidator = JsValueValidator((value: JsValue) => if (value.isBool && value.asJsBool.value) JsValueOk else JsValueError(TRUE_NOT_FOUND(value)))
  val FALSE: JsValidator = JsValueValidator((value: JsValue) => if (value.isBool && !value.asJsBool.value) JsValueOk else JsValueError(FALSE_NOT_FOUND(value)))
  val boolean: JsValidator = JsValueValidator((value: JsValue) => if (value.isBool) JsValueOk else JsValueError(BOOLEAN_NOT_FOUND(value)))

}
