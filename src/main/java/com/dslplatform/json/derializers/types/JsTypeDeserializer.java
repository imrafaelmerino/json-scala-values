package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import value.JsNull$;
import value.JsValue;

import java.io.IOException;

public abstract class JsTypeDeserializer
{

    public abstract JsValue value(final JsonReader<?> reader) throws IOException;

    public JsValue nullOrValue(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : value(reader);
    }


}
