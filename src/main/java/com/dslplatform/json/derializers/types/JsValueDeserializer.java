package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.arrays.JsArrayOfValueDeserializer;
import value.*;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public final  class JsValueDeserializer extends JsTypeDeserializer
{
    private JsObjDeserializer objDeserializer;

    private JsArrayOfValueDeserializer arrayDeserializer;

    public void setNumberDeserializer(final JsNumberDeserializer numberDeserializer)
    {
        this.numberDeserializer = numberDeserializer;
    }

    private JsNumberDeserializer numberDeserializer;

    public void setObjDeserializer(final JsObjDeserializer objDeserializer)
    {
        this.objDeserializer = objDeserializer;
    }

    public void setArrayDeserializer(final JsArrayOfValueDeserializer arrayDeserializer)
    {
        this.arrayDeserializer = arrayDeserializer;
    }

    @Override
    public JsValue value(final JsonReader<?> reader) throws IOException
    {

        switch (reader.last())
        {
            case 't':
                if (!reader.wasTrue())
                {
                    throw reader.newParseErrorAt("Expecting 'true' for true constant",
                                                 0
                                                );
                }
                return TRUE$.MODULE$;
            case 'f':
                if (!reader.wasFalse())
                {
                    throw reader.newParseErrorAt("Expecting 'false' for false constant",
                                                 0
                                                );
                }
                return FALSE$.MODULE$;
            case '"':
                return new JsStr(reader.readString());
            case '{':
                return objDeserializer.value(reader);
            case '[':
                return arrayDeserializer.array(reader);
            default:
                return numberDeserializer.value(reader);
        }
    }

    public JsValue valueSuchThat(final JsonReader<?> reader,
                                 final Function<JsValue, Result> fn

                                ) throws IOException
    {
        final JsValue value = value(reader);
        final Result result = fn.apply(value);
        if (result.isValid()) return value;
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final Function<JsValue, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }


}
