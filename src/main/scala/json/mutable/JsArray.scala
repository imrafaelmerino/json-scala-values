package json.mutable

import json.{AbstractJsArray, JsElem, JsPair, JsPath}

import scala.collection.mutable

case class JsArray(override val seq: mutable.Seq[JsElem]) extends AbstractJsArray(seq) with json.JsArray with json.mutable.Json[JsArray]
{
  override def empty: JsArray = JsArray(seq.empty)

  override def init: JsArray = JsArray(seq.init)

  override def tail: JsArray = JsArray(seq.tail)

}

object JsArray
{


}
