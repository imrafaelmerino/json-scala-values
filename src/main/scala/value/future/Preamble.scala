package value.future

import value.{JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNull, JsObj, JsPath, JsStr, JsValue}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object Preamble

  implicit def keyValueTry2pathFuture(p: (String, Try[JsValue]))
                                     (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(a)
                                            ))

  implicit def keyStrTry2pathFuture(p: (String, Try[String]))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsStr(a))
                                            ))

  implicit def keyIntTry2pathFuture(p: (String, Try[Int]))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsInt(a))
                                            ))

  implicit def keyLongTry2pathFuture(p: (String, Try[Long]))
                                    (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsLong(a))
                                            ))

  implicit def keyDoubleTry2pathFuture(p: (String, Try[Double]))
                                      (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsDouble(a))
                                            ))

  implicit def keyBigDecTry2pathFuture(p: (String, Try[BigDecimal]))
                                      (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsBigDec(a))
                                            ))

  implicit def keyBigIntTry2pathFuture(p: (String, Try[BigInt]))
                                      (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsBigInt(a))
                                            ))

  implicit def keyBoolTry2pathFuture(p: (String, Try[Boolean]))
                                    (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(JsBool(a))
                                            ))

  implicit def keyObjTry2pathFuture(p: (String, Try[JsObj]))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(a)
                                            ))

  implicit def keyArrTry2pathFuture(p: (String, Try[JsArray]))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.fold(e => Future.failed(e),
                                            a => Future.successful(a)
                                            ))


  implicit def strTry2Future(p: Try[String])
                            (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsStr(a))
           )

  implicit def valueTry2Future[T <: JsValue](p: Try[T])
                                            (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(a)
           )


  implicit def intTry2Future(p: Try[Int])
                            (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsInt(a))
           )

  implicit def longTry2Future(p: Try[Long])
                             (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsLong(a))
           )

  implicit def doubleTry2Future(p: Try[Double])
                               (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsDouble(a))
           )

  implicit def bigDecTry2Future(p: Try[BigDecimal])
                               (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsBigDec(a))
           )

  implicit def bigIntTry2Future(p: Try[BigInt])
                               (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsBigInt(a))
           )

  implicit def boolTry2Future(p: Try[Boolean])
                             (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(JsBool(a))
           )

  implicit def objTry2Future(p: Try[JsObj])
                            (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(a)
           )

  implicit def arrTry2Future(p: Try[JsArray])
                            (implicit executor: ExecutionContext): Future[JsValue] =
    p.fold(e => Future.failed(e),
           a => Future.successful(a)
           )

  implicit def keyValue2PathFuture(p: (String, JsValue))
                                  (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(p._2))

  implicit def keyStr2PathFuture(p: (String, String))
                                (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsStr(p._2)))

  implicit def keyStrFut2PathFuture(p: (String, Future[String]))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsStr))

  implicit def keyInt2PathFuture(p: (String, Int))
                                (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsInt(p._2)))

  implicit def keyIntFut2PathFuture(p: (String, Future[Int]))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsInt))

  implicit def keyLong2PathFuture(p: (String, Long))
                                 (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsLong(p._2)))

  implicit def keyLongFut2PathFuture(p: (String, Future[Long]))
                                    (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsLong))

  implicit def keyBool2PathFuture(p: (String, Boolean))
                                 (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsBool(p._2)))

  implicit def keyBoolFut2PathFuture(p: (String, Future[Boolean]))
                                    (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsBool))

  implicit def keyBigDec2PathFuture(p: (String, BigDecimal))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsBigDec(p._2)))

  implicit def keyBigDecFut2PathFuture(p: (String, Future[BigDecimal]))
                                      (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsBigDec))

  implicit def keyBigInt2PathFuture(p: (String, BigInt))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsBigInt(p._2)))

  implicit def keyBigIntFut2PathFuture(p: (String, Future[BigInt]))
                                      (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsBigInt))

  implicit def keyDouble2PathFuture(p: (String, Double))
                                   (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(JsDouble(p._2)))

  implicit def keyDoubleFut2PathFuture(p: (String, Future[Double]))
                                      (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2.map(JsDouble))

  implicit def keyJsObj2PathFuture(p: (String, JsObj))
                                  (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(p._2))

  implicit def keyJsObjFut2PathFuture(p: (String, Future[JsObj]))
                                     (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2)

  implicit def keyJsArray2PathFuture(p: (String, JsArray))
                                    (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(p._2))

  implicit def keyJsArrayFut2PathFuture(p: (String, Future[JsArray]))
                                       (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), p._2)

  implicit def keyNull2PathFuture(p: (String, JsNull.type))
                                 (implicit executor: ExecutionContext): (JsPath, Future[JsValue]) =
    (JsPath.empty.appended(p._1), Future.successful(p._2))

  implicit def value2Future(p: JsValue)
                           (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(p)

  implicit def str2Future(p: String)
                         (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsStr(p))

  implicit def int2Future(p: Int)
                         (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsInt(p))

  implicit def long2Future(p: Long)
                          (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsLong(p))

  implicit def double2Future(p: Double)
                            (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsDouble(p))

  implicit def bigInt2Future(p: BigInt)
                            (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsBigInt(p))

  implicit def bigDec2Future(p: BigDecimal)
                            (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsBigDec(p))

  implicit def bool2Future(p: Boolean)
                          (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(JsBool(p))

  implicit def jsObj2Future(p: JsObj)
                           (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(p)

  implicit def jsArray2Future(p: JsArray)
                             (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(p)

  implicit def null2Future(p: JsNull.type)
                          (implicit executor: ExecutionContext): Future[JsValue] = Future.successful(p)


