package value.spec

import value.JsValue

/**
 * List of errors returned when a Json doesn't satisfy the predefined specs
 */
private[value] object ValidationMessages
  val STRING_NOT_IN_ENUM: (String, Seq[String]) => String = (constant: String, seq: Seq[String]) => s"'$constant' not in ${seq.mkString(",")}"
  val ARRAY_WITH_NULL:String = "Array contains null"
  val ARRAY_OF_INT_NOT_FOUND:String = "Some element of the array is not an int number (32 bits)"
  val ARRAY_OF_LONG_NOT_FOUND:String = "Some element of the array is not a long number (64 bits)"
  val ARRAY_OF_STRING_NOT_FOUND:String = "Some element of the array is not a string"
  val ARRAY_OF_DECIMAL_NOT_FOUND:String = "Some element of the array is not a decimal number"
  val ARRAY_OF_INTEGRAL_NOT_FOUND:String = "Some element of the array is not an integral number"
  val ARRAY_OF_NUMBER_NOT_FOUND:String = "Some element of the array is not a number"
  val ARRAY_OF_OBJECT_NOT_FOUND:String = "Some element of the array is not a Json object"
  val ARRAY_OF_JS_ARRAY_NOT_FOUND:String = "Some element of the array is not a Json array"
  val ARRAY_OF_BOOLEAN_NOT_FOUND:String = "Some element of the array is not a boolean"
  val INT_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not an int number (32 bits)"
  val LONG_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a long number (64 bits)"
  val STRING_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a string"
  val BOOLEAN_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a boolean"
  val OBJ_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a json object"
  val ARRAY_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a json array"
  val NUMBER_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a number"
  val DECIMAL_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not a decimal number"
  val INTEGRAL_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not an integral number"
  val NULL_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not null"
  val NOTHING_FOUND = "Some value expected"
  val TRUE_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not true"
  val FALSE_NOT_FOUND: JsValue => String = (value: JsValue) => s"$value is not false"
  val NULL_FOUND:String = "null not allowed"
  val NOTHING_EXPECTED:String = "No value was expected"
