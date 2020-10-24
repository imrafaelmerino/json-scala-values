package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.MyNumberConverter;
import scala.math.BigInt;
import json.value.JsBigInt;
import json.value.JsNull$;
import json.value.JsValue;
import json.value.spec.Result;
import json.value.spec.Valid$;

import java.io.IOException;
import java.math.BigInteger;
import java.util.function.Function;

public final  class JsIntegralDeserializer extends JsTypeDeserializer

{
    @Override
    public JsBigInt value(final JsonReader<?> reader) throws IOException
    {
        try
        {
            return toScalaBigInt(MyNumberConverter.deserializeDecimal(reader));
        }
        catch (ArithmeticException e)
        {
            throw reader.newParseError("Integral number expected");
        }
    }

    public JsBigInt valueSuchThat(final JsonReader<?> reader,
                                  final Function<BigInteger, Result> fn
                                 ) throws IOException
    {
        final BigInteger value = MyNumberConverter.deserializeDecimal(reader)
                                                .toBigIntegerExact();
        final Result result = fn.apply(value);
        if (result == Valid$.MODULE$) return new JsBigInt(new BigInt(value));
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final Function<BigInteger, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }
}
