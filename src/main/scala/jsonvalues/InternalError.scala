package jsonvalues

private[jsonvalues] case class InternalError(code   : String,
                                             message: String
                                      ) extends UnsupportedOperationException(message)
{

}

object InternalError
{
  def tokenNotFoundParsingStringIntJsObj(token: String): InternalError = InternalError("0000",
                                                                                       s"Token $token not expected"
                                                                                       )


  def tokenNotFoundParsingStringIntJsArray(token: String): InternalError = InternalError("0001",
                                                                                         s"Token $token not expected"
                                                                                         )

  def endArrayTokenExpected(): InternalError = InternalError("0002",
                                                             "End array token } expected, but it never took place."
                                                             )


}
