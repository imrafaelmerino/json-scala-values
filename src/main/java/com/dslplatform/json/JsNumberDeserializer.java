package com.dslplatform.json;

import value.JsDouble;
import value.JsLong;
import value.JsNumber;

import java.io.IOException;
import java.math.BigDecimal;

import static com.dslplatform.json.JsNumberFns.toScalaBigDec;


public class JsNumberDeserializer extends JsPrimitiveDeserializer
{

    public JsNumber deserialize(final JsonReader<?> reader) throws IOException
    {
        final Number number = NumberConverter.deserializeNumber(reader);
        if (number instanceof Double) return new JsDouble(((double) number));
        else if (number instanceof Long) return new JsLong(((long) number));
        else if (number instanceof BigDecimal) return toScalaBigDec(((BigDecimal) number));
        throw new RuntimeException("internal error: not condisered " + number.getClass());
    }


}
