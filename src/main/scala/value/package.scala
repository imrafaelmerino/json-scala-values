import com.dslplatform.json.MyDslJson
import com.fasterxml.jackson.core.JsonFactory

package object value
{

  private[value] val dslJson = new MyDslJson[Object]

  private[value] val jacksonFactory = new JsonFactory

}
