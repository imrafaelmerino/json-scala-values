package com.dslplatform.json;

import scala.collection.immutable.Vector;
import scala.collection.immutable.Vector$;
import value.JsArray;
import value.JsArray$;
import value.JsValue;

import java.io.IOException;

import static com.dslplatform.json.DslJsConfiguration.objDeserializer;


class JsArrayDeserializer extends AbstractJsDeserializer implements JsonReader.ReadObject<JsArray>
{
    private JsArray deserializeArray(final JsonReader<?> reader) throws IOException
    {
        if (reader.last() != '[') throw reader.newParseError("Expecting '[' for list start");
        byte nextToken = reader.getNextToken();
        if (nextToken == ']') return JsArray$.MODULE$.empty();
        Vector<JsValue> res = Vector$.MODULE$.empty();
        res = (Vector<JsValue>) res.$colon$plus(deserializeObject(reader,
                                                                  objDeserializer,
                                                                  this
                                                                 ));
        while ((nextToken = reader.getNextToken()) == ',')
        {
            reader.getNextToken();
            res = (Vector<JsValue>) res.$colon$plus(deserializeObject(reader,
                                                                      objDeserializer,
                                                                      this
                                                                     ));
        }
        if (nextToken != ']') throw reader.newParseError("Expecting ']' for list end");
        return new JsArray(res);
    }


    @Override
    @SuppressWarnings("rawtypes")//method from third party library, not possible add <>
    public JsArray read(final JsonReader reader) throws IOException
    {
        return deserializeArray(reader);
    }



}
