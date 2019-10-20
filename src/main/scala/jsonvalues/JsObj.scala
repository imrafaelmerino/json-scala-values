package jsonvalues

import java.util.Objects.requireNonNull

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.START_ARRAY
import com.fasterxml.jackson.core.JsonTokenId._
import jsonvalues.Implicits._
import jsonvalues.JsPath./

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}

final case class JsObj(map: immutable.Map[String, JsValue] = HashMap.empty) extends Json[JsObj]
{

  override def toLazyList: LazyList[(JsPath, JsValue)] =
  {
    def toLazyList(obj: JsObj
                  ): LazyList[(JsPath, JsValue)] =
    {

      if (obj.isEmpty) LazyList.empty

      else obj.head #:: toLazyList(obj.tail)
    }

    toLazyList(this)
  }

  override def toLazyListRec: LazyList[(JsPath, JsValue)] = JsObj.toLazyList_(/,
                                                                              this
                                                                              )

  override def isObj: Boolean = true

  override def isArr: Boolean = false

  override def isEmpty: Boolean = map.isEmpty

  def head: (String, JsValue) = map.head

  def headOption(): Option[(String, JsValue)] = map.headOption

  def last: (String, JsValue) = map.last

  def lastOption: Option[(String, JsValue)] = map.lastOption

  def keys: Iterable[String] = map.keys

  def apply(key: String): JsValue = apply(Key(key))

  def apply(pos: Position): JsValue =
  {
    pos match
    {
      case Key(name) => map.applyOrElse(name,
                                        (_: String) => JsNothing
                                        )
      case Index(_) => JsNothing
    }
  }

  override def size: Int = map.size

  override def toString: String =
  {
    map.keys.map(key => map(key) match
    {
      case o: JsObj => s""""$key":${o.toString}"""
      case a: JsArray => s""""$key":${a.toString}"""
      case _ => s""""$key":${map(key)}"""
    }
                 ).mkString("{",
                            ",",
                            "}"
                            )
  }

  def keySet: Set[String] = map.keySet

  override def empty: JsObj = JsObj(map.empty)

  override def init: JsObj = JsObj(map.init)

  override def tail: JsObj = JsObj(map.tail)

  override def removed(path: JsPath): JsObj =
  {
    if (path.isEmpty) return this

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.removed(k))


        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.removed(tail)
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.removed(tail)
                                                     )
                                         )
            case _ => this
          }

        }

      }
    }
  }

  override def updated(pair: (JsPath, JsValue)): JsObj =
  {
    val (path, elem) = pair

    if (path.isEmpty) return this
    if (elem.isNothing) return this

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.updated(k,
                                               elem
                                               )
                                   )
        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.updated(tail,
                                                                 elem
                                                                 )
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.updated(tail,
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

  override def removedAll(xs: IterableOnce[JsPath]): JsObj =
  {
    @scala.annotation.tailrec
    def apply0(iter: Iterator[JsPath],
               obj : JsObj
              ): JsObj =
    {

      if (iter.isEmpty) obj
      else apply0(iter,
                  obj.removed(iter.next())
                  )
    }

    apply0(xs.iterator,
           this
           )
  }


  override def inserted(pair: (JsPath, JsValue)): JsObj =
  {
    val (path, elem) = pair
    if (path.isEmpty) return this
    if (elem.isNothing) return this

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.updated(k,
                                               elem
                                               )
                                   )
        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.inserted((tail,
                                                                    elem)
                                                                  )
                                                       )
                                           )
            case _ => JsObj(map.updated(k,
                                        JsArray().inserted(tail,
                                                           elem
                                                           )
                                        )
                            )
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.inserted((tail,
                                                                  elem)
                                                                )
                                                     )
                                         )
            case _ => JsObj(map.updated(k,
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

  override def equals(that: Any): Boolean =
  {
    if (that == null) false
    else that match
    {
      case JsObj(m) => m == map
      case _ => false
    }
  }
}


object JsObj
{


  val emptyMap: HashMap[String, JsValue] = HashMap.empty

  def apply(pair: (JsPath, JsValue)*): JsObj =
  {
    @scala.annotation.tailrec
    def applyRec(acc : JsObj,
                 pair: Seq[(JsPath, JsValue)]
                ): JsObj =
    {
      if (pair.isEmpty) acc
      else applyRec(acc.inserted(pair.head),
                    pair.tail
                    )
    }

    applyRec(JsObj(emptyMap),
             pair
             )
  }

  private[jsonvalues] def toLazyList_(path: JsPath,
                                     value: JsObj
                                    ): LazyList[(JsPath, JsValue)] =
  {
    if (value.isEmpty) return LazyList.empty
    val head = value.head

    head._2 match
    {
      case o: JsObj => if (o.isEmpty) (path / head._1, o) +: toLazyList_(path,
                                                                         value.tail
                                                                         ) else toLazyList_(path / head._1,
                                                                                            o
                                                                                            ) ++: toLazyList_(path,
                                                                                                              value.tail
                                                                                                              )
      case a: JsArray => if (a.isEmpty) (path / head._1, a) +: toLazyList_(path,
                                                                           value.tail
                                                                           ) else JsArray.toLazyList_(path / head._1 / -1,
                                                                                                      a
                                                                                                      ) ++: toLazyList_(path,
                                                                                                                        value.tail
                                                                                                                        )
      case _ => (path / head._1, head._2) +: toLazyList_(path,
                                                         value.tail
                                                         )

    }
  }

  import java.io.IOException

  import com.fasterxml.jackson.core.JsonParser

  def parse(str: String): Try[JsObj] =
  {
    var parser: JsonParser = null
    try
    {
      parser = Json.JACKSON_FACTORY.createParser(requireNonNull(str))
      val event: JsonToken = parser.nextToken
      if (event eq START_ARRAY) Failure(MalformedJson.jsArrayExpected(str))
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

  @throws[IOException]
  private[jsonvalues] def parse(parser: JsonParser): JsObj =
  {
    var map: HashMap[String, JsValue] = HashMap.empty
    var key = parser.nextFieldName
    while (
    {key != null})
    {
      var elem: JsValue = null
      parser.nextToken.id match
      {
        case ID_STRING => elem = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => elem = JsNumber(parser)
        case ID_NUMBER_FLOAT => elem = JsBigDec(parser.getDecimalValue)
        case ID_FALSE => elem = JsBool.FALSE
        case ID_TRUE => elem = JsBool.TRUE
        case ID_NULL => elem = JsNull
        case ID_START_OBJECT => elem = JsObj.parse(parser)
        case ID_START_ARRAY => elem = JsArray.parse(parser)
        case _ => throw InternalError.tokenNotFoundParsingStringIntJsObj(parser.currentToken.name)
      }
      map = map.updated(key,
                        elem
                        )
      key = parser.nextFieldName
    }
    JsObj(map)
  }


}