package json

case class UserError(code:String,message:String) extends UnsupportedOperationException(message)
{


}

object UserError {

  def incOfEmptyPath:UserError = UserError("0000","inc of empty path")
  def incOfKey(path: JsPath): UserError = UserError("0001",s"inc of $path. Last pos is a key.")

}
