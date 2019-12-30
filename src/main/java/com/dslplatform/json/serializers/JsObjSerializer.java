package com.dslplatform.json.serializers;

import com.dslplatform.json.JsonWriter;
import scala.Tuple2;
import scala.collection.Iterator;
import value.JsObj;
import value.JsValue;

public final class JsObjSerializer<T extends JsObj> implements JsonWriter.WriteObject<T>
{

    private JsValueSerializer valueSerializer;

    public JsObjSerializer(final JsValueSerializer valueSerializer)
    {
        this.valueSerializer = valueSerializer;
    }

    @Override
    public void write(final JsonWriter sw,
                      final T value
                     )
    {
        sw.writeByte(JsonWriter.OBJECT_START);
        final int size = value.size();
        if (size > 0)
        {
            final Iterator<Tuple2<String, JsValue>> iterator = value.map()
                                                                    .iterator();
            Tuple2<String, JsValue> kv = iterator.next();
            sw.writeString(kv._1);
            sw.writeByte(JsonWriter.SEMI);
            final JsValue fist = kv._2;
            valueSerializer.serialize(sw,
                                      fist
                                     );

            for (int i = 1; i < size; i++)
            {
                sw.writeByte(JsonWriter.COMMA);
                kv = iterator.next();
                sw.writeString(kv._1);
                sw.writeByte(JsonWriter.SEMI);
                final JsValue keyValue = kv._2;
                valueSerializer.serialize(sw,
                                          keyValue
                                         );
            }
        }
        sw.writeByte(JsonWriter.OBJECT_END);
    }
}
