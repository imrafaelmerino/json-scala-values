package jsonvalues

import scala.collection.immutable.HashMap
import scala.language.implicitConversions

sealed trait JsValidator
{
}


case class JsObjValidator(map: Map[String, JsValidator]) extends JsValidator
{}

case class JsArrayValidator(seq: Seq[JsValidator]) extends JsValidator
{}

case class JsValueValidator(f    : JsValue => JsPairValidationResult) extends JsValidator

object JsObjValidator
{
  def apply(pairs  : (String, JsValidator)*): JsObjValidator = new JsObjValidator(new HashMap())
}

object JsArrayValidator
{
  def apply(xs: JsValidator*): JsArrayValidator = new JsArrayValidator(xs)
}


object JsValueValidator
{

  val notNull: JsValidator = JsValueValidator((value: JsValue) => if (value.isNull) JsPairError("null") else JsPairOk())
  val required: JsValidator = JsValueValidator((value: JsValue) => if (value.isNothing) JsPairOk() else JsPairError("required"))
  val string: JsValidator = JsValueValidator((value: JsValue) => if (value.isStr) JsPairOk() else JsPairError("not a string"))
  val boolean: JsValidator = JsValueValidator((value: JsValue) => if (value.isBool) JsPairOk() else JsPairError("not a boolean"))
  val number: JsValidator = JsValueValidator((value: JsValue) => if (value.isNumber) JsPairOk() else JsPairError("not a number"))
  val decimal: JsValidator = JsValueValidator((value: JsValue) => if (value.isDouble || value.isBigDec) JsPairOk() else JsPairError("not a decimal"))
  val int: JsValidator = JsValueValidator((value: JsValue) => if (value.isInt) JsPairOk() else JsPairError("not an int"))
  val long: JsValidator = JsValueValidator((value: JsValue) => if (value.isLong) JsPairOk() else JsPairError("not a long"))
  val integral: JsValidator = JsValueValidator((value: JsValue) => if (value.isInt || value.isLong || value.isBigInt) JsPairOk() else JsPairError("not an integral number"))
  val jsObject: JsValidator = JsValueValidator((value: JsValue) => if (value.isObj) JsPairOk() else JsPairError("not an object"))

  def jsObjectWith(keys: String*): JsValidator = JsValueValidator((value: JsValue) => if (value.isObj && keys.forall(k => value.asInstanceOf[JsObj].contains(k))) JsPairOk() else JsPairError("not an object"))


  val jsArray: JsValidator = JsValueValidator((value: JsValue) => if (value.isArr) JsPairOk() else JsPairError("not an array"))

  def string(condition: String => Boolean,
             message  : String
            ): JsValidator = JsValueValidator((value: JsValue) =>
                                                if (value.isStr && condition.apply(value.asInstanceOf[JsStr].value)) JsPairOk() else JsPairError(message)
                                              )


  def jsObject(condition: JsObj => Boolean,
               message: String
              ): JsValidator = JsValueValidator((value   : JsValue) =>
                                                  if (value.isStr && condition.apply(value.asInstanceOf[JsObj])) JsPairOk() else JsPairError(message)
                                                )


  def jsArray(condition: JsArray => Boolean,
              message: String
             ): JsValidator = JsValueValidator((value : JsValue) =>
                                                 if (value.isStr && condition.apply(value.asInstanceOf[JsArray])) JsPairOk() else JsPairError(message)
                                               )


  def int(condition   : Int => Boolean,
          message     : String
         ): JsValidator = JsValueValidator((value        : JsValue) =>
                                             if (value.isInt && condition.apply(value.asInstanceOf[JsInt].value)) JsPairOk() else JsPairError(message)
                                           )


  def long(condition: Long => Boolean,
           message: String
          ): JsValidator = JsValueValidator((value    : JsValue) =>
                                              if (value.isLong && condition.apply(value.asInstanceOf[JsLong].value)) JsPairOk() else JsPairError(message)
                                            )


  def double(condition: Double => Boolean,
             message: String
            ): JsValidator = JsValueValidator((value         : JsValue) =>
                                                if (value.isDouble && condition.apply(value.asInstanceOf[JsDouble].value)) JsPairOk() else JsPairError(message)
                                              )

  def bigDec(condition: BigDecimal => Boolean,
             message  : String
            ): JsValidator = JsValueValidator((value     : JsValue) =>
                                                if (value.isBigDec && condition.apply(value.asInstanceOf[JsBigDec].value)) JsPairOk() else JsPairError(message)
                                              )

