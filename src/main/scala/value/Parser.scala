package value

import java.util.function.Function

import com.dslplatform.json.JsonReader
import value.Parser.getDeserializer
import value.ValueParserFactory.ValueParser
import value.spec._

import scala.collection.immutable.HashMap
import scala.collection.immutable.Map

/**
 * A parser parses an input into a Json
 * @tparam T the type of the Json returned
 */
sealed trait Parser[T <: Json[T]]
{}

/**
 * Represents a Json object parser. The Json object must conform the specification
 * @param spec specification of the Json object
 * @param additionalKeys if true, the parser accepts other keys different than the specified in the spec
 */
case class JsObjParser(spec: JsObjSpec,
                       additionalKeys: Boolean = false
                      ) extends Parser[JsObj]
{
  private val (required, deserializers) = JsObjParser.createDeserializers(spec.map,
                                                                          HashMap.empty.withDefault(key => (reader: JsonReader[_]) => throw reader.newParseError(s"key without spec found: $key")),
                                                                          Vector.empty
                                                                          )

  private[value] val objDeserializer = ValueParserFactory.ofObjSpec(required,
                                                                    deserializers
                                                                    )

}


private[value] case class JsArrayParser(deserializer: ValueParser) extends Parser[JsArray]
{}

object JsObjParser
{

  private[value] def createDeserializers(spec: Map[SpecKey, JsSpec],
                                         result      : Map[String, Function[JsonReader[_], JsValue]],
                                         requiredKeys: Vector[String],
                                        ): (Vector[String], Map[String, Function[JsonReader[_], JsValue]]) =
  {
    if (spec.isEmpty) (requiredKeys, result)
    else
    {
      def head = spec.head

      head._1 match
      {
        case * => createDeserializers(spec.tail,
                                      result.withDefaultValue(ValueParserFactory.ofValue(nullable = true)),
                                      requiredKeys
                                      )
        case NamedKey(name) =>
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
                                    result.updated(name,
                                                   ValueParserFactory.ofObjSpec(headRequired,
                                                                                headDeserializers
                                                                                )
                                                   ),
                                    requiredKeys.appended(name)
                                    )
              case IsObjSpec(headSpec,
                             nullable,
                             required
              ) =>
                val (headRequired, headDeserializers) = createDeserializers(headSpec.map,
                                                                            HashMap.empty,
                                                                            Vector.empty
                                                                            )
                createDeserializers(spec.tail,
                                    result.updated(name,
                                                   ValueParserFactory.ofObjSpec(headRequired,
                                                                                headDeserializers,
                                                                                nullable = nullable
                                                                                )
                                                   ),
                                    if (required) requiredKeys.appended(name) else requiredKeys
                                    )

              case IsArraySpec(headSpec,
                               nullable,
                               required
              ) =>
                val headDeserializers = JsArrayParser.createDeserializers(headSpec.seq,
                                                                          Vector.empty
                                                                          )
                createDeserializers(spec.tail,
                                    result.updated(name,
                                                   ValueParserFactory.ofArraySpec(headDeserializers,
                                                                                  nullable = nullable
                                                                                  )
                                                   ),
                                    if (required) requiredKeys.appended(name) else requiredKeys
                                    )
              case JsArraySpec(seq) => createDeserializers(spec.tail,
                                                           result.updated(name,
                                                                          ValueParserFactory.ofArraySpec(JsArrayParser.createDeserializers(seq,
                                                                                                                                           Vector.empty
                                                                                                                                           ),
                                                                                                         nullable = false
                                                                                                         )
                                                                          ),
                                                           requiredKeys
                                                           )
              case ArrayOfObjSpec(objSpec,
                                  nullable,
                                  required,
                                  elemNullable
              ) =>
                val (headRequired, headDeserializers) = createDeserializers(objSpec.map,
                                                                            HashMap.empty,
                                                                            Vector.empty
                                                                            )
                createDeserializers(spec.tail,
                                    result.updated(name,
                                                   ValueParserFactory.ofArrayOfObjSpec(headRequired,
                                                                                       headDeserializers,
                                                                                       nullable,
                                                                                       elemNullable
                                                                                       )
                                                   ),
                                    if (required) requiredKeys.appended(name) else requiredKeys

                                    )
            }
            case p: JsPredicate =>
              val (required, fn) = getDeserializer(p)
              createDeserializers(spec.tail,
                                  result.updated(name,
                                                 fn
                                                 ),
                                  if (required) requiredKeys.appended(name) else requiredKeys
                                  )
          }
      }


    }
  }


}


object JsArrayParser
{

  /**
   * returns a parser that parses an input into a Json array that must conform the predicate
   * @param predicate the predicate that will test the Json array
   * @return a Json array parser
   */
  def apply(predicate: JsArrayPredicate): JsArrayParser =
  {
    val deserializer = getDeserializer(predicate)._2

    new JsArrayParser(deserializer)
  }

