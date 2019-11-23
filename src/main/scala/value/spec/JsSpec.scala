package value.spec

import value.Implicits._
import value.JsPath.empty
import value.spec.Messages.{NOTHING_FOUND, NULL_FOUND, NULL_NOT_FOUND}
import value.{JsArray, JsObj, JsPath, JsValue, UserError, spec}
import java.util.Objects.requireNonNull
import scala.collection.immutable

sealed trait JsSpec
{
  override def equals(that: Any): Boolean = throw UserError.equalsOnJsSpec
}

final case class JsObjSpec(map: immutable.Map[String, JsSpec]) extends JsSpec
{
  def validate(value: JsObj): immutable.Seq[(JsPath, Invalid)] = JsObjSpec.apply0(empty,
                                                                                  Vector.empty,
                                                                                  map,
                                                                                  requireNonNull(value)
                                                                                  )

  def ++(spec: JsObjSpec): JsObjSpec = JsObjSpec(map ++ requireNonNull(spec).map)

  def +(spec: (String, JsValueSpec)): JsObjSpec = JsObjSpec(map.updated(requireNonNull(spec)._1,
                                                                        spec._2
                                                                        )
                                                            )

  def -(name: String): JsObjSpec = JsObjSpec(map.removed(requireNonNull(name)))

  def ? = JsObjSpec_?(map)
}

final private[spec] case class JsObjSpec_?(map: immutable.Map[String, JsSpec]) extends JsSpec
{
  def validate(value: JsObj): Seq[(JsPath, Invalid)] = JsObjSpec_?.apply0(empty,
                                                                          Vector.empty,
                                                                          map,
                                                                          value
                                                                          )


}

final case class JsArraySpec(seq: immutable.Seq[JsSpec]) extends JsSpec
{
  def validate(value: JsArray): immutable.Seq[(JsPath, Invalid)] = JsArraySpec.apply0(-1,
                                                                                      Vector.empty,
                                                                                      seq,
                                                                                      requireNonNull(value)
                                                                                      )

  def ++(spec: JsArraySpec): JsArraySpec = JsArraySpec(seq ++ requireNonNull(spec).seq)

  @`inline` def :+(spec: JsValueSpec): JsArraySpec = appended(requireNonNull(spec))

  def appended(spec: JsValueSpec): JsArraySpec = JsArraySpec(seq.appended(requireNonNull(spec)))

  @`inline` def +:(spec: JsValueSpec): JsArraySpec = prepended(requireNonNull(spec))

  def prepended(spec: JsValueSpec): JsArraySpec = JsArraySpec(seq.prepended(requireNonNull(spec)))

  def ? = JsArraySpec_?(seq)


}

final private[spec] case class JsArraySpec_?(seq: immutable.Seq[JsSpec]) extends JsSpec
{
  def validate(value: JsArray): Seq[(JsPath, Invalid)] = JsArraySpec_?.apply0(empty / -1,
                                                                              Vector.empty,
                                                                              seq,
                                                                              requireNonNull(value)
                                                                              )
}

final case class JsValueSpec(f: JsValue => Result) extends JsSpec
{
  def ? = spec.JsValueSpec((value: JsValue) => if (value.isNothing) Valid else f.apply(value))

  def validate(array: JsArray): Seq[(JsPath, Invalid)] =
  {
    f.apply(requireNonNull(array)) match
    {
      case Valid => immutable.Vector.empty
      case errors: Invalid => immutable.Vector((empty, errors))
    }
  }

  def validate(obj: JsObj): immutable.Seq[(JsPath, Invalid)] =
  {

    f.apply(requireNonNull(obj)) match
    {
      case Valid => immutable.Vector.empty
      case errors: Invalid => immutable.Vector((empty, errors))
    }
  }
}

private[spec] object JsObjSpec_?
{

  def apply0(path: JsPath,
             result: immutable.Seq[(JsPath, Invalid)],
             validations: immutable.Map[String, JsSpec],
             value: JsValue
            ): immutable.Seq[(JsPath, Invalid)] =
  {
    if (value.isNothing) Seq.empty else JsObjSpec.apply0(path,
                                                         result,
                                                         validations,
                                                         value
                                                         )
  }


}

object JsObjSpec
{

  def apply(pairs: (String, JsSpec)*): JsObjSpec =
  {
    @scala.annotation.tailrec
    def apply0(map  : immutable.Map[String, JsSpec],
               pairs: (String, JsSpec)*
              ): immutable.Map[String, JsSpec] =
    {
      if (pairs.isEmpty) map
      else
      {
        val head = pairs.head
        apply0(map.updated(head._1,
                           head._2
                           ),
               pairs.tail: _*
               )
      }
    }

    new JsObjSpec(apply0(immutable.HashMap.empty,
                         requireNonNull(pairs): _*
                         )
                  )
  }


