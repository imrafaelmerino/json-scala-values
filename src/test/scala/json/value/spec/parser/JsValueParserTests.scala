package json.value.spec.parser

import com.github.plokhotnyuk.jsoniter_scala.core.*
import json.value.*
import json.value.gen.JsObjGen
import json.value.spec.codec.*
import json.value.spec.parser.*
import json.value.spec.*
import org.scalacheck.Gen
import org.scalatest.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

class JsValueParsersTests extends AnyFlatSpec with should.Matchers {

  "parsing an obj" should "return a JsObj" in {
    val codec = JsObjCodec(JsObjSpecParser(Map(
      "a" -> JsValueParser.DEFAULT), true,List.empty))

    val a = readFromString("{\n  \"a\": 1 }")(codec)
    a("a") should be(JsInt(1))

    val b = readFromString("{\n  \"a\": true}")(codec)
    b("a") should be(JsBool.TRUE)

    val c = readFromString("{\n  \"a\": false}")(codec)
    c("a") should be(JsBool.FALSE)

    val d = readFromString("{\n  \"a\": null}")(codec)
    d("a") should be(JsNull)

    val e = readFromString("{\n  \"a\": \"null\"}")(codec)
    e("a") should be(JsStr("null"))

    val f = readFromString("{\n  \"a\": 1.5}")(codec)
    f("a") should be(JsBigDec(1.5))

    val g = readFromString("{\n  \"a\": 100000000000000000000000000}")(codec)
    g("a") should be(JsBigInt(BigInt("100000000000000000000000000")))

    val h = readFromString("{\n  \"a\": [1,true,null,false,1.5]}")(codec)
    h("a") should be(JsArray(JsInt(1),JsBool.TRUE,JsNull,JsBool.FALSE,JsBigDec(1.5)))

    val i = readFromString("{\n  \"a\": {\n  \"a\": [1,true,null,false,1.5]}}")(codec)
    i("a") should be(JsObj("a" -> JsArray(JsInt(1),JsBool.TRUE,JsNull,JsBool.FALSE,JsBigDec(1.5))))

    val str = "{\n  \"a\": {\n  \"a\": {\n  \"a\": {\n  \"a\": [1,true,null,false,1.5]}}}}"
    val j = JsObj("a"-> JsObj("a"-> JsObj("a"-> JsObj("a"->JsArray(JsInt(1),JsBool.TRUE,JsNull,JsBool.FALSE,JsBigDec(1.5))))))
    readFromString(str)(codec) should be(j)

  }
 

}
