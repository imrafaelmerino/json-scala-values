package com.dslplatform.json;

import value.JsStr;

import java.io.IOException;

public class JsStrDeserializer extends JsPrimitiveDeserializer
{
    public JsStr deserialize(final JsonReader reader) throws IOException
    {
        return new JsStr(StringConverter.deserialize(reader));
    }

}
