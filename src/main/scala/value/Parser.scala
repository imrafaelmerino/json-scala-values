package value

import com.dslplatform.json.{DslJson, JsBoolDeserializer, JsIntDeserializer, JsNumberDeserializer, JsObjDeserializer, JsStrDeserializer, JsonReader}
import value.Parser.getDeserializer
import value.spec.{
  IsArray, IsArrayEachSuchThat, IsArrayOfBool, IsArrayOfBoolSuchThat, IsArrayOfDecimal, IsArrayOfDecimalEachSuchThat, IsArrayOfDecimalSuchThat, IsArrayOfInt, IsArrayOfIntEachSuchThat, IsArrayOfIntSuchThat, IsArrayOfIntegral, IsArrayOfIntegralEachSuchThat, IsArrayOfIntegralSuchThat, IsArrayOfLong, IsArrayOfLongEachSuchThat, IsArrayOfLongSuchThat, IsArrayOfNumber, IsArrayOfNumberEachSuchThat,
  IsArrayOfNumberSuchThat, IsArrayOfObj, IsArrayOfObjEachSuchThat, IsArrayOfObjSuchThat, IsArrayOfStr, IsArrayOfStrEachSuchThat, IsArrayOfStrSuchThat, IsArraySuchThat, IsBool, IsDecimal, IsDecimalSuchThat, IsFalse, IsInt, IsIntSuchThat, IsIntegral, IsIntegralSuchThat, IsLong, IsLongSuchThat, IsNotNull, IsNull, IsNumber, IsNumberSuchThat, IsObj, IsObjSuchThat, IsStr, IsStrSuchThat, IsTrue,
  IsValue, IsValueSuchThat, JsArrayOfBoolPredicate, JsArrayOfDecimalPredicate, JsArrayOfIntPredicate, JsArrayOfIntegralPredicate, JsArrayOfLongPredicate, JsArrayOfNumberPredicate, JsArrayOfObjectPredicate, JsArrayOfStrPredicate, JsArrayOfValuePredicate, JsArrayPredicate, JsArraySpec, JsBoolPredicate, JsDecimalPredicate, JsIntPredicate, JsIntegralPredicate, JsLongPredicate, JsNumberPredicate,
  JsObjPredicate, JsObjSpec, JsPredicate, JsSpec, JsStrPredicate, JsonPredicate, PrimitivePredicate, Schema
}

import scala.collection.immutable.HashMap

sealed trait Parser[T <: Json[T]]
{
  def parse(bytes: Array[Byte]): T
}

case class JsObjParser(spec: JsObjSpec) extends Parser[JsObj]
{
  private val deserializers = JsObjParser.createDeserializers(spec.map,
                                                              collection.immutable.HashMap.empty,
                                                              JsPath.empty
                                                              )

  override def parse(bytes: Array[Byte]): JsObj =
  {
    val reader = Parser.dslJson.newReader(bytes)
    reader.getNextToken
    JsObjDeserializer.deserializeMap(reader,
                                     deserializers,
                                     JsPath.empty
                                     )
  }
}


case class JsArrayParser(spec: JsArraySpec) extends Parser[JsArray]
{
  private val deserializers = JsArrayParser.createDeserializers(spec)

  override def parse(bytes: Array[Byte]): JsArray = ???
}

object JsObjParser
{

  @scala.annotation.tailrec
  def createDeserializers(spec: Map[String, JsSpec],
                          result: HashMap[JsPath, java.util.function.Function[JsonReader[_], JsValue]],
                          path: JsPath
                         ): HashMap[JsPath, java.util.function.Function[JsonReader[_], JsValue]] =
  {
    if (spec.isEmpty) result
    else
    {
      def head = spec.head

      def currentPath = path / head._1

      head._2 match
      {
        case schema: Schema[_] => ???
        case p: JsPredicate => createDeserializers(spec.tail,
                                                   result.updated(currentPath,
                                                                  getDeserializer(p)
                                                                  ),
                                                   path
                                                   )
      }

    }
  }


}