  /**
   * returns a parser that parses an input into a Json array that must conform a specification. It's used to
   * define the schema of tuples
   * @param spec specification of the Json array
   * @return a Json array parser
   */
  def apply(spec: JsArraySpec): JsArrayParser =
  {
    val deserializers = JsArrayParser.createDeserializers(spec.seq,
                                                          Vector.empty
                                                          )
    val arrayDeserializer = ValueParserFactory.ofArraySpec(deserializers,
                                                           nullable = false
                                                           )

    new JsArrayParser(arrayDeserializer)
  }

  /**
   * returns a parser that parses an input into an array of Json objects that must conform a specification
   * @param arrayOfObjSpec object to define the spec of the Json objects and other characteristics of the array
   * @return a Json array parser
   */
  def apply(arrayOfObjSpec: ArrayOfObjSpec): JsArrayParser =
  {
    val (required, deserializers) = JsObjParser.createDeserializers(arrayOfObjSpec.spec.map,
                                                                    HashMap.empty
                                                                      .withDefault(key => (reader: JsonReader[_]) => throw reader.newParseError(s"key $key without spec found")),
                                                                    Vector.empty
                                                                    )
    val arrayDeserializer = ValueParserFactory.ofArrayOfObjSpec(required,
                                                                deserializers,
                                                                arrayOfObjSpec.nullable,
                                                                arrayOfObjSpec.elemNullable
                                                                )
    new JsArrayParser(arrayDeserializer)


  }

