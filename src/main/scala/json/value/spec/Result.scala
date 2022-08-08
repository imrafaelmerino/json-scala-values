package json.value.spec

import json.value.JsValue
sealed trait Result

object Valid extends Result

final case class Invalid(value:JsValue,error: SpecError) extends Result

enum SpecError:
   case SPEC_FOR_VALUE_NOT_DEFINED extends SpecError
   case KEY_REQUIRED extends SpecError
   case INT_EXPECTED extends SpecError
   case LONG_EXPECTED extends SpecError
   case DECIMAL_EXPECTED extends SpecError
   case BIG_INTEGER_EXPECTED extends SpecError
   case NULL_EXPECTED extends SpecError
   case STRING_EXPECTED extends SpecError
   case BOOLEAN_EXPECTED extends SpecError
   case OBJ_EXPECTED extends SpecError
   case ARRAY_EXPECTED extends SpecError
   case VALUE_CONDITION_FAILED extends SpecError
   case INT_CONDITION_FAILED extends SpecError
   case LONG_CONDITION_FAILED extends SpecError
   case DECIMAL_CONDITION_FAILED extends SpecError
   case KEY_CONDITION_FAILED extends SpecError
   case BIG_INTEGER_CONDITION_FAILED extends SpecError
   case STRING_CONDITION_FAILED extends SpecError
   case OBJ_CONDITION_FAILED extends SpecError
   case ARRAY_CONDITION_FAILED extends SpecError