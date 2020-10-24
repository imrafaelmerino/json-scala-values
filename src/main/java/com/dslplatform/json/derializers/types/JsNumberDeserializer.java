package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.NumberConverter;
import json.value.*;
import json.value.spec.Result;
import json.value.spec.Valid$;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;



public final  class JsNumberDeserializer extends JsTypeDeserializer
{

    @Override
    public JsNumber value(final JsonReader<?> reader) throws IOException
    {
        final Number number = NumberConverter.deserializeNumber(reader);
        if (number instanceof Double) return new JsDouble(((double) number));
        else if (number instanceof Long) return new JsLong(((long) number));
        else if (number instanceof BigDecimal) return toScalaBigDec(((BigDecimal) number));
        throw new RuntimeException("internal error: not condisered " + number.getClass());
    }


    public JsNumber valueSuchThat(final JsonReader<?> reader,
                                  final Function<JsNumber, Result> fn
                                 ) throws IOException
    {
        final JsNumber value = value(reader);
        final Result result = fn.apply(value);
        if (result == Valid$.MODULE$) return value;
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final Function<JsNumber, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }

}
