package jsonvalues

sealed trait Position
final case class Key(name:String) extends Position
final case class Index(i:Int)  extends Position

