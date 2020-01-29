package value.spec

import java.util.Objects.requireNonNull

import value.Preamble._
import value.JsPath.empty
import value.spec.ValidationMessages._
import value.spec.JsSpec.isValid
import value.{JsArray, JsNull, JsNumber, JsObj, JsPath, JsValue, Json, UserError}

import scala.collection.immutable

private[value] sealed trait JsSpec
{

  override def equals(that: Any): Boolean = throw UserError.equalsOnJsSpec
}

private[value] sealed trait Schema[T <: Json[T]] extends JsSpec
{
  def validate(json: T): LazyList[(JsPath, Invalid)]
}

private[value] sealed trait JsPredicate extends JsSpec
{
  def test(value: JsValue): Result
}

private[value] sealed trait PrimitivePredicate extends JsPredicate

private[value] sealed trait JsonPredicate extends JsPredicate

private[value] sealed trait JsStrPredicate extends PrimitivePredicate

private[value] sealed trait JsIntPredicate extends PrimitivePredicate

private[value] sealed trait JsLongPredicate extends PrimitivePredicate

private[value] sealed trait JsDecimalPredicate extends PrimitivePredicate

private[value] sealed trait JsNumberPredicate extends PrimitivePredicate

private[value] sealed trait JsIntegralPredicate extends PrimitivePredicate

private[value] sealed trait JsBoolPredicate extends PrimitivePredicate

private[value] sealed trait JsArrayPredicate extends JsonPredicate

private[value] sealed trait JsArrayOfIntPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfLongPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfDecimalPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfIntegralPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfNumberPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfBoolPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfStrPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfObjectPredicate extends JsArrayPredicate

private[value] sealed trait JsArrayOfValuePredicate extends JsArrayPredicate

private[value] sealed trait JsObjPredicate extends JsonPredicate


final private[value] case class IsInt(nullable: Boolean = false,
                                      required: Boolean = true
                                     ) extends JsIntPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isInt) Valid else Invalid(INT_NOT_FOUND(value))
                                                      )

}


final private[value] case class IsIntSuchThat(p       : Int => Result,
                                              nullable: Boolean = false,
                                              required: Boolean = true
                                             ) extends JsIntPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isInt) p(value.toJsInt.value) else Invalid(INT_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsLong(nullable: Boolean = false,
                                       required: Boolean = true
                                      ) extends JsLongPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isLong || value.isInt) Valid else Invalid(LONG_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsLongSuchThat(p       : Long => Result,
                                               nullable: Boolean = false,
                                               required: Boolean = true
                                              ) extends JsLongPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isLong || value.isInt) p(value.toJsLong.value) else Invalid(LONG_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsIntegral(nullable: Boolean = false,
                                           required: Boolean = true
                                          ) extends JsIntegralPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isIntegral) Valid else Invalid(INTEGRAL_NOT_FOUND(value))
                                                      )

}

final private[value] case class IsIntegralSuchThat(p       : BigInt => Result,
                                                   nullable: Boolean = false,
                                                   required: Boolean = true
                                                  ) extends JsIntegralPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isIntegral) p(value.toJsBigInt.value) else Invalid(INTEGRAL_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsDecimal(nullable: Boolean = false,
                                          required: Boolean = true
                                         ) extends JsDecimalPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isDecimal) Valid else Invalid(DECIMAL_NOT_FOUND(value))
                                                      )

}

