package com.dslplatform.json;

import value.JsArray;
import value.JsNull$;
import value.JsStr;
import value.JsValue;

import java.io.IOException;

public class JsStrDeserializer
{
    public static JsStr deserialize(final JsonReader reader) throws IOException
    {
        return new JsStr(StringConverter.deserialize(reader));
    }

    public static JsValue deserializeNullable(final JsonReader reader) throws IOException
    {
        String s = StringConverter.deserializeNullable(reader);
        return s == null ? JsNull$.MODULE$ : new JsStr(s);
    }

    public static JsArray deserializeArray(final JsonReader reader) throws IOException
    {
        return JsArray.fromStrings(StringConverter.deserializeCollection(reader));
    }


    public static JsArray deserializeNullableArray(final JsonReader reader) throws IOException
    {
        return JsArray.fromNullableStrings(StringConverter.deserializeNullableCollection(reader));
    }
}
