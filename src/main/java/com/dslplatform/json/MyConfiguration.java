package com.dslplatform.json;

import com.dslplatform.json.serializers.JsArraySerializer;
import com.dslplatform.json.serializers.JsObjSerializer;
import com.dslplatform.json.serializers.JsValueSerializer;
import value.JsArray;
import value.JsObj;

import static java.util.Objects.requireNonNull;

public class MyConfiguration implements Configuration
{

    private static final JsValueSerializer valueSerializer = new JsValueSerializer();
    private static final JsonWriter.WriteObject<JsObj> objSerializer = new JsObjSerializer<>(valueSerializer);
    private static final JsonWriter.WriteObject<JsArray> arraySerializer = new JsArraySerializer<>(valueSerializer);
    static{
        valueSerializer.setArraySerializer(arraySerializer);
        valueSerializer.setObjectSerializer(objSerializer);
    }


    @Override
    @SuppressWarnings("rawtypes")//can add generic type, method form a third-party interface
    public void configure(final DslJson json)
    {

        final DslJson<?> a = requireNonNull(json);

        a.registerWriter(JsObj.class,
                         objSerializer
                        );

        a.registerWriter(JsArray.class,
                         arraySerializer
                        );
    }
}


