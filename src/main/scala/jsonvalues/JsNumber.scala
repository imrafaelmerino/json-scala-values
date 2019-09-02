package jsonvalues

trait JsNumber extends JsElem
{

  override def isArr: Boolean = false

  override def isObj: Boolean = false

  override def isStr: Boolean = false

  override def isBool: Boolean = false

  override def isNull: Boolean = false

  override def isNumber: Boolean = true

  override def isNothing: Boolean = false



}
