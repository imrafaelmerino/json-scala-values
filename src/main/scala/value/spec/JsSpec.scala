package value.spec

import value.Implicits._
import value.JsPath.empty
import value.spec.Messages.{
  ARRAY_OF_BOOLEAN_NOT_FOUND, ARRAY_OF_DECIMAL_NOT_FOUND, ARRAY_OF_INTEGRAL_NOT_FOUND, ARRAY_OF_INT_NOT_FOUND, ARRAY_OF_JSOBJECT_NOT_FOUND, ARRAY_OF_LONG_NOT_FOUND, ARRAY_OF_NUMBER_NOT_FOUND, ARRAY_OF_STRING_NOT_FOUND, BOOLEAN_NOT_FOUND, DECIMAL_NUMBER_NOT_FOUND, FALSE_NOT_FOUND, INTEGRAL_NUMBER_NOT_FOUND, INT_NOT_FOUND, ARRAY_NOT_FOUND, OBJ_NOT_FOUND,
  LONG_NOT_FOUND, NULL_FOUND, NULL_NOT_FOUND, NUMBER_NOT_FOUND, STRING_NOT_FOUND, TRUE_NOT_FOUND
}
import value.{JsArray, JsNumber, JsObj, JsPath, JsValue, Json, UserError}

import scala.collection.immutable

sealed trait JsSpec
{
  override def equals(that: Any): Boolean = throw UserError.equalsOnJsSpec
}

sealed trait Schema[T <: Json[T]] extends JsSpec
{
  def test(json: T): immutable.Seq[(JsPath, Invalid)]
}

sealed trait JsPredicate extends JsSpec
{
  def test(value: JsValue): Result
}

sealed trait PrimitivePredicate extends JsPredicate

sealed trait JsonPredicate extends JsPredicate

sealed trait JsStrPredicate extends PrimitivePredicate

sealed trait JsIntPredicate extends PrimitivePredicate

sealed trait JsLongPredicate extends PrimitivePredicate

sealed trait JsDecimalPredicate extends PrimitivePredicate

sealed trait JsNumberPredicate extends PrimitivePredicate

sealed trait JsIntegralPredicate extends PrimitivePredicate

sealed trait JsBoolPredicate extends PrimitivePredicate

sealed trait JsArrayPredicate extends JsonPredicate

sealed trait JsArrayOfIntPredicate extends JsArrayPredicate

sealed trait JsArrayOfLongPredicate extends JsArrayPredicate

sealed trait JsArrayOfDecimalPredicate extends JsArrayPredicate

sealed trait JsArrayOfIntegralPredicate extends JsArrayPredicate

sealed trait JsArrayOfNumberPredicate extends JsArrayPredicate

sealed trait JsArrayOfBoolPredicate extends JsArrayPredicate

sealed trait JsArrayOfStrPredicate extends JsArrayPredicate

sealed trait JsArrayOfObjectPredicate extends JsArrayPredicate

sealed trait JsArrayOfValuePredicate extends JsArrayPredicate

sealed trait JsObjPredicate extends JsonPredicate


final private[value] case class IsInt() extends JsIntPredicate
{
  override def test(value: JsValue): Result = if (value.isInt) Valid else Invalid(INT_NOT_FOUND(value))
}

final private[value] case class IsIntSuchThat(p: Int => Result) extends JsIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsInt().test(value)
    if (result.isValid) p(value.asJsInt.value) else result
  }
}

final private[value] case class IsLong() extends JsLongPredicate
{
  override def test(value: JsValue): Result = if (value.isLong) Valid else Invalid(LONG_NOT_FOUND(value))
}

final private[value] case class IsLongSuchThat(p: Long => Result) extends JsLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsLong().test(value)
    if (result.isValid) p(value.asJsLong.value) else result
  }
}

final private[value] case class IsIntegral() extends JsIntegralPredicate
{
  override def test(value: JsValue): Result = if (value.isIntegral) Valid else Invalid(INTEGRAL_NUMBER_NOT_FOUND(value))
}

final private[value] case class IsIntegralSuchThat(p: BigInt => Result) extends JsIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsIntegral().test(value)
    if (result.isValid) p(value.asJsBigInt.value) else result
  }
}

final private[value] case class IsDecimal() extends JsDecimalPredicate
{
  override def test(value: JsValue): Result = if (value.isDecimal) Valid else Invalid(DECIMAL_NUMBER_NOT_FOUND(value))
}

final private[value] case class IsDecimalSuchThat(p: BigDecimal => Result) extends JsDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsDecimal().test(value)
    if (result.isValid) p(value.asJsBigDec.value) else result
  }
}

final private[value] case class IsNumber() extends JsNumberPredicate
{
  override def test(value: JsValue): Result = if (value.isNumber) Valid else Invalid(NUMBER_NOT_FOUND(value))
}

final private[value] case class IsNumberSuchThat(p: JsNumber => Result) extends JsNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsNumber().test(value)
    if (result.isValid) p(value.asJsNumber) else result
  }
}


