package json.value

/**
 * represents an error made by the user of the library. If the user receives this error it means there is
 * a bug in their code.
 *
 * @param message the error message
 */
private[json] case class UserError(message: String) extends UnsupportedOperationException(message)


object UserError
{

  /**
   * inc on paths is only possible when the last position is an index
   *
   * @return an user error
   */
  val incOfEmptyPath: UserError = UserError("inc of an empty path.")

  /**
   * inc on paths is only possible when the last position is an index
   *
   * @return an user error
   */
  def incOfKey(path: JsPath): UserError = UserError(s"inc of $path. Last position is not an index.")

  
}