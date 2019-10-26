package jsonvalues

sealed trait JsValidationResult

case class ValidationSuccess() extends JsValidationResult

case class ValidationFailure(errors: Seq[JsPairError]) extends JsValidationResult
