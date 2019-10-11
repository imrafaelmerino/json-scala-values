package json.immutable

import json.{AbstractJsObj, Index, JsElem, JsPair, JsPath, Key}

import scala.collection.immutable
import scala.collection.immutable.HashMap


case class JsObj(override protected val map: immutable.Map[String, JsElem]) extends AbstractJsObj(map) with json.JsObj with json.immutable.Json[JsObj]
{

  override def keySet: Set[String] = map.keySet

  override def empty: JsObj = JsObj.NIL

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
            case a: Some[JsArray] => JsObj(map.updated(k,
                                                       a.get.removed(tail)
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case o: Some[JsObj] => JsObj(map.updated(k,
                                                     o.get.removed(tail)
                                                     )
                                         )
            case _ => this
          }

        }

      }
    }
  }

  override def updated(pair: (JsPath, JsElem)): JsObj =
  {
    val (path, elem) = pair

    if (path.isEmpty) return this

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
            case a: Some[JsArray] => JsObj(map.updated(k,
                                                       a.get.updated(tail,
                                                                     elem
                                                                     )
                                                       )


                                           )
            case _ => this
          }
          case Key(_) => map.lift(k) match
          {
            case o: Some[JsObj] => JsObj(map.updated(k,
                                                     o.get.updated(tail,
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

  override def inserted(pair: (JsPath, JsElem)): JsObj =
  {
    val (path, elem) = pair

    if (path.isEmpty) return this

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
            case a: Some[JsArray] => JsObj(map.updated(k,
                                                       a.get.inserted(tail,
                                                                      elem
                                                                      )
                                                       )


                                           )
            case _ => JsObj(map.updated(k,
                                        JsArray.NIL.inserted(tail,
                                                             elem
                                                             )
                                        )
                            )
          }
          case Key(_) => map.lift(k) match
          {
            case o: Some[JsObj] => JsObj(map.updated(k,
                                                     o.get.inserted(tail,
                                                                    elem
                                                                    )
                                                     )
                                         )
            case _ => JsObj(map.updated(k,
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

}


object JsObj
{

  val NIL = new JsObj(HashMap.empty)

  protected def apply(map: immutable.Map[String, JsElem]): JsObj = new JsObj(map)

  def apply(pair: JsPair*): JsObj =
  {
    @scala.annotation.tailrec
    def applyRec(acc : JsObj,
                 pair: Seq[JsPair]
                ): JsObj =
    {
      if (pair.isEmpty) acc
      else applyRec(acc.inserted(pair.head),
                    pair.tail
                    )
    }

    applyRec(NIL,
             pair
             )
  }

}