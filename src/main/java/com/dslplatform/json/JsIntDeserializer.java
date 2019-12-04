package com.dslplatform.json;

import value.*;

import java.io.IOException;

public class JsIntDeserializer
{

    public static JsValue deserialize(final JsonReader<?> reader) throws IOException
    {
        return new JsInt(NumberConverter.deserializeInt(reader));
    }

    public static JsValue deserializeNullable(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserialize(reader);
    }

    public static JsValue deserializeArray(final JsonReader<?> reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        return JsArray.from(NumberConverter.deserializeIntArray(reader));
    }

    public static JsValue deserializeNullableArray(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeArray(reader);
    }


    public static JsValue deserializeArrayOfNullable(final JsonReader<?> reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        return JsArray.fromNullableInt(NumberConverter.deserializeIntNullableCollection(reader));
    }

    public static JsValue deserializeNullableArrayOfNullable(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeArrayOfNullable(reader);
    }


}
