package json.value.specs

import java.util.concurrent.TimeUnit
import org.scalatest.FlatSpec
import json.value.{JsArray, JsBigDec, JsBigInt, JsBool, JsObj, JsStr}
import json.value.Preamble._
import json.value.future.Preamble._
import json.value.future.{JsArrayFuture, JsObjFuture}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions
import scala.util.Try

class JsObjFutureSpecSpec extends FlatSpec
{
  "json.value implicit conversion" should "kick in" in
  {

    val future: Future[JsObj] = JsObjFuture("a" -> 1,
                                            "b" -> "hi",
                                            "c" -> JsObj.empty,
                                            "e" -> false,
                                            "f" -> 1.5,
                                            "g" -> 10L,
                                            "h" -> JsArray.empty,
                                            "i" -> BigInt(10),
                                            "j" -> BigDecimal(1.5)
                                            )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsObj("a" -> 1,
            "b" -> "hi",
            "c" -> JsObj.empty,
            "e" -> false,
            "f" -> 1.5,
            "g" -> 10L,
            "h" -> JsArray.empty,
            "i" -> BigInt(10),
            "j" -> BigDecimal(1.5)
            )
      )
  }
  "try implicit conversions" should "kick in" in
  {
    val future: Future[JsObj] = JsObjFuture("a" -> Try(1),
                                            "b" -> Try("hi"),
                                            "c" -> Try(JsObj.empty),
                                            "e" -> Try(false),
                                            "f" -> Try(1.5),
                                            "g" -> Try(10L),
                                            "h" -> Try(JsArray.empty),
                                            "i" -> Try(BigInt(10)),
                                            "j" -> Try(BigDecimal(1.5)),
                                            "i" -> Try(JsStr("a"))
                                            )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsObj("a" -> 1,
            "b" -> "hi",
            "c" -> JsObj.empty,
            "e" -> false,
            "f" -> 1.5,
            "g" -> 10L,
            "h" -> JsArray.empty,
            "i" -> BigInt(10),
            "j" -> BigDecimal(1.5),
            "i" -> "a"
            )
      )
  }
  "future implicit conversions" should "kick in" in
  {
    val future = JsObjFuture("a" -> Future
    {"a"},
                             "b" -> Future
                             {1},
                             "c" -> Future
                             {false},
                             "e" -> Future({10L}),
                             "f" -> Future({1.5}),
                             "g" -> Future({BigInt(10)}),
                             "h" -> Future({BigDecimal(1.5)}),
                             "i" -> Future({JsObj.empty}),
                             "j" -> Future({JsArray.empty}),
                             )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsObj(
        "a" -> "a",
        "b" -> 1,
        "c" -> false,
        "e" -> 10L,
        "f" -> 1.5,
        "g" -> BigInt(10),
        "h" -> BigDecimal(1.5),
        "i" -> JsObj.empty,
        "j" -> JsArray.empty,
        )
      )
  }
  "try and future implicit conversions" should "kick in" in
  {
    val future: Future[JsObj] = JsObjFuture("a" -> Future({1}),
                                            "b" -> Try({"hi"}),
                                            "c" -> Future({JsObj.empty}),
                                            "e" -> JsBool(false),
                                            "f" -> Future({1.5}),
                                            "g" -> Try({10L}),
                                            "h" -> JsArray.empty,
                                            "i" -> JsBigInt(10),
                                            "j" -> JsBigDec(1.5),
                                            "k" -> JsArrayFuture(Future({1}),
                                                                 Try({"hi"})
                                                                 )
                                            )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsObj("a" -> 1,
            "b" -> "hi",
            "c" -> JsObj.empty,
            "e" -> false,
            "f" -> 1.5,
            "g" -> 10L,
            "h" -> JsArray.empty,
            "i" -> BigInt(10),
            "j" -> BigDecimal(1.5),
            "k" -> JsArray(1,
                           "hi"
                           )
            )
      )
  }
}
