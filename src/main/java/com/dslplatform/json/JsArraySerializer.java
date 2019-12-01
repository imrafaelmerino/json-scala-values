package com.dslplatform.json;

import value.JsArray;

class JsArraySerializer<T extends JsArray> implements JsonWriter.WriteObject<T>
{
    @Override
    public void write(final JsonWriter writer,
                      final T list
                     )
    {
        writer.writeByte(JsonWriter.ARRAY_START);
        if (list.size() != 0)
        {
            writer.serializeObject(list.head());
            for (int i = 1; i < list.size(); i++)
            {
                writer.writeByte(JsonWriter.COMMA);
                writer.serializeObject(list.apply(i));
            }
        }
        writer.writeByte(JsonWriter.ARRAY_END);
    }


}
