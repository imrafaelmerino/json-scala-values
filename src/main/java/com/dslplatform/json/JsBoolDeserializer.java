package com.dslplatform.json;

import value.*;

import java.io.IOException;

public class JsBoolDeserializer extends JsPrimitiveDeserializer
{

    public JsBool deserialize(final JsonReader reader) throws IOException
    {
        if (reader.wasTrue()) return TRUE$.MODULE$;
        else if (reader.wasFalse()) return FALSE$.MODULE$;
        throw reader.newParseErrorAt("Found invalid boolean value",
                                     0
                                    );
    }

    public JsBool deserializeTrue(final JsonReader reader) throws IOException
    {
        if (reader.wasTrue()) return TRUE$.MODULE$;
        throw reader.newParseErrorAt("Found invalid boolean value. True was expected.",
                                     0
                                    );
    }


    public JsBool deserializeFalse(final JsonReader reader) throws IOException
    {
        if (reader.wasFalse()) return FALSE$.MODULE$;
        throw reader.newParseErrorAt("Found invalid boolean value. False was expected.",
                                     0
                                    );
    }


}
