package value

private[value] case class InternalError(code: String,
                                        message: String
                                       ) extends UnsupportedOperationException(message)
{

}

object InternalError
{
  def tokenNotFoundParsingStringIntoJsObj(token: String): InternalError = InternalError("0000",
                                                                                        s"Token $token not expected"
                                                                                        )


  def tokenNotFoundParsingStringIntoJsArray(token: String): InternalError = InternalError("0001",
                                                                                          s"Token $token not expected"
                                                                                          )

  def endArrayTokenExpected(): InternalError = InternalError("0002",
                                                             "End array token } expected, but it never took place."
                                                             )


}
