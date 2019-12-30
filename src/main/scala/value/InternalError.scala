package value

/**
 * represents an internal error of the library. If this exception is thrown, it means there is an error
 * in the source code of the library and something need to be changed.
 *
 * @param code
 * @param message
 */
private[value] case class InternalError(code: String,
                                        message: String
                                       ) extends UnsupportedOperationException(message)
{

}


private[value] object InternalError
{
  /**
   * token not expected while parsing an input into a Json object
   *
   * @param token the unexpected token
   * @return an InternalError
   */
  def tokenNotFoundParsingStringIntoJsObj(token: String): InternalError = InternalError("0000",
                                                                                        s"Token $token not expected"
                                                                                        )


  /**
   * token not expected while parsing an input into a Json array
   *
   * @param token the unexpected token
   * @return an InternalError
   */
  def tokenNotFoundParsingStringIntoJsArray(token: String): InternalError = InternalError("0001",
                                                                                          s"Token $token not expected"
                                                                                          )

  /**
   * when parsing an input into a Json array and the } character is not found, an InternalError is thrown
   *
   * @return an InternalError
   */
  def endArrayTokenExpected(): InternalError = InternalError("0002",
                                                             "End array token } expected, but it never took place."
                                                             )

  /**
   * when a new JsValue is created without an id
   *
   * @return an InternalError
   */
  def jsonValueIdNotConsidered: InternalError = InternalError("0003",
                                                              "JsValue.id() not considered. Default branch of a switch statement was executed."
                                                              )

}
