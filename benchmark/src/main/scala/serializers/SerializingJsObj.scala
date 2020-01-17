package serializers

import java.util
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.ObjectMapper
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit, Scope, State}
import org.openjdk.jmh.infra.Blackhole
import value.{JsObj, JsObjParser}

@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Benchmark)
class SerializingJsObj
{

  val objectUT: String = "{\n  \"a\": \"a\",\n  \"b\": 2,\n  \"c\": true,\n  \"d\": 10,\n  \"e\": \"b\",\n  \"f\": false,\n  \"integers\": [\n    1,\n    2,\n    3,\n    4,\n    5,\n    6,\n    7,\n    8,\n    9,\n    10,\n    11,\n    12,\n    13,\n    14,\n    15,\n    16\n  ],\n  \"strings\": [\n    \"1\",\n    \"2\",\n    \"3\",\n    \"4\",\n    \"5\",\n    \"6\",\n    \"7\",\n    " +
                         "\"8\",\n    \"9\",\n    \"10\",\n    \"11\",\n    \"12\",\n    \"13\",\n    \"14\",\n    \"15\",\n    \"16\"\n  ],\n  \"objects\": [\n    {\n      \"a\": \"hi\",\n      \"b\": 10,\n      \"c\": true,\n      \"d\": [\n        \"a\",\n        \"b\",\n        \"c\",\n        \"d\",\n        \"e\",\n        \"f\",\n        \"g\"\n      ],\n      \"e\": 1.10\n    },\n" +
                         "    {\n      \"a\": \"hi\",\n      \"b\": 10,\n      \"c\": true,\n      \"d\": [\n        \"a\",\n        \"b\",\n        \"c\",\n        \"d\",\n        \"e\",\n        \"f\",\n        \"g\"\n      ],\n      \"e\": 1.10\n    }\n  ]\n}  "

  val json: JsObj = JsObjParser.parse(objectUT).getOrElse(JsObj.empty)

  val objectMapper = new ObjectMapper

  val map: util.Map[String, Object] = objectMapper.readValue(objectUT,
                                                             classOf[java.util.Map[String, Object]]
                                                             )
  @Benchmark
  def jackson_to_bytes(bh: Blackhole): Unit =
  {
    val bytes = objectMapper.writeValueAsBytes(map)
    bh.consume(bytes)
  }

  @Benchmark
  def jackson_to_string(bh: Blackhole): Unit =
  {
    val bytes = objectMapper.writeValueAsString(map)
    bh.consume(bytes)
  }

  @Benchmark
  def json_values_to_bytes(bh: Blackhole): Unit =
  {
    val str = json.serialize
    bh.consume(str)
  }

  @Benchmark
  def json_values_to_string(bh: Blackhole): Unit =
  {
    val str = json.toString
    bh.consume(str)
  }

}

