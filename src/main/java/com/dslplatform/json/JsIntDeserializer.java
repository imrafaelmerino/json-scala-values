package com.dslplatform.json;

import value.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class JsIntDeserializer extends JsPrimitiveDeserializer
{

    public JsValue deserialize(final JsonReader<?> reader) throws IOException
    {
        return new JsInt(NumberConverter.deserializeInt(reader));
    }
}
