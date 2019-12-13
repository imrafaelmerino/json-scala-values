package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.NumberConverter;
import value.*;
import value.spec.Invalid;
import value.spec.Result;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;


public class JsDecimalDeserializer extends JsTypeDeserializer
{

    @Override
    public JsBigDec value(final JsonReader<?> reader) throws IOException
    {
        return toScalaBigDec(NumberConverter.deserializeDecimal(reader));
    }

    public JsBigDec valueSuchThat(final JsonReader<?> reader,
                                  final Function<BigDecimal, Result> fn
                                 ) throws IOException
    {
        final BigDecimal value = NumberConverter.deserializeDecimal(reader);
        final Result result = fn.apply(value);
        if (result.isValid()) return toScalaBigDec(NumberConverter.deserializeDecimal(reader));
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
