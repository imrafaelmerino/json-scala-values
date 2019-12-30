package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import scala.collection.immutable.HashMap;
import value.*;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public final  class JsObjDeserializer extends JsTypeDeserializer
{

    private final JsValueDeserializer valueDeserializer;

    public JsObjDeserializer(final JsValueDeserializer valueDeserializer)
    {
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public JsObj value(final JsonReader<?> reader) throws IOException
    {
        if (isEmptyObj(reader)) return EMPTY_OBJ;

        String key = reader.readKey();
        HashMap<String, JsValue> map = EMPTY_MAP.updated(key,
                                                         valueDeserializer.value(reader
                                                                                )
                                                        );
        byte nextToken;
        while ((nextToken = reader.getNextToken()) == ',')
        {
            reader.getNextToken();
            key = reader.readKey();
            map = map.updated(key,
                              valueDeserializer.value(reader
                                                     )
                             );
        }
        if (nextToken != '}') throw reader.newParseError("Expecting '}' for map end");
        return new JsObj(map);
    }

    public JsObj valueSuchThat(final JsonReader<?> reader,
                               final Function<JsObj, Result> fn
                              ) throws IOException
    {
        final JsObj value = value(reader);
        final Result result = fn.apply(value);
        if (result.isValid()) return value;
        throw reader.newParseError(result.toString());

    }

    public JsValue nullOrValueSuchThat(final JsonReader<?> reader,
                                       final Function<JsObj, Result> fn
                                      ) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : valueSuchThat(reader,
                                                                  fn
                                                                 );
    }
}