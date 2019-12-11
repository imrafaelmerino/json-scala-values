package value

import java.util.function.Function
import com.dslplatform.json.{CustomDslJson, JsonReader}
import value.Parser.getDeserializer
import value.spec._
import scala.collection.immutable.HashMap

sealed trait Parser[T <: Json[T]]
{
  def parse(bytes: Array[Byte]): T

}

case class JsObjParser(spec: JsObjSpec,
                       additionalKeys: Boolean = false
                      ) extends Parser[JsObj]
{
  private val (required, deserializers) = JsObjParser.createDeserializers(spec.map,
                                                                          HashMap.empty,
                                                                          Vector.empty
                                                                          )

  private val objDeserializer = ValueParserFactory.ofObjSpec(required,
                                                             deserializers
                                                             )

  override def parse(bytes: Array[Byte]): JsObj =
  {
    val reader = Parser.dslJson.getReader(bytes)
    reader.getNextToken
    objDeserializer(reader).asJsObj
  }
}


case class JsArrayParser(spec: JsArraySpec) extends Parser[JsArray]
{
  private val deserializers = JsArrayParser.createDeserializers(spec.seq,
                                                                Vector.empty
                                                                )
  private val arrayDeserializer = ValueParserFactory.ofArraySpec(deserializers)

  override def parse(bytes: Array[Byte]): JsArray =
  {
    val reader = Parser.dslJson.getReader(bytes)
    reader.getNextToken
    arrayDeserializer(reader).asJsArray
  }
}

object JsObjParser
{

  private[value] def createDeserializers(spec: Map[String, JsSpec],
                                         result        : HashMap[String, Function[JsonReader[_], JsValue]],
                                         requiredKeys  : Vector[String],
                                        ): (Vector[String], HashMap[String, Function[JsonReader[_], JsValue]]) =
  {
    if (spec.isEmpty) (requiredKeys, result)
    else
    {
      def head = spec.head

      head._2 match
      {
        case schema: Schema[_] => schema match
        {
          case JsObjSpec(map) =>
            val (headRequired, headDeserializers) = createDeserializers(map,
                                                                        HashMap.empty,
                                                                        Vector.empty
                                                                        )
            createDeserializers(spec.tail,
                                result.updated(head._1,
                                               ValueParserFactory.ofObjSpec(headRequired,
                                                                            headDeserializers
                                                                            )
                                               ),
                                requiredKeys.appended(head._1)
                                )
          case JsArraySpec(seq) => createDeserializers(spec.tail,
                                                       result.updated(head._1,
                                                                      ValueParserFactory.ofArraySpec(JsArrayParser.createDeserializers(seq,
                                                                                                                                       Vector.empty
                                                                                                                                       )
                                                                                                     )
                                                                      ),
                                                       requiredKeys
                                                       )
          case ArrayOfObjSpec(objSpec,
                              nullable,
                              required,
                              eachElemNullable
          ) =>
            val (headRequired, headDeserializers) = createDeserializers(objSpec.map,
                                                                        HashMap.empty,
                                                                        Vector.empty
                                                                        )
            createDeserializers(spec.tail,
                                result.updated(head._1,
                                               ValueParserFactory.ofArrayOfObjSpec(headRequired,
                                                                                   headDeserializers,
                                                                                   nullable,
                                                                                   eachElemNullable
                                                                                   )
                                               ),
                                if (required) requiredKeys.appended(head._1) else requiredKeys

                                )
        }
        case p: JsPredicate =>
          val (required, fn) = getDeserializer(p)
          createDeserializers(spec.tail,
                              result.updated(head._1,
                                             fn
                                             ),
                              if (required) requiredKeys.appended(head._1) else requiredKeys
                              )
      }

    }
  }


}


object JsArrayParser
{

  private[value] def createDeserializers(spec: Seq[JsSpec],
                                         result        : Vector[Function[JsonReader[_], JsValue]]
                                        ): Vector[Function[JsonReader[_], JsValue]] =
  {
    if (spec.isEmpty) return result

    def head = spec.head

    head match
    {
      case schema: Schema[_] => schema match
      {
        case JsObjSpec(map) =>
          val (required, deserializers) = JsObjParser.createDeserializers(map,
                                                                          HashMap.empty,
                                                                          Vector.empty
                                                                          )
          createDeserializers(spec.tail,
                              result.appended(
                                ValueParserFactory.ofObjSpec(required,
                                                             deserializers
                                                             )
                                )
                              )
        case JsArraySpec(seq) => createDeserializers(spec.tail,
                                                     result.appended(
                                                       ValueParserFactory.ofArraySpec(createDeserializers(seq,
                                                                                                          Vector.empty
                                                                                                          )
                                                                                      )
                                                       )
                                                     )
        case ArrayOfObjSpec(objSpec,
                            nullable,
                            _,
                            elemNullable
        ) =>
          val (value, deserializers) = JsObjParser.createDeserializers(objSpec.map,
                                                                       HashMap.empty,
                                                                       Vector.empty
                                                                       )
          createDeserializers(spec.tail,
                              result.appended(
                                ValueParserFactory.ofArrayOfObjSpec(value,
                                                                    deserializers,
                                                                    nullable,
                                                                    elemNullable
                                                                    )
                                )
                              )
      }
      case p: JsPredicate => createDeserializers(spec.tail,
                                                 result.appended(getDeserializer(p)._2
                                                                 )
                                                 )
    }


  }

}