final private[value] case class IsDecimalSuchThat(p       : BigDecimal => Result,
                                                  nullable: Boolean = false,
                                                  required: Boolean = true
                                                 ) extends JsDecimalPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isDecimal) p(value.toJsBigDec.value) else Invalid(DECIMAL_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsNumber(nullable: Boolean = false,
                                         required: Boolean = true
                                        ) extends JsNumberPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isNumber) Valid else Invalid(NUMBER_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsNumberSuchThat(p       : JsNumber => Result,
                                                 nullable: Boolean = false,
                                                 required: Boolean = true
                                                ) extends JsNumberPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isNumber) p(value.toJsNumber) else Invalid(NUMBER_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsValue(required: Boolean = true) extends JsPredicate
{
  override def test(value: JsValue): Result =
  {
    if (value.isNothing && required) Invalid(NOTHING_FOUND)
    Valid
  }
}

final private[value] case class IsValueSuchThat(p       : JsValue => Result,
                                                required: Boolean = true
                                               ) extends JsPredicate
{

  override def test(value: JsValue): Result =
  {
    if (value.isNothing && required) Invalid(NOTHING_FOUND)
    p(value)
  }
}

final private[value] case class IsStr(nullable: Boolean = false,
                                      required: Boolean = true
                                     ) extends JsStrPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isStr) Valid else Invalid(STRING_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsStrSuchThat(p       : String => Result,
                                              nullable: Boolean = false,
                                              required: Boolean = true
                                             ) extends JsStrPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isStr) p(value.toJsStr.value) else Invalid(STRING_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsBool(nullable: Boolean = false,
                                       required: Boolean = true
                                      ) extends JsBoolPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isBool) Valid else Invalid(BOOLEAN_NOT_FOUND(value))
                                                      )
}

final private[value] case class IsTrue(nullable: Boolean = false,
                                       required: Boolean = true
                                      ) extends JsBoolPredicate
{
  override def test(value: JsValue): Result = isValid(value,
                                                      nullable,
                                                      required,
                                                      value => if (value.isBool && value.toJsBool.value) Valid else Invalid(TRUE_NOT_FOUND(value))
                                                      )

}

final private[value] case class IsFalse(nullable: Boolean = false,
                                        required: Boolean = true
                                       ) extends JsBoolPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            value =>
              if (value.isBool && !value.toJsBool.value) Valid
              else Invalid(FALSE_NOT_FOUND(value))
            )

}


final private[value] case class IsObj(nullable: Boolean = false,
                                      required: Boolean = true
                                     ) extends JsObjPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            value =>
              if (value.isObj) Valid
              else Invalid(OBJ_NOT_FOUND(value))
            )
}

final private[value] case class IsObjSuchThat(p       : JsObj => Result,
                                              nullable: Boolean = false,
                                              required: Boolean = true
                                             ) extends JsObjPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            value =>
              if (value.isObj) p(value.toJsObj)
              else Invalid(OBJ_NOT_FOUND(value))
            )
}

final private[value] case class IsArray(nullable    : Boolean = false,
                                        required    : Boolean = true,
                                        elemNullable: Boolean = true
                                       ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            value =>
              if (value.isArr) Valid
              else Invalid(ARRAY_NOT_FOUND(value))
            )
}

final private[value] case class IsArrayOfTestedValue(p           : JsValue => Result,
                                                     nullable    : Boolean = false,
                                                     required    : Boolean = true,
                                                     elemNullable: Boolean = true
                                                    ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr =>
              if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
              else value.toJsArray.seq.map(value =>
                                             if (value.isNull)
                                               if (elemNullable) Valid else Invalid(NULL_FOUND)
                                             else p(value)
                                           ).find(_ != Valid).getOrElse(Valid)
            )
}


final private[value] case class IsArrayOfValueSuchThat(p           : JsArray => Result,
                                                       nullable    : Boolean = false,
                                                       required    : Boolean = true,
                                                       elemNullable: Boolean = true
                                                      ) extends JsArrayOfValuePredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr =>
              if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
              else if (elemNullable) p(value.toJsArray)
              else value.toJsArray.seq.map(value => if (value.isNull) Invalid(NULL_FOUND) else Valid)
                .find(_ != Valid).getOrElse(p(value.toJsArray))
            )
}

final private[value] case class IsArrayOfInt(nullable    : Boolean = false,
                                             required    : Boolean = true,
                                             elemNullable: Boolean = true
                                            ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr =>
              if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
              else value.toJsArray.seq.map(value =>
                                             if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                             else if (!value.isInt) Invalid(INT_NOT_FOUND(value))
                                             else Valid
                                           ).find(_ != Valid).getOrElse(Valid)
            )
}

