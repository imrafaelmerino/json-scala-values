package value.spec

/**
 * represents a Key of a JsObjSpec
 */
private[value] sealed trait SpecKey
{}

/**
 * key identified by name
 * @param name name of the key
 */
private[value] case class NamedKey(name: String) extends SpecKey

/**
 * represents all the keys not specified in a JsObjSpec:
 *
 * {{{
 *   JsObjSpec( "a" -> str,
 *              "b" -> int,
 *               *  -> any
 *            )
 * }}}
 *
 * Without specifying *, no key other than a and b are allowed by the spec
 *
 */
object * extends SpecKey
