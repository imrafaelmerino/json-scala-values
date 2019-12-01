package com.dslplatform.json;

import value.*;

import java.io.IOException;

abstract class AbstractJsDeserializer
{


    JsValue deserializeObject(final JsonReader<?> reader,
                              final JsonReader.ReadObject<? extends JsObj> objReader,
                              final JsonReader.ReadObject<? extends JsArray> arrayReader
                             ) throws IOException
    {
        switch (reader.last())
        {
            case 'n':
                if (!reader.wasNull())
                {
                    throw reader.newParseErrorAt("Expecting 'null' for null constant",
                                                 0
                                                );
                }
                return JsNull$.MODULE$;
            case 't':
                if (!reader.wasTrue())
                {
                    throw reader.newParseErrorAt("Expecting 'true' for true constant",
                                                 0
                                                );
                }
                return TRUE$.MODULE$;
            case 'f':
                if (!reader.wasFalse())
                {
                    throw reader.newParseErrorAt("Expecting 'false' for false constant",
                                                 0
                                                );
                }
                return FALSE$.MODULE$;
            case '"':
                return new JsStr(reader.readString());
            case '{':
                return objReader.read(reader);
            case '[':
                return arrayReader.read(reader);
            default:
                return JsNumberDeserializer.deserializeNumber(reader);
        }
    }
}
