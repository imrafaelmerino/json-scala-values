package value.spec

import java.util.Objects.requireNonNull

sealed trait Result
{
  def isValid: Boolean

  def isInvalid(messages: Seq[String] => Boolean): Boolean

  def +(result: Result): Result
}

object Valid extends Result
{
  def isInvalid: Boolean = false

  override def +(result: Result): Result =
  {
    requireNonNull(result) match
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
  def isInvalid: Boolean = true

  override def +(result: Result): Result =
  {
    requireNonNull(result) match
    {
      case Valid => this
      case Invalid(messages) => Invalid(this.messages ++ messages)
    }
  }

  override def equals(that: Any): Boolean = that match
  {
    case Invalid(messages) => this.messages == messages
    case _ => false
  }

  override def isValid: Boolean = false

  override def isInvalid(predicate: Seq[String] => Boolean): Boolean = requireNonNull(predicate)(messages)

}

object Invalid
{
  def apply(message: String): Invalid = Invalid(Seq.empty.appended(requireNonNull(message)))
}