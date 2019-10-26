package jsonvalues

sealed trait JsPairValidationResult{}

case class JsPairOk() extends JsPairValidationResult

case class JsPairError(pair: (JsPath, JsValue),
                       message: String
                       ) extends JsPairValidationResult
