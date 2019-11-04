package jsonvalues.spec

import jsonvalues.spec.ErrorMessages.{NULL_FOUND, NULL_NOT_FOUND, NOTHING_FOUND}
import jsonvalues.{JsValidator, JsValue, JsValueError, JsValueOk, JsValueValidator}

import scala.language.implicitConversions

object JsValueSpec
{
  val notNull: JsValidator = JsValueValidator((value: JsValue) => if (value.isNull) JsValueError(NULL_FOUND) else JsValueOk)
  val `null`: JsValidator = JsValueValidator((value: JsValue) => if (!value.isNull) JsValueError(NULL_NOT_FOUND(value)) else JsValueOk)
  val any: JsValidator = JsValueValidator((value: JsValue) => if (!value.isNothing) JsValueOk else JsValueError(NOTHING_FOUND))


  def or(xs: JsValueValidator*): JsValidator =
  {
    @scala.annotation.tailrec
    def ||(result: JsValueValidator,
           xs    : JsValueValidator*
          ): JsValidator =
    {
      if (xs.isEmpty) result
      else ||(JsValueValidator(value =>
                               {
                                 val partial = result.f.apply(value)
                                 partial match
                                 {
                                   case JsValueOk => JsValueOk
                                   case e1: JsValueError => xs.head.f.apply(value) match
                                   {
                                     case JsValueOk => JsValueOk
                                     case e2: JsValueError => e1 ++ e2
                                   }
                                 }

                               }
                               ),
              xs.tail: _*
              )
    }

    ||(xs.head,
       xs.tail: _*
       )
  }


  def and(xs: JsValueValidator*): JsValidator =
  {


    @scala.annotation.tailrec
    def &&(result: JsValueValidator,
           xs    : JsValueValidator*
          ): JsValidator =
    {
      if (xs.isEmpty) result
      else &&(JsValueValidator(value =>
                               {
                                 result.f.apply(value) match
                                 {
                                   case JsValueOk => xs.head.f.apply(value)
                                   case error: JsValueError => error
                                 }

                               }
                               ),
              xs.tail: _*
              )
    }

    &&(xs.head,
       xs.tail: _*
       )


  }
}
