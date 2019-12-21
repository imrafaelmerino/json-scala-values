package deserializers

import java.io.StringReader
import java.util

import org.leadpony.justify.api.{JsonValidationService, Problem, ProblemHandler}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.load.Dereferencing
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import javax.json.JsonObject
import javax.json.stream.JsonParser
import org.leadpony.justify.api


object Example
{

  def main(args: Array[String]): Unit =
  {
//    val mapper = new ObjectMapper()
//    val jsonSchema: JsonNode = JsonLoader.fromString("{\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"title\": \"Person\",\n  \"type\": \"object\",\n  \"properties\": {\n    \"firstName\": {\n      \"type\": \"string\",\n      \"description\": \"The person's first name.\"\n    },\n    \"lastName\": {\n      \"type\": \"string\",\n      \"description\": \"The person's last name.\"\n    },\n    \"age\": {\n      \"description\": \"Age in years which must be equal to or greater than zero.\",\n      \"type\": \"integer\",\n      \"minimum\": 0\n    },\n    \"latitude\": {\n      \"type\": \"number\",\n      \"minimum\": -90,\n      \"maximum\": 90\n    },\n    \"longitude\": {\n      \"type\": \"number\",\n      \"minimum\": -180,\n      \"maximum\": 180\n    },\n    \"fruits\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"string\"\n      }\n    },\n    \"numbers\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"integer\"\n      }\n    },\n    \"vegetables\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"#/definitions/veggie\"\n      }\n    }\n  },\n  \"definitions\": {\n    \"veggie\": {\n      \"type\": \"object\",\n      \"required\": [\n        \"veggieName\",\n        \"veggieLike\"\n      ],\n      \"properties\": {\n        \"veggieName\": {\n          \"type\": \"string\",\n          \"description\": \"The name of the vegetable.\"\n        },\n        \"veggieLike\": {\n          \"type\": \"boolean\",\n          \"description\": \"Do I like this vegetable?\"\n        }\n      }\n    }\n  }\n}\n")
//    val cfg: LoadingConfiguration = LoadingConfiguration.newBuilder().dereferencing(Dereferencing.INLINE).freeze()
//    val factory: JsonSchemaFactory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()
//    val schema: JsonSchema = factory.getJsonSchema(jsonSchema)
    val json_str = "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"age\": 21,\n  \"latitude\": 48.858093,\n  \"longitude\": 2.294694,\n  \"fruits\": [\n    \"apple\",\n    \"orange\",\n    \"pear\"\n  ],\n  \"numbers\": [\n    1,\n    2,\n    3,\n    4,\n    5,\n    6,\n    7,\n    8,\n    9,\n    10\n  ],\n  \"vegetables\": [\n    {\n      \"veggieName\": \"potato\",\n      " +
                   "\"veggieLike\": true\n    },\n    {\n      \"veggieName\": \"broccoli\",\n      \"veggieLike\": false\n    }\n  ]\n}\n      " +
                   "\"veggieName\": \"broccoli\",\n      \"veggieLike\": false\n    }\n  ]\n}"
//    val json: JsonNode = mapper.readTree(json_str)
//    val report = schema.validate(json)
//
//    println(report)

    val jsonSchemaStr: String = "{\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"title\": \"Person\",\n  \"type\": \"object\",\n  \"properties\": {\n    \"firstName\": {\n      \"type\": \"string\",\n      \"description\": \"The person's first name.\"\n    },\n    \"lastName\": {\n      \"type\": \"string\",\n      \"description\": \"The person's last name.\"\n    },\n    \"age\": {\n" +
                                "      \"description\": \"Age in years which must be equal to or greater than zero.\",\n      \"type\": \"integer\",\n      \"minimum\": 0\n    },\n    \"latitude\": {\n      \"type\": \"number\",\n      \"minimum\": -90,\n      \"maximum\": 90\n    },\n    \"longitude\": {\n      \"type\": \"number\",\n      \"minimum\": -180,\n      \"maximum\": 180\n    },\n    " +
                                "\"fruits\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"string\"\n      }\n    },\n    \"numbers\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"integer\"\n      }\n    },\n    \"vegetables\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"#/definitions/veggie\"\n      }\n    }\n  },\n  " +
                                "\"definitions\": {\n    \"veggie\": {\n      \"type\": \"object\",\n      \"required\": [\n        \"veggieName\",\n        \"veggieLike\"\n      ],\n      \"properties\": {\n        \"veggieName\": {\n          \"type\": \"string\",\n          \"description\": \"The name of the vegetable.\"\n        },\n        \"veggieLike\": {\n          \"type\": \"boolean\",\n       " +
                                "   \"description\": \"Do I like this vegetable?\"\n        }\n      }\n    }\n  }\n}\n"


    val serviceJustify: JsonValidationService = JsonValidationService.newInstance
    val schemaJustify: api.JsonSchema = serviceJustify.readSchema( new StringReader(jsonSchemaStr))

    val reader = serviceJustify.createReader(new StringReader(json_str),
                                                         schemaJustify,
                                                         new ProblemHandler
                                                         {
                                                           override def handleProblems(problems: util.List[Problem]): Unit = println(problems)
                                                         }
                                                         )

    val json = reader.readObject()

    reader.close()

    println(json)
  }

}
