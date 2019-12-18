package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.HashMap$;
import value.*;

import java.io.IOException;
import java.math.BigDecimal;

public abstract class JsTypeDeserializer
{

    public final static HashMap<String, JsValue> EMPTY_MAP = HashMap$.MODULE$.empty();
    public final static JsObj EMPTY_OBJ = JsObj$.MODULE$.empty();

    public abstract JsValue value(final JsonReader<?> reader) throws IOException;

    public JsValue nullOrValue(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : value(reader);
    }

    public JsBigDec toScalaBigDec(BigDecimal bd)
    {
        return new JsBigDec(new scala.math.BigDecimal(bd));
    }


    public JsBigInt toScalaBigInt(BigDecimal bd)
    {
        return new JsBigInt(new scala.math.BigInt(bd.toBigIntegerExact()));
    }

    protected boolean isEmptyObj(final JsonReader<?> reader) throws IOException
    {
        if (reader.last() != '{') throw reader.newParseError("Expecting '{' for map start");
        byte nextToken = reader.getNextToken();
        if (nextToken == '}') return true;
        return false;
    }

}
