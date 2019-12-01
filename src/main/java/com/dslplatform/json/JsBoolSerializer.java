package com.dslplatform.json;

import value.JsArray;
import value.JsBool;
import value.JsValue;

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
