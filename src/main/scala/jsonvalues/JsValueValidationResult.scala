package jsonvalues

sealed trait JsValueValidationResult
{
  def isSuccess:Boolean
  def isFailure:Boolean = !isSuccess
  def isFailure(messages:Seq[String] => Boolean):Boolean
  def ++(result: JsValueValidationResult): JsValueValidationResult
}

object JsValueOk extends JsValueValidationResult
{
  override def ++(result: JsValueValidationResult): JsValueValidationResult =
  {
    result match
    {
      case JsValueOk => JsValueOk
      case error: JsValueError => error
    }
  }

  override def isSuccess: Boolean = true

  override def isFailure(messages: Seq[String] => Boolean): Boolean = false
}

final case class JsValueError(messages: Seq[String]) extends JsValueValidationResult
{
  override def ++(result: JsValueValidationResult): JsValueValidationResult = {
    result match
    {
      case JsValueOk => result
      case JsValueError(messages) => JsValueError(messages++this.messages)
    }
  }

  override def equals(that: Any): Boolean = that match
  {
    case JsValueError(messages) => this.messages == messages
    case _ => false
  }

  override def isSuccess: Boolean = false

  override def isFailure(predicate: Seq[String] => Boolean): Boolean = predicate(messages)
}

object JsValueError
{
  def apply(message: String): JsValueError = JsValueError(Seq.empty.appended(message))
}