final private[value] case class IsValue() extends JsPredicate
{
  override def test(value: JsValue): Result = Valid
}

final private[value] case class IsValueSuchThat(p: JsValue => Result) extends JsPredicate
{
  override def test(value: JsValue): Result =
  {
    p(value.asJsStr.value)
  }
}

final private[value] case class IsStr() extends JsStrPredicate
{
  override def test(value: JsValue): Result = if (value.isStr) Valid else Invalid(STRING_NOT_FOUND(value))
}

final private[value] case class IsStrSuchThat(p    : String => Result) extends JsStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsDecimal().test(value)
    if (result.isValid) p(value.asJsStr.value) else result
  }
}

final private[value] case class IsBool() extends JsBoolPredicate
{
  override def test(value: JsValue): Result = if (value.isBool) Valid else Invalid(BOOLEAN_NOT_FOUND(value))
}

final private[value] case class IsTrue() extends JsBoolPredicate
{
  override def test(value: JsValue): Result = if (value.isBool) Valid else Invalid(TRUE_NOT_FOUND)
}

final private[value] case class IsFalse() extends JsBoolPredicate
{
  override def test(value: JsValue): Result = if (!value.isBool) Valid else Invalid(FALSE_NOT_FOUND)
}


final private[value] case class IsNull() extends PrimitivePredicate
{
  override def test(value: JsValue): Result = if (value.isNull) Valid else Invalid(NULL_NOT_FOUND(value))
}

final private[value] case class IsNotNull() extends PrimitivePredicate
{
  override def test(value: JsValue): Result = if (!value.isNull) Valid else Invalid(NULL_FOUND)
}

final private[value] case class IsObj() extends JsObjPredicate
{
  override def test(value: JsValue): Result = if (value.isObj) Valid else Invalid(OBJ_NOT_FOUND(value))
}

final private[value] case class IsObjSuchThat(p: JsObj => Result) extends JsObjPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsObj().test(value)
    if (result.isValid) p(value.asJsObj) else result
  }
}

final private[value] case class IsArray() extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result = if (value.isArr) Valid else Invalid(ARRAY_NOT_FOUND(value))
}

final private[value] case class IsArrayEachSuchThat(p  : JsValue => Result
                                                   ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
  {

    val result = IsArray().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value)).find(!_.isValid).getOrElse(Valid)
    else result
  }
}


final private[value] case class IsArraySuchThat(p: JsArray => Result) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}

final private[value] case class IsArrayOfInt() extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isInt)) Valid
    else Invalid(ARRAY_OF_INT_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfIntEachSuchThat(p  : Int => Result
                                                        ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfInt().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsInt.value)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfIntSuchThat(p: JsArray => Result) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfInt().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfStr() extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isStr)) Valid
    else Invalid(ARRAY_OF_STRING_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfStrEachSuchThat(p: String => Result
                                                        ) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfStr().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsStr.value)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfStrSuchThat(p: JsArray => Result) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfStr().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfLong() extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isLong)) Valid
    else Invalid(ARRAY_OF_LONG_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfLongEachSuchThat(p: Long => Result
                                                         ) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfLong().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsLong.value)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfLongSuchThat(p: JsArray => Result) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfLong().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}

