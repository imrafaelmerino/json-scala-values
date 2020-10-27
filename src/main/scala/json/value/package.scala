import com.dslplatform.json.{JsonWriter, MyDslJson}
import com.dslplatform.json.serializers.{JsArraySerializer, JsObjSerializer, JsValueSerializer}
import com.fasterxml.jackson.core.JsonFactory
import json.value.JsArray
import json.value.JsObj
package object value
{

  val dslJson = buildDslJson

  val jacksonFactory = new JsonFactory

  def buildDslJson: MyDslJson[Object] =
  {
    val dslJson = new MyDslJson[Object]


    val valueSerializer: JsValueSerializer = new JsValueSerializer
    val objSerializer: JsonWriter.WriteObject[JsObj] = new JsObjSerializer(valueSerializer)
    val arraySerializer: JsonWriter.WriteObject[JsArray] = new JsArraySerializer(valueSerializer)

    valueSerializer.setArraySerializer(arraySerializer)
    valueSerializer.setObjectSerializer(objSerializer)

    dslJson.registerWriter(classOf[JsObj],
                           objSerializer
                           )

    dslJson.registerWriter(classOf[JsArray],
                           arraySerializer
                           )

    dslJson
  }

}