final private[value] case class IsArrayOfTestedInt(p           : Int => Result,
                                                   nullable    : Boolean = false,
                                                   required    : Boolean = true,
                                                   elemNullable: Boolean = true
                                                  ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr =>
              if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
              else value.toJsArray.seq.map(value =>
                                             if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                             else if (!value.isInt) Invalid(INT_NOT_FOUND(value))
                                             else p(value.toJsInt.value)
                                           ).find(_ != Valid).getOrElse(Valid)
            )


}


final private[value] case class IsArrayOfIntSuchThat(p           : JsArray => Result,
                                                     nullable    : Boolean = false,
                                                     required    : Boolean = true,
                                                     elemNullable: Boolean = true
                                                    ) extends JsArrayOfIntPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isInt || (elemNullable && v.isNull))) p(value.toJsArray)
            else Invalid(ARRAY_OF_INT_NOT_FOUND)
            )

}


final private[value] case class IsArrayOfStr(nullable    : Boolean = false,
                                             required    : Boolean = true,
                                             elemNullable: Boolean = true
                                            ) extends JsArrayOfStrPredicate
{

  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr =>
              if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
              else value.toJsArray.seq.map(value =>
                                             if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                             else if (!value.isStr) Invalid(STRING_NOT_FOUND(value))
                                             else Valid
                                           ).find(_ != Valid).getOrElse(Valid)
            )
}

final private[value] case class IsArrayOfTestedStr(p           : String => Result,
                                                   nullable    : Boolean = false,
                                                   required    : Boolean = true,
                                                   elemNullable: Boolean = true
                                                  ) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else value.toJsArray.seq.map(value =>
                                           if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                           else if (!value.isStr) Invalid(STRING_NOT_FOUND(value))
                                           else p(value.toJsStr.value)
                                         ).find(_ != Valid).getOrElse(Valid)
            )
}


final private[value] case class IsArrayOfStrSuchThat(p           : JsArray => Result,
                                                     nullable    : Boolean = false,
                                                     required    : Boolean = true,
                                                     elemNullable: Boolean = true
                                                    ) extends JsArrayOfStrPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isStr || (elemNullable && v.isNull))) p(value.toJsArray)
            else Invalid(ARRAY_OF_STRING_NOT_FOUND)
            )
}


final private[value] case class IsArrayOfLong(nullable    : Boolean = false,
                                              required    : Boolean = true,
                                              elemNullable: Boolean = true
                                             ) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result == Valid && value.toJsArray.seq.forall(v => v.isLong || v.isInt || (elemNullable && v.isNull))) Valid
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
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else value.toJsArray.seq.map(value =>
                                           if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                           else if (!(value.isLong || value.isInt)) Invalid(LONG_NOT_FOUND(value))
                                           else p(value.toJsLong.value)
                                         ).find(_ != Valid).getOrElse(Valid)
            )
}


final private[value] case class IsArrayOfLongSuchThat(p           : JsArray => Result,
                                                      nullable    : Boolean = false,
                                                      required    : Boolean = true,
                                                      elemNullable: Boolean = true
                                                     ) extends JsArrayOfLongPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr =>
              if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
              else if (value.toJsArray.seq.forall(v => (v.isLong || v.isInt) ||
                                                       (elemNullable && v.isNull)
                                                  )
              ) p(arr.toJsArray)
              else Invalid(ARRAY_OF_LONG_NOT_FOUND)
            )
}

final private[value] case class IsArrayOfDecimal(nullable    : Boolean = false,
                                                 required    : Boolean = true,
                                                 elemNullable: Boolean = true
                                                ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result == Valid && value.toJsArray.seq.forall(v => v.isDecimal || (elemNullable && v.isNull)))
      Valid
    else
      Invalid(ARRAY_OF_DECIMAL_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfTestedDecimal(p           : BigDecimal => Result,
                                                       nullable    : Boolean = false,
                                                       required    : Boolean = true,
                                                       elemNullable: Boolean = true
                                                      ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else value.toJsArray.seq.map(value =>
                                           if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                           else if (!value.isDecimal) Invalid(DECIMAL_NOT_FOUND(value))
                                           else p(value.toJsBigDec.value)
                                         ).find(_ != Valid).getOrElse(Valid)
            )
}


