package value.spec

import Messages.{BOOLEAN_NOT_FOUND, FALSE_NOT_FOUND, TRUE_NOT_FOUND}
import value.JsValue

object JsBoolSpecs
{

  val TRUE: JsValueSpec = JsValueSpec((value: JsValue) =>
                                        if (value.isBool && value.asJsBool.value)
                                          Valid else Invalid(TRUE_NOT_FOUND(value))
                                      )
  val FALSE: JsValueSpec = JsValueSpec((value: JsValue) =>
                                         if (value.isBool && !value.asJsBool.value) Valid
                                         else Invalid(FALSE_NOT_FOUND(value))
                                       )
  val boolean: JsValueSpec = JsValueSpec((value: JsValue) =>
                                           if (value.isBool) Valid
                                           else Invalid(BOOLEAN_NOT_FOUND(value))
                                         )

}
