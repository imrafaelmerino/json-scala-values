package json.value.spec

/**
 * represents a Key of a JsObjSpec
 */
private[json] sealed trait SpecKey
{
  def name:String
}

/**
 * key identified by name
 * @param name name of the key
 */
private[json] case class NamedKey(name: String) extends SpecKey

/**
 * represents all the keys not specified in a JsObjSpec.
 * Without specifying *, no key other than a and b are allowed by the spec
 *
 */
object * extends SpecKey
{
  override def name: String = "*"
}
