package value.spec

import value.Implicits._

//TODO Poner en  singletons los JsIntSpec, todos menos el generico que se crea un por funcion
object JsBoolSpecs
{

  val TRUE: JsSpec = IsTrue()
  val FALSE: JsSpec = IsFalse()
  val boolean: JsSpec = IsBool()

}
