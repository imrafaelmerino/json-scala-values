package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.StringConverter;
import value.JsNull$;
import value.JsStr;
import value.JsValue;
import value.spec.Invalid;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public class JsStrDeserializer extends JsTypeDeserializer
{
    @Override
    public JsStr value(final JsonReader reader) throws IOException
    {
        return new JsStr(StringConverter.deserialize(reader));
    }


    public JsStr valueSuchThat(final JsonReader<?> reader,
                               final Function<String, Result> fn
                              ) throws IOException
    {
        final String value = StringConverter.deserialize(reader);
        final Result result = fn.apply(value);
        if (result.isValid()) return new JsStr(value);
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final Function<String, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }

}
