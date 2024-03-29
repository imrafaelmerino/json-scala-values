package json.value.spec

import json.value.JsValue
sealed trait Result

object Valid extends Result

final case class Invalid(value:JsValue, error: SpecError) extends Result
final case class SpecError(message:String)

object SpecError:
   val SPEC_FOR_VALUE_NOT_DEFINED = SpecError("SPEC_FOR_VALUE_NOT_DEFINED")
   val KEY_REQUIRED = SpecError("KEY_REQUIRED")
   val CONS_EXPECTED = SpecError("CONSTANT_EXPECTED")
   val ENUM_VAL_EXPECTED = SpecError("ENUMERATION_VALUE_EXPECTED")
   val INT_EXPECTED = SpecError("INT_EXPECTED")
   val LONG_EXPECTED = SpecError("LONG_EXPECTED")
   val DECIMAL_EXPECTED = SpecError("DECIMAL_EXPECTED")
   val BIG_INTEGER_EXPECTED = SpecError("BIG_INTEGER_EXPECTED")
   val NULL_EXPECTED = SpecError("NULL_EXPECTED")
   val STRING_EXPECTED = SpecError("STRING_EXPECTED")
   val BOOLEAN_EXPECTED = SpecError("BOOLEAN_EXPECTED")
   val OBJ_EXPECTED = SpecError("OBJ_EXPECTED")
   val ARRAY_EXPECTED = SpecError("ARRAY_EXPECTED")
   val VALUE_CONDITION_FAILED = SpecError("VALUE_CONDITION_FAILED")
   val INT_CONDITION_FAILED = SpecError("INT_CONDITION_FAILED")
   val LONG_CONDITION_FAILED = SpecError("LONG_CONDITION_FAILED")
   val DECIMAL_CONDITION_FAILED = SpecError("DECIMAL_CONDITION_FAILED")
   val KEY_CONDITION_FAILED = SpecError("KEY_CONDITION_FAILED")
   val BIG_INTEGER_CONDITION_FAILED = SpecError("BIG_INTEGER_CONDITION_FAILED")
   val STRING_CONDITION_FAILED = SpecError("STRING_CONDITION_FAILED")
   val OBJ_CONDITION_FAILED = SpecError("OBJ_CONDITION_FAILED")
   val ARRAY_CONDITION_FAILED = SpecError("ARRAY_CONDITION_FAILED")
   val INSTANT_CONDITION_FAILED = SpecError("INSTANT_CONDITION_FAILED")
   val INSTANT_EXPECTED = SpecError("INSTANT_EXPECTED")
   val ARRAY_LENGTH_LOWER_THAN_MIN: Int => SpecError = 
      min => SpecError(s"length must be bigger than $min")
   val ARRAY_LENGTH_BIGGER_THAN_MAX: Int => SpecError = 
      max => SpecError(s"length must be lower than $max")
