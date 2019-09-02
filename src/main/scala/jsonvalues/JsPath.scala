package jsonvalues

import scala.collection.immutable.Vector

case class JsPath(positions: Vector[Position])
{
  def inc(): JsPath =
  {
    if (isEmpty) throw UserError.incOfEmptyPath
    last match
    {
      case Key(_) => throw UserError.incOfKey(this)
      case Index(i) => (i + 1) +: init
    }
  }


  def :+(i: Int): JsPath =
  {
    JsPath(positions appended Index(i))
  }

  def +:(i: Int): JsPath =
  {
    JsPath(positions prepended Index(i))
  }

  def :+(name: String): JsPath =
  {
    JsPath(positions appended Key(name))
  }

  def +:(name: String): JsPath =
  {
    JsPath(positions prepended Key(name))
  }

  def ++(path: JsPath): JsPath =
  {
    JsPath(positions ++ path.positions)
  }

  def ++:(path: JsPath): JsPath =
  {
    JsPath(positions ++: path.positions)
  }

  def head: Position = positions.head

  def tail: JsPath = JsPath(positions.tail)

  def last: Position = positions.last

  def init: JsPath = JsPath(positions.init)

  def isEmpty: Boolean = positions.isEmpty

}

object JsPath
{

  import scala.language.implicitConversions

  val empty = JsPath(Vector.empty)

  implicit def fromKey(name: String): JsPath =
  {
    empty :+ name
  }

  implicit def fromIndex(n: Int): JsPath =
  {
    empty :+ n
  }

  implicit def path(path: String): JsPath =
  {
    empty
  }
}