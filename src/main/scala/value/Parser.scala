package value
import com.dslplatform.json.MyDslJson
import com.fasterxml.jackson.core.JsonFactory
import java.io.{IOException, InputStream}
import java.util.Objects.requireNonNull
import java.util.function.Function

import com.dslplatform.json.JsonReader
import com.fasterxml.jackson.core.JsonToken.{START_ARRAY, START_OBJECT}
import com.fasterxml.jackson.core.JsonTokenId._
import com.fasterxml.jackson.core.{JsonParser, JsonToken}
import value.Parser.getDeserializer
import value.ValueParserFactory.ValueParser
import value.spec._

import scala.collection.immutable
import scala.collection.immutable.{HashMap, Map}
import scala.util.{Failure, Success, Try}

private[value] val dslJson = MyDslJson[Object]()

private[value] val jacksonFactory = JsonFactory()
/**
 * A parser parses an input into a Json
 *
 * @tparam T the type of the Json returned
 */
sealed trait Parser[T <: Json[T]]

/**
 * Represents a Json object parser. The parsed Json object must conform the specification
 *
 * @param spec           specification of the Json object
 * @param additionalKeys if true, the parser accepts other keys different than the specified in the spec
 */
case class JsObjParser(spec          : JsObjSpec,
                       additionalKeys: Boolean = false
                      ) extends Parser[JsObj]
  private val (required, deserializers) =
    JsObjParser.createDeserializers(spec.map,
                                    HashMap.empty.withDefault(key => Parser.fn(key)),
                                    Vector.empty
                                    )

  private[value] val objDeserializer = ValueParserFactory.ofObjSpec(required,
                                                                    deserializers
                                                                    )

  /**
   * parses an array of bytes into a Json object that must conform the spec of the parser. If the
   * array of bytes doesn't represent a well-formed Json or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param bytes a Json object serialized in an array of bytes
   * @return a try computation with the result
   */
  def parse(bytes: Array[Byte] ): Either[InvalidJson, JsObj] =
    Try(dslJson.deserializeToJsObj(requireNonNull(bytes),
                                   this.objDeserializer
                                   )
        ) match
      case Failure(exception) => Left(InvalidJson(exception.getMessage))
      case Success(json) => Right(json)

  /**
   * parses a string into a Json object that must conform the spec of the parser. If the
   * string doesn't represent a well-formed Json or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param str a Json object serialized in a string
   * @return a try computation with the result
   */
  def parse(str: String ): Either[InvalidJson, JsObj] =
    Try(dslJson.deserializeToJsObj(requireNonNull(str).getBytes,
                                   this.objDeserializer
                                   )
        ) match
      case Failure(exception) => Left(InvalidJson(exception.getMessage))
      case Success(json) => Right(json)

  /**
   * parses an input stream of bytes into a Json object that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json object or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned. Any I/O exception processing the input stream is wrapped in a Try computation as well
   *
   * @param inputStream the input stream of bytes
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream): Try[JsObj] =
        Try(dslJson.deserializeToJsObj(requireNonNull(inputStream),
                                       this.objDeserializer
                                       )
           )

case class JsArrayParser(private[value] val deserializer: ValueParser) extends Parser[JsArray]
  /**
   * parses an array of bytes into a Json array that must conform the spec of the parser. If the
   * array of bytes doesn't represent a well-formed Json  or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param bytes a Json array serialized in an array of bytes
   * @return a try computation with the result
   */
  def parse(bytes: Array[Byte]): Either[InvalidJson, JsArray] =
    Try(dslJson.deserializeToJsArray(requireNonNull(bytes),
                                     this.deserializer
                                     )
        ) match
      case Failure(exception) => Left(InvalidJson(exception.getMessage))
      case Success(json) => Right(json)

  /**
   * parses a string into a Json array that must conform the spec of the parser. If the
   * string doesn't represent a well-formed Json array or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned.
   *
   * @param str a Json array serialized in a string
   * @return a try computation with the result
   */
  def parse(str: String): Either[InvalidJson, JsArray] =
    Try(dslJson.deserializeToJsArray(requireNonNull(str).getBytes(),
                                     this.deserializer
                                     )
        ) match
      case Failure(exception) => Left(InvalidJson(exception.getMessage))
      case Success(json) => Right(json)

  /**
   * parses an input stream of bytes into a Json array that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json array or is a well-formed Json that doesn't
   * conform the spec of the parser, a ParsingException failure wrapped in a Try computation is
   * returned. Any I/O exception processing the input stream is wrapped in a Try computation as well
   *
   * @param inputStream the input stream of bytes
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream): Try[JsArray] =
              Try(dslJson.deserializeToJsArray(requireNonNull(inputStream),
                                               this.deserializer
                                              )
                 )


object JsArrayParser
  /**
   * returns a parser that parses an input into a Json array that must conform the predicate
   *
   * @param predicate the predicate that will test the Json array
   * @return a Json array parser
   */
  def apply(predicate: JsArrayPredicate): JsArrayParser =
    val deserializer = getDeserializer(predicate)._2
    JsArrayParser(deserializer)

  /**
   * returns a parser that parses an input into a Json array that must conform a specification. It's used to
   * define the schema of tuples
   *
   * @param spec specification of the Json array
   * @return a Json array parser
   */
  def apply(spec: JsArraySpec): JsArrayParser =
    val deserializers = JsArrayParser.createDeserializers(spec.seq,
                                                          Vector.empty
                                                          )
    val arrayDeserializer = ValueParserFactory.ofArraySpec(deserializers,
                                                           nullable = false
                                                           )
    JsArrayParser(arrayDeserializer)


  /**
   * returns a parser that parses an input into an array of Json objects that must conform a specification
   *
   * @param arrayOfObjSpec object to define the spec of the Json objects and other characteristics of the array
   * @return a Json array parser
   */
  def apply(arrayOfObjSpec: ArrayOfObjSpec): JsArrayParser =
    val (required, deserializers) = JsObjParser.createDeserializers(arrayOfObjSpec.spec.map,
                                                                    HashMap.empty
                                                                      .withDefault(key => Parser.fn(key)),
                                                                    Vector.empty
                                                                    )
    val arrayDeserializer = ValueParserFactory.ofArrayOfObjSpec(required,
                                                                deserializers,
                                                                arrayOfObjSpec.nullable,
                                                                arrayOfObjSpec.elemNullable
                                                                )
    JsArrayParser(arrayDeserializer)

  /**
   * parses an input stream of bytes into a Json array that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json array, a MalformedJson failure wrapped
   * in a Try computation is returned. Any I/O exception processing the input stream is wrapped in a Try
   * computation as well
   *
   * @param inputStream the input stream of bytes
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream): Try[JsArray] =
    var parser: JsonParser = null
    try
      parser = jacksonFactory.createParser(requireNonNull(inputStream))
      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT)
        Failure(InvalidJson.jsArrayExpected)
      else
        Success(parse(parser))
    finally
      if (parser != null) parser.close()

  /**
   * parses an array of bytes into a Json array. If the array of bytes doesn't represent a well-formed
   * Json array, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param bytes a Json array serialized in an array of bytes
   * @return a try computation with the result
   */
  def parse(bytes: Array[Byte]): Either[InvalidJson, JsArray] =
    var parser: JsonParser = null
    try
      parser = jacksonFactory.createParser(requireNonNull(bytes))
      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT)
        Left(InvalidJson.jsArrayExpected)
      else
        Right(JsArrayParser.parse(parser))
    catch
      case e: IOException => Left(InvalidJson.errorWhileParsing(bytes,
                                                                e
                                                                )
                                  )
    finally
      if (parser != null) parser.close()

  /**
   * parses a string into a Json array. If the string doesn't represent a well-formed
   * Json array, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param str a Json array serialized in a string
   * @return a try computation with the result
   */
  def parse(str: String): Either[InvalidJson, JsArray] =
    var parser: JsonParser = null
    try
      parser = jacksonFactory.createParser(requireNonNull(str))
      val event: JsonToken = parser.nextToken
      if (event eq START_OBJECT)
        Left(InvalidJson.jsArrayExpected)
      else
        Right(parse(parser))
    catch
      case e: IOException => Left(InvalidJson.errorWhileParsing(str,
                                                                e
                                                                )
                                  )
    finally
      if (parser != null) parser.close()

  @throws[IOException]
  private[value] def parse(parser: JsonParser): JsArray =
    var root: Vector[JsValue] = Vector.empty
    while (true)
      val token: JsonToken = parser.nextToken
      var value: JsValue = null
      token.id match
        case ID_END_ARRAY => return JsArray(root)
        case ID_START_OBJECT => value = JsObjParser.parse(parser)
        case ID_START_ARRAY => value = parse(parser)
        case ID_STRING => value = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => value = JsNumber(parser)
        case ID_NUMBER_FLOAT => value = JsBigDec(parser.getDecimalValue)
        case ID_TRUE => value = TRUE
        case ID_FALSE => value = FALSE
        case ID_NULL => value = JsNull
        case _ => throw InternalError.tokenNotFoundParsingStringIntoJsArray(token.name)
      root = root.appended(value)
    throw InternalError.endArrayTokenExpected

  private[value] def createDeserializers(spec  : Seq[JsSpec],
                                         result: Vector[Function[JsonReader[_], JsValue]]
                                        ): Vector[Function[JsonReader[_], JsValue]] =
    if (spec.isEmpty) return result
    def head = spec.head
    head match
      case schema: Schema[_] => schema match
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
                       _) =>
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
                         _) =>
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
                            elemNullable) =>
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
      case p: JsPredicate => createDeserializers(spec.tail,
                                                 result.appended(getDeserializer(p)._2
                                                                 )
                                                 )

