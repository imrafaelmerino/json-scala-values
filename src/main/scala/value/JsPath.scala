package value

import scala.collection.immutable.Vector
import java.util.Objects.requireNonNull



/**
 * Represents the full path location of an element in a json. The easiest way of creating a JsPath is:
 * {{{
 * import value.Preamble._
 * val path  = "a" / "b" / "c"
 * val path1 =  0 / "a" / 1
 * }}}
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

  /** Alias for `appended` */
  @`inline` def /(index: Int): JsPath = appended(requireNonNull(index))

  def appended(i: Int): JsPath =
  {
    JsPath(positions appended Index(requireNonNull(i)))
  }

  /** Alias for `prepended` */
  @`inline` def \(index: Int): JsPath = prepended(requireNonNull(index))

  def prepended(i: Int): JsPath =
  {
    JsPath(positions prepended Index(requireNonNull(i)))
  }

  /** Alias for `appended` */
  @`inline` def /(key: String): JsPath = appended(requireNonNull(key))

  def appended(name: String): JsPath =
  {
    JsPath(positions appended Key(requireNonNull(name)))
  }

  /** Alias for `appended` */
  @`inline` def /(key: Key): JsPath = appended(key)

  def appended(key: Key): JsPath =
  {
    JsPath(positions appended key)
  }

  /** Alias for `prepended` */
  @`inline` def \(key: String): JsPath = prepended(requireNonNull(key))

  def prepended(key: String): JsPath =
  {
    JsPath(positions prepended Key(requireNonNull(key)))
  }

  @`inline` def /(path: JsPath): JsPath = appended(requireNonNull(path))

  def appended(path: JsPath): JsPath =
  {
    JsPath(positions ++ requireNonNull(path).positions)
  }

  @`inline` def \(path: JsPath): JsPath = prepended(requireNonNull(path))

  def prepended(path: JsPath): JsPath =
  {
    JsPath(requireNonNull(path).positions ++: positions)
  }

  def head: Position = positions.head

  def tail: JsPath = JsPath(positions.tail)

  def last: Position = positions.last

  def init: JsPath = JsPath(positions.init)

  def isEmpty: Boolean = positions.isEmpty



  override def toString
  : String = positions.mkString(" / ")

}

object JsPath
{
  val empty: JsPath = JsPath(Vector.empty)

  /**
   * points to the last element of an array
   */
  val MINUS_ONE = JsPath(Vector(Index(-1)))


}