package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.NumberConverter;
import value.*;
import value.spec.Invalid;
import value.spec.Result;

import java.io.IOException;
import java.util.function.DoubleFunction;

public class JsDoubleDeserializer extends JsTypeDeserializer
{

    @Override
    public JsDouble value(final JsonReader<?> reader) throws IOException
    {
        return new JsDouble(NumberConverter.deserializeDouble(reader));
    }

    public JsDouble valueSuchThat(final JsonReader<?> reader,
                                  final DoubleFunction<Result> fn
                                 ) throws IOException
    {
        final double value = NumberConverter.deserializeDouble(reader);
        final Result result = fn.apply(value);
        if (result.isValid()) return new JsDouble(value);
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final DoubleFunction<Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }


}
