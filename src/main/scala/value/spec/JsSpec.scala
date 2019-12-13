package value.spec

import value.Implicits._
import value.JsPath.empty
import value.spec.ErrorMessages.{
  ARRAY_NOT_FOUND, ARRAY_OF_BOOLEAN_NOT_FOUND, ARRAY_OF_DECIMAL_NOT_FOUND, ARRAY_OF_INTEGRAL_NOT_FOUND, ARRAY_OF_INT_NOT_FOUND, ARRAY_OF_OBJECT_NOT_FOUND, ARRAY_OF_LONG_NOT_FOUND, ARRAY_OF_NUMBER_NOT_FOUND, ARRAY_OF_STRING_NOT_FOUND, ARRAY_WITH_NULL, BOOLEAN_NOT_FOUND, DECIMAL_NOT_FOUND, FALSE_NOT_FOUND, INTEGRAL_NOT_FOUND, INT_NOT_FOUND, LONG_NOT_FOUND,
  NOTHING_FOUND, NULL_FOUND, NUMBER_NOT_FOUND, OBJ_NOT_FOUND, STRING_NOT_FOUND, TRUE_NOT_FOUND
}
import value.{JsArray, JsNumber, JsObj, JsPath, JsValue, Json, UserError}

import scala.collection.immutable


sealed trait JsSpec
{

  override def equals(that: Any): Boolean = throw UserError.equalsOnJsSpec
}

sealed trait Schema[T <: Json[T]] extends JsSpec
{
  def validate(json: T): LazyList[(JsPath, Invalid)]
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


final private[value] case class IsInt(nullable: Boolean = false,
                                      required: Boolean = true
                                     ) extends JsIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isInt) Invalid(INT_NOT_FOUND(value))
    else Valid
  }

}


final private[value] case class IsIntSuchThat(p: Int => Result,
                                              nullable: Boolean = false,
                                              required: Boolean = true
                                             ) extends JsIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsInt(nullable,
                       required
                       ).test(value)
    if (result.isValid) p(value.asJsInt.value) else result
  }
}

final private[value] case class IsLong(nullable: Boolean = false,
                                       required: Boolean = true
                                      ) extends JsLongPredicate
{
  override def test(value: JsValue): Result =

  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isLong) Invalid(LONG_NOT_FOUND(value))
    else Valid
  }
}

final private[value] case class IsLongSuchThat(p: Long => Result,
                                               nullable: Boolean = false,
                                               required: Boolean = true
                                              ) extends JsLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsLong(nullable,
                        required
                        ).test(value)
    if (result.isValid) p(value.asJsLong.value) else result
  }
}

final private[value] case class IsIntegral(nullable: Boolean = false,
                                           required: Boolean = true
                                          ) extends JsIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isIntegral) Invalid(INTEGRAL_NOT_FOUND(value))
    else Valid
  }

}

final private[value] case class IsIntegralSuchThat(p: BigInt => Result,
                                                   nullable: Boolean = false,
                                                   required: Boolean = true
                                                  ) extends JsIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsIntegral(nullable,
                            required
                            ).test(value)
    if (result.isValid) p(value.asJsBigInt.value) else result
  }
}

final private[value] case class IsDecimal(nullable: Boolean = false,
                                          required: Boolean = true
                                         ) extends JsDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isDecimal) Invalid(DECIMAL_NOT_FOUND(value))
    else Valid
  }

}

final private[value] case class IsDecimalSuchThat(p: BigDecimal => Result,
                                                  nullable: Boolean = false,
                                                  required: Boolean = true
                                                 ) extends JsDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsDecimal(nullable,
                           required
                           ).test(value)
    if (result.isValid) p(value.asJsBigDec.value) else result
  }
}

final private[value] case class IsNumber(nullable: Boolean = false,
                                         required: Boolean = true
                                        ) extends JsNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isNumber) Invalid(NUMBER_NOT_FOUND(value))
    else Valid
  }
}

final private[value] case class IsNumberSuchThat(p: JsNumber => Result,
                                                 nullable: Boolean = false,
                                                 required: Boolean = true
                                                ) extends JsNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsNumber(nullable,
                          required
                          ).test(value)
    if (result.isValid) p(value.asJsNumber) else result
  }
}


final private[value] case class IsValue(required: Boolean = true) extends JsPredicate
{
  //todo implementar required
  override def test(value: JsValue): Result = Valid
}

