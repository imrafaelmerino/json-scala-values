package com.dslplatform.json;

import scala.Tuple2;
import scala.collection.Iterator;
import value.JsObj;
import value.JsValue;


class JsObjSerializer<T extends JsObj> implements JsonWriter.WriteObject<T>
{


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
            sw.serializeObject(kv._2);
            for (int i = 1; i < size; i++)
            {
                sw.writeByte(JsonWriter.COMMA);
                kv = iterator.next();
                sw.writeString(kv._1);
                sw.writeByte(JsonWriter.SEMI);
                sw.serializeObject(kv._2);
            }
        }
        sw.writeByte(JsonWriter.OBJECT_END);
    }
}
