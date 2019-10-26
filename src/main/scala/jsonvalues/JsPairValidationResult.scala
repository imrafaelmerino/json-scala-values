package jsonvalues

sealed trait JsValueValidation{}
case class JsValueOk() extends JsValueValidation
case class JsValueError(pair:(JsPath,JsValue),message:String) extends JsValueValidation
