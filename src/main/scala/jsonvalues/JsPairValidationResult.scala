package jsonvalues

sealed trait JsPairValidationResult{}

case class JsPairOk() extends JsPairValidationResult

case class JsPairError(message: String) extends JsPairValidationResult
