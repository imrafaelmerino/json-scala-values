import com.dslplatform.json.MyDslJson
import com.fasterxml.jackson.core.JsonFactory
import com.dslplatform.json.JsonWriter
import com.dslplatform.json.serializers.JsArraySerializer
import com.dslplatform.json.serializers.JsObjSerializer
import com.dslplatform.json.serializers.JsValueSerializer

package object value
{

  private[value] val dslJson = buildDslJson

  private[value] val jacksonFactory = new JsonFactory

  def buildDslJson:MyDslJson[Object] = {
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
