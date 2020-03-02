package value.spec

/**
 * represents the result of validating a Json against a spec
 */
sealed trait Result
{
  def fold[B](ifValid: => B)
             (f: Invalid => B): B

  def isInvalid(message: String => Boolean): Boolean

}

/**
 * successful result
 */
object Valid extends Result
{

  override def toString: String = "Valid"

  override def fold[B](ifValid: => B)
                      (f: Invalid => B): B = ifValid

  override def isInvalid(message: String => Boolean): Boolean = false

}

/**
 * represents an error
 *
 * @param message the error message
 */
final case class Invalid(message: String) extends Result

  override def fold[B](ifValid: => B)
                      (f: Invalid => B): B = f(this)

  override def toString: String = message

  override def equals(that: Any): Boolean = that match
    case Invalid(message) => this.message == message
    case _ => false

  override def isInvalid(predicate: String => Boolean): Boolean = predicate(message)