final private[value] case class IsArrayOfDecimalSuchThat(p           : JsArray => Result,
                                                         nullable    : Boolean = false,
                                                         required    : Boolean = true,
                                                         elemNullable: Boolean = true
                                                        ) extends JsArrayOfDecimalPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isDecimal || (elemNullable && v.isNull))) p(arr.toJsArray)
            else Invalid(ARRAY_OF_DECIMAL_NOT_FOUND)
            )
}


final private[value] case class IsArrayOfNumber(nullable    : Boolean = false,
                                                required    : Boolean = true,
                                                elemNullable: Boolean = true
                                               ) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result == Valid && value.toJsArray.seq.forall(v => v.isNumber || (elemNullable && v.isNull))) Valid
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
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else value.toJsArray.seq.map(value =>
                                           if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                           else if (!value.isNumber) Invalid(NUMBER_NOT_FOUND(value))
                                           else p(value.toJsNumber)
                                         ).find(_ != Valid).getOrElse(Valid)
            )
}


final private[value] case class IsArrayOfNumberSuchThat(p           : JsArray => Result,
                                                        nullable    : Boolean = false,
                                                        required    : Boolean = true,
                                                        elemNullable: Boolean = true
                                                       ) extends JsArrayOfNumberPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isNumber || (elemNullable && v.isNull))) p(arr.toJsArray)
            else Invalid(ARRAY_OF_NUMBER_NOT_FOUND)
            )
}


final private[value] case class IsArrayOfIntegral(nullable    : Boolean = false,
                                                  required    : Boolean = true,
                                                  elemNullable: Boolean = true
                                                 ) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result == Valid && value.toJsArray.seq.forall(v => v.isIntegral || (elemNullable && v.isNull))) Valid
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
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else value.toJsArray.seq.map(value =>
                                           if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                           else if (!value.isIntegral) Invalid(INTEGRAL_NOT_FOUND(value))
                                           else p(value.toJsBigInt.value)
                                         ).find(_ != Valid).getOrElse(Valid)
            )
}


final private[value] case class IsArrayOfIntegralSuchThat(p           : JsArray => Result,
                                                          nullable    : Boolean = false,
                                                          required    : Boolean = true,
                                                          elemNullable: Boolean = true
                                                         ) extends JsArrayOfIntegralPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isIntegral || (elemNullable && v.isNull))) p(arr.toJsArray)
            else Invalid(ARRAY_OF_INTEGRAL_NOT_FOUND)
            )
}


final private[value] case class IsArrayOfBool(nullable    : Boolean = false,
                                              required    : Boolean = true,
                                              elemNullable: Boolean = true
                                             ) extends JsArrayOfBoolPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result == Valid && value.toJsArray.seq.forall(v => v.isBool || (elemNullable && v.isNull))) Valid
    else Invalid(ARRAY_OF_BOOLEAN_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfBoolSuchThat(p           : JsArray => Result,
                                                      nullable    : Boolean = false,
                                                      required    : Boolean = true,
                                                      elemNullable: Boolean = true
                                                     ) extends JsArrayOfBoolPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isBool || (elemNullable && v.isNull))) p(arr.toJsArray)
            else Invalid(ARRAY_OF_BOOLEAN_NOT_FOUND)
            )
}

final private[value] case class IsArrayOfObj(nullable    : Boolean = false,
                                             required    : Boolean = true,
                                             elemNullable: Boolean = true
                                            ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
  {
    val result = IsArray(nullable,
                         required
                         ).test(value)
    if (result == Valid &&
        value.toJsArray.seq.forall(v => v.isObj || (elemNullable && v.isNull))
    ) Valid
    else Invalid(ARRAY_OF_OBJECT_NOT_FOUND)
  }
}

