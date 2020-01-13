package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.MyNumberConverter;
import value.*;
import value.spec.Invalid;
import value.spec.Result;
import value.spec.Valid$;

import java.io.IOException;
import java.util.function.LongFunction;

public final  class JsLongDeserializer extends JsTypeDeserializer
{

    @Override
    public JsLong value(final JsonReader<?> reader) throws IOException
    {
        return new JsLong(MyNumberConverter.deserializeLong(reader));
    }

    public JsLong valueSuchThat(final JsonReader<?> reader,
                                final LongFunction<Result> fn
                               ) throws IOException
    {
        final long value = MyNumberConverter.deserializeLong(reader);
        final Result result = fn.apply(value);
        if (result == Valid$.MODULE$) return new JsLong(value);
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final LongFunction<Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }


}
