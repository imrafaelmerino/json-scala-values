package com.dslplatform.json;

import value.*;

import java.io.IOException;

public class JsLongDeserializer extends JsPrimitiveDeserializer
{
    public JsValue deserialize(final JsonReader<?> reader) throws IOException
    {
        return new JsLong(NumberConverter.deserializeLong(reader));
    }

}
