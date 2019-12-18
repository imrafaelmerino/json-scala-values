package value;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;

class MyDslJson<Object> extends DslJson<Object>
{

    public JsonReader getReader(byte[] bytes
                               )
    {
        return localReader.get()
                          .process(bytes,
                                   bytes.length
                                  );
    }

}
