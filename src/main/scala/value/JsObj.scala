package value

import java.io.ByteArrayOutputStream
import java.util.Objects.requireNonNull

import JsPath./
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.START_ARRAY
import com.fasterxml.jackson.core.JsonTokenId._
import value.Implicits._
import value.spec.{Invalid, JsObjSpec}

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

  def conform(specs: (String, JsObjSpec)*): Seq[String] = specs.filter((spec: (String, JsObjSpec)) => this.validate(spec._2).isEmpty).map((spec: (String, JsObjSpec)) => spec._1)

  override def toLazyListRec: LazyList[(JsPath, JsValue)] = JsObj.toLazyList_(/,
                                                                              this
                                                                              )

  def containsKey(key: String): Boolean = map.contains(key)

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

  override def updated(path : JsPath,
                       value: JsValue,
                      ): JsObj =
  {

    if (path.isEmpty) return this
    if (value.isNothing) return this

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.updated(k,
                                               value
                                               )
                                   )
        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.updated(tail,
                                                                 value
                                                                 )
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.updated(tail,
                                                               value
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


  override def inserted(path   : JsPath,
                        value  : JsValue,
                        padWith: JsValue = JsNull
                       ): JsObj =
  {
    if (path.isEmpty) return this
    if (value.isNothing) return this

    path.head match
    {
      case Index(_) => this
      case Key(k) => path.tail match
      {
        case JsPath.empty => JsObj(map.updated(k,
                                               value
                                               )
                                   )
        case tail => tail.head match
        {
          case Index(_) => map.lift(k) match
          {
            case Some(a: JsArray) => JsObj(map.updated(k,
                                                       a.inserted(tail,
                                                                  value,
                                                                  padWith
                                                                  )
                                                       )
                                           )
            case _ => JsObj(map.updated(k,
                                        JsArray.empty.inserted(tail,
                                                               value,
                                                               padWith
                                                               )
                                        )
                            )
          }
          case Key(_) => map.lift(k) match
          {
            case Some(o: JsObj) => JsObj(map.updated(k,
                                                     o.inserted(tail,
                                                                value,
                                                                padWith
                                                                )
                                                     )
                                         )
            case _ => JsObj(map.updated(k,
                                        JsObj().inserted(tail,
                                                         value,
                                                         padWith
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

  def validate(spec: JsObjSpec): LazyList[(JsPath, Invalid)] = spec.validate(this)

  override def asJsObj: JsObj = this

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsObj

  override def filterRec(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filterRec(/,
                                                                                         map,
                                                                                         HashMap.empty,
                                                                                         p
                                                                                         )
                                                                         )

  override def filter(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filter(/,
                                                                                   map,
                                                                                   HashMap.empty,
                                                                                   p
                                                                                   )
                                                                      )


  override def filterJsObjRec(p: (JsPath, JsObj) => Boolean): JsObj = JsObj(JsObj.filterJsObjRec(/,
                                                                                                 map,
                                                                                                 HashMap.empty,
                                                                                                 p
                                                                                                 )
                                                                            )

  override def filterJsObj(p: (JsPath, JsObj) => Boolean): JsObj = JsObj(JsObj.filterJsObj(/,
                                                                                           map,
                                                                                           HashMap.empty,
                                                                                           p
                                                                                           )
                                                                         )

  override def filterKeyRec(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filterKeysRec(/,
                                                                                                map,
                                                                                                HashMap.empty,
                                                                                                p
                                                                                                )
                                                                            )

  override def filterKey(p: (JsPath, JsValue) => Boolean): JsObj = JsObj(JsObj.filterKeys(/,
                                                                                          map,
                                                                                          HashMap.empty,
                                                                                          p
                                                                                          )
                                                                         )


  override def mapRec[J <: JsValue](m: (JsPath, JsValue) => J,
                                    p: (JsPath, JsValue) => Boolean = (_, _) => true
                                   ): JsObj = JsObj(JsObj.mapRec(/,
                                                                 this.map,
                                                                 HashMap.empty,
                                                                 m,
                                                                 p
                                                                 )
                                                    )

  override def map[J <: JsValue](m: (JsPath, JsValue) => J,
                                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                                ): JsObj = JsObj(JsObj.map(/,
                                                           this.map,
                                                           HashMap.empty,
                                                           m,
                                                           p
                                                           )
                                                 )

  override def reduceRec[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                            m: (JsPath, JsValue) => V,
                            r: (V, V) => V
                           ): Option[V] = JsObj.reduceRec(JsPath.empty,
                                                          map,
                                                          p,
                                                          m,
                                                          r,
                                                          Option.empty
                                                          )

  override def reduce[V](p: (JsPath, JsValue) => Boolean = (_, _) => true,
                         m: (JsPath, JsValue) => V,
                         r: (V, V) => V
                        ): Option[V] = JsObj.reduce(JsPath.empty,
                                                    map,
                                                    p,
                                                    m,
                                                    r,
                                                    Option.empty
                                                    )

  override def asJson: Json[_] = this

  override def mapKeyRec(m: (JsPath, JsValue) => String,
                         p: (JsPath, JsValue) => Boolean = (_, _) => true
                        ): JsObj = JsObj(JsObj.mapKeyRec(/,
                                                         map,
                                                         HashMap.empty,
                                                         m,
                                                         p
                                                         )
                                         )

  override def mapKey(m: (JsPath, JsValue) => String,
                      p: (JsPath, JsValue) => Boolean = (_, _) => true
                     ): JsObj = JsObj(JsObj.mapKey(/,
                                                   map,
                                                   HashMap.empty,
                                                   m,
                                                   p
                                                   )
                                      )

}


object JsObj
{

  val empty = new JsObj(immutable.HashMap.empty)

  def apply(pair: (JsPath, JsValue)*): JsObj =
  {
    @scala.annotation.tailrec
    def applyRec(acc : JsObj,
                 pair: Seq[(JsPath, JsValue)]
                ): JsObj =
    {
      if (pair.isEmpty) acc
      else applyRec(acc.inserted(pair.head._1,
                                 pair.head._2
                                 ),
                    pair.tail
                    )
    }

    applyRec(empty,
             pair
             )
  }

  private[value] def toLazyList_(path : JsPath,
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
  protected[value] def parse(parser: JsonParser): JsObj =
  {
    var map: immutable.Map[String, JsValue] = HashMap.empty
    var key = parser.nextFieldName
    while (
    {key != null})
    {
      var value: JsValue = null
      parser.nextToken.id match
      {
        case ID_STRING => value = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => value = JsNumber(parser)
        case ID_NUMBER_FLOAT => value = JsBigDec(parser.getDecimalValue)
        case ID_FALSE => value = FALSE
        case ID_TRUE => value = TRUE
        case ID_NULL => value = JsNull
        case ID_START_OBJECT => value = JsObj.parse(parser)
        case ID_START_ARRAY => value = JsArray.parse(parser)
        case _ => throw InternalError.tokenNotFoundParsingStringIntoJsObj(parser.currentToken.name)
      }
      map = map.updated(key,
                        value
                        )
      key = parser.nextFieldName
    }
    JsObj(map)
  }


  @scala.annotation.tailrec
  private[value] def filter(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, json: Json[_]) => filter(path,
                                          input.tail,
                                          result.updated(key,
                                                         json
                                                         ),
                                          p
                                          )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filter(path,
                                                   input.tail,
                                                   result.updated(key,
                                                                  head
                                                                  ),
                                                   p
                                                   ) else filter(path,
                                                                 input.tail,
                                                                 result,
                                                                 p
                                                                 )
    }
  }

  private[value] def filterRec(path  : JsPath,
                               input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               p     : (JsPath, JsValue) => Boolean
                              ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => filterRec(path,
                                              input.tail,
                                              result.updated(key,
                                                             JsObj(filterRec(path / key,
                                                                             headMap,
                                                                             HashMap.empty,
                                                                             p
                                                                             )
                                                                   )
                                                             ),
                                              p
                                              )
      case (key, JsArray(headSeq)) => filterRec(path,
                                                input.tail,
                                                result.updated(key,
                                                               JsArray(JsArray.filterRec(path / key / -1,
                                                                                         headSeq,
                                                                                         Vector.empty,
                                                                                         p
                                                                                         )
                                                                       )
                                                               ),
                                                p
                                                )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filterRec(path,
                                                      input.tail,
                                                      result.updated(key,
                                                                     head
                                                                     ),
                                                      p
                                                      ) else filterRec(path,
                                                                       input.tail,
                                                                       result,
                                                                       p
                                                                       )
    }
  }

  private[value] def mapRec(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : (JsPath, JsValue) => JsValue,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) => mapRec(path,
                                           input.tail,
                                           result.updated(key,
                                                          JsObj(mapRec(path / key,
                                                                       headMap,
                                                                       HashMap.empty,
                                                                       m,
                                                                       p
                                                                       )
                                                                )
                                                          ),
                                           m,
                                           p
                                           )
      case (key, JsArray(headSeq)) => mapRec(path,
                                             input.tail,
                                             result.updated(key,
                                                            JsArray(JsArray.mapRec(path / key / -1,
                                                                                   headSeq,
                                                                                   Vector.empty,
                                                                                   m,
                                                                                   p
                                                                                   )
                                                                    )
                                                            ),
                                             m,
                                             p
                                             )
      case (key, head: JsValue) =>
        val headPath = path / key
        if (p(headPath,
              head
              )) mapRec(path,
                        input.tail,
                        result.updated(key,
                                       m(headPath,
                                         head
                                         )
                                       ),
                        m,
                        p
                        ) else mapRec(path,
                                      input.tail,
                                      result.updated(key,
                                                     head
                                                     ),
                                      m,
                                      p
                                      )
    }
  }

  @scala.annotation.tailrec
  private[value] def map(path  : JsPath,
                         input : immutable.Map[String, JsValue],
                         result: immutable.Map[String, JsValue],
                         m     : (JsPath, JsValue) => JsValue,
                         p     : (JsPath, JsValue) => Boolean
                        ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => map(path,
                                  input.tail,
                                  result.updated(key,
                                                 o
                                                 ),
                                  m,
                                  p
                                  )

      case (key, head: JsValue) =>
        val headPath = path / key
        if (p(headPath,
              head
              )) map(path,
                     input.tail,
                     result.updated(key,
                                    m(headPath,
                                      head
                                      )
                                    ),
                     m,
                     p
                     ) else map(path,
                                input.tail,
                                result.updated(key,
                                               head
                                               ),
                                m,
                                p
                                )
    }
  }

  private[value] def filterJsObjRec(path  : JsPath,
                                    input : immutable.Map[String, JsValue],
                                    result: immutable.Map[String, JsValue],
                                    p     : (JsPath, JsObj) => Boolean
                                   ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(path / key,
                                    o
                                    )) filterJsObjRec(path,
                                                      input.tail,
                                                      result.updated(key,
                                                                     JsObj(filterJsObjRec(path / key,
                                                                                          o.map,
                                                                                          HashMap.empty,
                                                                                          p
                                                                                          )
                                                                           )
                                                                     ),
                                                      p
                                                      ) else filterJsObjRec(path,
                                                                            input.tail,
                                                                            result,
                                                                            p
                                                                            )
      case (key, JsArray(headSeq)) => filterJsObjRec(path,
                                                     input.tail,
                                                     result.updated(key,
                                                                    JsArray(JsArray.filterJsObjRec(path / key / -1,
                                                                                                   headSeq,
                                                                                                   Vector.empty,
                                                                                                   p
                                                                                                   )
                                                                            )
                                                                    ),
                                                     p
                                                     )
      case (key, head: JsValue) => filterJsObjRec(path,
                                                  input.tail,
                                                  result.updated(key,
                                                                 head
                                                                 ),
                                                  p
                                                  )
    }
  }


  @scala.annotation.tailrec
  private[value] def filterJsObj(path  : JsPath,
                                 input : immutable.Map[String, JsValue],
                                 result: immutable.Map[String, JsValue],
                                 p     : (JsPath, JsObj) => Boolean
                                ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(path / key,
                                    o
                                    )) filterJsObj(path,
                                                   input.tail,
                                                   result.updated(key,
                                                                  o
                                                                  ),
                                                   p
                                                   ) else filterJsObj(path,
                                                                      input.tail,
                                                                      result,
                                                                      p
                                                                      )

      case (key, head: JsValue) => filterJsObj(path,
                                               input.tail,
                                               result.updated(key,
                                                              head
                                                              ),
                                               p
                                               )
    }
  }

  private[value] def mapKeyRec(path  : JsPath,
                               input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               m     : (JsPath, JsValue) => String,
                               p     : (JsPath, JsValue) => Boolean
                              ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) =>
        val headPath = path / key
        mapKeyRec(path,
                  input.tail,
                  result.updated(if (p(headPath,
                                       o
                                       )) m(headPath,
                                            o
                                            ) else key,
                                 JsObj(mapKeyRec(headPath,
                                                 o.map,
                                                 HashMap.empty,
                                                 m,
                                                 p
                                                 )
                                       )
                                 ),
                  m,
                  p
                  )
      case (key, arr: JsArray) =>
        val headPath = path / key
        mapKeyRec(path,
                  input.tail,
                  result.updated(if (p(headPath,
                                       arr
                                       )) m(headPath,
                                            arr
                                            ) else key,
                                 JsArray(JsArray.mapKeyRec(path / key / -1,
                                                           arr.seq,
                                                           Vector.empty,
                                                           m,
                                                           p
                                                           )
                                         )
                                 ),
                  m,
                  p
                  )
      case (key, head: JsValue) =>
        val headPath = path / key
        mapKeyRec(path,
                  input.tail,
                  result.updated(if (p(headPath,
                                       head
                                       )) m(headPath,
                                            head
                                            ) else key,
                                 head
                                 ),
                  m,
                  p
                  )
    }

  }

  @scala.annotation.tailrec
  private[value] def mapKey(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : (JsPath, JsValue) => String,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) return result
    val key = input.head._1
    val headPath = path / key
    mapKey(path,
           input.tail,
           result.updated(if (p(headPath,
                                input.head._2
                                )) m(headPath,
                                     input.head._2
                                     ) else key,
                          input.head._2
                          ),
           m,
           p
           )


  }

  @scala.annotation.tailrec
  private[value] def filterKeys(path  : JsPath,
                                input : immutable.Map[String, JsValue],
                                result: immutable.Map[String, JsValue],
                                p     : (JsPath, JsValue) => Boolean
                               ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else
    {

      val key = input.head._1
      val headPath = path / key
      if (p(headPath,
            input.head._2
            )) filterKeys(path,
                          input.tail,
                          result.updated(key,
                                         input.head._2
                                         ),
                          p
                          ) else filterKeys(path,
                                            input.tail,
                                            result,
                                            p
                                            )
    }


  }


  private[value] def filterKeysRec(path  : JsPath,
                                   input : immutable.Map[String, JsValue],
                                   result: immutable.Map[String, JsValue],
                                   p     : (JsPath, JsValue) => Boolean
                                  ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(path / key,
                                    o
                                    )) filterKeysRec(path,
                                                     input.tail,
                                                     result.updated(key,
                                                                    JsObj(filterKeysRec(path / key,
                                                                                        o.map,
                                                                                        HashMap.empty,
                                                                                        p
                                                                                        )
                                                                          )
                                                                    ),
                                                     p
                                                     ) else filterKeysRec(path,
                                                                          input.tail,
                                                                          result,
                                                                          p
                                                                          )
      case (key, arr: JsArray) => if (p(path / key,
                                        arr
                                        )) filterKeysRec(path,
                                                         input.tail,
                                                         result.updated(key,
                                                                        JsArray(JsArray.filterKeyRec(path / key / -1,
                                                                                                     arr.seq,
                                                                                                     Vector.empty,
                                                                                                     p
                                                                                                     )
                                                                                )
                                                                        ),
                                                         p
                                                         ) else filterKeysRec(path,
                                                                              input.tail,
                                                                              result,
                                                                              p
                                                                              )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filterKeysRec(path,
                                                          input.tail,
                                                          result.updated(key,
                                                                         head
                                                                         ),
                                                          p
                                                          ) else filterKeysRec(path,
                                                                               input.tail,
                                                                               result,
                                                                               p
                                                                               )
    }
  }

  protected[value] def reduceRec[V](path : JsPath,
                                    input: immutable.Map[String, JsValue],
                                    p    : (JsPath, JsValue) => Boolean,
                                    m    : (JsPath, JsValue) => V,
                                    r    : (V, V) => V,
                                    acc  : Option[V]
                                   ): Option[V] =
  {

    if (input.isEmpty) acc
    else
    {
      val (key, head): (String, JsValue) = input.head
      head match
      {
        case JsObj(headMap) => reduceRec(path,
                                         input.tail,
                                         p,
                                         m,
                                         r,
                                         Json.reduceHead(r,
                                                         acc,
                                                         reduceRec(path / key,
                                                                   headMap,
                                                                   p,
                                                                   m,
                                                                   r,
                                                                   Option.empty
                                                                   )
                                                         )
                                         )
        case JsArray(headSeq) => reduceRec(path,
                                           input.tail,
                                           p,
                                           m,
                                           r,
                                           Json.reduceHead(r,
                                                           acc,
                                                           JsArray.reduceRec(path / key / -1,
                                                                             headSeq,
                                                                             p,
                                                                             m,
                                                                             r,
                                                                             Option.empty
                                                                             )
                                                           )
                                           )
        case value: JsValue => if (p(path / key,
                                     value
                                     )) reduceRec(path,
                                                  input.tail,
                                                  p,
                                                  m,
                                                  r,
                                                  Json.reduceHead(r,
                                                                  acc,
                                                                  m(path / key,
                                                                    head
                                                                    )
                                                                  )
                                                  ) else reduceRec(path,
                                                                   input.tail,
                                                                   p,
                                                                   m,
                                                                   r,
                                                                   acc
                                                                   )
      }
    }
  }

  @scala.annotation.tailrec
  protected[value] def reduce[V](path : JsPath,
                                 input: immutable.Map[String, JsValue],
                                 p    : (JsPath, JsValue) => Boolean,
                                 m    : (JsPath, JsValue) => V,
                                 r    : (V, V) => V,
                                 acc  : Option[V]
                                ): Option[V] =
  {

    if (input.isEmpty) acc
    else
    {
      val (key, head): (String, JsValue) = input.head
      if (p(path / key,
            head
            )) reduce(path,
                      input.tail,
                      p,
                      m,
                      r,
                      Json.reduceHead(r,
                                      acc,
                                      m(path / key,
                                        head
                                        )
                                      )
                      ) else reduce(path,
                                    input.tail,
                                    p,
                                    m,
                                    r,
                                    acc
                                    )
    }

  }

}