final private[value] case class IsValueSuchThat(p: JsValue => Result,
                                                required: Boolean = true
                                               ) extends JsPredicate
{
  //todo implementar required

  override def test(value: JsValue): Result =
  {
    p(value)
  }
}

final private[value] case class IsStr(nullable: Boolean = false,
                                      required: Boolean = true
                                     ) extends JsStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isStr) Invalid(STRING_NOT_FOUND(value))
    else Valid
  }
}

final private[value] case class IsStrSuchThat(p: String => Result,
                                              nullable: Boolean = false,
                                              required: Boolean = true
                                             ) extends JsStrPredicate
{
  override def test(value: JsValue): Result =

  {
    val result = IsStr(nullable,
                       required
                       ).test(value)
    if (result.isValid) p(value.asJsStr.value) else result
  }
}

final private[value] case class IsBool(nullable: Boolean = false,
                                       required: Boolean = true
                                      ) extends JsBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isBool) Invalid(BOOLEAN_NOT_FOUND(value))
    else Valid
  }
}

final private[value] case class IsTrue(nullable: Boolean = false,
                                       required: Boolean = true
                                      ) extends JsBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && (!value.isBool || !value.asJsBool.value)) Invalid(TRUE_NOT_FOUND(value))
    else Valid
  }

}

final private[value] case class IsFalse(nullable: Boolean = false,
                                        required: Boolean = true
                                       ) extends JsBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && (!value.isBool || value.asJsBool.value)) Invalid(FALSE_NOT_FOUND(value))
    else Valid
  }
}


final private[value] case class IsObj(nullable: Boolean = false,
                                      required: Boolean = true
                                     ) extends JsObjPredicate
{
  override def test(value: JsValue): Result =

  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isObj) Invalid(OBJ_NOT_FOUND(value))
    else Valid

  }
}

final private[value] case class IsObjSuchThat(p: JsObj => Result,
                                              nullable: Boolean = false,
                                              required: Boolean = true
                                             ) extends JsObjPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsObj(nullable,
                       required
                       ).test(value)
    if (result.isValid) p(value.asJsObj) else result
  }
}

final private[value] case class IsArray(nullable: Boolean = false,
                                        required: Boolean = true,
                                        elemNullable    : Boolean = true
                                       ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
  {
    val result = JsSpec.isValid(value,
                                nullable,
                                required
                                )
    if (!result.isValid) result
    else if (!value.isNothing && !value.isArr) Invalid(ARRAY_NOT_FOUND(value))
    else if (!elemNullable && value.asJsArray.seq.exists(_.isNull)) Invalid(ARRAY_WITH_NULL)
    else Valid
  }
}

final private[value] case class IsArrayOfTestedValue(p           : JsValue => Result,
                                                     nullable    : Boolean = false,
                                                     required    : Boolean = true,
                                                     elemNullable: Boolean = true
                                                   ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
  {

    val result = IsArray(nullable,
                         required,
                         elemNullable
                         ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value => p(value)).find(!_.isValid).getOrElse(Valid)
    else result
  }
}


final private[value] case class IsArrayOfValueSuchThat(p           : JsArray => Result,
                                                       nullable    : Boolean = false,
                                                       required    : Boolean = true,
                                                       elemNullable: Boolean = true
                                               ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required,
                         elemNullable
                         ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}

