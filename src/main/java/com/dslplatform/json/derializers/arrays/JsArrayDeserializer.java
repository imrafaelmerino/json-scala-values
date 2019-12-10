package com.dslplatform.json.derializers.arrays;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsTypeDeserializer;
import value.JsArray;
import value.JsArray$;
import value.JsNull$;
import value.JsValue;
import value.spec.Invalid;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public abstract class JsArrayDeserializer
{
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
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        JsArray buffer = JsArray$.MODULE$.empty();
        if (reader.wasNull())
        {
            buffer = buffer.appended(JsNull$.MODULE$);
        } else
        {
            buffer = buffer.appended(deserializer.value(reader));
        }
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            if (reader.wasNull())
            {
                buffer = buffer.appended(JsNull$.MODULE$);
            } else
            {
                buffer = buffer.appended(deserializer.value(reader));
            }
        }
        reader.checkArrayEnd();
        return buffer;
    }

    public JsValue nullOrArrayWithNull(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayWithNull(reader);
    }

    public JsArray array(final JsonReader reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        if (reader.last() == ']') return JsArray$.MODULE$.empty();
        JsArray buffer = JsArray$.MODULE$.empty();
        buffer = buffer.appended(deserializer.value(reader));
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
        if (result.isValid()) return array;
        throw reader.newParseError(((Invalid) result).messages()
                                                     .mkString(","));
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
        if (result.isValid()) return array;
        throw reader.newParseError(((Invalid) result).messages()
                                                     .mkString(","));
    }

}
