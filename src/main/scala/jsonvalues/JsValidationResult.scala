package jsonvalues

sealed trait ValidationResult
case class ValidationSuccess() extends ValidationResult
case class ValidationFailure(errors:Seq[(JsPath,String)]) extends ValidationResult
