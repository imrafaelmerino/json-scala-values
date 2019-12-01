package com.dslplatform.json;


import value.JsBigDec;

import java.math.BigDecimal;

class JsNumberFns
{
    static JsBigDec toScalaBigDec(BigDecimal bd)
    {
        return new JsBigDec(new scala.math.BigDecimal(bd));
    }

}
