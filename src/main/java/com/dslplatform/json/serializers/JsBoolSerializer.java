package com.dslplatform.json.serializers;

import com.dslplatform.json.BoolConverter;
import com.dslplatform.json.JsonWriter;
import value.JsArray;
import value.JsBool;

public class JsBoolSerializer
{

    public static void serialize(final JsBool bool,
                                 final JsonWriter sw
                                )
    {
        BoolConverter.serialize(bool.value(),
                                sw
                               );
    }


    public static void serialize(final JsArray value,
                                 final JsonWriter sw
                                )
    {
        if (value.isEmpty())
        {
            sw.writeAscii("[]");
        } else
        {
            sw.writeByte(JsonWriter.ARRAY_START);
            serialize(value.apply(0)
                           .asJsBool(),
                      sw
                     );
            for (int i = 1; i < value.size(); i++)
            {
                serialize(value.apply(i)
                               .asJsBool(),
                          sw
                         );
            }
            sw.writeByte(JsonWriter.ARRAY_END);
        }
    }

}
