package jsonvalues

import scala.collection.immutable

sealed trait JsValidator
{
  override def equals(that: Any): Boolean = throw new UnsupportedOperationException("Functions can't be tested for equality.")
}

final case class JsObjValidator(map: immutable.Map[String, JsValidator]) extends JsValidator
{
  def validate(value: JsObj): immutable.Seq[(JsPath, JsValueValidationResult)] =
  {
    JsObjValidator.apply0(JsPath.empty,
                          Vector.empty,
                          map,
                          value
                          )
  }
}

final case class JsObjValidator_?(map: immutable.Map[String, JsValidator]) extends JsValidator
{
  def validate(value: JsObj): Seq[(JsPath, JsValueValidationResult)] =
  {
    JsObjValidator_?.apply0(JsPath.empty,
                            Vector.empty,
                            map,
                            value
                            )
  }
}

final case class JsArrayValidator(seq: immutable.Seq[JsValidator]) extends JsValidator
{
  def validate(value: JsArray): immutable.Seq[(JsPath, JsValueError)] =
  {
    JsArrayValidator.apply0(JsPath.empty / -1,
                            Vector.empty,
                            seq,
                            value
                            )
  }
}

final case class JsArrayValidator_?(seq: immutable.Seq[JsValidator]) extends JsValidator
{
  def validate(value: JsArray): Seq[(JsPath, JsValueError)] =
  {
    JsArrayValidator_?.apply0(JsPath.empty / -1,
                              Vector.empty,
                              seq,
                              value
                              )
  }
}

final case class JsValueValidator(f: JsValue => JsValueValidationResult) extends JsValidator
{
  def ?(): JsValueValidator = JsValueValidator((value: JsValue) =>
                                               {
                                                 if (value.isNothing) JsValueOk else f.apply(value)
                                               }
                                               )

  def validate(array: JsArray): Seq[(JsPath, JsValueError)] =
  {

    f.apply(array) match
    {
      case JsValueOk => immutable.Vector.empty
      case errors: JsValueError => immutable.Vector((JsPath.empty, errors))
    }
  }

  def validate(obj  : JsObj): immutable.Seq[(JsPath, JsValueError)] =
  {

    f.apply(obj) match
    {
      case JsValueOk => immutable.Vector.empty
      case errors: JsValueError => immutable.Vector((JsPath.empty, errors))
    }
  }

}

object JsObjValidator_?
{
  def apply(pairs: (String, JsValidator)*): JsObjValidator_? =
  {

    @scala.annotation.tailrec
    def apply0(map: immutable.Map[String, JsValidator],
               pairs: (String, JsValidator)*
              ): immutable.Map[String, JsValidator] =
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

    new JsObjValidator_?(apply0(immutable.HashMap.empty,
                                pairs: _*
                                )
                         )
  }

  private[jsonvalues] def apply0(path: JsPath,
                                 result: immutable.Seq[(JsPath, JsValueError)],
                                 validations: immutable.Map[String, JsValidator],
                                 value: JsValue
                                ): immutable.Seq[(JsPath, JsValueError)] =
  {
    if (value.isNothing) Seq.empty else JsObjValidator.apply0(path,
                                                              result,
                                                              validations,
                                                              value
                                                              )
  }


}

object JsObjValidator
{

  def apply(pairs: (String, JsValidator)*): JsObjValidator =
  {
    @scala.annotation.tailrec
    def apply0(map  : immutable.Map[String, JsValidator],
               pairs: (String, JsValidator)*
              ): immutable.Map[String, JsValidator] =
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

    new JsObjValidator(apply0(immutable.HashMap.empty,
                              pairs: _*
                              )
                       )
  }


