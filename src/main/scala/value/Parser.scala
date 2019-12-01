package value

import com.dslplatform.json.JsonReader
import value.spec.{JsArraySpec, JsObjSpec}

sealed trait Parser[T <: Json[T]]
{
  def parse: T
}

case class JsObjParser(spec: JsObjSpec) extends Parser[JsObj]
{
  private val deserializers = JsObjParser.createDeserializers(spec)

  override def parse: JsObj =
  {
    ???
  }
}


case class JsArrayParser(spec: JsArraySpec) extends Parser[JsArray]
{
  private val deserializers = JsArrayParser.createDeserializers(spec)

  override def parse: JsArray = ???
}

object JsObjParser
{

  def createDeserializers(spec: JsObjSpec): Map[String, JsonReader[_] => JsValue] =
  {
    ???
  }

}


object JsArrayParser
{

  def createDeserializers(spec: JsArraySpec): Map[String, JsonReader[_] => JsValue] =
  {
    ???
  }

}
