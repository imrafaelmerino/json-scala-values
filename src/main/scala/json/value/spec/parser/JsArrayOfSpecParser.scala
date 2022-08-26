package json.value.spec.parser

import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonWriter, readFromArray, readFromString}
import json.value.spec.codec.JsArrayCodec
import json.value.spec.parser.Parser
import json.value.{JsArray, JsObj, JsValue}

final case class JsArrayOfSpecParser(private[json] val valueParser:Parser[_],min:Int,max:Int)
  extends JsonSpecParser[JsArray]:
  private val arrayCodec =  JsArrayCodec(this)

  override def parse(in: JsonReader) = 
    val b = in.nextToken()
    if b == '[' then 
      val array = parseArrayAfterOpenSquareBracket(in, valueParser)
      if array.length < min then in.decodeError(ParserSpecError.ARRAY_LENGTH_LOWER_THAN_MIN(min))
      if array.length > max then in.decodeError(ParserSpecError.ARRAY_LENGTH_BIGGER_THAN_MAX(max))
      array
    else in.decodeError(ParserSpecError.START_ARRAY_EXPECTED)

  @inline override private[json] def codec = arrayCodec

