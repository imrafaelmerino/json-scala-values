
package value.spec
import value.JsPath

import scala.collection.immutable.Seq
sealed trait Result
{
  def isValid: Boolean

  def isInvalid(messages: Seq[String] => Boolean): Boolean

  def +(result: Result): Result

  //TODO REFACTOR NO SE ENTIENDE BIEN
  def +?(seq: Seq[(JsPath,Invalid)], path:JsPath): Seq[(JsPath,Invalid)]}

object Valid extends Result
{
  def isInvalid:Boolean = false

  override def +(result: Result): Result =
  {
    result match
    {
      case Valid => Valid
      case error: Invalid => error
    }
  }

  override def isValid: Boolean = true

  override def isInvalid(messages: Seq[String] => Boolean): Boolean = false

  override def +?(seq: Seq[(JsPath,Invalid)], path:JsPath): Seq[(JsPath,Invalid)] = seq
}

final case class Invalid(messages: Seq[String]) extends Result
{
  def isInvalid:Boolean = true

  override def +(result: Result): Result =
  {
    result match
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

  override def isInvalid(predicate: Seq[String] => Boolean): Boolean = predicate(messages)

  override def +?(seq: Seq[(JsPath,Invalid)], path:JsPath): Seq[(JsPath,Invalid)] = seq.appended((path,this))

}

object Invalid
{
  def apply(message: String): Invalid = Invalid(Seq.empty.appended(message))
}