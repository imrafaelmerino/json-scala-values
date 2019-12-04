package com.dslplatform.json;

import value.*;

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

    public static JsValue deserializeNullable(final JsonReader reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserialize(reader);

    }


    public static JsArray deserializeArray(final JsonReader reader) throws IOException
    {
        return JsArray.from(BoolConverter.deserializeBoolArray(reader));
    }


    public static JsArray deserializeNullableArray(final JsonReader reader) throws IOException
    {
        return JsArray.fromNullableBooleans(BoolConverter.deserializeNullableCollection(reader));
    }


}
