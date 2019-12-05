package com.dslplatform.json;

import value.JsDouble;

import java.io.IOException;

public class JsDoubleDeserializer extends JsPrimitiveDeserializer
{
    public JsDouble deserialize(final JsonReader<?> reader) throws IOException
    {
        return new JsDouble(NumberConverter.deserializeDouble(reader));
    }
}
