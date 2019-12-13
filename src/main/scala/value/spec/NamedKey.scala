package value.spec

private[value] sealed trait SpecKey
{}

private[value] case class NamedKey(name: String) extends SpecKey

object * extends SpecKey