final private[value] case class IsArrayOfInt(nullable: Boolean = false,
                                             required: Boolean = true,
                                             elemNullable: Boolean = true
                                            ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isInt || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_INT_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedInt(p           : Int => Result,
                                                   nullable    : Boolean = false,
                                                   required    : Boolean = true,
                                                   elemNullable: Boolean = true
                                                        ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                {
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isInt) Invalid(INT_NOT_FOUND(value))
                                                  else p(value.asJsInt.value)
                                                }
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfIntSuchThat(p: JsArray => Result,
                                                     nullable: Boolean = false,
                                                     required: Boolean = true,
                                                     elemNullable    : Boolean = true
                                                    ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfInt(nullable,
                              required,
                              elemNullable
                              ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfStr(nullable: Boolean = false,
                                             required: Boolean = true,
                                             elemNullable: Boolean = true
                                            ) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isStr || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_STRING_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedStr(p           : String => Result,
                                                   nullable    : Boolean = false,
                                                   required    : Boolean = true,
                                                   elemNullable: Boolean = true
                                                        ) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfStr(nullable,
                              required
                              ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                {
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isStr) Invalid(STRING_NOT_FOUND(value))
                                                  else p(value.asJsStr.value)
                                                }
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfStrSuchThat(p: JsArray => Result,
                                                     nullable: Boolean = false,
                                                     required: Boolean = true,
                                                     elemNullable: Boolean = true
                                                    ) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfStr(nullable,
                              required,
                              elemNullable
                              ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfLong(nullable: Boolean = false,
                                              required: Boolean = true,
                                              elemNullable: Boolean = true
                                             ) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isLong || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_LONG_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedLong(p           : Long => Result,
                                                    nullable    : Boolean = false,
                                                    required    : Boolean = true,
                                                    elemNullable: Boolean = true
                                                         ) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfLong(nullable,
                               required
                               ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                {
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isLong) Invalid(LONG_NOT_FOUND(value))
                                                  else p(value.asJsLong.value)
                                                }
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfLongSuchThat(p: JsArray => Result,
                                                      nullable: Boolean = false,
                                                      required: Boolean = true,
                                                      elemNullable: Boolean = true
                                                     ) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfLong(nullable,
                               required,
                               elemNullable
                               ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}

final private[value] case class IsArrayOfDecimal(nullable: Boolean = false,
                                                 required: Boolean = true,
                                                 elemNullable: Boolean = true
                                                ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isDecimal || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_DECIMAL_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedDecimal(p           : BigDecimal => Result,
                                                       nullable    : Boolean = false,
                                                       required    : Boolean = true,
                                                       elemNullable: Boolean = true
                                                            ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfDecimal(nullable,
                                  required
                                  ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                {
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isDecimal) Invalid(DECIMAL_NOT_FOUND(value))
                                                  else p(value.asJsBigDec.value)
                                                }
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfDecimalSuchThat(p: JsArray => Result,
                                                         nullable: Boolean = false,
                                                         required: Boolean = true,
                                                         elemNullable: Boolean = true
                                                        ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfDecimal(nullable,
                                  required,
                                  elemNullable
                                  ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfNumber(nullable: Boolean = false,
                                                required: Boolean = true,
                                                elemNullable: Boolean = true
                                               ) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isNumber || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_NUMBER_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedNumber(p           : JsNumber => Result,
                                                      nullable    : Boolean = false,
                                                      required    : Boolean = true,
                                                      elemNullable: Boolean = true
                                                           ) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfNumber(nullable,
                                 required
                                 ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                {
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isNumber) Invalid(NUMBER_NOT_FOUND(value))
                                                  else p(value.asJsNumber)
                                                }
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfNumberSuchThat(p: JsArray => Result,
                                                        nullable: Boolean = false,
                                                        required: Boolean = true,
                                                        elemNullable: Boolean = true
                                                       ) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfNumber(nullable,
                                 required,
                                 elemNullable
                                 ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfIntegral(nullable: Boolean = false,
                                                  required: Boolean = true,
                                                  elemNullable: Boolean = true
                                                 ) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isIntegral || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_INTEGRAL_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedIntegral(p           : BigInt => Result,
                                                        nullable    : Boolean = false,
                                                        required    : Boolean = true,
                                                        elemNullable: Boolean = true
                                                             ) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfIntegral(nullable,
                                   required
                                   ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                {
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isIntegral) Invalid(INTEGRAL_NOT_FOUND(value))
                                                  else p(value.asJsBigInt.value)
                                                }
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class IsArrayOfIntegralSuchThat(p: JsArray => Result,
                                                          nullable: Boolean = false,
                                                          required: Boolean = true,
                                                          elemNullable: Boolean = true,
                                                         ) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfIntegral(nullable,
                                   required,
                                   elemNullable
                                   ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfBool(nullable: Boolean = false,
                                              required: Boolean = true,
                                              elemNullable: Boolean = true
                                             ) extends JsArrayOfBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isBool || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_BOOLEAN_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfBoolSuchThat(p: JsArray => Result,
                                                      nullable: Boolean = false,
                                                      required: Boolean = true,
                                                      elemNullable: Boolean = true
                                                     ) extends JsArrayOfBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfBool(nullable,
                               required,
                               elemNullable
                               ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}

final private[value] case class IsArrayOfObj(nullable: Boolean = false,
                                             required: Boolean = true,
                                             elemNullable: Boolean = true
                                            ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result.isValid && value.asJsArray.seq.forall(v => v.isObj || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_OBJECT_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfObjSuchThat(p: JsArray => Result,
                                                     nullable: Boolean = false,
                                                     required: Boolean = true,
                                                     elemNullable: Boolean = true,
                                                    ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfObj(nullable,
                              required,
                              elemNullable
                              ).test(value)
    if (result.isValid) p(value.asJsArray) else result
  }
}


final private[value] case class IsArrayOfTestedObj(p           : JsObj => Result,
                                                   nullable    : Boolean = false,
                                                   required    : Boolean = true,
                                                   elemNullable: Boolean = true
                                                        ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArrayOfObj(nullable,
                              required
                              ).test(value)
    if (result.isValid) value.asJsArray.seq.map(value =>
                                                  if (elemNullable && value.isNull) Valid
                                                  else if (!value.isObj) Invalid(OBJ_NOT_FOUND(value))
                                                  else p(value.asJsObj)
                                                ).find(!_.isValid).getOrElse(Valid)
    else result

  }
}


final private[value] case class JsObjSpec(map: immutable.Map[SpecKey, JsSpec]) extends Schema[JsObj]
{
  def validate(value: JsObj): LazyList[(JsPath, Invalid)] = JsObjSpec.apply0(empty,
                                                                             LazyList.empty,
                                                                             map,
                                                                             value
                                                                             )

  def ++(spec: JsObjSpec): JsObjSpec = JsObjSpec(map ++ spec.map)

  def +(spec: (String, JsSpec)): JsObjSpec = JsObjSpec(map.updated(NamedKey(spec._1),
                                                                   spec._2
                                                                   )
                                                       )

  def -(name: String): JsObjSpec = JsObjSpec(map.removed(NamedKey(name)))

}

final private[value] case class JsArraySpec(seq: Seq[JsSpec]) extends Schema[JsArray]
{
  def validate(value: JsArray): LazyList[(JsPath, Invalid)] = JsArraySpec.apply0(-1,
                                                                                 LazyList.empty,
                                                                                 seq,
                                                                                 value
                                                                                 )

  def ++(spec: JsArraySpec): JsArraySpec = JsArraySpec(seq ++ spec.seq)

  @`inline` def :+(spec: JsSpec): JsArraySpec = appended(spec)

  def appended(spec: JsSpec): JsArraySpec = JsArraySpec(seq.appended(spec))

  @`inline` def +:(spec: JsSpec): JsArraySpec = prepended(spec)

  def prepended(spec: JsSpec): JsArraySpec = JsArraySpec(seq.prepended(spec))

}


final private[value] case class IsObjSpec(spec: JsObjSpec,
                                          nullable : Boolean,
                                          required : Boolean
                                         ) extends Schema[JsObj]
{
  override def validate(obj: JsObj): LazyList[(JsPath, Invalid)] =
  {
    if (obj == null && !nullable)
      LazyList.empty.appended((JsPath.empty, Invalid(ErrorMessages.NULL_FOUND)))
    else
      obj.validate(spec)
  }
}


final private[value] case class ArrayOfObjSpec(spec            : JsObjSpec,
                                               nullable        : Boolean,
                                               required        : Boolean,
                                               elemNullable    : Boolean = true
                                              ) extends Schema[JsArray]
{
  override def validate(array: JsArray): LazyList[(JsPath, Invalid)] =
  {

    @scala.annotation.tailrec
    def apply(path  : JsPath,
              array : JsArray,
              result: LazyList[(JsPath, Invalid)]
             ): LazyList[(JsPath, Invalid)] =
    {
      if (array.isEmpty) return result
      val currentPath = path.inc
      val head = array.head
      if (head.isNull && elemNullable) result.appended((currentPath, Invalid(NULL_FOUND)))
      else if (!head.isObj) result.appended((currentPath, Invalid(OBJ_NOT_FOUND(head))))
      else apply(currentPath,
                 array.tail,
                 result.appendedAll(head.asJsObj.validate(spec))
                 )
    }

    if (array == null && !nullable)
      LazyList.empty.appended((JsPath.empty, Invalid(ErrorMessages.NULL_FOUND)))
    else
      apply(-1,
            array,
            LazyList.empty
            )
  }
}


object JsObjSpec
{

  def apply(pairs: (SpecKey, JsSpec)*): JsObjSpec =
  {
    @scala.annotation.tailrec
    def apply0(map  : immutable.Map[SpecKey, JsSpec],
               pairs: (SpecKey, JsSpec)*
              ): immutable.Map[SpecKey, JsSpec] =
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
                              result     : LazyList[(JsPath, Invalid)],
                              validations: immutable.Map[SpecKey, JsSpec],
                              value      : JsValue
                             ): LazyList[(JsPath, Invalid)] =
  {
    value match
    {
      case obj: JsObj =>

        if (validations.isEmpty) result

        else
        {
          val head = validations.head

          head._1 match
          {
            case * =>
              //TODO REFACTOR
              val keysWithSpec = validations.keys.filter(_.isInstanceOf[NamedKey]).map(_.asInstanceOf[NamedKey].name)
              val keysWithoutSpec = obj.map.removedAll(keysWithSpec)
              apply0(path,
                     if (keysWithoutSpec.nonEmpty) result.appended((path, Invalid(s"Keys without spec: $keysWithoutSpec"))) else result,
                     validations.tail,
                     obj
                     )

            case NamedKey(name) =>
              head._2 match
              {
                case schema: Schema[_] => schema match
                {
                  case JsObjSpec(headValidations) =>
                    apply0(path,
                           result ++ apply0(path / name,
                                            LazyList.empty,
                                            headValidations,
                                            obj(name)
                                            ),
                           validations.tail,
                           obj
                           )
                  case JsArraySpec(headValidations) =>
                    apply0(path,
                           result ++ JsArraySpec.apply0(path / name / -1,
                                                        LazyList.empty,
                                                        headValidations,
                                                        obj(name)
                                                        ),
                           validations.tail,
                           obj
                           )
                }

                case p: JsPredicate => apply0(path,
                                              p.test(obj(name)) match
                                              {
                                                case Valid => result
                                                case error: Invalid => result.appended((path / name, error))
                                              },
                                              validations.tail,
                                              obj
                                              )
              }
          }


        }
      case _ => result :+ (path, Invalid(s"Json object required. Received: $value"))
    }
  }
}

object JsArraySpec
{
  def apply(x : JsSpec,
            xs: JsSpec*
           ): JsArraySpec = new JsArraySpec(xs.prepended(x))

  protected[value] def apply0(path       : JsPath,
                              result     : LazyList[(JsPath, Invalid)],
                              validations: Seq[JsSpec],
                              value      : JsValue
                             ): LazyList[(JsPath, Invalid)] =
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
                                                                         LazyList.empty,
                                                                         specs,
                                                                         arr(headPath.last)
                                                                         ),
                                              validations.tail,
                                              arr
                                              )
              case JsArraySpec(specs) =>
                apply0(headPath,
                       result ++ apply0(headPath / -1,
                                        LazyList.empty,
                                        specs,
                                        arr(headPath.last)
                                        ),
                       validations.tail,
                       arr
                       )
            }
            case p: JsPredicate => apply0(headPath,
                                          p.test(arr(headPath.last)) match
                                          {
                                            case Valid => result
                                            case error: Invalid => result.appended((headPath, error))
                                          },
                                          validations.tail,
                                          arr
                                          )
          }
        }
      case _ => result :+ (path, Invalid(s"JsArray object required. Received: $value"))
    }
  }
}

object JsSpec
{

  val any: JsSpec = IsValue()

  def any(nullable: Boolean,
          required: Boolean
         ): JsSpec = IsValueSuchThat((value: JsValue) =>
                                     {
                                       if (!nullable && value.isNull) Invalid(NULL_FOUND)
                                       if (required && value.isNothing) Invalid(NOTHING_FOUND)
                                       Valid
                                     }
                                     )

  def isValid(value   : JsValue,
              nullable: Boolean,
              required: Boolean
             ): Result =
  {
    if (required && value.isNothing) return Invalid(NOTHING_FOUND)
    if (!nullable && value.isNull) return Invalid(NULL_FOUND)
    Valid
  }


  def isNullableValid(value   : JsValue,
                      nullable: Boolean
                     ): Result =
  {
    if (!nullable && value.isNull) Invalid(NULL_FOUND)
    else Valid
  }

  def isOptionalValid(value   : JsValue,
                      required: Boolean
                     ): Result =
  {
    if (!required && value.isNothing) Invalid(NOTHING_FOUND)
    else Valid
  }

}