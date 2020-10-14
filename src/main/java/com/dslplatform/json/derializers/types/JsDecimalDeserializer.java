package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.MyNumberConverter;
import value.*;
import value.spec.Result;
import value.spec.Valid$;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;


public final  class JsDecimalDeserializer extends JsTypeDeserializer
{

    @Override
    public JsBigDec value(final JsonReader<?> reader) throws IOException
    {
        return toScalaBigDec(MyNumberConverter.deserializeDecimal(reader));
    }

    public JsBigDec valueSuchThat(final JsonReader<?> reader,
                                  final Function<BigDecimal, Result> fn
                                 ) throws IOException
    {
        final BigDecimal value = MyNumberConverter.deserializeDecimal(reader);
        final Result result = fn.apply(value);
        if (result == Valid$.MODULE$) return toScalaBigDec(value);
        throw reader.newParseError(result.toString());
    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final Function<BigDecimal, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }
}
