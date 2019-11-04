package jsonvalues

sealed trait JsValueValidationResult
{
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
}

object JsValueError
{
  def apply(message: String): JsValueError = JsValueError(Seq.empty.appended(message))
}