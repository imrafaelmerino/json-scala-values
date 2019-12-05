package com.dslplatform.json;

import value.JsBigInt;
import value.JsValue;

import java.io.IOException;

import static com.dslplatform.json.JsNumberFns.toScalaBigInt;

public class JsIntegralDeserializer extends JsPrimitiveDeserializer

{
    @Override
    public JsValue deserialize(final JsonReader<?> reader) throws IOException
    {
        return toScalaBigInt(NumberConverter.deserializeDecimal(reader));
    }
}
