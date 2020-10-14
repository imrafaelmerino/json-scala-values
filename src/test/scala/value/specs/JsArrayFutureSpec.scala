package value.specs

import java.util.concurrent.TimeUnit
import org.scalatest.FlatSpec
import value.{JsArray, JsBigDec, JsBigInt, JsBool, JsNull, JsObj, JsStr}
import value.Preamble._
import value.future.JsArrayFuture
import value.future.Preamble._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions
import scala.util.Try

class JsArrayFutureSpec extends FlatSpec
{

  "primitive implicits conversion" should "kick in" in
  {
    val future: Future[JsArray] = JsArrayFuture(1,
                                                true,
                                                1.5,
                                                false,
                                                10L,
                                                BigInt(1),
                                                BigDecimal(1.5),
                                                JsObj.empty,
                                                JsArray.empty,
                                                "a",
                                                JsNull
                                                )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsArray(1,
              true,
              1.5,
              false,
              10L,
              BigInt(1),
              BigDecimal(1.5),
              JsObj.empty,
              JsArray.empty,
              "a",
              JsNull
              )
      )
  }
  "try implicit conversion" should "kick in" in
  {
    val future: Future[JsArray] = JsArrayFuture(Try(1),
                                                Try("hi"),
                                                Try(JsObj.empty),
                                                Try(false),
                                                Try(1.5),
                                                Try(10L),
                                                Try(JsArray.empty),
                                                Try(BigInt(10)),
                                                Try(BigDecimal(1.5)),
                                                Try(JsStr("a"))
                                                )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsArray(1,
              "hi",
              JsObj.empty,
              false,
              1.5,
              10L,
              JsArray.empty,
              BigInt(10),
              BigDecimal(1.5),
              "a"
              )
      )
  }
  "future implicit conversion" should "kick in" in
  {
    val future = JsArrayFuture(Future
                               {"a"},
                               Future
                               {1},
                               Future
                               {false},
                               Future({10L}),
                               Future({1.5}),
                               Future({BigInt(10)}),
                               Future({BigDecimal(1.5)}),
                               Future({JsObj.empty}),
                               Future({JsArray.empty}),
                               )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsArray(
        "a",
        1,
        false,
        10L,
        1.5,
        BigInt(10),
        BigDecimal(1.5),
        JsObj.empty,
        JsArray.empty,
        )
      )
  }
  "future and try implicit conversions" should "kick in" in
  {
    val future: Future[JsArray] = JsArrayFuture(Future({1}),
                                                Try({"hi"}),
                                                Future({JsObj.empty}),
                                                JsBool(false),
                                                Future({1.5}),
                                                Try({10L}),
                                                JsArray.empty,
                                                JsBigInt(10),
                                                JsBigDec(1.5)
                                                )


    assert(
      Await.result(future,
                   Duration(10,
                            TimeUnit.SECONDS
                            )
                   ) ==
      JsArray(1,
              "hi",
              JsObj.empty,
              false,
              1.5,
              10L,
              JsArray.empty,
              BigInt(10),
              BigDecimal(1.5)
              )
      )
  }
}
