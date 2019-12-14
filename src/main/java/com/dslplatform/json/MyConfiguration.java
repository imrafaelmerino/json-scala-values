package com.dslplatform.json;

import com.dslplatform.json.serializers.JsArraySerializer;
import com.dslplatform.json.serializers.JsNumberSerializer;
import com.dslplatform.json.serializers.JsObjSerializer;
import value.*;

import static java.util.Objects.requireNonNull;

public class MyConfiguration implements Configuration
{
    private static final JsonWriter.WriteObject<JsStr> JSON_STR_WRITER =
    (writer, value) -> writer.writeString(requireNonNull(value).value());

    private static final JsonWriter.WriteObject<JsLong> JSON_LONG_WRITER =
    (writer, value) -> JsNumberSerializer.serializeLong(writer,
                                                        requireNonNull(value)
                                                       );

    private static final JsonWriter.WriteObject<JsInt> JSON_INT_WRITER =
    (writer, value) -> JsNumberSerializer.serializeInt(writer,
                                                       requireNonNull(value)

                                                      );

    private static final JsonWriter.WriteObject<JsBigDec> JSON_BIGDEC_WRITER =
    (writer, value) -> JsNumberSerializer.serializeDecimal(writer,
                                                           requireNonNull(value)

                                                          );

    private static final JsonWriter.WriteObject<JsBigInt> JSON_BIGINT_WRITER =
    (writer, value) -> writer.writeAscii(requireNonNull(value).value()
                                                              .bigInteger()
                                                              .toString());


    private static final JsonWriter.WriteObject<value.JsNull$> JSON_NULL_WRITER =
    (writer, value) -> writer.writeNull();

    private static final JsonWriter.WriteObject<JsBool> JSON_BOOL_WRITER =
    (writer, value) -> writer.writeAscii(Boolean.toString(requireNonNull(value).value()));

    private static final JsonWriter.WriteObject<JsDouble> JSON_DOUBLE_WRITER =
    (writer, value) -> JsNumberSerializer.serializeDecimal(writer,
                                                           requireNonNull(value)

                                                          );


    private static final JsonWriter.WriteObject<JsObj> objSerializer = new JsObjSerializer<>();
    private static final JsonWriter.WriteObject<JsArray> arraySerializer = new JsArraySerializer<>();

    @Override
    @SuppressWarnings("rawtypes")//can add generic type, method form a third-party interface
    public void configure(final DslJson json)
    {

        final DslJson<?> a = requireNonNull(json);

        a.registerWriter(JsBool.class,
                         JSON_BOOL_WRITER
                        );
        a.registerWriter(JsBigInt.class,
                         JSON_BIGINT_WRITER
                        );
        a.registerWriter(value.JsNull$.class,
                         JSON_NULL_WRITER
                        );
        a.registerWriter(JsBigDec.class,
                         JSON_BIGDEC_WRITER
                        );
        a.registerWriter(JsInt.class,
                         JSON_INT_WRITER
                        );
        a.registerWriter(JsLong.class,
                         JSON_LONG_WRITER
                        );
        a.registerWriter(JsStr.class,
                         JSON_STR_WRITER
                        );

        a.registerWriter(JsDouble.class,
                         JSON_DOUBLE_WRITER
                        );

        a.registerWriter(JsObj.class,
                         objSerializer
                        );

        a.registerWriter(JsArray.class,
                         arraySerializer
                        );
    }
}


