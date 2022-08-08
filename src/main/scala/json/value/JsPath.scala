package json.value

import scala.Conversion
import scala.collection.immutable.Vector

/**
 * Represents the full path location of an element in a json. The easiest way of creating a JsPath is.
 *
 * @param positions keys and/or indexes a path is made up of
 */
final case class JsPath(private[json] val positions: Vector[Position]):
  def length: Int = positions.size

  def inc: JsPath =
    if isEmpty then throw UserError.incOfEmptyPath
    last match
      case Key(_) => throw UserError.incOfKey(this)
      case Index(i) => init / (i + 1)



  /** Alias for `appended` */
  @`inline` def /(index: Int): JsPath = appended(index)

  def appended(i: Int): JsPath = JsPath(positions appended Index(i))

  /** Alias for `prepended` */
  @`inline` def \(index: Int): JsPath = prepended(index)

  def prepended(i: Int): JsPath = JsPath(positions prepended Index(i))

  /** Alias for `appended` */
  @`inline` def /(key: String): JsPath = appended(key)

  def appended(name: String): JsPath = JsPath(positions appended Key(name))

  /** Alias for `prepended` */
  @`inline` def \(key: String): JsPath = prepended(key)

  def prepended(key: String): JsPath = JsPath(positions prepended Key(key))

  @`inline` def /(path: JsPath): JsPath = appended(path.nn)

  def appended(path: JsPath): JsPath = JsPath(positions ++ path.nn.positions)

  @`inline` def \(path: JsPath): JsPath = prepended(path.nn)

  def prepended(path: JsPath): JsPath = JsPath(path.nn.positions ++: positions)

  def head: Position = positions.head

  def tail: JsPath = JsPath(positions.tail)

  def last: Position = positions.last

  def init: JsPath = JsPath(positions.init)

  def isEmpty: Boolean = positions.isEmpty

  override def toString: String = positions.mkString(" / ")


object JsPath:
  val root: JsPath = JsPath(Vector.empty)
  /**
   * points to the last element of an array
   */
  val MINUS_ONE: JsPath = JsPath(Vector(Index(-1)))