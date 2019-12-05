package com.dslplatform.json;

import value.*;

import java.io.IOException;

abstract class JsPrimitiveDeserializer
{

    abstract JsValue deserialize(final JsonReader<?> reader) throws IOException;

    public JsValue deserializeNullable(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserialize(reader);
    }

    public JsValue deserializeNullableArray(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeArray(reader);
    }

    public JsValue deserializeArrayOfNullable(final JsonReader<?> reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        JsArray buffer = JsArray$.MODULE$.empty();
        if (reader.wasNull())
        {
            buffer = buffer.appended(JsNull$.MODULE$);
        } else
        {
            buffer = buffer.appended(deserialize(reader));
        }
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            if (reader.wasNull())
            {
                buffer = buffer.appended(JsNull$.MODULE$);
            } else
            {
                buffer = buffer.appended(deserialize(reader));
            }
        }
        reader.checkArrayEnd();
        return buffer;
    }

    public JsValue deserializeNullableArrayOfNullable(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeArrayOfNullable(reader);
    }

    public JsArray deserializeArray(final JsonReader reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        reader.getNextToken();
        if (reader.last() == ']')
        {
            return JsArray$.MODULE$.empty();
        }
        JsArray buffer = JsArray$.MODULE$.empty();
        buffer = buffer.appended(deserialize(reader));
        int i = 1;
        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            buffer = buffer.appended(deserialize(reader));
        }
        reader.checkArrayEnd();
        return buffer;
    }

}
