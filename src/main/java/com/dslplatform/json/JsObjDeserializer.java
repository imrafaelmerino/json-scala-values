package com.dslplatform.json;

import scala.collection.immutable.HashMap;
import scala.collection.immutable.HashMap$;
import value.JsObj;
import value.JsObj$;
import value.JsPath;
import value.JsValue;

import java.io.IOException;

import static com.dslplatform.json.DslJsConfiguration.arrayDeserializer;


class JsObjDeserializer extends AbstractJsDeserializer implements JsonReader.ReadObject<JsObj>
{

    private JsObj deserializeMap(final JsonReader<?> reader
                                 ) throws IOException
    {
        final byte last = reader.last();
        if (last != '{') throw reader.newParseError("Expecting '{' for map start");
        byte nextToken = reader.getNextToken();
        if (nextToken == '}') return JsObj$.MODULE$.empty();
        HashMap<String, JsValue> map = HashMap$.MODULE$.empty();
        String key = reader.readKey();
        map = map.updated(key,
                          deserializeObject(reader,
                                            this,
                                            arrayDeserializer
                                           )
                         );
        while ((nextToken = reader.getNextToken()) == ',')
        {
            reader.getNextToken();
            key = reader.readKey();
            map = map.updated(key,
                              deserializeObject(reader,
                                                this,
                                                arrayDeserializer
                                               )
                             );
        }
        if (nextToken != '}') throw reader.newParseError("Expecting '}' for map end");
        return new JsObj(map);
    }

    @Override
    @SuppressWarnings("rawtypes")//method from third party library, not possible add <>
    public JsObj read(final JsonReader reader) throws IOException
    {
        return deserializeMap(reader);
    }

}
