package value.spec
sealed trait Result
{

  def orExceptionIfInvalid[V, E <: Exception](validResult: V,
                                              map     : Invalid => E
                                             ): V

  def isValid: Boolean

  def isInvalid(message: String => Boolean): Boolean

}
object Valid extends Result
{

  override def toString: String = "Valid"

  def isInvalid:Boolean = false

  override def isValid: Boolean = true

  override def isInvalid(message: String => Boolean): Boolean = false

  override def orExceptionIfInvalid[V, E <: Exception](validResult: V,
                                                       map      : Invalid => E
                                             ): V = validResult
}

final case class Invalid(message: String) extends Result
{
  def isInvalid:Boolean = true

  override def toString: String = message

  override def equals(that: Any): Boolean = that match
  {
    case Invalid(message) => this.message == message
    case _ => false
  }

  override def isValid: Boolean = false

  override def isInvalid(predicate: String => Boolean): Boolean = predicate(message)

  override def orExceptionIfInvalid[V, E <: Exception](validResult: V,
                                                       map     : Invalid => E
                                             ): V = throw map(this)
}

