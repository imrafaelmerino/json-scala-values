package value.spec

import value.JsValue

private[value] object Messages
{
  val OBJECT_KEY_NOT_FOUND: String => String = (key: String) => s"The key $key doesn't exist"
  val OBJECT_NUMBER_KEYS_GREATER_THAN_MAXIMUM: (Int, Int) => String = (keys: Int, maximum: Int) => s"The number of keys $keys is greater than the maximum $maximum"
  val OBJECT_NUMBER_KEYS_LOWER_THAN_MINIMUM: (Int, Int) => String = (keys: Int, minimum: Int) => s"The number of keys $keys is lower than the minimum $minimum"
  val OBJECT_DEPENDANT_KEY_NOT_FOUND: (String, String) => String = (key: String, dependantKey: String) => s"The key $key exists but $dependantKey doesn't exist"
  val STRING_OF_LENGTH_LOWER_THAN_MINIMUM: (String, Int) => String = (string: String, minimum: Int) => s"'$string' of length lower than minimum $minimum"
  val STRING_OF_LENGTH_GREATER_THAN_MAXIMUM: (String, Int) => String = (string: String, maximum: Int) => s"'$string' of length greater than maximum $maximum"
  val STRING_DOESNT_MATCH_PATTERN: (String, String) => String = (string: String, pattern: String) => s"'$string' doesn't match the pattern $pattern"
  val STRING_NOT_IN_ENUM: (String, Seq[String]) => String = (constant: String, enum: Seq[String]) => s"'$constant' not in ${enum.mkString(",")}"
  val INT_ARRAY_OF_LENGTH_LOWER_THAN_MINIMUM: (Int, Int) => String = (length: Int, minimum: Int) => s"Array of length $length lower than minimum $minimum"
  val LONG_ARRAY_OF_LENGTH_LOWER_THAN_MINIMUM: (Long, Long) => String = (length: Long, minimum: Long) => s"Array of length $length lower than minimum $minimum"
  val INT_ARRAY_OF_LENGTH_GREATER_THAN_MAXIMUM: (Int, Int) => String = (length: Int, maximum: Int) => s"Array of length $length greater than maximum $maximum"
  val LONG_ARRAY_OF_LENGTH_GREATER_THAN_MAXIMUM: (Long, Long) => String = (length: Long, maximum: Long) => s"Array of length $length greater than maximum $maximum"
  val ARRAY_WITH_DUPLICATES = "Array contains duplicates"
  val ARRAY_WITH_NULL = "Array contains null"
  val ARRAY_OF_INT_NOT_FOUND = "Some element of the array is not an int number (32 bits)"
  val ARRAY_OF_LONG_NOT_FOUND = "Some element of the array is not a long number (64 bits)"
  val ARRAY_OF_STRING_NOT_FOUND = "Some element of the array is not a string"
  val ARRAY_OF_DECIMAL_NOT_FOUND = "Some element of the array is not a decimal number"
  val ARRAY_OF_INTEGRAL_NOT_FOUND = "Some element of the array is not an integral number"
  val ARRAY_OF_NUMBER_NOT_FOUND = "Some element of the array is not a number"
  val ARRAY_OF_JSOBJECT_NOT_FOUND = "Some element of the array is not a Json object"
  val ARRAY_OF_JS_ARRAY_NOT_FOUND = "Some element of the array is not a Json array"
  val ARRAY_OF_BOOLEAN_NOT_FOUND = "Some element of the array is not a boolean"
  val INT_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not an int number (32 bits)"
  val LONG_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not an long number (64 bits)"
  val STRING_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a string"
  val BOOLEAN_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a boolean"
  val OBJ_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a json object"
  val ARRAY_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a json array"
  val NUMBER_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a number"
  val DECIMAL_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a decimal number"
  val INTEGRAL_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a integral number"
  val NULL_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not null"
  val NOTHING_FOUND = "Some value is expected"
  val TRUE_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not true"
  val FALSE_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not false"
  val NULL_FOUND = "null not allowed"
  val INT_LOWER_THAN_MINIMUM: (Int, Int) => String = (value: Int, minimum: Int) => s"$value is lower than minimum $minimum"
  val INT_GREATER_THAN_MAXIMUM: (Int, Int) => String = (value: Int, maximum: Int) => s"$value is greater than maximum $maximum"
  val INT_LOWER_OR_EQUAL_THAN_EXCLUSIVE_MINIMUM: (JsValue, Int) => String = (value: JsValue, exclusiveMinimum: Int) => s"$value is lower or equal than exclusiveMinimum $exclusiveMinimum"
  val INT_EQUAL_TO_EXCLUSIVE_MINIMUM: (Int, Int) => String = (value: Int, exclusiveMinimum: Int) => s"$value is equal to the exclusiveMinimum $exclusiveMinimum"
  val INT_EQUAL_TO_EXCLUSIVE_MAXIMUM: (Int, Int) => String = (value: Int, exclusiveMaximum: Int) => s"$value is equal to the exclusiveMaximum $exclusiveMaximum"
  val INT_GREATER_OR_EQUAL_THAN_EXCLUSIVE_MAXIMUM: (JsValue, Int) => String = (value: JsValue, exclusiveMaximum: Int) => s"$value is greater or equal than exclusiveMaximum $exclusiveMaximum"
  val INT_NOT_MULTIPLE_OF_NUMBER: (Int, Int) => String = (value: Int, multiple: Int) => s"$value is not multiple of $multiple"
  val LONG_LOWER_THAN_MINIMUM: (Long, Long) => String = (value: Long, minimum: Long) => s"$value is lower than minimum $minimum"
  val LONG_GREATER_THAN_MAXIMUM: (Long, Long) => String = (value: Long, maximum: Long) => s"$value is greater than maximum $maximum"
  val LONG_EQUAL_TO_EXCLUSIVE_MINIMUM: (Long, Long) => String = (value: Long, exclusiveMinimum: Long) => s"$value is equal to the exclusiveMinimum $exclusiveMinimum"
  val LONG_EQUAL_TO_EXCLUSIVE_MAXIMUM: (Long, Long) => String = (value: Long, exclusiveMaximum: Long) => s"$value is equal to the exclusiveMaximum $exclusiveMaximum"
  val LONG_LOWER_OR_EQUAL_THAN_EXCLUSIVE_MINIMUM: (Long, Long) => String = (value: Long, exclusiveMinimum: Long) => s"$value is lower or equal than exclusiveMinimum $exclusiveMinimum"
  val LONG_GREATER_OR_EQUAL_THAN_EXCLUSIVE_MAXIMUM: (Long, Long) => String = (value: Long, exclusiveMaximum: Long) => s"$value is greater or equal than exclusiveMaximum $exclusiveMaximum"
  val LONG_MULTIPLE_OF_NUMBER_NOT_FOUND: (Long, Long) => String = (value: Long, multiple: Long) => s"$value is not multiple of $multiple"
  val DECIMAL_LOWER_THAN_MINIMUM: (BigDecimal, BigDecimal) => String = (value: BigDecimal, minimum: BigDecimal) => s"$value is lower than minimum $minimum"
  val DECIMAL_GREATER_THAN_MAXIMUM: (BigDecimal, BigDecimal) => String = (value: BigDecimal, maximum: BigDecimal) => s"$value is greater than maximum $maximum"
  val DECIMAL_LOWER_OR_EQUAL_THAN_EXCLUSIVE_MINIMUM: (BigDecimal, BigDecimal) => String = (value: BigDecimal, exclusiveMinimum: BigDecimal) => s"$value is lower or equal than exclusiveMinimum $exclusiveMinimum"
  val DECIMAL_EQUAL_TO_EXCLUSIVE_MINIMUM: (BigDecimal, BigDecimal) => String = (value: BigDecimal, exclusiveMinimum: BigDecimal) => s"$value is equal to the exclusiveMinimum $exclusiveMinimum"
  val DECIMAL_EQUAL_TO_EXCLUSIVE_MAXIMUM: (BigDecimal, BigDecimal) => String = (value: BigDecimal, exclusiveMaximum: BigDecimal) => s"$value is equal to the exclusiveMaximum $exclusiveMaximum"
  val DECIMAL_GREATER_OR_EQUAL_THAN_EXCLUSIVE_MAXIMUM: (BigDecimal, BigDecimal) => String = (value: BigDecimal, exclusiveMaximum: BigDecimal) => s"$value is greater or equal than exclusiveMaximum $exclusiveMaximum"
  val DECIMAL_NOT_MULTIPLE_OF: (BigDecimal, BigDecimal) => String = (value: BigDecimal, multiple: BigDecimal) => s"$value is not multiple of $multiple"
  val INTEGRAL_LOWER_THAN_MINIMUM: (BigInt, BigInt) => String = (value: BigInt, minimum: BigInt) => s"$value is lower than minimum $minimum"
  val INTEGRAL_EQUAL_TO_EXCLUSIVE_MINIMUM: (BigInt, BigInt) => String = (value: BigInt, exclusiveMinimum: BigInt) => s"$value is equal to the exclusiveMinimum $exclusiveMinimum"
  val INTEGRAL_EQUAL_TO_EXCLUSIVE_MAXIMUM: (BigInt, BigInt) => String = (value: BigInt, exclusiveMaximum: BigInt) => s"$value is equal to the exclusiveMaximum $exclusiveMaximum"
  val INTEGRAL_GREATER_THAN_MAXIMUM: (BigInt, BigInt) => String = (value: BigInt, maximum: BigInt) => s"$value is greater than maximum $maximum"
  val INTEGRAL_LOWER_OR_EQUAL_THAN_EXCLUSIVE_MINIMUM: (BigInt, BigInt) => String = (value: BigInt, exclusiveMinimum: BigInt) => s"$value is lower or equal than exclusiveMinimum $exclusiveMinimum"
  val INTEGRAL_GREATER_OR_EQUAL_THAN_EXCLUSIVE_MAXIMUM: (BigInt, BigInt) => String = (value: BigInt, exclusiveMaximum: BigInt) => s"$value is greater or equal than exclusiveMaximum $exclusiveMaximum"
  val INTEGRAL_NOT_MULTIPLE_OF: (BigInt, BigInt) => String = (value: BigInt, multiple: BigInt) => s"$value is not multiple of $multiple"
}
