package com.dslplatform.json;

public class CustomDslJson<TContext> extends DslJson<TContext>
{

    public JsonReader getReader(byte[] bytes
                               )
    {
        return localReader.get()
                          .process(bytes,
                                   bytes.length
                                  );
    }
}
