package com.dslplatform.json.serializers;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import value.*;

public class JsNumberSerializer
{

    public static void serializeInt(JsonWriter sw,
                                    JsInt n
                                   )
    {
        NumberConverter.serialize(n.value(),
                                  sw
                                 );
    }


    public static void serializeIntArray(JsonWriter sw,
                                         JsArray value
                                        )
    {
        if (value.isEmpty())
        {
            sw.writeAscii("[]");
        } else
        {
            sw.writeByte(JsonWriter.ARRAY_START);
            serializeInt(sw,
                         value.apply(0)
                              .asJsInt()
                        );
            for (int i = 1; i < value.size(); i++)
            {
                sw.writeByte(JsonWriter.COMMA);
                serializeInt(sw,
                             value.apply(i)
                                  .asJsInt()
                            );
            }
            sw.writeByte(JsonWriter.ARRAY_END);
        }
    }


    public static void serializeLong(JsonWriter sw,
                                     JsLong n
                                    )
    {
        NumberConverter.serialize(n.value(),
                                  sw
                                 );
    }


    public static void serializeLongArray(JsonWriter sw,
                                          JsArray value
                                         )
    {
        if (value.isEmpty())
        {
            sw.writeAscii("[]");
        } else
        {
            sw.writeByte(JsonWriter.ARRAY_START);
            serializeLong(sw,
                          value.apply(0)
                               .asJsLong()
                         );
            for (int i = 1; i < value.size(); i++)
            {
                sw.writeByte(JsonWriter.COMMA);
                serializeLong(sw,
                              value.apply(i)
                                   .asJsLong()
                             );
            }
            sw.writeByte(JsonWriter.ARRAY_END);
        }
    }


    public static void serializeDouble(JsonWriter sw,
                                       JsDouble n
                                      )
    {
        NumberConverter.serialize(n
                                  .value(),
                                  sw
                                 );
    }


    public static void serializeDoubleArray(JsonWriter sw,
                                            JsArray value
                                           )
    {
        if (value.isEmpty())
        {
            sw.writeAscii("[]");
        } else
        {
            sw.writeByte(JsonWriter.ARRAY_START);
            serializeDouble(sw,
                            value.apply(0)
                                 .asJsDouble()
                           );
            for (int i = 1; i < value.size(); i++)
            {
                sw.writeByte(JsonWriter.COMMA);
                serializeDouble(sw,
                                value.apply(i)
                                     .asJsDouble()
                               );
            }
            sw.writeByte(JsonWriter.ARRAY_END);
        }
    }


    public static void serializeDecimal(JsonWriter sw,
                                        JsValue n
                                       )
    {
        NumberConverter.serialize(n.asJsBigDec()
                                   .value()
                                   .bigDecimal(),
                                  sw
                                 );
    }

    public static void serializeNull(JsonWriter sw
                                    )
    {
        sw.writeNull();
    }

    public static void serializeDecimalArray(JsonWriter sw,
                                             JsArray value
                                            )
    {
        if (value.isEmpty())
        {
            sw.writeAscii("[]");
        } else
        {
            sw.writeByte(JsonWriter.ARRAY_START);
            serializeDecimal(sw,
                             value.apply(0)
                                  .asJsDouble()
                            );
            for (int i = 1; i < value.size(); i++)
            {
                sw.writeByte(JsonWriter.COMMA);
                serializeDecimal(sw,
                                 value.apply(i)
                                );
            }
            sw.writeByte(JsonWriter.ARRAY_END);
        }
    }
}
