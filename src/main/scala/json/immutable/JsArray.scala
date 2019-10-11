package json.immutable

import json.immutable.JsArray.{fillWithNull, remove}
import json.{AbstractJsArray, Index, JsElem, JsNull, JsPath, JsStr, Key}

import scala.collection.immutable

case class JsArray(override protected val seq: immutable.Seq[JsElem]) extends AbstractJsArray(seq) with json.JsArray with json.immutable.Json[JsArray]
{
  @`inline` final def :+(elem: JsElem): JsArray = appended(elem)

  def appended(ele: JsElem): JsArray = JsArray(seq.appended(ele))

  @`inline` final def +:(elem: JsElem): JsArray = prepended(elem)

  def prepended(ele: JsElem): JsArray = JsArray(seq.prepended(ele))

  @`inline` final def ++:(xs: IterableOnce[JsElem]): JsArray = prependedAll(xs)

  def prependedAll(xs: IterableOnce[JsElem]): JsArray = JsArray(seq.prependedAll(xs))

  @`inline` final def :++(xs: IterableOnce[JsElem]): JsArray = appendedAll(xs)

  def appendedAll(xs : IterableOnce[JsElem]): JsArray = JsArray(seq.appendedAll(xs))

  override def empty: JsArray = JsArray.NIL

  override def init: JsArray = JsArray(seq.init)

  override def tail: JsArray = JsArray(seq.tail)

  override def inserted(pair: (JsPath, JsElem)): JsArray =
  {
    val (path, elem) = pair

    if (path.isEmpty) return this

    path.head match
    {
      case Key(_) => this
      case Index(i) => path.tail match
      {
        case JsPath.empty => JsArray(fillWithNull(seq,
                                                  i,
                                                  elem
                                                  )
                                     )

        case tail: JsPath => tail.head match
        {
          case Index(_) => seq.lift(i) match
          {
            case a: Some[JsArray] => JsArray(fillWithNull(seq,
                                                          i,
                                                          a.get.inserted(tail,
                                                                         elem
                                                                         )
                                                          )
                                             )
            case _ => JsArray(fillWithNull(seq,
                                           i,
                                           JsArray.NIL.inserted(tail,
                                                                elem
                                                                )
                                           )
                              )
          }
          case Key(_) => seq.lift(i) match
          {
            case o: Some[JsObj] => JsArray(fillWithNull(seq,
                                                        i,
                                                        o.get.inserted(tail,
                                                                       elem
                                                                       )
                                                        )
                                           )
            case _ => JsArray(fillWithNull(seq,
                                           i,
                                           JsObj.NIL.inserted(tail,
                                                              elem
                                                              )
                                           )
                              )
          }
        }
      }
    }
  }

  override def removed(path: JsPath): JsArray =
  {

    if (path.isEmpty) return this

    path.head match
    {
      case Key(_) => this
      case Index(i) => path.tail match
      {
        case JsPath.empty => JsArray(remove(i,
                                            seq
                                            )
                                     )

        case tail: JsPath => tail.head match
        {
          case Index(_) => seq.lift(i) match
          {
            case a: Some[JsArray] => JsArray(seq.updated(i,
                                                         a.get.removed(tail
                                                                       )
                                                         )
                                             )
            case _ => this
          }
          case Key(_) => seq.lift(i) match
          {
            case o: Some[JsObj] => JsArray(seq.updated(i,
                                                       o.get.removed(tail
                                                                     )
                                                       )
                                           )
            case _ => this
          }
        }
      }
    }
  }

  override def updated(pair: (JsPath, JsElem)): JsArray =
  {

    val (path, elem) = pair

    if (path.isEmpty) return this

    path.head match
    {
      case Key(_) => this
      case Index(i) => path.tail match
      {
        case JsPath.empty => JsArray(seq.updated(i,
                                                 elem
                                                 )
                                     )

        case tail: JsPath => tail.head match
        {
          case Index(_) => seq.lift(i) match
          {
            case a: Some[JsArray] => JsArray(seq.updated(i,
                                                         a.get.inserted(tail,
                                                                        elem
                                                                        )
                                                         )
                                             )
            case _ => this
          }
          case Key(_) => seq.lift(i) match
          {
            case o: Some[JsObj] => JsArray(seq.updated(i,
                                                       o.get.inserted(tail,
                                                                      elem
                                                                      )
                                                       )
                                           )
            case _ => this
          }
        }
      }
    }

  }

  override def removedAll(xs: IterableOnce[JsPath]): JsArray =
  {

    @scala.annotation.tailrec
    def removeRec(iter: Iterator[JsPath],
                  arr : JsArray
                 ): JsArray =
    {

      if (iter.isEmpty) arr
      else removeRec(iter,
                     arr.removed(iter.next())
                     )
    }

    removeRec(xs.iterator,
              this
              )

  }


}

object JsArray
{
  val NIL = JsArray(Vector.empty)

  @scala.annotation.tailrec
  private def fillWithNull(seq: Seq[JsElem],
                           i  : Int,
                           e  : JsElem
                          ): Seq[JsElem] =
  {
    val length = seq.length
    if (i < length) seq.updated(i,
                                e
                                )
    else if (i == length) seq.appended(e)
    else fillWithNull(seq.appended(JsNull),
                      i,
                      e
                      )

  }

  private def remove(i  : Int,
                     seq: Seq[JsElem]
                    ): Seq[JsElem] =
  {

    if (seq.isEmpty) seq
    else if (i >= seq.size) seq
    else
    {
      val (prefix, suffix): (Seq[JsElem], Seq[JsElem]) = seq.splitAt(i)
      prefix.appendedAll(suffix.tail)
    }
  }


  def apply(a: JsElem): JsArray = JsArray(Vector(a))

  def apply(a       : JsElem,
            b       : JsElem
           ): JsArray = JsArray(Vector(a,
                                       b
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem,
            e: JsElem
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d,
                                       e
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem,
            e: JsElem,
            f: JsElem,
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d,
                                       e,
                                       f
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem,
            e: JsElem,
            f: JsElem,
            g: JsElem
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d,
                                       e,
                                       f,
                                       g
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem,
            e: JsElem,
            f: JsElem,
            g: JsElem,
            h: JsElem,
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d,
                                       e,
                                       f,
                                       g,
                                       h
                                       )
                                )


  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem,
            e: JsElem,
            f: JsElem,
            g: JsElem,
            h: JsElem,
            i: JsElem
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d,
                                       e,
                                       f,
                                       g,
                                       h,
                                       i
                                       )
                                )

  def apply(a: JsElem,
            b: JsElem,
            c: JsElem,
            d: JsElem,
            e: JsElem,
            f: JsElem,
            g: JsElem,
            h: JsElem,
            i: JsElem,
            j: JsElem
           ): JsArray = JsArray(Vector(a,
                                       b,
                                       c,
                                       d,
                                       e,
                                       f,
                                       g,
                                       h,
                                       i,
                                       j
                                       )
                                )


  def apply(xs: Iterable[JsElem]): JsArray = ???


}