  protected[value] def apply0(path       : JsPath,
                              result     : immutable.Seq[(JsPath, Invalid)],
                              validations: immutable.Map[String, JsSpec],
                              value      : JsValue
                             ): immutable.Seq[(JsPath, Invalid)] =
  {

    value match
    {
      case obj: JsObj =>
        if (validations.isEmpty) result
        else
        {
          val head = validations.head
          head._2 match
          {
            case JsObjSpec(headValidations) => apply0(path,
                                                      result ++ apply0(path / head._1,
                                                                       Vector.empty,
                                                                       headValidations,
                                                                       obj(head._1)
                                                                       ),
                                                      validations.tail,
                                                      obj
                                                      )
            case JsObjSpec_?(headValidations) => apply0(path,
                                                        result ++ JsObjSpec_?.apply0(path / head._1,
                                                                                     Vector.empty,
                                                                                     headValidations,
                                                                                     obj(head._1)
                                                                                     ),
                                                        validations.tail,
                                                        obj
                                                        )
            case JsArraySpec(headValidations) => apply0(path,
                                                        result ++ JsArraySpec.apply0(path / head._1 / -1,
                                                                                     Vector.empty,
                                                                                     headValidations,
                                                                                     obj(head._1)
                                                                                     ),
                                                        validations.tail,
                                                        obj
                                                        )
            case JsArraySpec_?(headValidations) => apply0(path,
                                                          result ++ JsArraySpec_?.apply0(path / head._1 / -1,
                                                                                         Vector.empty,
                                                                                         headValidations,
                                                                                         obj(head._1)
                                                                                         ),
                                                          validations.tail,
                                                          obj
                                                          )
            case JsValueSpec(predicate) =>
              val headResult = predicate(obj(head._1))
              apply0(path,
                     headResult match
                     {
                       case Valid => result
                       case e: Invalid => result :+ (path / head._1, e)
                     },
                     validations.tail,
                     obj
                     )


          }
        }
      case _ => result :+ (path, Invalid(s"Json object required. Received: $value"))
    }
  }
}

private[value] object JsArraySpec_?
{

  def apply0(path: JsPath,
             result: immutable.Seq[(JsPath, Invalid)],
             validations: immutable.Seq[JsSpec],
             value: JsValue
            ): immutable.Seq[(JsPath, Invalid)] =
  {
    if (value.isNothing) Seq.empty else JsArraySpec.apply0(path,
                                                           result,
                                                           validations,
                                                           value
                                                           )
  }
}

object JsArraySpec
{
  def apply(x: JsSpec,
            xs: JsSpec*
           ): JsArraySpec = new JsArraySpec(requireNonNull(xs).prepended(requireNonNull(x)))

  protected[value] def apply0(path: JsPath,
                              result: immutable.Seq[(JsPath, Invalid)],
                              validations: immutable.Seq[JsSpec],
                              value: JsValue
                             ): immutable.Seq[(JsPath, Invalid)] =
  {

    value match
    {
      case arr: JsArray =>
        if (validations.isEmpty) result
        else
        {
          val headPath = path.inc
          validations.head match
          {
            case JsObjSpec(headValidations) => apply0(headPath,
                                                      result ++ JsObjSpec.apply0(headPath,
                                                                                 Vector.empty,
                                                                                 headValidations,
                                                                                 arr(headPath.last)
                                                                                 ),
                                                      validations.tail,
                                                      arr
                                                      )
            case JsObjSpec_?(headValidations) => apply0(headPath,
                                                        result ++ JsObjSpec_?.apply0(headPath,
                                                                                     Vector.empty,
                                                                                     headValidations,
                                                                                     arr(headPath.last)
                                                                                     ),
                                                        validations.tail,
                                                        arr
                                                        )
            case JsArraySpec(headValidations) => apply0(headPath,
                                                        result ++ apply0(headPath / -1,
                                                                         Vector.empty,
                                                                         headValidations,
                                                                         arr(headPath.last)
                                                                         ),
                                                        validations.tail,
                                                        arr
                                                        )
            case JsArraySpec_?(headValidations) => apply0(headPath,
                                                          result ++ JsArraySpec_?.apply0(headPath / -1,
                                                                                         Vector.empty,
                                                                                         headValidations,
                                                                                         arr(headPath.last)
                                                                                         ),
                                                          validations.tail,
                                                          arr
                                                          )
            case JsValueSpec(predicate) =>
              val headResult = predicate(arr(headPath.last))
              apply0(headPath,
                     headResult match
                     {
                       case Valid => result
                       case e: Invalid => result :+ (headPath, e)
                     },
                     validations.tail,
                     arr
                     )
          }
        }
      case _ => result :+ (path, Invalid(s"JsArray object required. Received: $value"))
    }
  }
}

object JsValueSpec
{

  val notNull: JsValueSpec = spec.JsValueSpec((value: JsValue) => if (value.isNull) Invalid(NULL_FOUND) else Valid)
  val `null`: JsValueSpec = spec.JsValueSpec((value: JsValue) => if (!value.isNull) Invalid(NULL_NOT_FOUND(value)) else Valid)
  val any: JsValueSpec = spec.JsValueSpec((value: JsValue) => if (!value.isNothing) Valid else Invalid(NOTHING_FOUND))


  def or(xs: JsValueSpec*): JsSpec =
  {
    @scala.annotation.tailrec
    def ||(result: JsValueSpec,
           xs: JsValueSpec*
          ): JsValueSpec =
    {
      if (xs.isEmpty) result
      else ||(JsValueSpec(value =>
                          {
                            val partial = result.f.apply(value)
                            partial match
                            {
                              case Valid => Valid
                              case e1: Invalid => xs.head.f.apply(value) match
                              {
                                case Valid => Valid
                                case e2: Invalid => e1 + e2
                              }
                            }

                          }
                          ),
              xs.tail: _*
              )
    }

    ||(requireNonNull(xs).head,
       xs.tail: _*
       )
  }


  def and(xs: JsValueSpec*): JsValueSpec =
  {


    @scala.annotation.tailrec
    def &&(result: JsValueSpec,
           xs: JsValueSpec*
          ): JsValueSpec =
    {
      if (xs.isEmpty) result
      else &&(JsValueSpec(value =>
                          {
                            result.f.apply(value) match
                            {
                              case Valid => xs.head.f.apply(value)
                              case error: Invalid => error
                            }

                          }
                          ),
              xs.tail: _*
              )
    }

    &&(requireNonNull(xs).head,
       xs.tail: _*
       )


  }

}



