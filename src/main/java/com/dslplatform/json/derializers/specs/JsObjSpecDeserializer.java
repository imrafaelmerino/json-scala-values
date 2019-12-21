package com.dslplatform.json.derializers.specs;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsTypeDeserializer;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;
import value.JsNull$;
import value.JsObj;
import value.JsValue;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public class JsObjSpecDeserializer extends JsTypeDeserializer
{
    private final Map<String, Function<JsonReader<?>, JsValue>> deserializers;

    public JsObjSpecDeserializer(final Map<String, Function<JsonReader<?>, JsValue>> deserializers)
    {
        this.deserializers = deserializers;
    }

    @Override
    public JsObj value(final JsonReader<?> reader) throws IOException
    {
        if (isEmptyObj(reader)) return EMPTY_OBJ;
        String key = reader.readKey();
        HashMap<String, JsValue> map = EMPTY_MAP.updated(key,
                                                         deserializers.apply(key)
                                                                      .apply(reader
                                                                            )
                                                        );
        byte nextToken;
        while ((nextToken = reader.getNextToken()) == ',')
        {
            reader.getNextToken();
            key = reader.readKey();
            map = map.updated(key,
                              deserializers.apply(key)
                                           .apply(reader)
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