  def bigInt(condition: BigInt => Boolean,
             message: String
            ): JsValidator = JsValueValidator((value   : JsValue) =>
                                                if (value.isNumber && condition.apply(value.asInstanceOf[JsBigInt].value)) JsPairOk() else JsPairError(message)
                                              )

  def number(condition: JsNumber => Boolean,
             message  : String
            ): JsValidator = JsValueValidator((value     : JsValue) =>
                                                if (value.isNumber && condition.apply(value.asInstanceOf[JsNumber])) JsPairOk() else JsPairError(message)
                                              )


  val TRUE: JsValidator = JsValueValidator((value: JsValue) => if (value.isBool && value.asInstanceOf[JsBool].value) JsPairOk() else JsPairError("not TRUE"))

  val FALSE: JsValidator = JsValueValidator((value: JsValue) => if (value.isBool && !value.asInstanceOf[JsBool].value) JsPairOk() else JsPairError("not FALSE"))

  def ENUM(constants: String*): JsValidator = string(str => constants.contains(str),
                                                     s"not a string in $constants "
                                                     )


  val arrayOfInt: JsValidator = JsValueValidator((value      : JsValue) =>
                                                   if (value.isArr && value.asInstanceOf[JsArray].seq.forall(v => v.isInt)) JsPairOk() else JsPairError("not an array of Int")
                                                 )


  val arrayOfString: JsValidator = JsValueValidator((value   : JsValue) =>
                                                      if (value.isArr && value.asInstanceOf[JsArray].seq.forall(v => v.isStr)) JsPairOk() else JsPairError("not an array of String")
                                                    )

  val arrayOfLong: JsValidator = JsValueValidator((value        : JsValue) =>
                                                    if (value.isArr && value.asInstanceOf[JsArray].seq.forall(v => v.isLong)) JsPairOk() else JsPairError("not an array of Long")
                                                  )

  val arrayOfDouble: JsValidator = JsValueValidator((value     : JsValue) =>
                                                      if (value.isArr && value.asInstanceOf[JsArray].seq.forall(v => v.isDouble)) JsPairOk() else JsPairError("not an array of Double")
                                                    )

  def arrayOf(condition: JsValue => JsPairValidationResult,
              message  : String
             ): JsValidator =
  {
    JsValueValidator((value: JsValue) =>
                       if (value.isArr &&
                           value.asInstanceOf[JsArray].seq.forall(v => condition.apply(v) match
                           {
                             case JsPairOk() => true
                             case JsPairError(_) => false
                           }
                                                                  )) JsPairOk() else JsPairError(message)
                     )

  }

  implicit def constant(cons: String): JsValidator = string(s => s == cons,
                                                            s"string not equals to $cons"
                                                            )

  implicit def constant(cons: Int): JsValidator = int(s => s == cons,
                                                      s"integer not equals to $cons"
                                                      )

  implicit def constant(cons: Long): JsValidator = long(s => s == cons,
                                                        s"long not equals to $cons"
                                                        )

  implicit def constant(cons: BigInt): JsValidator = bigInt(s => s == cons,
                                                            s"bigint not equals to $cons"
                                                            )

  implicit def constant(cons: BigDecimal): JsValidator = bigDec(s => s == cons,
                                                                s"bigdec not equals to $cons"
                                                                )

  implicit def constant(cons: Double): JsValidator = double(s => s == cons,
                                                            s"double not equals to $cons"
                                                            )

  implicit def constant(cons: JsObj): JsValidator = jsObject(s => s == cons,
                                                             s"Json object not equals to $cons"
                                                             )

  implicit def constant(cons: JsArray): JsValidator = jsArray(s => s == cons,
                                                              s"Json array not equals to $cons"
                                                              )

  implicit def constant(cons: Boolean): JsValidator = if (cons) TRUE else FALSE

  implicit def constant(cons: jsonvalues.JsNull.type): JsValidator = JsValueValidator((value: JsValue) => if (value.isNull) JsPairOk() else JsPairError("not null"))

  implicit def constant(cons: jsonvalues.JsNothing.type): JsValidator = JsValueValidator((value: JsValue) => if (value.isNothing) JsPairOk() else JsPairError("exists value"))


  def all(xs: JsValueValidator*): JsValidator =
  {

    JsValueValidator((value: JsValue) =>
                     {

                       @scala.annotation.tailrec
                       def apply0(
                                   xs: JsValueValidator*
                                 ): JsPairValidationResult =
                       {

                         if (xs.isEmpty) JsPairOk()
                         else xs.head.f.apply(value) match
                         {
                           case e: JsPairError => e
                           case JsPairOk() => apply0(xs.tail: _*)
                         }
                       }

                       apply0(xs: _*)
                     }
                     )

  }

}



