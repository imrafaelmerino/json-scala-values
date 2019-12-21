package com.dslplatform.json.derializers.types;

import com.dslplatform.json.JsonReader;
import value.*;

import java.io.IOException;

public class JsBoolDeserializer extends JsTypeDeserializer
{

    public JsBool value(final JsonReader reader) throws IOException
    {
        if (reader.wasTrue()) return TRUE$.MODULE$;
        else if (reader.wasFalse()) return FALSE$.MODULE$;
        throw reader.newParseErrorAt("Found invalid boolean value",
                                     0
                                    );
    }


    public JsBool True(final JsonReader reader) throws IOException
    {
        if (reader.wasTrue()) return TRUE$.MODULE$;
        throw reader.newParseErrorAt("Found invalid boolean value. True was expected.",
                                     0
                                    );
    }

    public JsValue nullOrTrue(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : True(reader);
    }


    public JsBool False(final JsonReader reader) throws IOException
    {
        if (reader.wasFalse()) return FALSE$.MODULE$;
        throw reader.newParseErrorAt("Found invalid boolean value. False was expected.",
                                     0
                                    );
    }

    public JsValue nullOrFalse(final JsonReader<?> reader) throws IOException
    {
        return reader.wasNull() ? JsNull$.MODULE$ : False(reader);
    }


}