object JsArrayParser
{

  def createDeserializers(spec: JsArraySpec): HashMap[String, JsonReader[_] => JsValue] =
  {
    ???
  }

}

object Parser
{

  val dslJson = new DslJson()

  def getDeserializer(spec: JsPredicate): java.util.function.Function[JsonReader[_], JsValue] =
  {
    spec match
    {
      case p: JsPredicate => p match
      {
        case p: PrimitivePredicate => p match
        {
          case p: JsStrPredicate => p match
          {
            case IsStr(nullable,
                       _
            ) => Parser.getStrDeserializer(nullable)
            case IsStrSuchThat(_,
                               nullable,
                               _
            ) => Parser.getStrDeserializer(nullable)
          }
          case p: JsIntPredicate => p match
          {
            case IsInt(nullable,
                       _
            ) => getIntDeserializer(nullable)
            case IsIntSuchThat(_,
                               nullable,
                               _
            ) => getIntDeserializer(nullable)
          }
          case p: JsLongPredicate => p match
          {
            case IsLong(nullable,
                        _
            ) => getLongDeserializer(nullable)
            case IsLongSuchThat(_,
                                nullable,
                                _
            ) => getLongDeserializer(nullable)
          }
          case p: JsDecimalPredicate => p match
          {
            case IsDecimal(nullable,
                           _
            ) => getDecimalDeserializer(nullable)
            case IsDecimalSuchThat(_,
                                   nullable,
                                   _
            ) => getDecimalDeserializer(nullable)
          }
          case p: JsNumberPredicate => p match
          {
            case IsNumber(nullable,
                          _
            ) => getNumberDeserializer(nullable)
            case IsNumberSuchThat(_,
                                  nullable,
                                  _
            ) => getNumberDeserializer(nullable)
          }
          case p: JsIntegralPredicate => p match
          {
            case IsIntegral(nullable,
                            _
            ) => ???
            case IsIntegralSuchThat(_,
                                    nullable,
                                    _
            ) => ???
          }
          case p: JsBoolPredicate => p match
          {
            case IsBool(nullable,
                        _
            ) => getBoolDeserializer(nullable)
            case IsTrue(nullable,
                        _
            ) => ???
            case IsFalse(nullable,
                         _
            ) => ???
          }
          case IsNull(_) => ???
          case IsNotNull(_) => ???
        }
        case p: JsonPredicate => p match
        {
          case p: JsArrayPredicate => p match
          {
            case p: JsArrayOfIntPredicate => p match
            {
              case IsArrayOfInt(nullable,
                                _,
                                eachElemNullable
              ) => getArrayOfIntDeserializer(nullable,
                                             eachElemNullable
                                             )
              case IsArrayOfIntEachSuchThat(_,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => getArrayOfIntDeserializer(nullable,
                                             eachElemNullable
                                             )
              case IsArrayOfIntSuchThat(_,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => getArrayOfIntDeserializer(nullable,
                                             eachElemNullable
                                             )
            }
            case p: JsArrayOfLongPredicate => p match
            {
              case IsArrayOfLong(nullable,
                                 _,
                                 eachElemNullable
              ) => ???
              case IsArrayOfLongEachSuchThat(p,
                                             nullable,
                                             _,
                                             eachElemNullable
              ) => ???
              case IsArrayOfLongSuchThat(p,
                                         nullable,
                                         _,
                                         eachElemNullable
              ) => ???
            }
            case p: JsArrayOfDecimalPredicate => p match
            {
              case IsArrayOfDecimal(nullable,
                                    _,
                                    eachElemNullable
              ) => ???
              case IsArrayOfDecimalEachSuchThat(p,
                                                nullable,
                                                _,
                                                eachElemNullable
              ) => ???
              case IsArrayOfDecimalSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => ???
            }
            case p: JsArrayOfIntegralPredicate => p match
            {
              case IsArrayOfIntegral(nullable,
                                     _,
                                     eachElemNullable
              ) => ???
              case IsArrayOfIntegralEachSuchThat(p,
                                                 nullable,
                                                 _,
                                                 eachElemNullable
              ) => ???
              case IsArrayOfIntegralSuchThat(p,
                                             nullable,
                                             _,
                                             eachElemNullable
              ) => ???
            }
            case p: JsArrayOfNumberPredicate => p match
            {
              case IsArrayOfNumber(nullable,
                                   _,
                                   eachElemNullable
              ) => ???
              case IsArrayOfNumberEachSuchThat(p,
                                               nullable,
                                               _,
                                               eachElemNullable
              ) => ???
              case IsArrayOfNumberSuchThat(p,
                                           nullable,
                                           _,
                                           eachElemNullable
              ) => ???
            }
            case p: JsArrayOfBoolPredicate => p match
            {
              case IsArrayOfBool(nullable,
                                 _,
                                 eachElemNullable
              ) => ???
              case IsArrayOfBoolSuchThat(p,
                                         nullable,
                                         _,
                                         eachElemNullable
              ) => ???
            }
            case p: JsArrayOfStrPredicate => p match
            {
              case IsArrayOfStr(nullable,
                                _,
                                eachElemNullable
              ) => ???
              case IsArrayOfStrEachSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => ???
              case IsArrayOfStrSuchThat(p,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => ???
            }
            case p: JsArrayOfObjectPredicate => p match
            {
              case IsArrayOfObj(nullable,
                                _,
                                eachElemNullable
              ) => ???
              case IsArrayOfObjEachSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => ???
              case IsArrayOfObjSuchThat(p,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => ???
            }
            case p: JsArrayOfValuePredicate => p match
            {
              case IsArray(nullable,
                           _,
                           eachElemNullable
              ) => ???
              case IsArrayEachSuchThat(p,
                                       nullable,
                                       _,
                                       eachElemNullable
              ) => ???
              case IsArraySuchThat(p,
                                   nullable,
                                   _,
                                   eachElemNullable
              ) => ???
            }
          }
          case p: JsObjPredicate => p match
          {
            case IsObj(nullable,
                       _
            ) => ???
            case IsObjSuchThat(_,
                               nullable,
                               _
            ) => ???
          }
        }
        case IsValue() => ???
        case IsValueSuchThat(p) => ???
      }
    }
  }

  def getStrDeserializer(nullable: Boolean
                        ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable) (reader: JsonReader[_]) => JsStrDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => JsStrDeserializer.deserialize(reader)
  }

  def getIntDeserializer(nullable: Boolean
                        ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable) (reader: JsonReader[_]) => JsIntDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => JsIntDeserializer.deserialize(reader)
  }

  def getArrayOfIntDeserializer(nullable: Boolean,
                                eachElemNullable: Boolean
                               ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => JsIntDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => JsIntDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => JsIntDeserializer.deserializeArrayOfNullable(reader)
    else (reader  : JsonReader[_]) => JsIntDeserializer.deserializeArray(reader)
  }

  def getLongDeserializer(nullable: Boolean
                         ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable) (reader: JsonReader[_]) => JsNumberDeserializer.deserializeNullableLong(reader) else (reader: JsonReader[_]) => JsNumberDeserializer.deserializeLong(reader)
  }

  def getDecimalDeserializer(nullable: Boolean
                            ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable) (reader: JsonReader[_]) => JsNumberDeserializer.deserializeNullalbleDecimal(reader) else (reader: JsonReader[_]) => JsNumberDeserializer.deserializeDecimal(reader)
  }


  def getNumberDeserializer(nullable: Boolean
                           ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable) (reader: JsonReader[_]) => JsNumberDeserializer.deserializeNullableNumber(reader) else (reader: JsonReader[_]) => JsNumberDeserializer.deserializeNumber(reader)
  }


  def getBoolDeserializer(nullable: Boolean
                         ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable) (reader: JsonReader[_]) => JsBoolDeserializer.deserializeNullable(reader) else (reader: JsonReader[_]) => JsBoolDeserializer.deserialize(reader)
  }
}
