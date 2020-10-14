package value

import scala.collection.immutable.Vector
import java.util.Objects.requireNonNull



/**
 * Represents the full path location of an element in a json. The easiest way of creating a JsPath is.
 * @param positions keys and/or indexes a path is made up of
 */
final case class JsPath(private [value] val positions: Vector[Position])
{

  def length: Int = positions.size

  def inc: JsPath =
  {
    if (isEmpty) throw UserError.incOfEmptyPath
    last match
    {
      case Key(_) => throw UserError.incOfKey(this)
      case Index(i) => init / (i + 1)
    }
  }

  /** Alias for `append` */
  @`inline` def /(index: Int): JsPath = append(requireNonNull(index))

  def append(i: Int): JsPath =
    JsPath(positions :+ Index(requireNonNull(i)))

  /** Alias for `prepended` */
  @`inline` def \(index: Int): JsPath = prepended(requireNonNull(index))

  def prepended(i: Int): JsPath =
    JsPath(Index(requireNonNull(i)) +: positions)

  /** Alias for `append` */
  @`inline` def /(key: String): JsPath = append(requireNonNull(key))

  def append(name: String): JsPath =
    JsPath(positions :+ Key(requireNonNull(name)))

  /** Alias for `prepended` */
  @`inline` def \(key: String): JsPath = prepended(requireNonNull(key))

  def prepended(key: String): JsPath =
    JsPath(Key(requireNonNull(key)) +: positions)

  @`inline` def /(path: JsPath): JsPath = append(requireNonNull(path))

  def append(path: JsPath): JsPath =
    JsPath(positions ++ requireNonNull(path).positions)

  @`inline` def \(path: JsPath): JsPath = prepended(requireNonNull(path))

  def prepended(path: JsPath): JsPath =
    JsPath(requireNonNull(path).positions ++: positions)

  def head: Position = positions.head

  def tail: JsPath = JsPath(positions.tail)

  def last: Position = positions.last

  def init: JsPath = JsPath(positions.init)

  def isEmpty: Boolean = positions.isEmpty

  override def toString: String = positions.mkString(" / ")

}

object JsPath
{
  val empty: JsPath = JsPath(Vector.empty)

  /**
   * points to the last element of an array
   */
  val MINUS_ONE: JsPath = JsPath(Vector(Index(-1)))


}
