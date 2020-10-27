package com.dslplatform.json.derializers.specs;
import com.dslplatform.json.JsonReader;
import scala.collection.Iterator;
import scala.collection.immutable.Map;
import scala.collection.immutable.Vector;
import json.value.JsObj;
import json.value.JsValue;
import java.io.IOException;
import java.util.function.Function;

public final  class JsObjSpecWithRequiredKeysDeserializer extends JsObjSpecDeserializer
{
    private final Vector<String> required;


    public JsObjSpecWithRequiredKeysDeserializer(final Vector<String> required,
                                                 final Map<String, Function<JsonReader<?>, JsValue>> deserializers
                                                )
    {
        super(deserializers);
        this.required = required;
    }

    @Override
    public JsObj value(final JsonReader<?> reader) throws IOException
    {
        final JsObj obj = super.value(reader);
        final Iterator<String> iterator = required.iterator();
        while (iterator.hasNext())
        {
            final String key = iterator.next();
            if (!obj.containsKey(key)) throw reader.newParseError("Required key not found: " + key);
        }
        return obj;
    }


}
