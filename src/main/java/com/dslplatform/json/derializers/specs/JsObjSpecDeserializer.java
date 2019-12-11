package com.dslplatform.json.derializers.specs;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsTypeDeserializer;
import com.dslplatform.json.derializers.types.JsValueDeserializer;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.HashMap$;
import value.JsNull$;
import value.JsObj;
import value.JsObj$;
import value.JsValue;
import value.spec.Invalid;
import value.spec.Result;

import java.io.IOException;
import java.util.function.Function;

public class JsObjSpecDeserializer extends JsTypeDeserializer
{
    private final HashMap<String, Function<JsonReader<?>, JsValue>> deserializers;
    //    private final boolean additionalKeys;
    private final JsValueDeserializer valueDeserializer;

    public JsObjSpecDeserializer(final HashMap<String, Function<JsonReader<?>, JsValue>> deserializers,
                                 final JsValueDeserializer valueDeserializer
                                )
    {
        this.deserializers = deserializers;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public JsObj value(final JsonReader<?> reader) throws IOException
    {
        final byte last = reader.last();
        if (last != '{') throw reader.newParseError("Expecting '{' for map start");
        byte nextToken = reader.getNextToken();
        if (nextToken == '}') return JsObj$.MODULE$.empty();
        HashMap<String, JsValue> map = HashMap$.MODULE$.empty();
        String key = reader.readKey();
        map = map.updated(key,
                          deserializers.apply(key)
                                       .apply(reader
                                             )
                         );
        while ((nextToken = reader.getNextToken()) == ',')
        {
            reader.getNextToken();
            key = reader.readKey();
            map = map.updated(key,
                              deserializers.apply(key)
                                           .apply(reader
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
        throw reader.newParseError(((Invalid) result).messages()
                                                     .mkString(","));
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
