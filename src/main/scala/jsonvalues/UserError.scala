package jsonvalues

case class UserError(code:Int,message:String) extends Exception
{


}

object UserError {

  def incOfEmptyPath:UserError = UserError(1,"inc of empty path")
  def incOfKey(path: JsPath): UserError = UserError(2,s"inc of $path. Last pos is a key.")

}
