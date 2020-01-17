package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.MyNumberConverter;
import value.*;
import value.spec.Invalid;
import value.spec.Result;
import value.spec.Valid$;

import java.io.IOException;
import java.util.function.IntFunction;

public final  class JsIntDeserializer extends JsTypeDeserializer
{
    @Override
    public JsInt value(final JsonReader<?> reader) throws IOException
    {
        return new JsInt(MyNumberConverter.deserializeInt(reader));
    }

    public JsInt valueSuchThat(final JsonReader<?> reader,
                               final IntFunction<Result> fn
                              ) throws IOException
    {
        final int value = MyNumberConverter.deserializeInt(reader);
        final Result result = fn.apply(value);
        if (result == Valid$.MODULE$) return new JsInt(value);
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final IntFunction<Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }

}