final private[value] case class IsArrayOfDecimal() extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isDecimal)) Valid
    else Invalid(ARRAY_OF_DECIMAL_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfDecimalEachSuchThat(p: BigDecimal => Result
                                                            ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfDecimal().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsBigDec.value)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfDecimalSuchThat(p: JsArray => Result) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfDecimal().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfNumber() extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isNumber)) Valid
    else Invalid(ARRAY_OF_NUMBER_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfNumberEachSuchThat(p: JsNumber => Result
                                                           ) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfNumber().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsNumber)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfNumberSuchThat(p : JsArray => Result) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfNumber().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfIntegral() extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isBigInt)) Valid
    else Invalid(ARRAY_OF_INTEGRAL_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfIntegralEachSuchThat(p: BigInt => Result
                                                             ) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfIntegral().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsBigInt.value)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfIntegralSuchThat(p: JsArray => Result) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfIntegral().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfBool() extends JsArrayOfBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isBool)) Valid
    else Invalid(ARRAY_OF_BOOLEAN_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfBoolSuchThat(p    : JsArray => Result) extends JsArrayOfBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfBool().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfObj() extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray().test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isObj)) Valid
    else Invalid(ARRAY_OF_JSOBJECT_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfObjEachSuchThat(p : JsObj => Result
                                                        ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfObj().test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value.asJsObj)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfObjSuchThat(p     : JsArray => Result) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfIntegral().test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class JsObjSpec(map: immutable.Map[String, JsSpec]) extends Schema[JsObj]
{
  def test(value: JsObj): immutable.Seq[(JsPath, Invalid)] = JsObjSpec.apply0(empty,
                                                                              Vector.empty,
                                                                              map,
                                                                              value
                                                                              )

  def ++(spec: JsObjSpec): JsObjSpec = JsObjSpec(map ++ spec.map)

  def +(spec: (String, JsSpec)): JsObjSpec = JsObjSpec(map.updated(spec._1,
                                                                   spec._2
                                                                   )
                                                       )

  def -(name: String): JsObjSpec = JsObjSpec(map.removed(name))

}

final private[value] case class JsArraySpec(seq: immutable.Seq[JsSpec]) extends Schema[JsArray]
{
  def test(value: JsArray): immutable.Seq[(JsPath, Invalid)] = JsArraySpec.apply0(-1,
                                                                                  Vector.empty,
                                                                                  seq,
                                                                                  value
                                                                                  )

  def ++(spec: JsArraySpec): JsArraySpec = JsArraySpec(seq ++ spec.seq)

  @`inline` def :+(spec: JsSpec): JsArraySpec = appended(spec)

  def appended(spec: JsSpec): JsArraySpec = JsArraySpec(seq.appended(spec))

  @`inline` def +:(spec: JsSpec): JsArraySpec = prepended(spec)

  def prepended(spec: JsSpec): JsArraySpec = JsArraySpec(seq.prepended(spec))

}


private[spec] object JsObjSpec_?
{

  def apply0(path       : JsPath,
             result: immutable.Seq[(JsPath, Invalid)],
             validations: immutable.Map[String, JsSpec],
             value      : JsValue
            ): immutable.Seq[(JsPath, Invalid)] =
  {
    if (value.isNothing) Seq.empty else JsObjSpec.apply0(path,
                                                         result,
                                                         validations,
                                                         value
                                                         )
  }


}

object JsObjSpec
{

  def apply(pairs: (String, JsSpec)*): JsObjSpec =
  {
    @scala.annotation.tailrec
    def apply0(map  : immutable.Map[String, JsSpec],
               pairs: (String, JsSpec)*
              ): immutable.Map[String, JsSpec] =
    {
      if (pairs.isEmpty) map
      else
      {
        val head = pairs.head
        apply0(map.updated(head._1,
                           head._2
                           ),
               pairs.tail: _*
               )
      }
    }

    new JsObjSpec(apply0(immutable.HashMap.empty,
                         pairs: _*
                         )
                  )
  }


  protected[value] def apply0(path       : JsPath,
                              result     : immutable.Seq[(JsPath, Invalid)],
                              validations: immutable.Map[String, JsSpec],
                              value      : JsValue
                             ): immutable.Seq[(JsPath, Invalid)] =
  {

    value match
    {
      case obj: JsObj =>
        if (validations.isEmpty) result
        else
        {
          val head = validations.head


          head._2 match
          {
            case schema: Schema[_] => schema match
            {
              case JsObjSpec(headValidations) =>
                apply0(path,
                       result ++ apply0(path / head._1,
                                        Vector.empty,
                                        headValidations,
                                        obj(head._1)
                                        ),
                       validations.tail,
                       obj
                       )
              case JsArraySpec(headValidations) =>
                apply0(path,
                       result ++ JsArraySpec.apply0(path / head._1 / -1,
                                                    Vector.empty,
                                                    headValidations,
                                                    obj(head._1)
                                                    ),
                       validations.tail,
                       obj
                       )
            }

            case p: JsPredicate => apply0(path,
                                          p.test(value) +? (result, path / head._1),
                                          validations.tail,
                                          obj
                                          )
          }
        }
      case _ => result :+ (path, Invalid(s"Json object required. Received: $value"))
    }
  }
}


object JsArraySpec
{
  def apply(x: JsSpec,
            xs: JsSpec*
           ): JsArraySpec = new JsArraySpec(xs.prepended(x))

  protected[value] def apply0(path: JsPath,
                              result: immutable.Seq[(JsPath, Invalid)],
                              validations: immutable.Seq[JsSpec],
                              value: JsValue
                             ): immutable.Seq[(JsPath, Invalid)] =
  {

    value match
    {
      case arr: JsArray =>
        if (validations.isEmpty) result
        else
        {
          val headPath = path.inc
          validations.head match
          {
            case schema: Schema[_] => schema match
            {
              case JsObjSpec(specs) => apply0(headPath,
                                              result ++ JsObjSpec.apply0(headPath,
                                                                         Vector.empty,
                                                                         specs,
                                                                         arr(headPath.last)
                                                                         ),
                                              validations.tail,
                                              arr
                                              )
              case JsArraySpec(specs) =>
                apply0(headPath,
                       result ++ apply0(headPath / -1,
                                        Vector.empty,
                                        specs,
                                        arr(headPath.last)
                                        ),
                       validations.tail,
                       arr
                       )
            }
            case p: JsPredicate => apply0(headPath,
                                          p.test(value) +? (result, headPath),
                                          validations.tail,
                                          arr
                                          )
          }
        }
      case _ => result :+ (path, Invalid(s"JsArray object required. Received: $value"))
    }
  }
}





