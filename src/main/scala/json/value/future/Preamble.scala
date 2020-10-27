package json.value.future

import json.value.{JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNull, JsObj, JsPath, JsStr, JsValue}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try
import JsPath.empty
import Future.successful
import Future.failed

object Preamble
{

  private type EC = ExecutionContext

  given keyTryValue2PathFut(using EC) as Conversion[(String, Try[JsValue]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(a)
                                          ))

  given keyTryStr2PathFut(using EC) as Conversion[(String, Try[String]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsStr(a))
                                          ))

  given keyTryInt2PathFut(using EC) as Conversion[(String, Try[Int]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsInt(a))
                                          ))

  given keyTryLong2PathFut(using EC) as Conversion[(String, Try[Long]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsLong(a))
                                          ))

  given keyTryDouble2PathFut(using EC) as Conversion[(String, Try[Double]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsDouble(a))
                                          ))

  given keyTryBigDec2PathFut(using EC) as Conversion[(String, Try[BigDecimal]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsBigDec(a))
                                          ))

  given keyTryBigInt2PathFut(using EC) as Conversion[(String, Try[BigInt]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsBigInt(a))
                                          ))

  given keyTryBool2PathFut(using EC) as Conversion[(String, Try[Boolean]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(JsBool(a))
                                          ))

  given keyTryObjPathFut(using EC) as Conversion[(String, Try[JsObj]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(a)
                                          ))

  given keyTryArr2PathFut(using EC) as Conversion[(String, Try[JsArray]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.fold(e => failed(e),
                                          a => successful(a)
                                          ))

  given tryStr2Fut(using EC) as Conversion[Try[String], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsStr(a))
                )

  given tryInt2Fut(using EC) as Conversion[Try[Int], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsInt(a))
                )

  given tryLong2Fut(using EC) as Conversion[Try[Long], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsLong(a))
                )

  given tryDouble2Fut(using EC) as Conversion[Try[Double], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsDouble(a))
                )

  given tryBigDec2Fut(using EC) as Conversion[Try[BigDecimal], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsBigDec(a))
                )

  given tryBigInt2Fut(using EC) as Conversion[Try[BigInt], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsBigInt(a))
                )

  given tryBool2Fut(using EC) as Conversion[Try[Boolean], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(JsBool(a))
                )

  given tryObj2Fut(using EC) as Conversion[Try[JsObj], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(a)
                )

  given tryArr2Fut(using EC) as Conversion[Try[JsArray], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(a)
                )

  given tryVal2Fut(using EC) as Conversion[Try[JsValue], Future[JsValue]] =
    p => p.fold(e => failed(e),
                a => successful(a)
                )

  given keyVal2PathFut(using EC) as Conversion[(String, JsValue), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyStr2PathFut(using EC) as Conversion[(String, String), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsStr(p._2)))

  given keyInt2PathFut(using EC) as Conversion[(String, Int), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsInt(p._2)))

  given keyLong2PathFut(using EC) as Conversion[(String, Long), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsLong(p._2)))

  given keyBool2PathFut(using EC) as Conversion[(String, Boolean), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsBool(p._2)))

  given keyBigDec2PathFut(using EC) as Conversion[(String, BigDecimal), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsBigDec(p._2)))

  given keyBigInt2PathFut(using EC) as Conversion[(String, BigInt), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsBigInt(p._2)))

  given keyDouble2PathFut(using EC) as Conversion[(String, Double), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsDouble(p._2)))

  given keyObj2PathFut(using EC) as Conversion[(String, JsObj), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyArr2PathFut(using EC) as Conversion[(String, JsArray), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyNull2PathFut(using EC) as Conversion[(String, JsNull.type), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyFutStr2PathFut(using EC) as Conversion[(String, Future[String]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsStr(it)))

  given keyFutInt2PathFut(using EC) as Conversion[(String, Future[Int]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsInt(it)))

  given keyFutLong2PathFut(using EC) as Conversion[(String, Future[Long]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsLong(it)))

  given keyFutBool2PathFut(using EC) as Conversion[(String, Future[Boolean]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsBool(it)))

  given keyFutBigDec2PathFut(using EC) as Conversion[(String, Future[BigDecimal]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsBigDec(it)))

  given keyFutBigInt2PathFut(using EC) as Conversion[(String, Future[BigInt]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsBigInt(it)))

  given keyFutDouble2PathFut(using EC) as Conversion[(String, Future[Double]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsDouble(it)))

  given keyFutObj2PathFut(using EC) as Conversion[(String, Future[JsObj]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2)

  given keyFutArr2PathFut(using EC) as Conversion[(String, Future[JsArray]), (JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2)

  given val2Fut(using EC) as Conversion[JsValue, Future[JsValue]] = successful(_)

  given str2Fut(using EC) as Conversion[String, Future[JsValue]] = s => successful(JsStr(s))

  given int2Fut(using EC) as Conversion[Int, Future[JsValue]] = s => successful(JsInt(s))

  given long2Fut(using EC) as Conversion[Long, Future[JsValue]] = s => successful(JsLong(s))

  given double2Fut(using EC) as Conversion[Double, Future[JsValue]] = s => successful(JsDouble(s))

  given bigInt2Fut(using EC) as Conversion[BigInt, Future[JsValue]] = s => successful(JsBigInt(s))

  given bigDec2Fut(using EC) as Conversion[BigDecimal, Future[JsValue]] = s => successful(JsBigDec(s))

  given bool2Fut(using EC) as Conversion[Boolean, Future[JsValue]] = s => successful(JsBool(s))

  given obj2Fut(using EC) as Conversion[JsObj, Future[JsValue]] = successful(_)

  given array2Fut(using EC) as Conversion[JsArray, Future[JsValue]] = successful(_)
}