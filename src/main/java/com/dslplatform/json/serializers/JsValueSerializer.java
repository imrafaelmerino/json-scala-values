package com.dslplatform.json.serializers;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import json.value.JsArray;
import json.value.JsObj;
import json.value.JsValue;

import java.util.Objects;


public final class JsValueSerializer
{

    private JsonWriter.WriteObject<JsObj> objectSerializer;
    private JsonWriter.WriteObject<JsArray> arraySerializer;

    public void setObjectSerializer(final JsonWriter.WriteObject<JsObj> objectSerializer)
    {
        this.objectSerializer = Objects.requireNonNull(objectSerializer);
    }

    public void setArraySerializer(final JsonWriter.WriteObject<JsArray> arraySerializer)
    {
        this.arraySerializer = Objects.requireNonNull(arraySerializer);
    }

    void serialize(final JsonWriter writer,
                   final JsValue value
                  )
    {

        switch (value.id())
        {
            case 0:
            {
                writer.writeAscii(Boolean.toString(value.toJsBool()
                                                        .value()));
                break;
            }
            case 1:
            {
                writer.writeNull();
                break;
            }
            case 2:
            {
                writer.writeString(value.toJsStr()
                                        .value());
                break;
            }
            case 3:
            {
                objectSerializer.write(writer,
                                       value.toJsObj()
                                      );
                break;
            }
            case 4:
            {
                arraySerializer.write(writer,
                                      value.toJsArray()
                                     );
                break;
            }
            case 5:
            case 8:
            {
                NumberConverter.serialize(value.toJsBigDec()
                                               .value()
                                               .bigDecimal(),
                                          writer
                                         );
                break;
            }
            case 6:
            {
                writer.writeAscii(value.toJsBigInt()
                                       .value()
                                       .bigInteger()
                                       .toString());

                break;
            }
            case 7:
            {
                NumberConverter.serialize(value.toJsLong()
                                               .value(),
                                          writer
                                         );
                break;
            }
            case 9:
            {
                NumberConverter.serialize(value.toJsInt()
                                               .value(),
                                          writer
                                         );
                break;
            }

            default:
                throw new IllegalStateException("JsValue.id() not considered. Default branch of a switch statement was executed.");

        }

    }
}
