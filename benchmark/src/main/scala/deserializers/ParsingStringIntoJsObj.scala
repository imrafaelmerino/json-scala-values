package deserializers

import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.load.Dereferencing
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import value.Implicits._
import value.spec.JsBoolSpecs._
import value.spec.JsIntSpecs._
import value.spec.JsArraySpecs._
import value.spec.JsNumberSpecs._
import value.spec.JsObjSpec
import value.spec.JsStrSpecs._
import value.{JsObj, JsObjParser}

import scala.util.Try


@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Benchmark)
class ParsingStringIntoJsObj
{

  val objectUT1: Array[Byte] = "\n{\n  \"a\" : {\n    \"b\": 1,\n    \"c\": [1,2,3,4,5,6,7],\n    \"d\": [\"a\",\"b\",\"c\",\"d\",\"e\"],\n    \"e\": true,\n    \"f\": {\n      \"g\": \"hi\",\n      \"h\": {\n        \"i\": [{\"a\": 1,\"b\": \"bye\"},{\"a\": 4,\"b\": \"hi\"}]\n      },\n      \"k\": {\"l\": false,\"m\": \"red\",\"n\": 1.5}\n    }\n  }\n}".getBytes()
  val objectUT: Array[Byte] = (
    "{\n  \"a\": \"a\",\n  \"b\": 2,\n  \"c\": true,\n  \"d\": 10,\n  \"e\": \"b\",\n  \"f\": false,\n  \"integers\": [\n    1,\n    2,\n    3,\n    4,\n    5,\n    6,\n    7,\n    8,\n    9,\n    10,\n    11,\n    12,\n    13,\n    14,\n    15,\n    16\n  ],\n  \"strings\": [\n    \"1\",\n    \"2\",\n    \"3\",\n    \"4\",\n    \"5\",\n    \"6\",\n    \"7\",\n    " +
    "\"8\",\n    \"9\",\n    \"10\",\n    \"11\",\n    \"12\",\n    \"13\",\n    \"14\",\n    \"15\",\n    \"16\"\n  ],\n  \"objects\": [\n    {\n      \"a\": \"hi\",\n      \"b\": 10,\n      \"c\": true,\n      \"d\": [\n        \"a\",\n        \"b\",\n        \"c\",\n        \"d\",\n        \"e\",\n        \"f\",\n        \"g\"\n      ],\n      \"e\": 1.10\n    },\n" +
    "    {\n      \"a\": \"hi\",\n      \"b\": 10,\n      \"c\": true,\n      \"d\": [\n        \"a\",\n        \"b\",\n        \"c\",\n        \"d\",\n        \"e\",\n        \"f\",\n        \"g\"\n      ],\n      \"e\": 1.10\n    }\n  ]\n}  "
    ).getBytes()

  val objectUTStr: String = new String(objectUT)


  val spec = JsObjSpec("a" -> str,
                       "b" -> int,
                       "c" -> bool,
                       "d" -> int,
                       "e" -> str,
                       "f" -> bool,
                       "integers" -> array_of_int,
                       "strings" -> array_of_str,
                       "objects" -> arrayOfObjSpec(JsObjSpec("a" -> str,
                                                             "b" -> int,
                                                             "c" -> bool,
                                                             "d" -> array_of_str,
                                                             "e" -> decimal
                                                             )
                                                   )
                       )

  val objectMapper = new ObjectMapper

  val jsonParser = JsObjParser(spec)

  @Benchmark
  def json_values_with_spec(bh: Blackhole): Unit =
  {
    val obj = jsonParser.parse(objectUT)
    bh.consume(obj)
  }


  @Benchmark
  def jackson_map(bh: Blackhole): Unit =
  {
    val map = objectMapper.readValue(objectUT,
                                     classOf[java.util.Map[String, Object]]
                                     )
    bh.consume(map)
  }


  @Benchmark
  def json_values(bh: Blackhole): Unit =
  {
    val obj: Try[JsObj] = JsObj.parse(objectUTStr)
    bh.consume(obj)
  }

//  @Benchmark
//  @throws[InterruptedException]
//  def scala_hash_map(e: ExecutorState): Boolean =
//  {
//    val count = new CountDownLatch(NUMBER_TASKS)
//    var i = 0
//    while (
//    {i < NUMBER_TASKS})
//    {
//      e.service.submit(() =>
//                       {
//                         def foo() =
//                           try Jsons.immutable.`object`.parse(`object`).orElseThrow
//                           catch
//                           {
//                             case ex: MalformedJson =>
//                               throw new RuntimeException(ex)
//                           } finally
//                             count.countDown()
//
//                         foo()
//                       }
//                       )
//
//      {i += 1; i - 1}
//    }
//    count.await(10,
//                TimeUnit.SECONDS
//                )
//  }
}


