package com.dslplatform.json;


import scala.collection.immutable.Vector;
import scala.collection.immutable.Vector$;
import value.*;

import java.io.IOException;
import java.math.BigDecimal;

import static com.dslplatform.json.DslJsConfiguration.objDeserializer;
import static com.dslplatform.json.JsNumberFns.toScalaBigDec;


public class JsNumberDeserializer
{

    public static JsDouble deserializeDouble(final JsonReader<?> reader) throws IOException
    {
        return new JsDouble(NumberConverter.deserializeDouble(reader));
    }

    public static JsValue deserializeNullOrDouble(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeDouble(reader);
    }



    public static JsArray deserializeDoubleArray(final JsonReader<?> reader) throws IOException
    {
        return JsArray.from(NumberConverter.deserializeDoubleArray(reader));
    }

    public static JsArray deserializeDoubleNullableArray(final JsonReader<?> reader) throws IOException
    {
        return JsArray.fromNullableDouble(NumberConverter.deserializeDoubleNullableCollection(reader));
    }







    public static JsLong deserializeLong(final JsonReader<?> reader) throws IOException
    {
        return new JsLong(NumberConverter.deserializeLong(reader));
    }

    public static JsValue deserializeNullableLong(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeLong(reader);
    }

    public static JsArray deserializeLongArray(final JsonReader<?> reader) throws IOException
    {
        return JsArray.from(NumberConverter.deserializeLongArray(reader));
    }


    public static JsArray deserializeLongNullableArray(final JsonReader<?> reader) throws IOException
    {
        return JsArray.fromNullableLong(NumberConverter.deserializeLongNullableCollection(reader));
    }


    public static JsBigDec deserializeDecimal(final JsonReader<?> reader) throws IOException
    {
        return toScalaBigDec(NumberConverter.deserializeDecimal(reader));
    }

    public static JsValue deserializeNullalbleDecimal(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeDecimal(reader);
    }

    public static JsArray deserializeDecimalArray(final JsonReader<?> reader) throws IOException
    {
        return JsArray.fromDecimals(NumberConverter.deserializeDecimalCollection(reader));
    }

    public static JsArray deserializeDecimalNullableArray(final JsonReader<?> reader) throws IOException
    {
        return JsArray.fromNullableDecimal(NumberConverter.deserializeDecimalNullableCollection(reader));
    }

    public static JsValue deserializeNullableNumber(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : deserializeNumber(reader);
    }


    public static JsNumber deserializeNumber(final JsonReader<?> reader) throws IOException
    {
        final Number number = NumberConverter.deserializeNumber(reader);
        if (number instanceof Double)
        {
            return new JsDouble(((double) number));
        } else if (number instanceof Long)
        {
            return new JsLong(((Long) number));
        } else if (number instanceof BigDecimal)
        {
            return toScalaBigDec(((BigDecimal) number));
        }
        throw new RuntimeException("internal error: not condisered " + number.getClass());
    }


    public static JsArray deserializeNumberArray(final JsonReader reader) throws IOException
    {
        if (reader.last() == ']')
        {
            return JsArray$.MODULE$.empty();
        }
        JsArray buffer = JsArray$.MODULE$.empty();
        buffer = buffer.appended(deserializeNumber(reader));

        while (reader.getNextToken() == ',')
        {
            reader.getNextToken();
            buffer = buffer.appended(deserializeNumber(reader));
        }
        reader.checkArrayEnd();
        return buffer;
    }

}
