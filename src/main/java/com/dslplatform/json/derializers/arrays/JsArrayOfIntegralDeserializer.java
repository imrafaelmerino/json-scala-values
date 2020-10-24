package com.dslplatform.json.derializers.arrays;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsIntegralDeserializer;
import json.value.JsArray;
import json.value.JsNull$;
import json.value.JsValue;
import json.value.spec.Result;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;

public final  class JsArrayOfIntegralDeserializer extends JsArrayDeserializer
{

    private JsIntegralDeserializer deserializer;

    public JsArrayOfIntegralDeserializer(final JsIntegralDeserializer deserializer)
    {
        super(Objects.requireNonNull(deserializer));
        this.deserializer = deserializer;
    }

    public JsValue nullOrArrayEachSuchThat(final JsonReader<?> reader,
                                           final Function<BigInteger, Result> fn
                                          ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayEachSuchThat(reader,
                                                                      fn
                                                                     );
    }

    public JsValue arrayWithNullEachSuchThat(final JsonReader<?> reader,
                                             final Function<BigInteger, Result> fn
                                            ) throws IOException
    {
        if (ifIsEmptyArray(reader)) return EMPTY;

        JsArray buffer = appendNullOrValue(reader,
                                           fn,
                                           EMPTY
                                          );

        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            buffer = appendNullOrValue(reader,
                                       fn,
                                       buffer
                                      );

        }
        reader.checkArrayEnd();
        return buffer;
    }

    public JsValue nullOrArrayWithNullEachSuchThat(final JsonReader<?> reader,
                                                   final Function<BigInteger, Result> fn
                                                  ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayWithNullEachSuchThat(reader,
                                                                              fn
                                                                             );
    }

    public JsArray arrayEachSuchThat(final JsonReader reader,
                                     final Function<BigInteger, Result> fn
                                    ) throws IOException
    {
        if (ifIsEmptyArray(reader)) return EMPTY;

        JsArray buffer = EMPTY.appended(deserializer.valueSuchThat(reader,
                                                                   fn
                                                                  ));
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            buffer = buffer.appended(deserializer.valueSuchThat(reader,
                                                                fn
                                                               ));
        }
        reader.checkArrayEnd();
        return buffer;
    }

    private JsArray appendNullOrValue(final JsonReader<?> reader,
                                      final Function<BigInteger, Result> fn,
                                      JsArray buffer
                                     ) throws IOException
    {
        return reader.wasNull() ? buffer.appended(JsNull$.MODULE$) : buffer.appended(deserializer.valueSuchThat(reader,
                                                                                                                fn
                                                                                                               ));

    }
}
