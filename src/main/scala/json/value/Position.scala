package json.value

/**
 * represents a position in a Json. A JsPath is a list of positions.
 */
sealed trait Position

/**
 * represents a key in a Json object
 *
 * @param name name of the key
 */
final case class Key(name: String) extends Position:
  override def toString: String = s"""\"$name\""""

/**
 * represents an index in a Json array
 *
 * @param i the number of the index
 */
final case class Index(i: Int) extends Position:
  override def toString: String = Integer.toString(i)

  