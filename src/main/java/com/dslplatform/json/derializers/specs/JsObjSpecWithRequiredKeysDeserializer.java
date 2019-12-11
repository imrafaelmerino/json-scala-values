package com.dslplatform.json.derializers.specs;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsTypeDeserializer;
import com.dslplatform.json.derializers.types.JsValueDeserializer;
import scala.collection.Iterator;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Vector;
import value.JsNull$;
import value.JsObj;
import value.JsValue;
import value.spec.Invalid;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public class JsObjSpecWithRequiredKeysDeserializer extends JsObjSpecDeserializer
{
    private final Vector<String> required;


    public JsObjSpecWithRequiredKeysDeserializer(final Vector<String> required,
                                                 final HashMap<String, Function<JsonReader<?>, JsValue>> deserializers,
                                                 final JsValueDeserializer valueDeserializer
                                                )
    {
        super(deserializers,
              valueDeserializer
             );
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
