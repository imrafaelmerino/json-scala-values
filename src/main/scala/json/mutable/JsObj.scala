package json.mutable

import json.{AbstractJsObj, JsElem, JsPair, JsPath}

import scala.collection.mutable

case class JsObj(override val map: mutable.Map[String, JsElem]) extends AbstractJsObj(map) with json.JsObj with json.mutable.Json[JsObj]
{

  /** Alias for `add` */
  @`inline` final def +=(pair: JsPair): JsObj = addOne(pair)

  def addOne(pair: JsPair): JsObj = ???


  /** Alias for `substractOne` */
  @`inline` final def -=(elem: JsPath): (JsObj, Boolean) = substractOne(elem)

  def substractOne(elem: JsPath): (JsObj, Boolean) = ???

  @`inline` final def -=(elem: JsPath,
                         elem1: JsPath,
                         elems: JsPath*
                        ): JsObj = substractOne(elem,
                                                elem1,
                                                elems: _*
                                                )

  def substractOne(elem: JsPath,
                   elem1: JsPath,
                   elems: JsPath*
                  ): JsObj =
  {
    this.substractOne(elem)
    this.substractOne(elem1)
    for (e <- elems) this.substractOne(e)
    this
  }

  @`inline` final def --=(xs: IterableOnce[JsPath]): JsObj = substractAll(xs)

  def substractAll(xs: IterableOnce[JsPath]): JsObj =
  {
    for (pair <- xs) this.substractOne(pair)
    this
  }


  @`inline` final def ++=(xs: IterableOnce[JsPair]): JsObj = addAll(xs)

  def addAll(xs: IterableOnce[JsPair]): JsObj =
  {
    for (pair <- xs) this.addOne(pair)
    this

  }

  def clear(): Unit = this.map.clear()

  def empty = JsObj(map.empty)

  override def keySet: mutable.Set[String] = map.keySet.asInstanceOf[mutable.Set[String]]

  override def init: JsObj = JsObj(map.init)

  override def tail: JsObj = JsObj(map.tail)

}

object JsObj
{


}