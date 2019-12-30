package com.dslplatform.json.derializers.specs;

import com.dslplatform.json.derializers.arrays.JsArrayDeserializer;

import java.util.Objects;

public final  class JsArrayOfObjSpecDeserializer extends JsArrayDeserializer
{

    public JsArrayOfObjSpecDeserializer(final JsObjSpecDeserializer deserializer)
    {
        super(Objects.requireNonNull(deserializer));
    }








}