object Parser
{

  private[value] val dslJson = new CustomDslJson[Object]

  private[value] def getDeserializer(spec: JsPredicate): (Boolean, Function[JsonReader[_], JsValue]) =
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
                       required
            ) => (required, ValueParserFactory.ofStr(nullable))
            case IsStrSuchThat(p,
                               nullable,
                               required
            ) => (required, ValueParserFactory.ofStrSuchThat(p,
                                                             nullable
                                                             ))
          }
          case p: JsIntPredicate => p match
          {
            case IsInt(nullable,
                       required
            ) => (required, ValueParserFactory.ofInt(nullable))
            case IsIntSuchThat(p,
                               nullable,
                               required
            ) => (required, ValueParserFactory.ofIntSuchThat(p,
                                                             nullable
                                                             ))
          }
          case p: JsLongPredicate => p match
          {
            case IsLong(nullable,
                        required
            ) => (required, ValueParserFactory.ofLong(nullable))
            case IsLongSuchThat(p,
                                nullable,
                                required
            ) => (required, ValueParserFactory.ofLongSuchThat(p,
                                                              nullable
                                                              ))
          }
          case p: JsDecimalPredicate => p match
          {
            case IsDecimal(nullable,
                           required
            ) => (required, ValueParserFactory.ofDecimal(nullable))
            case IsDecimalSuchThat(p,
                                   nullable,
                                   required
            ) => (required, ValueParserFactory.ofDecimalSuchThat(p,
                                                                 nullable
                                                                 ))
          }
          case p: JsNumberPredicate => p match
          {
            case IsNumber(nullable,
                          required
            ) => (required, ValueParserFactory.ofNumber(nullable))
            case IsNumberSuchThat(p,
                                  nullable,
                                  required
            ) => (required, ValueParserFactory.ofNumberSuchThat(p,
                                                                nullable
                                                                ))
          }
          case p: JsIntegralPredicate => p match
          {
            case IsIntegral(nullable,
                            required
            ) => (required, ValueParserFactory.ofIntegral(nullable))
            case IsIntegralSuchThat(p,
                                    nullable,
                                    required
            ) => (required, ValueParserFactory.ofIntegralSuchThat(p,
                                                                  nullable
                                                                  ))
          }
          case p: JsBoolPredicate => p match
          {
            case IsBool(nullable,
                        required
            ) => (required, ValueParserFactory.ofBool(nullable))
            case IsTrue(nullable,
                        required
            ) => (required, ValueParserFactory.ofTrue(nullable))
            case IsFalse(nullable,
                         required
            ) => (required, ValueParserFactory.ofFalse(nullable))
          }
        }
        case p: JsonPredicate => p match
        {
          case p: JsArrayPredicate => p match
          {
            case p: JsArrayOfIntPredicate => p match
            {
              case IsArrayOfInt(nullable,
                                required,
                                eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfInt(nullable,
                                                              eachElemNullable
                                                              ))
              case IsArrayOfIntEachSuchThat(p,
                                            nullable,
                                            required,
                                            eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntEachSuchThat(p,
                                                                          nullable,
                                                                          eachElemNullable
                                                                          ))
              case IsArrayOfIntSuchThat(p,
                                        nullable,
                                        required,
                                        eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntSuchThat(p,
                                                                      nullable,
                                                                      eachElemNullable
                                                                      ))
            }
            case p: JsArrayOfLongPredicate => p match
            {
              case IsArrayOfLong(nullable,
                                 required,
                                 eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfLong(nullable,
                                                               eachElemNullable
                                                               ))
              case IsArrayOfLongEachSuchThat(p,
                                             nullable,
                                             required,
                                             eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfLongEachSuchThat(p,
                                                                           nullable,
                                                                           eachElemNullable
                                                                           ))
              case IsArrayOfLongSuchThat(p,
                                         nullable,
                                         required,
                                         eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfLongSuchThat(p,
                                                                       nullable,
                                                                       eachElemNullable
                                                                       ))
            }
            case p: JsArrayOfDecimalPredicate => p match
            {
              case IsArrayOfDecimal(nullable,
                                    required,
                                    eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfDecimal(nullable,
                                                                  eachElemNullable
                                                                  ))
              case IsArrayOfDecimalEachSuchThat(p,
                                                nullable,
                                                required,
                                                eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfDecimalEachSuchThat(p,
                                                                              nullable,
                                                                              eachElemNullable
                                                                              ))
              case IsArrayOfDecimalSuchThat(p,
                                            nullable,
                                            required,
                                            eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfDecimalSuchThat(p,
                                                                          nullable,
                                                                          eachElemNullable
                                                                          ))
            }
            case p: JsArrayOfIntegralPredicate => p match
            {
              case IsArrayOfIntegral(nullable,
                                     required,
                                     eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntegral(nullable,
                                                                   eachElemNullable
                                                                   ))
              case IsArrayOfIntegralEachSuchThat(p,
                                                 nullable,
                                                 required,
                                                 eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntegralEachSuchThat(p,
                                                                               nullable,
                                                                               eachElemNullable
                                                                               ))
              case IsArrayOfIntegralSuchThat(p,
                                             nullable,
                                             required,
                                             eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntegralSuchThat(p,
                                                                           nullable,
                                                                           eachElemNullable
                                                                           ))
            }
            case p: JsArrayOfNumberPredicate => p match
            {
              case IsArrayOfNumber(nullable,
                                   required,
                                   eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfNumber(nullable,
                                                                 eachElemNullable
                                                                 ))
              case IsArrayOfNumberEachSuchThat(p,
                                               nullable,
                                               required,
                                               eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfNumberEachSuchThat(p,
                                                                             nullable,
                                                                             eachElemNullable
                                                                             ))
              case IsArrayOfNumberSuchThat(p,
                                           nullable,
                                           required,
                                           eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfNumberSuchThat(p,
                                                                         nullable,
                                                                         eachElemNullable
                                                                         ))
            }
            case p: JsArrayOfBoolPredicate => p match
            {
              case IsArrayOfBool(nullable,
                                 required,
                                 eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfBool(nullable,
                                                               eachElemNullable
                                                               ))
              case IsArrayOfBoolSuchThat(p,
                                         nullable,
                                         required,
                                         eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfBoolSuchThat(p,
                                                                       nullable,
                                                                       eachElemNullable
                                                                       ))
            }
            case p: JsArrayOfStrPredicate => p match
            {
              case IsArrayOfStr(nullable,
                                required,
                                eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfStr(nullable,
                                                              eachElemNullable
                                                              ))
              case IsArrayOfStrEachSuchThat(p,
                                            nullable,
                                            required,
                                            eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfStrEachSuchThat(p,
                                                                          nullable,
                                                                          eachElemNullable
                                                                          ))
              case IsArrayOfStrSuchThat(p,
                                        nullable,
                                        required,
                                        eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfStrSuchThat(p,
                                                                      nullable,
                                                                      eachElemNullable
                                                                      ))
            }
            case p: JsArrayOfObjectPredicate => p match
            {
              case IsArrayOfObj(nullable,
                                required,
                                eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfObj(nullable,
                                                              eachElemNullable
                                                              ))
              case IsArrayOfObjSuchThat(p,
                                        nullable,
                                        required,
                                        eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfObjSuchThat(p,
                                                                      nullable,
                                                                      eachElemNullable
                                                                      ))
              case IsArrayOfObjEachSuchThat(p,
                                            nullable,
                                            required,
                                            eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfObjEachSuchThat(p,
                                                                          nullable,
                                                                          eachElemNullable
                                                                          ))
            }
            case p: JsArrayOfValuePredicate => p match
            {
              case IsArray(nullable,
                           required,
                           eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfValue(nullable,
                                                                eachElemNullable
                                                                ))
              case IsArrayEachSuchThat(p,
                                       nullable,
                                       required,
                                       eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfValueEachSuchThat(p,
                                                                            nullable,
                                                                            eachElemNullable
                                                                            ))
              case IsArraySuchThat(p,
                                   nullable,
                                   required,
                                   eachElemNullable
              ) => (required, ValueParserFactory.ofArrayOfValueSuchThat(p,
                                                                        nullable,
                                                                        eachElemNullable
                                                                        ))
            }
          }
          case p: JsObjPredicate => p match
          {
            case IsObj(nullable,
                       required
            ) => (required, ValueParserFactory.ofObj(nullable))
            case IsObjSuchThat(p,
                               nullable,
                               required
            ) => (required, ValueParserFactory.ofObjSuchThat(p,
                                                             nullable
                                                             )
            )
          }
        }
        case IsValue(required) => (required, ValueParserFactory.ofValue(true))
        case IsValueSuchThat(p,
                             required
        ) => (required, ValueParserFactory.ofValueSuchThat((value: JsValue) => p(value),
                                                           nullable = true
                                                           ))
      }
    }
  }


}
