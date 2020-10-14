package com.dslplatform.json.derializers.arrays;

import com.dslplatform.json.derializers.types.JsBoolDeserializer;

import java.util.Objects;

public final class JsArrayOfBoolDeserializer extends JsArrayDeserializer
{

    public JsArrayOfBoolDeserializer(final JsBoolDeserializer deserializer)
    {
        super(Objects.requireNonNull(deserializer));
    }





}
