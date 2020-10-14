package com.dslplatform.json.serializers;

import com.dslplatform.json.JsonWriter;
import scala.collection.immutable.Seq;
import value.JsArray;
import value.JsValue;

public final class JsArraySerializer implements JsonWriter.WriteObject<JsArray>
{
    private JsValueSerializer valueSerializer;

    public JsArraySerializer(final JsValueSerializer valueSerializer)
    {
        this.valueSerializer = valueSerializer;
    }

    @Override
    public void write(final JsonWriter writer,
                      final JsArray list
                     )
    {
        writer.writeByte(JsonWriter.ARRAY_START);
        final Seq<JsValue> seq = list.values();
        final int size = seq.size();
        if (size != 0)
        {
            final JsValue first = seq.apply(0);
            valueSerializer.serialize(writer,
                                      first
                                     );
            for (int i = 1; i < size; i++)
            {
                writer.writeByte(JsonWriter.COMMA);
                final JsValue value = seq.apply(i);
                valueSerializer.serialize(writer,
                                          value
                                         );
            }
        }
        writer.writeByte(JsonWriter.ARRAY_END);
    }


}
