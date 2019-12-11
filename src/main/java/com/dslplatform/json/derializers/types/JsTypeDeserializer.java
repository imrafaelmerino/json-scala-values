package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import value.JsBigDec;
import value.JsBigInt;
import value.JsNull$;
import value.JsValue;

import java.io.IOException;
import java.math.BigDecimal;

public abstract class JsTypeDeserializer
{

    public abstract JsValue value(final JsonReader<?> reader) throws IOException;

    public JsValue nullOrValue(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : value(reader);
    }

    public  JsBigDec toScalaBigDec(BigDecimal bd)
    {
        return new JsBigDec(new scala.math.BigDecimal(bd));
    }


    public  JsBigInt toScalaBigInt(BigDecimal bd)
    {
        return new JsBigInt(new scala.math.BigInt(bd.toBigIntegerExact()));
    }


}
