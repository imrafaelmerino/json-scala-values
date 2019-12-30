package deserializers

import java.io.StringReader
import java.util
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.load.Dereferencing
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import javax.json.{JsonObject, JsonReader}
import org.leadpony.justify.api
import org.leadpony.justify.api.{JsonValidationService, Problem, ProblemHandler}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import value.spec.JsArraySpecs.{arrayOfObjSpec, array_of_int, array_of_str}
import value.spec.JsBoolSpecs.bool
import value.spec.JsIntSpecs.intSuchThat
import value.spec.JsNumberSpecs.decimalSuchThat
import value.spec.JsStrSpecs.str
import value.spec.{Invalid, JsArraySpecs, JsObjSpec, Result, Valid}
import value.{JsObj, JsObjParser}

@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Benchmark)
class JsSchemaValidations
{

  val jsonSchemaStr: String = "{\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"title\": \"Person\",\n  \"type\": \"object\",\n  \"properties\": {\n    \"firstName\": {\n      \"type\": \"string\",\n      \"description\": \"The person's first name.\"\n    },\n    \"lastName\": {\n      \"type\": \"string\",\n      \"description\": \"The person's last name.\"\n    },\n    " +
                              "\"age\": {\n" +
                              "      \"description\": \"Age in years which must be equal to or greater than zero.\",\n      \"type\": \"integer\",\n      \"minimum\": 0\n    },\n    \"latitude\": {\n      \"type\": \"number\",\n      \"minimum\": -90,\n      \"maximum\": 90\n    },\n    \"longitude\": {\n      \"type\": \"number\",\n      \"minimum\": -180,\n      \"maximum\": 180\n    },\n    " +
                              "\"fruits\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"string\"\n      }\n    },\n    \"numbers\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"integer\"\n      }\n    },\n    \"vegetables\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"#/definitions/veggie\"\n      }\n    }\n  },\n  " +
                              "\"definitions\": {\n    \"veggie\": {\n      \"type\": \"object\",\n      \"required\": [\n        \"veggieName\",\n        \"veggieLike\"\n      ],\n      \"properties\": {\n        \"veggieName\": {\n          \"type\": \"string\",\n          \"description\": \"The name of the vegetable.\"\n        },\n        \"veggieLike\": {\n          \"type\": \"boolean\"," +
                              "\n       " +
                              "   \"description\": \"Do I like this vegetable?\"\n        }\n      }\n    }\n  }\n}\n"
  val jsonSchema: JsonNode = JsonLoader.fromString(jsonSchemaStr)
  val cfg: LoadingConfiguration = LoadingConfiguration.newBuilder().dereferencing(Dereferencing.INLINE).freeze()
  val factory: JsonSchemaFactory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()
  val schema: JsonSchema = factory.getJsonSchema(jsonSchema)
  val objectMapper = new ObjectMapper()

  private def greaterOrEqualThan(value: Int): Int => Result = i => if (i >= value) Valid else Invalid(s"minimum $value")

  private def interval(min: BigDecimal,
                       max: BigDecimal
                      ): (BigDecimal => Result) =
    (d: BigDecimal) => if (d <= max || d >= -min) Valid else Invalid(s"Not between [$min,$max]")

  val json_str: String = "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"age\": 21,\n  \"latitude\": 48.858093,\n  \"longitude\": 2.294694,\n  \"fruits\": [\n    \"apple\",\n    \"orange\",\n    \"pear\"\n  ],\n  \"numbers\": [\n    1,\n    2,\n    3,\n    4,\n    5,\n    6,\n    7,\n    8,\n    9,\n    10\n  ],\n  \"vegetables\": [\n    {\n      \"veggieName\": \"potato\",\n     " +
                         " \"veggieLike\": true\n    },\n    {\n      \"veggieName\": \"broccoli\",\n      \"veggieLike\": false\n    }\n  ]\n}\n      " + "\"veggieName\": \"broccoli\",\n      \"veggieLike\": false\n    }\n  ]\n}"

  val json_bytes: Array[Byte] = json_str.getBytes

  val spec = JsObjSpec("firstName" -> str,
                       "lastName" -> str,
                       "age" -> intSuchThat(greaterOrEqualThan(0)),
                       "latitude" -> decimalSuchThat(interval(-90,
                                                              90
                                                              )
                                                     ),
                       "longitude" -> decimalSuchThat(interval(-180,
                                                               180
                                                               )
                                                      ),
                       "fruits" -> array_of_str,
                       "numbers" -> JsArraySpecs.array_of_int,
                       "vegetables" -> arrayOfObjSpec(JsObjSpec("veggieName" -> str,
                                                                "veggieLike" -> bool
                                                                )
                                                      )
                       )

  val parser = JsObjParser(spec)

  val serviceJustify: JsonValidationService = JsonValidationService.newInstance

  val schemaJustify: api.JsonSchema = serviceJustify.readSchema(new StringReader(jsonSchemaStr))

  @Benchmark
  def json_schema_validator(bh: Blackhole): Unit =
  {
    val json: JsonNode = objectMapper.readTree(json_str)
    val report = schema.validate(json)
    bh.consume(report)
  }

  @Benchmark
  def json_values_spec(bh: Blackhole): Unit =
  {
    val result = parser.parse(json_bytes)
    bh.consume(result)
  }

  @Benchmark
  def justify(bh: Blackhole): Unit =
  {

    val reader: JsonReader = serviceJustify.createReader(new StringReader(json_str),
                                                         schemaJustify,
                                                         new ProblemHandler
                                                         {
                                                           override def handleProblems(problems: util.List[Problem]): Unit = bh.consume(problems)
                                                         }
                                                         )

    val json: JsonObject = reader.readObject()

    reader.close()

    bh.consume(json)
  }

  @Benchmark
  def json_values_parse_and_validation_with_spec(bh: Blackhole): Unit =
  {
    val result: JsObj = JsObj.parse(json_str).get
    val errors = result.validate(spec)
    bh.consume(errors)
  }
}
