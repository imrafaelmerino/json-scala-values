package jsonvalues

import scala.language.implicitConversions

import scala.collection.immutable.Vector

final case class JsPath(private[jsonvalues] val positions: Vector[Position])
{

  def size: Int = positions.size

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
  @`inline` def /(index: Int): JsPath = appended(index)

  def appended(i: Int): JsPath =
  {
    JsPath(positions appended Index(i))
  }

  /** Alias for `prepended` */
  @`inline` def \(index: Int): JsPath = prepended(index)

  def prepended(i: Int): JsPath =
  {
    JsPath(positions prepended Index(i))
  }

  /** Alias for `appended` */
  @`inline` def /(key: String): JsPath = appended(key)

  def appended(name: String): JsPath =
  {
    JsPath(positions appended Key(name))
  }

  /** Alias for `prepended` */
  @`inline` def \(key: String): JsPath = prepended(key)

  def prepended(key: String): JsPath =
  {
    JsPath(positions prepended Key(key))
  }

  @`inline` def /(path: JsPath): JsPath = appended(path)

  def appended(path: JsPath): JsPath =
  {
    JsPath(positions ++ path.positions)
  }

  @`inline` def \(path: JsPath): JsPath = prepended(path)

  def prepended(path: JsPath): JsPath =
  {
    JsPath(positions ++: path.positions)
  }

  def head: Position = positions.head

  def tail: JsPath = JsPath(positions.tail)

  def last: Position = positions.last

  def init: JsPath = JsPath(positions.init)

  def isEmpty: Boolean = positions.isEmpty

  override def toString: String = positions.toString()

}

object JsPath
{
  val / : JsPath = JsPath(Vector.empty)

  @`inline` val empty: JsPath = /

  implicit def fromStr(name: String): JsPath =
  {
    empty / name
  }

  implicit def fromInt(n: Int): JsPath =
  {
    empty / n
  }
  

}