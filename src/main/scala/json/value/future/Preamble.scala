package json.value.future

import json.value.{JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNull, JsObj, JsPath, JsStr, JsValue}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try
import JsPath.empty
import Future.successful
import Future.failed

object Preamble

  private type EC = ExecutionContext
  private type C = Conversion

  given keyTryValue2PathFut(given EC):C[(String,Try[JsValue]),(JsPath, Future[JsValue])] =
      p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(a)) )

  given keyTryStr2PathFut(given EC):C[(String,Try[String]),(JsPath, Future[JsValue])] =
      p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsStr(a))))

  given keyTryInt2PathFut(given EC):C[(String,Try[Int]),(JsPath, Future[JsValue])] =
      p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsInt(a))))

  given keyTryLong2PathFut(given EC):C[(String,Try[Long]),(JsPath, Future[JsValue])] =
      p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsLong(a))))

  given keyTryDouble2PathFut(given EC):C[(String,Try[Double]),(JsPath, Future[JsValue])] =
       p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsDouble(a))))

  given keyTryBigDec2PathFut(given EC):C[(String,Try[BigDecimal]),(JsPath, Future[JsValue])] =
       p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsBigDec(a))))

  given keyTryBigInt2PathFut(given EC):C[(String,Try[BigInt]),(JsPath, Future[JsValue])] =
       p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsBigInt(a))))

  given keyTryBool2PathFut(given EC):C[(String,Try[Boolean]),(JsPath, Future[JsValue])] =
       p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(JsBool(a))))

  given keyTryObjPathFut(given EC):C[(String,Try[JsObj]),(JsPath, Future[JsValue])] =
       p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(a)))

  given keyTryArr2PathFut(given EC):C[(String,Try[JsArray]),(JsPath, Future[JsValue])] =
       p => (empty.appended(p._1), p._2.fold(e => failed(e), a => successful(a)))

  given tryStr2Fut(given EC):C[Try[String],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsStr(a)))

  given tryInt2Fut(given EC):C[Try[Int],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsInt(a)))

  given tryLong2Fut(given EC):C[Try[Long],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsLong(a)))

  given tryDouble2Fut(given EC):C[Try[Double],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsDouble(a)))

  given tryBigDec2Fut(given EC):C[Try[BigDecimal],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsBigDec(a)))

  given tryBigInt2Fut(given EC):C[Try[BigInt],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsBigInt(a)))

  given tryBool2Fut(given EC):C[Try[Boolean],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(JsBool(a)))

  given tryObj2Fut(given EC):C[Try[JsObj],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(a))

  given tryArr2Fut(given EC):C[Try[JsArray],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(a))

  given tryVal2Fut(given EC):C[Try[JsValue],Future[JsValue]] =
       p => p.fold(e => failed(e), a => successful(a))

  given keyVal2PathFut(given EC):C[(String,JsValue),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyStr2PathFut(given EC):C[(String,String),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsStr(p._2)))

  given keyInt2PathFut(given EC):C[(String,Int),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsInt(p._2)))

  given keyLong2PathFut(given EC):C[(String,Long),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsLong(p._2)))

  given keyBool2PathFut(given EC):C[(String,Boolean),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsBool(p._2)))

  given keyBigDec2PathFut(given EC):C[(String,BigDecimal),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsBigDec(p._2)))

  given keyBigInt2PathFut(given EC):C[(String,BigInt),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsBigInt(p._2)))

  given keyDouble2PathFut(given EC):C[(String,Double),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(JsDouble(p._2)))

  given keyObj2PathFut(given EC):C[(String,JsObj),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyArr2PathFut(given EC):C[(String,JsArray),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyNull2PathFut(given EC):C[(String,JsNull.type),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), successful(p._2))

  given keyFutStr2PathFut(given EC):C[(String,Future[String]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it=>JsStr(it)))

  given keyFutInt2PathFut(given EC):C[(String,Future[Int]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it=>JsInt(it)))

  given keyFutLong2PathFut(given EC):C[(String,Future[Long]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it=>JsLong(it)))

  given keyFutBool2PathFut(given EC):C[(String,Future[Boolean]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it=>JsBool(it)))

  given keyFutBigDec2PathFut(given EC):C[(String,Future[BigDecimal]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it => JsBigDec(it)))

  given keyFutBigInt2PathFut(given EC):C[(String,Future[BigInt]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it=>JsBigInt(it)))

  given keyFutDouble2PathFut(given EC):C[(String,Future[Double]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2.map(it=>JsDouble(it)))

  given keyFutObj2PathFut(given EC):C[(String,Future[JsObj]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2)

  given keyFutArr2PathFut(given EC):C[(String,Future[JsArray]),(JsPath, Future[JsValue])] =
    p => (empty.appended(p._1), p._2)

  given (given EC):C[JsValue,Future[JsValue]] = successful(_)

  given (given EC):C[String,Future[JsValue]] = s => successful(JsStr(s))

  given (given EC):C[Int,Future[JsValue]] = s => successful(JsInt(s))

  given (given EC):C[Long,Future[JsValue]] = s => successful(JsLong(s))

  given (given EC):C[Double,Future[JsValue]] = s => successful(JsDouble(s))

  given (given EC):C[BigInt,Future[JsValue]] = s => successful(JsBigInt(s))

  given (given EC):C[BigDecimal,Future[JsValue]] = s => successful(JsBigDec(s))

  given (given EC):C[Boolean,Future[JsValue]] = s => successful(JsBool(s))

  given (given EC):C[JsObj,Future[JsValue]] = successful(_)

  given (given EC):C[JsArray,Future[JsValue]] = successful(_)
