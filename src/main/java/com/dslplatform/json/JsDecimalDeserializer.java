package com.dslplatform.json;
import value.JsBigDec;
import java.io.IOException;
import static com.dslplatform.json.JsNumberFns.toScalaBigDec;

public class JsDecimalDeserializer extends JsPrimitiveDeserializer
{
    public JsBigDec deserialize(final JsonReader<?> reader) throws IOException
    {
        return toScalaBigDec(NumberConverter.deserializeDecimal(reader));
    }
}
