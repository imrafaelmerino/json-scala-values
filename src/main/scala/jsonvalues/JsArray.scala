package jsonvalues

import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.START_OBJECT
import com.fasterxml.jackson.core.JsonTokenId._
import jsonvalues.Implicits._
import jsonvalues.JsArray.remove

import scala.collection.immutable
import scala.util.{Failure, Success, Try}

final case class JsArray(seq: immutable.Vector[JsValue] = Vector.empty) extends Json[JsArray]
{

  def toLazyList: LazyList[(JsPath, JsValue)] =
  {

    def toLazyList(i  : Int,
                   arr: JsArray
                  ): LazyList[(JsPath, JsValue)] =
    {
      if (arr.isEmpty) LazyList.empty

      else
      {
        val pair = (i, arr.head)
        pair #:: toLazyList(i + 1,
                            arr.tail
                            )
      }

    }

    toLazyList(0,
               this
               )
  }

  def toLazyListRec: LazyList[(JsPath, JsValue)] = JsArray.toLazyList_(-1,
                                                                       this
                                                                       )

  def isObj: Boolean = false

  def isArr: Boolean = true

  def isEmpty: Boolean = seq.isEmpty

  def length(): Int = seq.length

  def apply(i: Int): JsValue = apply(Index(i))

  def apply(pos: Position): JsValue = pos match
  {

    case Index(i) => seq.applyOrElse(i,
                                     (_: Int) => jsonvalues.JsNothing
                                     )
    case Key(_) => jsonvalues.JsNothing
  }

  def head: JsValue = seq.head

  def size: Int = seq.size

  override def toString: String = seq.mkString("[",
                                               ",",
                                               "]"
                                               )


  @scala.annotation.tailrec
  protected[jsonvalues] def fillWithNull[E <: JsValue](seq: immutable.Vector[JsValue],
                                                       i  : Int,
                                                       e  : E
                                                      ): immutable.Vector[JsValue] =
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

  @`inline` def :+(elem: JsValue): JsArray = appended(elem)

  def appended(ele: JsValue): JsArray = if (ele.isNothing) this else JsArray(seq.appended(ele))

  @`inline` def +:(elem: JsValue): JsArray = prepended(elem)

  def prepended(ele: JsValue): JsArray = if (ele.isNothing) this else JsArray(seq.prepended(ele))

  @`inline` def ++:(xs: IterableOnce[JsValue]): JsArray = prependedAll(xs)

  def prependedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.prependedAll(xs.iterator.filterNot(e => e.isNothing)))

  @`inline` def :++(xs: IterableOnce[JsValue]): JsArray = appendedAll(xs)

  def appendedAll(xs: IterableOnce[JsValue]): JsArray = JsArray(seq.appendedAll(xs.iterator.filterNot(e => e.isNothing)))

  override def empty: JsArray = JsArray(seq.empty)

  override def init: JsArray = JsArray(seq.init)

  override def tail: JsArray = JsArray(seq.tail)

  override def inserted(pair: (JsPath, JsValue)): JsArray =
  {
    val (path, elem) = pair

    if (path.isEmpty) return this
    if (elem.isNothing) return this
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
            case Some(a: JsArray) => JsArray(fillWithNull(seq,
                                                          i,
                                                          a.inserted((tail,
                                                                       elem)
                                                                     )
                                                          )
                                             )
            case _ => JsArray(fillWithNull(seq,
                                           i,
                                           JsArray().inserted((tail,
                                                                elem)
                                                              )
                                           )
                              )
          }
          case Key(_) => seq.lift(i) match
          {
            case Some(o: JsObj) => JsArray(fillWithNull(seq,
                                                        i,
                                                        o.inserted((tail,
                                                                     elem)
                                                                   )
                                                        )
                                           )
            case _ => JsArray(fillWithNull(seq,
                                           i,
                                           JsObj().inserted((tail,
                                                              elem)
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
            case Some(a: JsArray) =>
              JsArray(seq.updated(i,
                                  a.removed(tail
                                            )
                                  )
                      )
            case _ => this
          }
          case Key(_) => seq.lift(i) match
          {
            case Some(o: JsObj) =>
              JsArray(seq.updated(i,
                                  o.removed(tail
                                            )
                                  )
                      )
            case _ => this
          }
        }
      }
    }
  }

  override def updated(pair: (JsPath, JsValue)): JsArray =
  {

    val (path, elem) = pair
    if (elem.isNothing) return this
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
            case Some(a: JsArray) =>
              val updated: Vector[JsValue] = seq.updated(i,
                                                         a.updated(tail,
                                                                   elem
                                                                   )
                                                         )
              JsArray(updated)
            case _ => this
          }
          case Key(_) => seq.lift(i) match
          {
            case Some(o: JsObj) =>
              val updated: Vector[JsValue] = seq.updated(i,
                                                         o.updated((tail,
                                                                     elem)
                                                                   )
                                                         )
              JsArray(updated)
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

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsArray(m) => m == seq
      case _ => false
    }
  }

}

