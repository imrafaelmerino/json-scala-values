package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.StringConverter;
import json.value.JsNull$;
import json.value.JsStr;
import json.value.JsValue;
import json.value.spec.Result;
import json.value.spec.Valid$;

import java.io.IOException;
import java.util.function.Function;

public final  class JsStrDeserializer extends JsTypeDeserializer
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
        if (result == Valid$.MODULE$) return new JsStr(value);
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
