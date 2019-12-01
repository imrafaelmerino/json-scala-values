package com.dslplatform.json;

import value.JsArray;
import value.JsStr;

public class JsStrSerializer
{
    public static void serialize(final JsStr value,
                                 final JsonWriter sw
                                )
    {
        StringConverter.serialize(value.value(),
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
                           .asJsStr(),
                      sw
                     );
            for (int i = 1; i < value.size(); i++)
            {
                serialize(value.apply(i)
                               .asJsStr(),
                          sw
                         );
            }
            sw.writeByte(JsonWriter.ARRAY_END);
        }
    }
}