final private[value] case class IsArrayOfObjSuchThat(p           : JsArray => Result,
                                                     nullable    : Boolean = false,
                                                     required    : Boolean = true,
                                                     elemNullable: Boolean = true,
                                                    ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else if (value.toJsArray.seq.forall(v => v.isObj || (elemNullable && v.isNull))) p(arr.toJsArray)
            else Invalid(ARRAY_OF_OBJECT_NOT_FOUND)
            )
}


final private[value] case class IsArrayOfTestedObj(p           : JsObj => Result,
                                                   nullable    : Boolean = false,
                                                   required    : Boolean = true,
                                                   elemNullable: Boolean = true
                                                  ) extends JsArrayOfObjectPredicate
{
  override def test(value: JsValue): Result =
    isValid(value,
            nullable,
            required,
            arr => if (!arr.isArr) Invalid(ARRAY_NOT_FOUND(value))
            else value.toJsArray.seq.map(value =>
                                           if (value.isNull) if (elemNullable) Valid else Invalid(NULL_FOUND)
                                           else if (!value.isObj) Invalid(OBJ_NOT_FOUND(value))
                                           else p(value.toJsObj)
                                         ).find(_ != Valid).getOrElse(Valid)
            )
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
  def validate(value: JsArray): LazyList[(JsPath, Invalid)] = JsArraySpec.apply0(JsPath.MINUS_ONE,
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

final private[value] case class IsArraySpec(spec: JsArraySpec,
                                            nullable: Boolean,
                                            required: Boolean
                                           ) extends Schema[JsArray]
{
  override def validate(arr: JsArray): LazyList[(JsPath, Invalid)] =
  {
    if (arr == null)
      return if (nullable) LazyList.empty else LazyList.empty.appended((JsPath.empty, Invalid(NULL_FOUND)))
    if (arr.isNothing)
      return if (!required) LazyList.empty else LazyList.empty.appended((JsPath.empty, Invalid(NOTHING_FOUND)))
    arr.validate(requireNonNull(spec))
  }
}


final private[value] case class IsObjSpec(spec: JsObjSpec,
                                          nullable: Boolean,
                                          required: Boolean
                                         ) extends Schema[JsObj]
{
  override def validate(obj: JsObj): LazyList[(JsPath, Invalid)] =
  {
    if (obj == null)
      return if (nullable) LazyList.empty
      else LazyList.empty.appended((JsPath.empty, Invalid(NULL_FOUND)))
    if (obj.isNothing)
      return if (!required) LazyList.empty
      else LazyList.empty.appended((JsPath.empty, Invalid(NOTHING_FOUND)))
    obj.validate(requireNonNull(spec))
  }
}

final private[value] case class ArrayOfObjSpec(spec        : JsObjSpec,
                                               nullable    : Boolean,
                                               required    : Boolean,
                                               elemNullable: Boolean
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
      if (head.isNull) if (elemNullable) result else result.appended((currentPath, Invalid(NULL_FOUND)))
      else if (!head.isObj) result.appended((currentPath, Invalid(OBJ_NOT_FOUND(head))))
      else apply(currentPath,
                 array.tail,
                 result.appendedAll(head.toJsObj.validate(spec))
                 )
    }

    if (array == null) return if (nullable) LazyList.empty else LazyList.empty.appended((JsPath.empty, Invalid(NULL_FOUND)))
    apply(JsPath.MINUS_ONE,
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
        apply0(map.updated(requireNonNull(head._1),
                           requireNonNull(head._2)
                           ),
               pairs.tail: _*
               )
      }
    }

    new JsObjSpec(apply0(immutable.HashMap.empty,
                         requireNonNull(pairs): _*
                         )
                  )
  }


  protected[value] def apply0(path  : JsPath,
                              result: LazyList[(JsPath, Invalid)],
                              specs : immutable.Map[SpecKey, JsSpec],
                              value : JsValue
                             ): LazyList[(JsPath, Invalid)] =
  {
    value match
    {
      case obj: JsObj =>

        if (specs.isEmpty) result

        else
        {
          val headSpec = specs.head

          headSpec._1 match
          {
            case * =>
              val keysWithSpec: Iterable[String] = specs.keys.filterNot(_ == *).map(_.name)
              val keysWithoutSpec = obj.map.removedAll(keysWithSpec)
              apply0(path,
                     if (keysWithoutSpec.nonEmpty) result.appended((path, Invalid(s"Keys without spec: $keysWithoutSpec"))) else result,
                     specs.tail,
                     obj
                     )

            case NamedKey(name) =>
              val headValue = obj(name)
              val headPath = path / name
              headSpec._2 match
              {
                case schema: Schema[_] => schema match
                {
                  case JsObjSpec(headSpecs) =>
                    apply0(path,
                           if (headValue.isNull) result :+ (headPath, Invalid(""))
                           else if (headValue.isNothing) result :+ (headPath, Invalid(""))
                           else result ++ apply0(headPath,
                                                 LazyList.empty,
                                                 headSpecs,
                                                 headValue
                                                 )
                           ,
                           specs.tail,
                           obj
                           )
                  case IsObjSpec(headSpecs,
                                 nullable,
                                 required
                  ) => apply0(path,
                              if (headValue.isNull) if (nullable) result else result :+ (headPath, Invalid(NULL_FOUND))
                              else if (headValue.isNothing) if (required) result :+ (headPath, Invalid(NOTHING_FOUND)) else result
                              else result ++ apply0(headPath,
                                                    LazyList.empty,
                                                    headSpecs.map,
                                                    headValue
                                                    ),
                              specs.tail,
                              obj
                              )
                  case IsArraySpec(arraySpec,
                                   nullable,
                                   required
                  ) => apply0(path,
                              if (headValue.isNull) if (nullable) result else result :+ (headPath, Invalid(NULL_FOUND))
                              else if (headValue.isNothing) if (required) result :+ (headPath, Invalid(NOTHING_FOUND)) else result
                              else result ++ JsArraySpec.apply0(headPath / -1,
                                                                LazyList.empty,
                                                                arraySpec.seq,
                                                                headValue
                                                                ),
                              specs.tail,
                              obj
                              )
                  case JsArraySpec(headSpecs) =>
                    apply0(path,
                           result ++ JsArraySpec.apply0(headPath / -1,
                                                        LazyList.empty,
                                                        headSpecs,
                                                        headValue
                                                        ),
                           specs.tail,
                           obj
                           )

                  case ArrayOfObjSpec(headSpecs,
                                      nullable,
                                      required,
                                      elemNullable
                  ) => apply0(path,
                              if (headValue.isNull) if (nullable) result else result :+ (headPath, Invalid(NULL_FOUND))
                              else if (headValue.isNothing) if (required) result :+ (headPath, Invalid(NOTHING_FOUND)) else result
                              else result ++ JsArraySpec.apply0(headPath / -1,
                                                                LazyList.empty,
                                                                headSpecs,
                                                                headValue,
                                                                elemNullable
                                                                ),
                              specs.tail,
                              obj
                              )
                }

                case p: JsPredicate => apply0(path,
                                              p.test(headValue) match
                                              {
                                                case Valid => result
                                                case error: Invalid => result.appended((headPath, error))
                                              },
                                              specs.tail,
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
           ): JsArraySpec = new JsArraySpec(requireNonNull(xs).prepended(requireNonNull(x)))

  @scala.annotation.tailrec
  protected[value] def apply0(path        : JsPath,
                              result      : LazyList[(JsPath, Invalid)],
                              spec        : JsObjSpec,
                              value       : JsValue,
                              elemNullable: Boolean
                             ): LazyList[(JsPath, Invalid)] =
  {
    val headPath = path.inc

    value match
    {
      case arr: JsArray =>
        if (arr.isEmpty) return result
        val head = arr.head
        head match
        {
          case obj: JsObj => apply0(headPath,
                                    result ++ JsObjSpec.apply0(headPath,
                                                               LazyList.empty,
                                                               spec.map,
                                                               obj
                                                               ),
                                    spec,
                                    arr.tail,
                                    elemNullable
                                    )
          case JsNull =>
            if (elemNullable) apply0(headPath,
                                     result,
                                     spec,
                                     arr.tail,
                                     elemNullable
                                     )
            else apply0(headPath,
                        result :+ (headPath, Invalid(NULL_FOUND)),
                        spec,
                        arr.tail,
                        elemNullable
                        )
          case _ => apply0(headPath,
                           result :+ (headPath, Invalid(s"JsObj object required. Received: $head")),
                           spec,
                           arr.tail,
                           elemNullable
                           )
        }
      case _ => result :+ (headPath, Invalid(s"JsArray object required. Received: $value"))

    }
  }

  protected[value] def apply0(path  : JsPath,
                              result: LazyList[(JsPath, Invalid)],
                              specs : Seq[JsSpec],
                              value : JsValue
                             ): LazyList[(JsPath, Invalid)] =
  {
    val headPath = path.inc
    value match
    {
      case arr: JsArray =>
        if (specs.isEmpty) result
        else
        {

          val headValue = arr(headPath.last)
          specs.head match
          {
            case schema: Schema[_] => schema match
            {
              case JsObjSpec(headSpecs) => apply0(headPath,
                                                  result ++ JsObjSpec.apply0(headPath,
                                                                             LazyList.empty,
                                                                             headSpecs,
                                                                             headValue
                                                                             ),
                                                  specs.tail,
                                                  arr
                                                  )
              case JsArraySpec(headSpecs) => apply0(headPath,
                                                    result ++ apply0(headPath / -1,
                                                                     LazyList.empty,
                                                                     headSpecs,
                                                                     headValue
                                                                     ),
                                                    specs.tail,
                                                    arr
                                                    )

              case IsArraySpec(arraySpec,
                               nullable,
                               required
              ) => apply0(path,
                          if (headValue.isNull) if (nullable) result else result :+ (headPath, Invalid(NULL_FOUND))
                          else if (headValue.isNothing) if (required) result :+ (headPath, Invalid(NOTHING_FOUND)) else result
                          else result ++ apply0(headPath / -1,
                                                LazyList.empty,
                                                arraySpec.seq,
                                                headValue
                                                ),
                          specs.tail,
                          arr
                          )
              case IsObjSpec(spec,
                             nullable,
                             required
              ) => apply0(headPath,
                {
                  if (headValue.isNull) if (nullable) result else result :+ (headPath, Invalid(NULL_FOUND))
                  else if (headValue.isNothing) if (required) result :+ (headPath, Invalid(NOTHING_FOUND)) else result
                  else result ++ JsObjSpec.apply0(headPath,
                                                  LazyList.empty,
                                                  spec.map,
                                                  headValue
                                                  )
                },
                          specs.tail,
                          arr
                          )
              case ArrayOfObjSpec(headSpecs,
                                  nullable,
                                  required,
                                  elemNullable
              ) => apply0(path,
                          if (headValue.isNull) if (nullable) result else result :+ (headPath, Invalid(NULL_FOUND))
                          else if (headValue.isNothing) if (required) result :+ (headPath, Invalid(NOTHING_FOUND)) else result
                          else result ++ JsArraySpec.apply0(headPath / -1,
                                                            LazyList.empty,
                                                            headSpecs,
                                                            headValue,
                                                            elemNullable
                                                            ),
                          specs.tail,
                          arr
                          )

            }
            case p: JsPredicate => apply0(headPath,
                                          p.test(headValue) match
                                          {
                                            case Valid => result
                                            case error: Invalid => result.appended((headPath, error))
                                          },
                                          specs.tail,
                                          arr
                                          )
          }
        }
      case _ => result :+ (headPath, Invalid(s"JsArray object required. Received: $value"))
    }
  }
}

private[value] object JsSpec
{

  private[value] def isValid(value   : JsValue,
                             nullable: Boolean,
                             required: Boolean,
                             p       : JsValue => Result
                            ): Result =
  {
    if (value.isNothing) if (!required) return Valid else return Invalid(NOTHING_FOUND)
    if (value.isNull) if (nullable) return Valid else return Invalid(NULL_FOUND)
    p(value)
  }


}