  private[value] def createDeserializers(spec  : Seq[JsSpec],
                                         result: Vector[Function[JsonReader[_], JsValue]]
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
        case IsObjSpec(headSpec,
                       headNullable,
                       _ // definiendo spec of tuples, el elemento es siempre required=true (TODO, HACER TEST PARA CONTRLOAR EL ERROR QUE SALGA)
        ) =>
          val (required, deserializers) = JsObjParser.createDeserializers(headSpec.map,
                                                                          HashMap.empty,
                                                                          Vector.empty
                                                                          )
          createDeserializers(spec.tail,
                              result.appended(ValueParserFactory.ofObjSpec(required,
                                                                           deserializers,
                                                                           headNullable
                                                                           )
                                              )
                              )

        case IsArraySpec(headSpec,
                         nullable,
                         _ //// definiendo spec of tuples, el elemento es siempre required=true (TODO, HACER TEST PARA CONTRLOAR EL ERROR QUE SALGA)
        ) =>
          val headDeserializers = JsArrayParser.createDeserializers(headSpec.seq,
                                                                    Vector.empty
                                                                    )
          createDeserializers(spec.tail,
                              result.appended(
                                ValueParserFactory.ofArraySpec(headDeserializers,
                                                               nullable = nullable
                                                               )
                                )
                              )
        case JsArraySpec(seq) => createDeserializers(spec.tail,
                                                     result.appended(
                                                       ValueParserFactory.ofArraySpec(createDeserializers(seq,
                                                                                                          Vector.empty
                                                                                                          ),
                                                                                      nullable = false
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

private[value] object Parser
{
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
                                elemNullable
              ) => (required, ValueParserFactory.ofArrayOfInt(nullable,
                                                              elemNullable
                                                              ))
              case IsArrayOfTestedInt(p,
                                      nullable,
                                      required,
                                      elemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntEachSuchThat(p,
                                                                          nullable,
                                                                          elemNullable
                                                                          ))
              case IsArrayOfIntSuchThat(p,
                                        nullable,
                                        required,
                                        elemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntSuchThat(p,
                                                                      nullable,
                                                                      elemNullable
                                                                      ))
            }
            case p: JsArrayOfLongPredicate => p match
            {
              case IsArrayOfLong(nullable,
                                 required,
                                 elemNullable
              ) => (required, ValueParserFactory.ofArrayOfLong(nullable,
                                                               elemNullable
                                                               ))
              case IsArrayOfTestedLong(p,
                                       nullable,
                                       required,
                                       elemNullable
              ) => (required, ValueParserFactory.ofArrayOfLongEachSuchThat(p,
                                                                           nullable,
                                                                           elemNullable
                                                                           ))
              case IsArrayOfLongSuchThat(p,
                                         nullable,
                                         required,
                                         elemNullable
              ) => (required, ValueParserFactory.ofArrayOfLongSuchThat(p,
                                                                       nullable,
                                                                       elemNullable
                                                                       ))
            }
            case p: JsArrayOfDecimalPredicate => p match
            {
              case IsArrayOfDecimal(nullable,
                                    required,
                                    elemNullable
              ) => (required, ValueParserFactory.ofArrayOfDecimal(nullable,
                                                                  elemNullable
                                                                  ))
              case IsArrayOfTestedDecimal(p,
                                          nullable,
                                          required,
                                          elemNullable
              ) => (required, ValueParserFactory.ofArrayOfDecimalEachSuchThat(p,
                                                                              nullable,
                                                                              elemNullable
                                                                              ))
              case IsArrayOfDecimalSuchThat(p,
                                            nullable,
                                            required,
                                            elemNullable
              ) => (required, ValueParserFactory.ofArrayOfDecimalSuchThat(p,
                                                                          nullable,
                                                                          elemNullable
                                                                          ))
            }
            case p: JsArrayOfIntegralPredicate => p match
            {
              case IsArrayOfIntegral(nullable,
                                     required,
                                     elemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntegral(nullable,
                                                                   elemNullable
                                                                   ))
              case IsArrayOfTestedIntegral(p,
                                           nullable,
                                           required,
                                           elemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntegralEachSuchThat(p,
                                                                               nullable,
                                                                               elemNullable
                                                                               ))
              case IsArrayOfIntegralSuchThat(p,
                                             nullable,
                                             required,
                                             elemNullable
              ) => (required, ValueParserFactory.ofArrayOfIntegralSuchThat(p,
                                                                           nullable,
                                                                           elemNullable
                                                                           ))
            }
            case p: JsArrayOfNumberPredicate => p match
            {
              case IsArrayOfNumber(nullable,
                                   required,
                                   elemNullable
              ) => (required, ValueParserFactory.ofArrayOfNumber(nullable,
                                                                 elemNullable
                                                                 ))
              case IsArrayOfTestedNumber(p,
                                         nullable,
                                         required,
                                         elemNullable
              ) => (required, ValueParserFactory.ofArrayOfNumberEachSuchThat(p,
                                                                             nullable,
                                                                             elemNullable
                                                                             ))
              case IsArrayOfNumberSuchThat(p,
                                           nullable,
                                           required,
                                           elemNullable
              ) => (required, ValueParserFactory.ofArrayOfNumberSuchThat(p,
                                                                         nullable,
                                                                         elemNullable
                                                                         ))
            }
            case p: JsArrayOfBoolPredicate => p match
            {
              case IsArrayOfBool(nullable,
                                 required,
                                 elemNullable
              ) => (required, ValueParserFactory.ofArrayOfBool(nullable,
                                                               elemNullable
                                                               ))
              case IsArrayOfBoolSuchThat(p,
                                         nullable,
                                         required,
                                         elemNullable
              ) => (required, ValueParserFactory.ofArrayOfBoolSuchThat(p,
                                                                       nullable,
                                                                       elemNullable
                                                                       ))
            }
            case p: JsArrayOfStrPredicate => p match
            {
              case IsArrayOfStr(nullable,
                                required,
                                elemNullable
              ) => (required, ValueParserFactory.ofArrayOfStr(nullable,
                                                              elemNullable
                                                              ))
              case IsArrayOfTestedStr(p,
                                      nullable,
                                      required,
                                      elemNullable
              ) => (required, ValueParserFactory.ofArrayOfStrEachSuchThat(p,
                                                                          nullable,
                                                                          elemNullable
                                                                          ))
              case IsArrayOfStrSuchThat(p,
                                        nullable,
                                        required,
                                        elemNullable
              ) => (required, ValueParserFactory.ofArrayOfStrSuchThat(p,
                                                                      nullable,
                                                                      elemNullable
                                                                      ))
            }
            case p: JsArrayOfObjectPredicate => p match
            {
              case IsArrayOfObj(nullable,
                                required,
                                elemNullable
              ) => (required, ValueParserFactory.ofArrayOfObj(nullable,
                                                              elemNullable
                                                              ))
              case IsArrayOfObjSuchThat(p,
                                        nullable,
                                        required,
                                        elemNullable
              ) => (required, ValueParserFactory.ofArrayOfObjSuchThat(p,
                                                                      nullable,
                                                                      elemNullable
                                                                      ))
              case IsArrayOfTestedObj(p,
                                      nullable,
                                      required,
                                      elemNullable
              ) => (required, ValueParserFactory.ofArrayOfObjEachSuchThat(p,
                                                                          nullable,
                                                                          elemNullable
                                                                          ))
            }
            case p: JsArrayOfValuePredicate => p match
            {
              case IsArray(nullable,
                           required,
                           elemNullable
              ) => (required, ValueParserFactory.ofArrayOfValue(nullable,
                                                                elemNullable
                                                                ))
              case IsArrayOfTestedValue(p,
                                        nullable,
                                        required,
                                        elemNullable
              ) => (required, ValueParserFactory.ofArrayOfValueEachSuchThat(p,
                                                                            nullable,
                                                                            elemNullable
                                                                            ))
              case IsArrayOfValueSuchThat(p,
                                          nullable,
                                          required,
                                          elemNullable
              ) => (required, ValueParserFactory.ofArrayOfValueSuchThat(p,
                                                                        nullable,
                                                                        elemNullable
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
        case IsValue(required) =>(required, ValueParserFactory.ofValue(true))
        case IsValueSuchThat(p,
                             required
        ) => (required, ValueParserFactory.ofValueSuchThat((value: JsValue) => p(value),
                                                           nullable = true
                                                           ))
      }
    }
  }
}