private[value] object Parser
  private[value] val fn: String => java.util.function.Function[com.dslplatform.json.JsonReader[_], value.JsValue] = key =>
    (t: JsonReader[_]) => throw t.newParseError(s"key $key without spec found")

  private[value] def getDeserializer(spec: JsPredicate): (Boolean, Function[JsonReader[_], JsValue]) =
    spec match
      case p: JsPredicate => p match
        case p: PrimitivePredicate => p match
          case p: JsStrPredicate => p match
            case IsStr(nullable,
                       required
            ) => (required, ValueParserFactory.ofStr(nullable))
            case IsStrSuchThat(p,
                               nullable,
                               required
            ) => (required, ValueParserFactory.ofStrSuchThat(p,
                                                             nullable
                                                             ))
          case p: JsIntPredicate => p match
            case IsInt(nullable,
                       required
            ) => (required, ValueParserFactory.ofInt(nullable))
            case IsIntSuchThat(p,
                               nullable,
                               required
            ) => (required, ValueParserFactory.ofIntSuchThat(p,
                                                             nullable
                                                             ))
          case p: JsLongPredicate => p match
            case IsLong(nullable,
                        required
            ) => (required, ValueParserFactory.ofLong(nullable))
            case IsLongSuchThat(p,
                                nullable,
                                required
            ) => (required, ValueParserFactory.ofLongSuchThat(p,
                                                              nullable
                                                              ))
          case p: JsDecimalPredicate => p match
            case IsDecimal(nullable,
                           required
            ) => (required, ValueParserFactory.ofDecimal(nullable))
            case IsDecimalSuchThat(p,
                                   nullable,
                                   required
            ) => (required, ValueParserFactory.ofDecimalSuchThat(p,
                                                                 nullable
                                                                 ))
          case p: JsNumberPredicate => p match
            case IsNumber(nullable,
                          required
            ) => (required, ValueParserFactory.ofNumber(nullable))
            case IsNumberSuchThat(p,
                                  nullable,
                                  required
            ) => (required, ValueParserFactory.ofNumberSuchThat(p,
                                                                nullable
                                                                ))
          case p: JsIntegralPredicate => p match
            case IsIntegral(nullable,
                            required
            ) => (required, ValueParserFactory.ofIntegral(nullable))
            case IsIntegralSuchThat(p,
                                    nullable,
                                    required
            ) => (required, ValueParserFactory.ofIntegralSuchThat(p,
                                                                  nullable
                                                                  ))
          case p: JsBoolPredicate => p match
            case IsBool(nullable,
                        required
            ) => (required, ValueParserFactory.ofBool(nullable))
            case IsTrue(nullable,
                        required
            ) => (required, ValueParserFactory.ofTrue(nullable))
            case IsFalse(nullable,
                         required
            ) => (required, ValueParserFactory.ofFalse(nullable))
        case p: JsonPredicate => p match
          case p: JsArrayPredicate => p match
            case p: JsArrayOfIntPredicate => p match
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
            case p: JsArrayOfLongPredicate => p match
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
            case p: JsArrayOfDecimalPredicate => p match
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
            case p: JsArrayOfIntegralPredicate => p match
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
            case p: JsArrayOfNumberPredicate => p match
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
            case p: JsArrayOfBoolPredicate => p match
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
            case p: JsArrayOfStrPredicate => p match
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
            case p: JsArrayOfObjectPredicate => p match
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
            case p: JsArrayOfValuePredicate => p match
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
          case p: JsObjPredicate => p match
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
        case IsValue(required) => (required, ValueParserFactory.ofValue())
        case IsValueSuchThat(p,
                             required
        ) => (required, ValueParserFactory.ofValueSuchThat((value: JsValue) => p(value)
                                                           ))

