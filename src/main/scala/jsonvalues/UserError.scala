package jsonvalues

private[jsonvalues] case class UserError(code   : String,
                                         message: String
                                  ) extends UnsupportedOperationException(message)


object UserError
{

  def incOfEmptyPath: UserError = UserError("0000",
                                            "inc of an empty path"
                                            )

  def incOfKey(path: JsPath): UserError = UserError("0001",
                                                    s"inc of $path. Last position is not an index."
                                                    )

}
