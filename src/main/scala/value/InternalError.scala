package value

/**
 * represents an internal error of the library. If this exception is thrown, it means there is an error
 * in the source code of the library and something need to be changed.
 *
 * @param message
 */
private[value] case class InternalError(message: String
                                       ) extends UnsupportedOperationException(message)
{

}


private[value] object InternalError
{
  def nothingFound(): Throwable = InternalError("JsNothing is an element that can not be persisted. If found during iteration, it'is because of a development error.")

  def longWasExpected(message: String) = InternalError(message)

  def decimalWasExpected(message: String) = InternalError(message)

  def integralWasExpected(message: String) = InternalError(message)

  def stringWasExpected(message: String) = InternalError(message)

  def objWasExpected(message: String) = InternalError(message)

  /**
   * token not expected while parsing an input into a Json object
   *
   * @param token the unexpected token
   * @return an InternalError
   */
  def tokenNotFoundParsingStringIntoJsObj(token: String): InternalError = InternalError(
    s"Token $token not expected"
    )


  /**
   * token not expected while parsing an input into a Json array
   *
   * @param token the unexpected token
   * @return an InternalError
   */
  def tokenNotFoundParsingStringIntoJsArray(token: String): InternalError = InternalError(
    s"Token $token not expected"
    )

  /**
   * when parsing an input into a Json array and the } character is not found, an InternalError is thrown
   *
   * @return an InternalError
   */
  def endArrayTokenExpected(): InternalError = InternalError(
    "End array token } expected, but it never took place."
    )

  /**
   * when a new JsValue is created without an id
   *
   * @return an InternalError
   */
  def jsonValueIdNotConsidered: InternalError = InternalError(
    "JsValue.id() not considered. Default branch of a switch statement was executed."
    )

}