object JsObjParser
  /**
   * parses an input stream of bytes into a Json object that must conform the spec of the parser. If the
   * the input stream of bytes doesn't represent a well-formed Json object, a MalformedJson failure wrapped
   * in a Try computation is returned. Any I/O exception processing the input stream is wrapped in a Try
   * computation as well
   *
   * @param inputStream the input stream of bytes
   * @return a try computation with the result
   */
  def parse(inputStream: InputStream): Try[JsObj] =
    var parser: JsonParser = null
    try
      parser = jacksonFactory.createParser(requireNonNull(inputStream))
      val event: JsonToken = parser.nextToken
      if (event eq START_ARRAY) Failure(InvalidJson.jsObjectExpected)
      else Success(parse(parser))
    finally
      if (parser != null) parser.close()

  /**
   * parses an array of bytes into a Json object. If the array of bytes doesn't represent a well-formed
   * Json object, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param bytes a Json object serialized in an array of bytes
   * @return a try computation with the result
   */
  def parse(bytes: Array[Byte]): Either[InvalidJson, JsObj] =
    var parser: JsonParser = null
    try
      parser = jacksonFactory.createParser(requireNonNull(bytes))
      if parser.nextToken eq START_ARRAY
      then Left(InvalidJson.jsObjectExpected)
      else Right(parse(parser))
    catch
      case e: IOException => Left(InvalidJson.errorWhileParsing(bytes,
                                                                e
                                                                )
                                  )
    finally
      if parser != null then parser.close()

  /**
   * parses a string into a Json object. If the string doesn't represent a well-formed
   * Json object, a MalformedJson failure wrapped in a Try computation is returned.
   *
   * @param str a Json object serialized in a string
   * @return a try computation with the result
   */
  def parse(str: String): Either[InvalidJson, JsObj] =
    var parser: JsonParser = null
    try
      parser = jacksonFactory.createParser(requireNonNull(str))
      if parser.nextToken eq START_ARRAY
      then Left(InvalidJson.jsObjectExpected)
      else Right(parse(parser))
    catch
      case e: IOException => Left(InvalidJson.errorWhileParsing(str,
                                                                e
                                                                )
                                  )
    finally
      if parser != null then parser.close()


  @throws[IOException]
  private[value] def parse(parser: JsonParser): JsObj =
    var map: immutable.Map[String, JsValue] = HashMap.empty
    var key = parser.nextFieldName
    while (key != null)
      var value: JsValue = null
      parser.nextToken.id match
        case ID_STRING => value = JsStr(parser.getValueAsString)
        case ID_NUMBER_INT => value = JsNumber(parser)
        case ID_NUMBER_FLOAT => value = JsBigDec(parser.getDecimalValue)
        case ID_FALSE => value = FALSE
        case ID_TRUE => value = TRUE
        case ID_NULL => value = JsNull
        case ID_START_OBJECT => value = parse(parser)
        case ID_START_ARRAY => value = JsArrayParser.parse(parser)
        case _ => throw InternalError.tokenNotFoundParsingStringIntoJsObj(parser.currentToken.name)
      map = map.updated(key,
                        value
                        )
      key = parser.nextFieldName
    JsObj(map)

  private[value] def createDeserializers(spec        : Map[SpecKey, JsSpec],
                                         result      : Map[String, Function[JsonReader[_], JsValue]],
                                         requiredKeys: Vector[String],
                                        ): (Vector[String], Map[String, Function[JsonReader[_], JsValue]]) =
    if spec.isEmpty
    then (requiredKeys, result)
    else
      def head = spec.head
      head._1 match
        case * => createDeserializers(spec.tail,
                                      result.withDefaultValue(ValueParserFactory.ofValue()),
                                      requiredKeys
                                      )
        case NamedKey(name) =>
          head._2 match
            case schema: Schema[_] => schema match
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
                             required) =>
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
                                    if required then requiredKeys.appended(name) else requiredKeys
                                    )

              case IsArraySpec(headSpec,
                               nullable,
                               required) =>
                val headDeserializers = JsArrayParser.createDeserializers(headSpec.seq,
                                                                          Vector.empty
                                                                          )
                createDeserializers(spec.tail,
                                    result.updated(name,
                                                   ValueParserFactory.ofArraySpec(headDeserializers,
                                                                                  nullable = nullable
                                                                                  )
                                                   ),
                                    if required then requiredKeys.appended(name) else requiredKeys
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
                                  elemNullable) =>
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
                                    if required then requiredKeys.appended(name) else requiredKeys)
            case p: JsPredicate =>
              val (required, fn) = getDeserializer(p)
              createDeserializers(spec.tail,
                                  result.updated(name,
                                                 fn
                                                 ),
                                  if required then requiredKeys.appended(name) else requiredKeys
                                  )
