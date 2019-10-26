package jsonvalues

class JsObjValidator
{

  def apply(pair: (JsPath, (JsObj, JsValue) => Boolean)*): ValidationResult = ValidationSuccess()

}
