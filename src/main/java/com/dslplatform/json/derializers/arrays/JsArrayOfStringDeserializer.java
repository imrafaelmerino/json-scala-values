package com.dslplatform.json.derializers.arrays;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsStrDeserializer;
import value.JsArray;
import value.JsArray$;
import value.JsNull$;
import value.JsValue;
import value.spec.Result;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

public class JsArrayOfStringDeserializer extends JsArrayDeserializer
{
    private JsStrDeserializer deserializer;

    public JsArrayOfStringDeserializer(final JsStrDeserializer deserializer)
    {
        super(Objects.requireNonNull(deserializer));
        this.deserializer = deserializer;
    }

    public JsValue nullOrArrayEachSuchThat(final JsonReader<?> reader,
                                           final Function<String, Result> fn
                                          ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayEachSuchThat(reader,
                                                                      fn
                                                                     );
    }

    public JsValue arrayWithNullEachSuchThat(final JsonReader<?> reader,
                                             final Function<String, Result> fn
                                            ) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        JsArray buffer = JsArray$.MODULE$.empty();
        if (reader.wasNull()) buffer = buffer.appended(JsNull$.MODULE$);
        else buffer = buffer.appended(deserializer.valueSuchThat(reader,
                                                                 fn
                                                                ));
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            if (reader.wasNull()) buffer = buffer.appended(JsNull$.MODULE$);
            else buffer = buffer.appended(deserializer.valueSuchThat(reader,
                                                                     fn
                                                                    ));
        }
        reader.checkArrayEnd();
        return buffer;
    }

    public JsValue nullOrArrayWithNullEachSuchThat(final JsonReader<?> reader,
                                                   final Function<String, Result> fn
                                                  ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : arrayWithNullEachSuchThat(reader,
                                                                              fn
                                                                             );
    }

    public JsArray arrayEachSuchThat(final JsonReader reader,
                                     final Function<String, Result> fn
                                    ) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        if (reader.last() == ']')
        {
            return JsArray$.MODULE$.empty();
        }
        JsArray buffer = JsArray$.MODULE$.empty();
        buffer = buffer.appended(deserializer.valueSuchThat(reader,
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

}
