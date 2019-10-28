package jsonvalues

sealed trait Spec
{

}

//case class All(seq: Seq[(String, JsContainerValidator)]) extends Spec
//
//case class Any(seq: Seq[(String, JsContainerValidator)]) extends Spec
//
//case class One(seq: Seq[(String, JsContainerValidator)]) extends Spec
//
//object Spec
//{
//  def all(xs: (String, JsContainerValidator)*): Spec = ???
//
//  def any(xs: (String, JsContainerValidator)*): Spec = ???
//
//  def one(xs: (String, JsContainerValidator)*): Spec = ???
//
//}
