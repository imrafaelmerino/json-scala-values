package deserializers

import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.ObjectMapper
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import json.value.Preamble.{given _}
import json.value.spec.JsBoolSpecs._
import json.value.spec.JsArraySpecs._
import json.value.spec.JsNumberSpecs._
import json.value.spec.JsObjSpec
import json.value.spec.JsStrSpecs._
import json.value.{JsObj, JsObjParser}

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

  val objectUTStr: String = String(objectUT)

  val spec = JsObjSpec("a" -> str,
                       "b" -> int,
                       "c" -> bool,
                       "d" -> int,
                       "e" -> str,
                       "f" -> bool,
                       "integers" -> arrayOfInt,
                       "strings" -> arrayOfStr,
                       "objects" -> arrayOf(JsObjSpec("a" -> str,
                                                      "b" -> int,
                                                      "c" -> bool,
                                                      "d" -> arrayOfStr,
                                                      "e" -> decimal
                                                      )
                                            )
                       )

  val objectMapper = ObjectMapper

  val jsonParser = JsObjParser(spec)

  @Benchmark
  def json_values_with_spec(bh: Blackhole): Unit =
  {
    bh.consume(jsonParser.parse(objectUT,
                                )
               )
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
    bh.consume(JsObjParser.parse(objectUTStr))
  }

}