object JsArray
{


  final private[jsonvalues] def remove(i  : Int,
                                       seq: immutable.Vector[JsValue]
                                      ): immutable.Vector[JsValue] =
  {

    if (seq.isEmpty) seq
    else if (i >= seq.size) seq
    else
    {
      val (prefix, suffix): (immutable.Vector[JsValue], immutable.Vector[JsValue]) = seq.splitAt(i)
      prefix.appendedAll(suffix.tail)
    }
  }

  private[jsonvalues] def toLazyList_(path : JsPath,
                                      value: JsArray
                                     ): LazyList[(JsPath, JsValue)] =
  {
    if (value.isEmpty) return LazyList.empty
    val head: JsValue = value.head
    val headPath: JsPath = path.inc
    head match
    {
      case a: JsArray => if (a.isEmpty) (headPath, a) +: toLazyList_(headPath,
                                                                     value.tail
                                                                     ) else toLazyList_(headPath / -1,
                                                                                        a
                                                                                        ) ++: toLazyList_(headPath,
                                                                                                          value.tail
                                                                                                          )
      case o: JsObj => if (o.isEmpty) (headPath, o) +: toLazyList_(headPath,
                                                                   value.tail
                                                                   ) else JsObj.toLazyList_(headPath,
                                                                                            o
                                                                                            ) ++: toLazyList_(headPath,
                                                                                                              value.tail
                                                                                                              )
      case _ => (headPath, head) +: toLazyList_(headPath,
                                                value.tail
                                                )
    }
  }

  def apply(elems: JsValue*): JsArray = JsArray(elems.toVector)

  import java.io.IOException

  import com.fasterxml.jackson.core.JsonParser

  @throws[IOException]
  private[jsonvalues] def parse(parser: JsonParser): JsArray =
  {
    var root: Vector[JsValue] = Vector.empty
    while (
    {true})
    {

      val token: JsonToken = parser.nextToken
      var elem: JsValue = null
      token.id match
      {
        case ID_END_ARRAY => return JsArray(root)
        case ID_START_OBJECT => elem = JsObj.parse(parser)
        case ID_START_ARRAY => elem = JsArray.parse(parser)
        case ID_STRING => elem = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => elem = JsNumber(parser)
        case ID_NUMBER_FLOAT => elem = JsBigDec(parser.getDecimalValue)
        case ID_TRUE => elem = JsBool.TRUE
        case ID_FALSE => elem = JsBool.FALSE
        case ID_NULL => elem = JsNull
        case _ => throw InternalError.tokenNotFoundParsingStringIntJsArray(token.name)
      }
      root = root.appended(elem)
    }
    throw InternalError.endArrayTokenExpected()
  }

  def parse(str: String): Try[JsArray] =
  {
    var parser: JsonParser = null
    try
    {
      parser = Json.JACKSON_FACTORY.createParser(requireNonNull(str))

      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT) Failure(MalformedJson.jsArrayExpected(str))
      else Success(parse(parser))

    }
    catch
    {
      case e: IOException => Failure(MalformedJson.errorWhileParsing(str,
                                                                     e
                                                                     )
                                     )
    } finally
      if (parser != null) parser.close()
  }

}