  private[jsonvalues] def apply0(path: JsPath,
                                 result     : immutable.Seq[(JsPath, JsValueError)],
                                 validations: immutable.Map[String, JsValidator],
                                 value      : JsValue
                                ): immutable.Seq[(JsPath, JsValueError)] =
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
            case JsObjValidator(headValidations) => apply0(path,
                                                           result ++ apply0(path / head._1,
                                                                            Vector.empty,
                                                                            headValidations,
                                                                            obj(head._1)
                                                                            ),
                                                           validations.tail,
                                                           obj
                                                           )
            case JsObjValidator_?(headValidations) => apply0(path,
                                                             result ++ JsObjValidator_?.apply0(path / head._1,
                                                                                               Vector.empty,
                                                                                               headValidations,
                                                                                               obj(head._1)
                                                                                               ),
                                                             validations.tail,
                                                             obj
                                                             )
            case JsArrayValidator(headValidations) => apply0(path,
                                                             result ++ JsArrayValidator.apply0(path / head._1 / -1,
                                                                                               Vector.empty,
                                                                                               headValidations,
                                                                                               obj(head._1)
                                                                                               ),
                                                             validations.tail,
                                                             obj
                                                             )
            case JsArrayValidator_?(headValidations) => apply0(path,
                                                               result ++ JsArrayValidator_?.apply0(path / head._1 / -1,
                                                                                                   Vector.empty,
                                                                                                   headValidations,
                                                                                                   obj(head._1)
                                                                                                   ),
                                                               validations.tail,
                                                               obj
                                                               )
            case JsValueValidator(predicate) =>
              val headResult = predicate(obj(head._1))
              apply0(path,
                     headResult match
                     {
                       case JsValueOk => result
                       case e: JsValueError => result :+ (path / head._1, e)
                     },
                     validations.tail,
                     obj
                     )


          }
        }
      case _ => result :+ (path, JsValueError(s"Json object required. Received: $value"))
    }
  }
}

object JsArrayValidator_?
{
  def apply(x : JsValidator,
            xs: JsValidator*
           ): JsArrayValidator_? = new JsArrayValidator_?(xs.prepended(x))

  private[jsonvalues] def apply0(path: JsPath,
                                 result: immutable.Seq[(JsPath, JsValueError)],
                                 validations: immutable.Seq[JsValidator],
                                 value      : JsValue
                                ): immutable.Seq[(JsPath, JsValueError)] =
  {
    if (value.isNothing) Seq.empty else JsArrayValidator.apply0(path,
                                                                result,
                                                                validations,
                                                                value
                                                                )
  }
}

object JsArrayValidator
{
  def apply(x : JsValidator,
            xs: JsValidator*
           ): JsArrayValidator = new JsArrayValidator(xs.prepended(x))

  private[jsonvalues] def apply0(path       : JsPath,
                                 result     : immutable.Seq[(JsPath, JsValueError)],
                                 validations: immutable.Seq[JsValidator],
                                 value      : JsValue
                                ): immutable.Seq[(JsPath, JsValueError)] =
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
            case JsObjValidator(headValidations) => apply0(headPath,
                                                           result ++ JsObjValidator.apply0(headPath,
                                                                                           Vector.empty,
                                                                                           headValidations,
                                                                                           arr(headPath.last)
                                                                                           ),
                                                           validations.tail,
                                                           arr
                                                           )
            case JsObjValidator_?(headValidations) => apply0(headPath,
                                                             result ++ JsObjValidator_?.apply0(headPath,
                                                                                               Vector.empty,
                                                                                               headValidations,
                                                                                               arr(headPath.last)
                                                                                               ),
                                                             validations.tail,
                                                             arr
                                                             )
            case JsArrayValidator(headValidations) => apply0(headPath,
                                                             result ++ apply0(headPath / -1,
                                                                              Vector.empty,
                                                                              headValidations,
                                                                              arr(headPath.last)
                                                                              ),
                                                             validations.tail,
                                                             arr
                                                             )
            case JsArrayValidator_?(headValidations) => apply0(headPath,
                                                               result ++ JsArrayValidator_?.apply0(headPath / -1,
                                                                                                   Vector.empty,
                                                                                                   headValidations,
                                                                                                   arr(headPath.last)
                                                                                                   ),
                                                               validations.tail,
                                                               arr
                                                               )
            case JsValueValidator(predicate) =>
              val headResult = predicate(arr(headPath.last))
              apply0(headPath,
                     headResult match
                     {
                       case JsValueOk => result
                       case e: JsValueError => result :+ (headPath, e)
                     },
                     validations.tail,
                     arr
                     )
          }
        }
      case _ => result :+ (path, JsValueError(s"JsArray object required. Received: $value"))
    }
  }
}





