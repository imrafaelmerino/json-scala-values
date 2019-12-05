package com.dslplatform.json;


import value.JsBigDec;
import value.JsBigInt;

import java.math.BigDecimal;

class JsNumberFns
{
    static JsBigDec toScalaBigDec(BigDecimal bd)
    {
        return new JsBigDec(new scala.math.BigDecimal(bd));
    }

    static JsBigInt toScalaBigInt(BigDecimal bd)
    {
        return new JsBigInt(new scala.math.BigInt(bd.toBigIntegerExact()));
    }
}
