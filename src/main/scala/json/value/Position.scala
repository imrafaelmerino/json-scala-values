package json.value

/**
 * represents a position in a Json. A JsPath is a list of positions.
 */
sealed trait Position
{
  def asKey: Key

  def isKey: Boolean

  def isKey(f: String => Boolean): Boolean

  def isIndex(f: Int => Boolean): Boolean

  def mapKey(key: String => String): Position

  def isIndex: Boolean

  def asIndex: Index
}

/**
 * represents a key in a Json object
 * @param name name of the key
 */
case class Key(name: String) extends Position
{
  override def asKey: Key = this

  override def isKey: Boolean = true

  override def mapKey(f: String => String): Position = Key(f(this.name))

  override def asIndex: Index = throw UserError.toIndexOfKey

  override def isKey(f: String => Boolean): Boolean = f(name)

  override def isIndex(f: Int => Boolean): Boolean = false

  override def isIndex: Boolean = false

  override def toString: String = s"""\"$name\""""
}

/**
 * represents an index in a Json array
 * @param i the number of the index
 */
final case class Index(i: Int) extends Position
{
  override def asKey: Key = throw UserError.toKeyOfIndex

  override def isKey: Boolean = false

  override def mapKey(key: String => String): Position = throw UserError.mapKeyOfIndex

  override def asIndex: Index = this

  override def isKey(f: String => Boolean): Boolean = false

  override def isIndex(f: Int => Boolean): Boolean = f(i)

  override def isIndex: Boolean = true

  override def toString: String = Integer.toString(i)
}

