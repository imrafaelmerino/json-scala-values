package value

import java.util.function.Function

import com.dslplatform.json.derializers.specs.{JsArrayOfObjSpecDeserializer, JsObjSpecDeserializer}
import com.dslplatform.json.{DslJson, JsonReader}
import value.JsObjParser.createDeserializers
import value.Parser.{getArrayOfObjSpecDeserializer, getDeserializer, getJsObjSpecDeserializer}
import value.spec._

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

  val objDeserializer = new JsObjSpecDeserializer(deserializers)

  override def parse(bytes: Array[Byte]): JsObj =
  {
    val reader = Parser.dslJson.newReader(bytes)
    reader.getNextToken
    objDeserializer.value(reader)
  }
}


case class JsArrayParser(spec: JsArraySpec) extends Parser[JsArray]
{
  private val deserializers = JsArrayParser.createDeserializers(spec)

  override def parse(bytes: Array[Byte]): JsArray = ???
}

object JsObjParser
{

  def createDeserializers(spec: Map[String, JsSpec],
                          result: HashMap[String, Function[JsonReader[_], JsValue]],
                          path: JsPath
                         ): HashMap[String, Function[JsonReader[_], JsValue]] =
  {
    if (spec.isEmpty) result
    else
    {
      def head = spec.head

      def currentPath = path / head._1

      head._2 match
      {
        case schema: Schema[_] => schema match
        {
          case JsObjSpec(map) =>
            createDeserializers(spec.tail,
                                result.updated(head._1,
                                               getJsObjSpecDeserializer(createDeserializers(map,
                                                                                            HashMap.empty,
                                                                                            currentPath
                                                                                            )
                                                                        )
                                               ),
                                path
                                )
          case JsArraySpec(seq) => ???

          case ArrayOfObjSpec(objSpec,
                              nullable,
                              optional,
                              eachElemNullable
          ) => createDeserializers(spec.tail,
                                   result.updated(head._1,
                                                  getArrayOfObjSpecDeserializer(objSpec,
                                                                                nullable,
                                                                                eachElemNullable
                                                                                )
                                                  ),
                                   path
                                   )
        }
        case p: JsPredicate => createDeserializers(spec.tail,
                                                   result.updated(head._1,
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


  def getDeserializer(spec: JsPredicate): Function[JsonReader[_], JsValue] =
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
            ) => FieldParserFactory.ofStr(nullable)
            case IsStrSuchThat(p,
                               nullable,
                               _
            ) => FieldParserFactory.ofStrSuchThat(p,
                                                  nullable
                                                  )
          }
          case p: JsIntPredicate => p match
          {
            case IsInt(nullable,
                       _
            ) => FieldParserFactory.ofInt(nullable)
            case IsIntSuchThat(p,
                               nullable,
                               _
            ) => FieldParserFactory.ofIntSuchThat(p,
                                                  nullable
                                                  )
          }
          case p: JsLongPredicate => p match
          {
            case IsLong(nullable,
                        _
            ) => FieldParserFactory.ofLong(nullable)
            case IsLongSuchThat(p,
                                nullable,
                                _
            ) => FieldParserFactory.ofLongSuchThat(p,
                                                   nullable
                                                   )
          }
          case p: JsDecimalPredicate => p match
          {
            case IsDecimal(nullable,
                           _
            ) => FieldParserFactory.ofDecimal((nullable))
            case IsDecimalSuchThat(p,
                                   nullable,
                                   _
            ) => FieldParserFactory.ofDecimalSuchThat(p,
                                                      nullable
                                                      )
          }
          case p: JsNumberPredicate => p match
          {
            case IsNumber(nullable,
                          _
            ) => FieldParserFactory.ofNumber(nullable)
            case IsNumberSuchThat(p,
                                  nullable,
                                  _
            ) => FieldParserFactory.ofNumberSuchThat(p,
                                                     nullable
                                                     )
          }
          case p: JsIntegralPredicate => p match
          {
            case IsIntegral(nullable,
                            _
            ) => FieldParserFactory.ofIntegral(nullable)
            case IsIntegralSuchThat(p,
                                    nullable,
                                    _
            ) => FieldParserFactory.ofIntegralSuchThat(p,
                                                       nullable
                                                       )
          }
          case p: JsBoolPredicate => p match
          {
            case IsBool(nullable,
                        _
            ) => FieldParserFactory.ofBool((nullable))
            case IsTrue(nullable,
                        _
            ) => FieldParserFactory.ofTrue(nullable)
            case IsFalse(nullable,
                         _
            ) => FieldParserFactory.ofFalse(nullable)
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
              ) => FieldParserFactory.ofArrayOfInt(nullable,
                                                   eachElemNullable
                                                   )
              case IsArrayOfIntEachSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => FieldParserFactory.ofArrayOfIntEachSuchThat(p,
                                                               nullable,
                                                               eachElemNullable
                                                               )
              case IsArrayOfIntSuchThat(p,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => FieldParserFactory.ofArrayOfIntSuchThat(p,
                                                           nullable,
                                                           eachElemNullable
                                                           )
            }
            case p: JsArrayOfLongPredicate => p match
            {
              case IsArrayOfLong(nullable,
                                 _,
                                 eachElemNullable
              ) => FieldParserFactory.ofArrayOfLong(nullable,
                                                    eachElemNullable
                                                    )
              case IsArrayOfLongEachSuchThat(p,
                                             nullable,
                                             _,
                                             eachElemNullable
              ) => FieldParserFactory.ofArrayOfLongEachSuchThat(p,
                                                                nullable,
                                                                eachElemNullable
                                                                )
              case IsArrayOfLongSuchThat(p,
                                         nullable,
                                         _,
                                         eachElemNullable
              ) => FieldParserFactory.ofArrayOfLongSuchThat(p,
                                                            nullable,
                                                            eachElemNullable
                                                            )
            }
            case p: JsArrayOfDecimalPredicate => p match
            {
              case IsArrayOfDecimal(nullable,
                                    _,
                                    eachElemNullable
              ) => FieldParserFactory.ofArrayOfDecimal(nullable,
                                                       eachElemNullable
                                                       )
              case IsArrayOfDecimalEachSuchThat(p,
                                                nullable,
                                                _,
                                                eachElemNullable
              ) => FieldParserFactory.ofArrayOfDecimalEachSuchThat(p,
                                                                   nullable,
                                                                   eachElemNullable
                                                                   )
              case IsArrayOfDecimalSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => FieldParserFactory.ofArrayOfDecimalSuchThat(p,
                                                               nullable,
                                                               eachElemNullable
                                                               )
            }
            case p: JsArrayOfIntegralPredicate => p match
            {
              case IsArrayOfIntegral(nullable,
                                     _,
                                     eachElemNullable
              ) => FieldParserFactory.ofArrayOfIntegral(nullable,
                                                        eachElemNullable
                                                        )
              case IsArrayOfIntegralEachSuchThat(p,
                                                 nullable,
                                                 _,
                                                 eachElemNullable
              ) => FieldParserFactory.ofArrayOfIntegralEachSuchThat(p,
                                                                    nullable,
                                                                    eachElemNullable
                                                                    )
              case IsArrayOfIntegralSuchThat(p,
                                             nullable,
                                             _,
                                             eachElemNullable
              ) => FieldParserFactory.ofArrayOfIntegralSuchThat(p,
                                                                nullable,
                                                                eachElemNullable
                                                                )
            }
            case p: JsArrayOfNumberPredicate => p match
            {
              case IsArrayOfNumber(nullable,
                                   _,
                                   eachElemNullable
              ) => FieldParserFactory.ofArrayOfNumber(nullable,
                                                      eachElemNullable
                                                      )
              case IsArrayOfNumberEachSuchThat(p,
                                               nullable,
                                               _,
                                               eachElemNullable
              ) => FieldParserFactory.ofArrayOfNumberEachSuchThat(p,
                                                                  nullable,
                                                                  eachElemNullable
                                                                  )
              case IsArrayOfNumberSuchThat(p,
                                           nullable,
                                           _,
                                           eachElemNullable
              ) => FieldParserFactory.ofArrayOfNumberSuchThat(p,
                                                              nullable,
                                                              eachElemNullable
                                                              )
            }
            case p: JsArrayOfBoolPredicate => p match
            {
              case IsArrayOfBool(nullable,
                                 _,
                                 eachElemNullable
              ) => FieldParserFactory.ofArrayOfBool(nullable,
                                                    eachElemNullable
                                                    )
              case IsArrayOfBoolSuchThat(p,
                                         nullable,
                                         _,
                                         eachElemNullable
              ) => FieldParserFactory.ofArrayOfBoolSuchThat(p,
                                                            nullable,
                                                            eachElemNullable
                                                            )
            }
            case p: JsArrayOfStrPredicate => p match
            {
              case IsArrayOfStr(nullable,
                                _,
                                eachElemNullable
              ) => FieldParserFactory.ofArrayOfStr(nullable,
                                                   eachElemNullable
                                                   )
              case IsArrayOfStrEachSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => FieldParserFactory.ofArrayOfStrEachSuchThat(p,
                                                               nullable,
                                                               eachElemNullable
                                                               )
              case IsArrayOfStrSuchThat(p,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => FieldParserFactory.ofArrayOfStrSuchThat(p,
                                                           nullable,
                                                           eachElemNullable
                                                           )
            }
            case p: JsArrayOfObjectPredicate => p match
            {
              case IsArrayOfObj(nullable,
                                _,
                                eachElemNullable
              ) => FieldParserFactory.ofArrayOfObj(nullable,
                                                   eachElemNullable
                                                   )
              case IsArrayOfObjSuchThat(p,
                                        nullable,
                                        _,
                                        eachElemNullable
              ) => FieldParserFactory.ofArrayOfObjSuchThat(p,
                                                           nullable,
                                                           eachElemNullable
                                                           )
              case IsArrayOfObjEachSuchThat(p,
                                            nullable,
                                            _,
                                            eachElemNullable
              ) => FieldParserFactory.ofArrayOfObjEachSuchThat(p,
                                                               nullable,
                                                               eachElemNullable
                                                               )
            }
            case p: JsArrayOfValuePredicate => p match
            {
              case IsArray(nullable,
                           _,
                           eachElemNullable
              ) => FieldParserFactory.ofArrayOfValue(nullable,
                                                     eachElemNullable
                                                     )
              case IsArrayEachSuchThat(p,
                                       nullable,
                                       _,
                                       eachElemNullable
              ) => FieldParserFactory.ofArrayOfValueEachSuchThat(p,
                                                                 nullable,
                                                                 eachElemNullable
                                                                 )
              case IsArraySuchThat(p,
                                   nullable,
                                   _,
                                   eachElemNullable
              ) => FieldParserFactory.ofArrayOfValueSuchThat(p,
                                                             nullable,
                                                             eachElemNullable
                                                             )
            }
          }
          case p: JsObjPredicate => p match
          {
            case IsObj(nullable,
                       _
            ) => FieldParserFactory.ofObj(nullable)
            case IsObjSuchThat(p,
                               nullable,
                               _
            ) => FieldParserFactory.ofObjSuchThat(p,
                                                  nullable
                                                  )
          }
        }
        case IsValue() => FieldParserFactory.ofValue(true)
        case IsValueSuchThat(p) => FieldParserFactory.ofValueSuchThat((value: JsValue) => p(value),
                                                                      nullable = true
                                                                      )
      }
    }
  }


  def getArrayOfObjSpecDeserializer(spec: JsObjSpec,
                                    nullable: Boolean,
                                    eachElemNullable: Boolean
                                   ): Function[JsonReader[_], JsValue] =
  {

    val deserializer = new JsArrayOfObjSpecDeserializer(new JsObjSpecDeserializer(createDeserializers(spec.map,
                                                                                                      HashMap.empty,
                                                                                                      JsPath.empty
                                                                                                      )
                                                                                  )
                                                        )
    if (nullable && eachElemNullable)
      (reader: JsonReader[_]) => deserializer.nullOrArrayWithNull(reader)
    else if (nullable && !eachElemNullable)
      (reader: JsonReader[_]) => deserializer.nullOrArray(reader)
    else if (!nullable && eachElemNullable)
      (reader: JsonReader[_]) => deserializer.nullOrArrayWithNull(reader)
    else
      (reader: JsonReader[_]) => deserializer.array(reader)

  }


  def getJsObjSpecDeserializer(keyDeserializers: HashMap[String, Function[JsonReader[_], JsValue]]): Function[JsonReader[_], JsValue] =
    (reader: JsonReader[_]) => new JsObjSpecDeserializer(keyDeserializers).value(reader)


}
