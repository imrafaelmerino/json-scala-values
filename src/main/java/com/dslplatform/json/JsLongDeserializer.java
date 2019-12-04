package com.dslplatform.json;

import value.*;

import java.io.IOException;

public class JsLongDeserializer
{

    public static JsValue deserialize(final JsonReader<?> reader) throws IOException
    {
        return new JsLong(NumberConverter.deserializeLong(reader));
    }

    public static JsValue deserializeNullable(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserialize(reader);
    }

    public static JsValue deserializeArray(final JsonReader<?> reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        return JsArray.from(NumberConverter.deserializeLongArray(reader));
    }

    public static JsValue deserializeNullableArrayOf(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeArray(reader);
    }


    public static JsValue deserializeArrayOfNullable(final JsonReader<?> reader) throws IOException
    {
        return JsArray.fromNullableLong(NumberConverter.deserializeLongNullableCollection(reader));
    }

    public static JsValue deserializeNullableArrayOfOfNullable(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeArrayOfNullable(reader);
    }


}
