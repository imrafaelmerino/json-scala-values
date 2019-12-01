package com.dslplatform.json;

import value.FALSE$;
import value.JsArray;
import value.JsBool;
import value.TRUE$;

import java.io.IOException;

public class JsBoolDeserializer
{

    public static JsBool deserialize(final JsonReader reader) throws IOException
    {
        if (reader.wasTrue())
        {
            return TRUE$.MODULE$;
        } else if (reader.wasFalse())
        {
            return FALSE$.MODULE$;
        }
        throw reader.newParseErrorAt("Found invalid boolean value",
                                     0
                                    );
    }

    public static JsArray deserializeArray(final JsonReader reader) throws IOException
    {
        return JsArray.fromBooleans(BoolConverter.deserializeBoolArray(reader));
    }


    public static JsArray deserializeNullableArray(final JsonReader reader) throws IOException
    {
        return JsArray.fromNullableBooleans(BoolConverter.deserializeNullableCollection(reader));
    }


}
