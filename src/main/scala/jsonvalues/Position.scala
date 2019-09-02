package jsonvalues

sealed trait Position
case class Key(name:String) extends Position
case class Index(i:Int)  extends Position

