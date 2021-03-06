package com.dslplatform.json.derializers.arrays;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsTypeDeserializer;
import json.value.JsArray;
import json.value.JsArray$;
import json.value.JsNull$;
import json.value.JsValue;
import json.value.spec.Result;
import json.value.spec.Valid$;

import java.io.IOException;
import java.util.function.Function;

public abstract class JsArrayDeserializer
{

    final static JsArray EMPTY = JsArray$.MODULE$.empty();
    private final JsTypeDeserializer deserializer;

    public JsArrayDeserializer(final JsTypeDeserializer deserializer)
    {
        this.deserializer = deserializer;
    }

    public JsValue nullOrArray(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : array(reader);
    }

    public JsArray arrayWithNull(final JsonReader<?> reader) throws IOException
    {
        if (ifIsEmptyArray(reader)) return EMPTY;

        JsArray buffer = appendNullOrValue(reader,
                                           EMPTY
                                          );
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            buffer = appendNullOrValue(reader,
                                       buffer
                                      );
        }
        reader.checkArrayEnd();
        return buffer;
    }

    private JsArray appendNullOrValue(final JsonReader<?> reader,
                                      final JsArray buffer
                                     ) throws IOException
    {
        return reader.wasNull() ? buffer.appended(JsNull$.MODULE$) : buffer.appended(deserializer.value(reader));

    }

    public JsValue nullOrArrayWithNull(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayWithNull(reader);
    }

    public JsArray array(final JsonReader reader) throws IOException
    {
        if (ifIsEmptyArray(reader)) return EMPTY;
        JsArray buffer = EMPTY.appended(deserializer.value(reader));
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            buffer = buffer.appended(deserializer.value(reader));
        }
        reader.checkArrayEnd();
        return buffer;
    }


    public JsValue nullOrArraySuchThat(final JsonReader<?> reader,
                                       final Function<JsArray, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arraySuchThat(reader,
                                                                  fn
                                                                 );
    }

    public JsValue arrayWithNullSuchThat(final JsonReader<?> reader,
                                         final Function<JsArray, Result> fn
                                        ) throws IOException
    {
        final JsArray array = arrayWithNull(reader);
        final Result result = fn.apply(array);
        if (result == Valid$.MODULE$) return array;
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrArrayWithNullSuchThat(final JsonReader<?> reader,
                                               final Function<JsArray, Result> fn
                                              ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayWithNullSuchThat(reader,
                                                                          fn
                                                                         );
    }

    public JsArray arraySuchThat(final JsonReader reader,
                                 final Function<JsArray, Result> fn
                                ) throws IOException
    {
        final JsArray array = array(reader);
        final Result result = fn.apply(array);
        if (result == Valid$.MODULE$) return array;
        throw reader.newParseError(result.toString());

    }

    boolean ifIsEmptyArray(final JsonReader reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        return reader.last() == ']';
    }

}
