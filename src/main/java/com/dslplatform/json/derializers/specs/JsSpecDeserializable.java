package com.dslplatform.json.derializers.specs;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.derializers.types.JsTypeDeserializer;
import scala.collection.immutable.HashMap;
import value.*;
import java.util.function.Function;

 abstract class JsSpecDeserializable extends JsTypeDeserializer
{
    protected final HashMap<String, Function<JsonReader<?>, JsValue>> deserializers;

    public JsSpecDeserializable(final HashMap<String, Function<JsonReader<?>, JsValue>> deserializers)
    {
        this.deserializers = deserializers;
    }
}
