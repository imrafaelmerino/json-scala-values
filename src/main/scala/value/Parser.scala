package value

import com.dslplatform.json.{DslJson, JsBoolDeserializer, JsDecimalDeserializer, JsDoubleDeserializer, JsIntDeserializer, JsIntegralDeserializer, JsLongDeserializer, JsNumberDeserializer, JsObjDeserializer, JsStrDeserializer, JsonReader}
import value.Parser.getDeserializer
import value.spec.{IsArray, IsArrayEachSuchThat, IsArrayOfBool, IsArrayOfBoolSuchThat, IsArrayOfDecimal, IsArrayOfDecimalEachSuchThat, IsArrayOfDecimalSuchThat, IsArrayOfInt, IsArrayOfIntEachSuchThat, IsArrayOfIntSuchThat, IsArrayOfIntegral, IsArrayOfIntegralEachSuchThat, IsArrayOfIntegralSuchThat, IsArrayOfLong, IsArrayOfLongEachSuchThat, IsArrayOfLongSuchThat, IsArrayOfNumber, IsArrayOfNumberEachSuchThat, IsArrayOfNumberSuchThat, IsArrayOfObj, IsArrayOfObjEachSuchThat, IsArrayOfObjSuchThat, IsArrayOfStr, IsArrayOfStrEachSuchThat, IsArrayOfStrSuchThat, IsArraySuchThat, IsBool, IsDecimal, IsDecimalSuchThat, IsFalse, IsInt, IsIntSuchThat, IsIntegral, IsIntegralSuchThat, IsLong, IsLongSuchThat, IsNotNull, IsNull, IsNumber, IsNumberSuchThat, IsObj, IsObjSuchThat, IsStr, IsStrSuchThat, IsTrue, IsValue, IsValueSuchThat, JsArrayOfBoolPredicate, JsArrayOfDecimalPredicate, JsArrayOfIntPredicate, JsArrayOfIntegralPredicate, JsArrayOfLongPredicate, JsArrayOfNumberPredicate, JsArrayOfObjectPredicate, JsArrayOfStrPredicate, JsArrayOfValuePredicate, JsArrayPredicate, JsArraySpec, JsBoolPredicate, JsDecimalPredicate, JsIntPredicate, JsIntegralPredicate, JsLongPredicate, JsNumberPredicate, JsObjPredicate, JsObjSpec, JsPredicate, JsSpec, JsStrPredicate, JsonPredicate, PrimitivePredicate, Schema}

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
                          path  : JsPath
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

  val intDeserializer = new JsIntDeserializer
  val longDeserializer = new JsLongDeserializer
  val doubleDeserializer = new JsDoubleDeserializer
  val integralDeserializer = new JsIntegralDeserializer
  val boolDeserializer = new JsBoolDeserializer
  val decimalDeserializer = new JsDecimalDeserializer
  val strDeserializer = new JsStrDeserializer
  val numberDeserializer = new JsNumberDeserializer

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
            ) => getIntDeserializer(nullable)
            case IsIntegralSuchThat(_,
                                    nullable,
                                    _
            ) => getIntDeserializer(nullable)
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
              ) => getArrayOfLongDeserializer(nullable,
                                              eachElemNullable
                                              )
              case IsArrayOfLongEachSuchThat(_,
                                             nullable,
                                             _,
                                             eachElemNullable
              ) => getArrayOfLongDeserializer(nullable,
                                              eachElemNullable
                                              )
              case IsArrayOfLongSuchThat(_,
                                         nullable,
                                         _,
                                         eachElemNullable
              ) => getArrayOfLongDeserializer(nullable,
                                              eachElemNullable
                                              )
            }
            case p: JsArrayOfDecimalPredicate => p match
            {
              case IsArrayOfDecimal(nullable,
                                    _,
                                    eachElemNullable
              ) => getArrayOfDecimalDeserializer(nullable,
                                                 eachElemNullable
                                                 )
              case IsArrayOfDecimalEachSuchThat(_,
                                                nullable,
                                                _,
                                                eachElemNullable
              ) => getArrayOfDecimalDeserializer(nullable,
                                                 eachElemNullable
                                                 )
              case IsArrayOfDecimalSuchThat(_,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => getArrayOfDecimalDeserializer(nullable,
                                                 eachElemNullable
                                                 )
            }
            case p: JsArrayOfIntegralPredicate => p match
            {
              case IsArrayOfIntegral(nullable,
                                     _,
                                     eachElemNullable
              ) => getArrayOfIntegralDeserializer(nullable,eachElemNullable)
              case IsArrayOfIntegralEachSuchThat(_,
                                                 nullable,
                                                 _,
                                                 eachElemNullable
              ) => getArrayOfIntegralDeserializer(nullable,eachElemNullable)
              case IsArrayOfIntegralSuchThat(_,
                                             nullable,
                                             _,
                                             eachElemNullable
              ) => getArrayOfIntegralDeserializer(nullable,eachElemNullable)
            }
            case p: JsArrayOfNumberPredicate => p match
            {
              case IsArrayOfNumber(nullable,
                                   _,
                                   eachElemNullable
              ) => getArrayOfNumberDeserializer(nullable,
                                                eachElemNullable
                                                )
              case IsArrayOfNumberEachSuchThat(_,
                                               nullable,
                                               _,
                                               eachElemNullable
              ) => getArrayOfNumberDeserializer(nullable,
                                                eachElemNullable
                                                )
              case IsArrayOfNumberSuchThat(_,
                                           nullable,
                                           _,
                                           eachElemNullable
              ) => getArrayOfNumberDeserializer(nullable,
                                                eachElemNullable
                                                )
            }
            case p: JsArrayOfBoolPredicate => p match
            {
              case IsArrayOfBool(nullable,
                                 _,
                                 eachElemNullable
              ) => getArrayOfBoolDeserializer(nullable,
                                              eachElemNullable
                                              )
              case IsArrayOfBoolSuchThat(_,
                                         nullable,
                                         _,
                                         eachElemNullable
              ) => getArrayOfBoolDeserializer(nullable,
                                              eachElemNullable
                                              )
            }
            case p: JsArrayOfStrPredicate => p match
            {
              case IsArrayOfStr(nullable,
                                _,
                                eachElemNullable
              ) => getArrayOfStrDeserializer(nullable,
                                             eachElemNullable
                                             )
              case IsArrayOfStrEachSuchThat(_,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => getArrayOfStrDeserializer(nullable,
                                             eachElemNullable
                                             )
              case IsArrayOfStrSuchThat(_,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => getArrayOfStrDeserializer(nullable,
                                             eachElemNullable
                                             )
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
    if (nullable) (reader: JsonReader[_]) => strDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => strDeserializer.deserialize(reader)
  }

  def getIntDeserializer(nullable: Boolean
                        ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => intDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => intDeserializer.deserialize(reader)


  def getIntegraltDeserializer(nullable: Boolean
                        ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => integralDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => integralDeserializer.deserialize(reader)

  def getLongDeserializer(nullable: Boolean
                         ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => longDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => longDeserializer.deserialize(reader)

  def getDoubleDeserializer(nullable: Boolean
                           ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => doubleDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => doubleDeserializer.deserialize(reader)

  def getDecimalDeserializer(nullable: Boolean
                            ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => decimalDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => decimalDeserializer.deserialize(reader)


  def getNumberDeserializer(nullable: Boolean
                           ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => numberDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => numberDeserializer.deserialize(reader)


  def getBoolDeserializer(nullable: Boolean
                         ): java.util.function.Function[JsonReader[_], JsValue] =
    if (nullable) (reader: JsonReader[_]) => boolDeserializer.deserializeNullable(reader)
    else (reader: JsonReader[_]) => boolDeserializer.deserialize(reader)

  def getArrayOfIntDeserializer(nullable        : Boolean,
                                eachElemNullable: Boolean
                               ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => intDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => intDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => intDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => intDeserializer.deserializeArray(reader)
  }

  def getArrayOfLongDeserializer(nullable        : Boolean,
                                 eachElemNullable: Boolean
                                ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => longDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => longDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => longDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => longDeserializer.deserializeArray(reader)
  }


  def getArrayOfDoubleDeserializer(nullable        : Boolean,
                                   eachElemNullable: Boolean
                                  ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => doubleDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => doubleDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => doubleDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => doubleDeserializer.deserializeArray(reader)
  }


  def getArrayOfDecimalDeserializer(nullable        : Boolean,
                                    eachElemNullable: Boolean
                                   ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => decimalDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => decimalDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => decimalDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => decimalDeserializer.deserializeArray(reader)
  }


  def getArrayOfNumberDeserializer(nullable        : Boolean,
                                   eachElemNullable: Boolean
                                  ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => numberDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => numberDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => numberDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => numberDeserializer.deserializeArray(reader)
  }

  def getArrayOfStrDeserializer(nullable        : Boolean,
                                eachElemNullable: Boolean
                               ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => strDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => strDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => strDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => strDeserializer.deserializeArray(reader)
  }

  def getArrayOfBoolDeserializer(nullable        : Boolean,
                                 eachElemNullable: Boolean
                                ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => boolDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => boolDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => boolDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => boolDeserializer.deserializeArray(reader)
  }

  def getArrayOfIntegralDeserializer(nullable: Boolean,
                                     eachElemNullable: Boolean
                                    ): java.util.function.Function[JsonReader[_], JsValue] =
  {
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => integralDeserializer.deserializeNullableArrayOfNullable(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => integralDeserializer.deserializeNullableArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => integralDeserializer.deserializeArrayOfNullable(reader)
    else (reader: JsonReader[_]) => integralDeserializer.deserializeArray(reader)
  }
}
