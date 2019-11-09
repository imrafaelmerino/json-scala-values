package value.spec

sealed trait Result
{
  def isValid: Boolean

  def isInvalid: Boolean = !isValid

  def isInvalid(messages: Seq[String] => Boolean): Boolean

  def ++(result: Result): Result
}

object Valid extends Result
{
  override def ++(result: Result): Result =
  {
    result match
    {
      case Valid => Valid
      case error: Invalid => error
    }
  }

  override def isValid: Boolean = true

  override def isInvalid(messages: Seq[String] => Boolean): Boolean = false
}

final case class Invalid(messages: Seq[String]) extends Result
{
  override def ++(result: Result): Result =
  {
    result match
    {
      case Valid => result
      case Invalid(messages) => Invalid(messages ++ this.messages)
    }
  }

  override def equals(that: Any): Boolean = that match
  {
    case Invalid(messages) => this.messages == messages
    case _ => false
  }

  override def isValid: Boolean = false

  override def isInvalid(predicate: Seq[String] => Boolean): Boolean = predicate(messages)
}

object Invalid
{
  def apply(message: String): Invalid = Invalid(Seq.empty.appended(message))
}