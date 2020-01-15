package value

/**
 * represents an error made by the user of the library. If the user receives this error it means there is
 * a bug in their code.
 *
 * @param message the error message
 */
private[value] case class UserError(message: String
                                   ) extends UnsupportedOperationException(message)


object UserError
{


  /**
   * inc on paths is only possible when the last position is an index
   *
   * @return an user error
   */
  def incOfEmptyPath: UserError = UserError(
    "inc of an empty path"
    )

  /**
   * inc on paths is only possible when the last position is an index
   *
   * @return an user error
   */
  def incOfKey(path: JsPath): UserError = UserError(
    s"inc of $path. Last position is not an index."
    )

  /**
   * positions which are indexes cant be converted into keys
   *
   * @return an user error
   */
  def toKeyOfIndex: UserError = UserError("asKey of Position of type Index."
                                          )

  /**
   * positions which are keys cant be converted into indexes
   *
   * @return an user error
   */
  def toIndexOfKey: UserError = UserError("asIndex of Position of type Key.")

  def mapKeyOfIndex: UserError = UserError("mapKey of Position of type Index.")

  def toJsLongOfJsStr: UserError = UserError("toJsLong of JsStr")

  def toJsNullOfJsStr: UserError = UserError("toJsNull of JsStr")

  def toJsIntOfJsStr: UserError = UserError("toJsInt of JsStr")

  def toJsBigIntOfJsStr: UserError = UserError("toJsBigInt of JsStr")

  def toJsBigDecOfJsStr: UserError = UserError("toJsBigDec of JsStr")

  def toJsBoolOfJsStr: UserError = UserError("toJsBoll of JsStr")

  def toJsObjOfJsStr: UserError = UserError("toJsObj of JsStr")

  def toJsDoubleOfJsStr: UserError = UserError("toJsDouble of JsStr")

  def toJsArrayOfJsStr: UserError = UserError("toJsArray of JsStr")

  def toJsNumberOfJsStr: UserError = UserError("toJsNumber of JsStr")

  def toJsonOfJsStr: UserError = UserError("toJson of JsStr")
  
  def toJsLongOfJsBool: UserError = UserError("toJsLong of JsBool")

  def toJsNullOfJsBool: UserError = UserError("toJsNull of JsBool")

  def toJsStrOfJsBool: UserError = UserError("toJsStr of JsBool")

  def toJsBigIntOfJsBool: UserError = UserError("toJsBigInt of JsBool")

  def toJsBigDecOfJsBool: UserError = UserError("toJsBigDec of JsBool")

  def toJsObjOfJsBool: UserError = UserError("toJsObj of JsBool")

  def toJsDoubleOfJsBool: UserError = UserError("toJsDouble of JsBool")

  def toJsArrayOfJsBool: UserError = UserError("toJsArray of JsBool")

  def toJsNumberOfJsBool: UserError = UserError("toJsNumber of JsBool")

  def toJsonOfJsBool: UserError = UserError("toJson of JsBool")

  def toJsIntOfJsBool: UserError = UserError("toJsInt of JsBool")

  def toJsLongOfJsNull: UserError = UserError("toJsLong of JsNull")

  def toJsBoolOfJsNull: UserError = UserError("toJsNull of JsNull")

  def toJsStrOfJsNull: UserError = UserError("toJsStr of JsNull")

  def toJsBigIntOfJsNull: UserError = UserError("toJsBigInt of JsNull")

  def toJsBigDecOfJsNull: UserError = UserError("toJsBigDec of JsNull")

  def toJsObjOfJsNull: UserError = UserError("toJsObj of JNull")

  def toJsDoubleOfJsNull: UserError = UserError("toJsDouble of JsNull")

  def toJsArrayOfJsNull: UserError = UserError("toJsArray of JsNull")

  def toJsNumberOfJsNull: UserError = UserError("toJsNumber of JsNull")

  def toJsonOfJsNull: UserError = UserError("toJson of JsNull")

  def toJsIntOfJsNull: UserError = UserError("toJsInt of JsNull")

  def toJsStrOfJsNumber: UserError = UserError("toJsStr of JsNumber")

  def toJsNullOfJsNumber: UserError = UserError("toJsNull of JsNumber")

  def toJsBoolOfJsNumber: UserError = UserError("toJsBool of JsNumber")

  def toJsObjOfJsNumber: UserError = UserError("toJsObj of JsNumber")

  def toJsArrayOfJsNumber: UserError = UserError("toJsArray of JsNumber")

  def toJsonOfJsNumber: UserError = UserError("toJson of JsNumber")

  def toJsLongOfJson: UserError = UserError("toJsLong of Json")

  def toJsNullOfJson: UserError = UserError("toJsNull of Json")

  def toJsIntOfJson: UserError = UserError("toJsInt of Json")

  def toJsBigIntOfJson: UserError = UserError("toJsBigInt of Json")

  def toJsBigDecOfJson: UserError = UserError("toJsBigDec of Json")

  def toJsBoolOfJson: UserError = UserError("toJsBool of Json")

  def toJsNumberOfJson: UserError = UserError("toJsNumber of Json")

  def toJsObjOfJsArray: UserError = UserError("toJsObj of JsArray")

  def toJsStrOfJson: UserError = UserError("toJsStr of Json")

  def toJsDoubleOfJson: UserError = UserError("toJsDouble of Json")

  def toJsArrayOfJsObj: UserError = UserError("toJsArray of JsObj")

  def toJsLongOfJsDouble: UserError = UserError("toJsLong of JsDouble")

  def toJsIntOfJsDouble: UserError = UserError("JsInt of JsDouble")

  def toJsBigIntOfJsDouble: UserError = UserError("toJsBigInt of JsDouble")

  def toJsIntOfJsLong: UserError = UserError("toJsInt of JsLong")

  def toJsLongOfJsBigDec: UserError = UserError("toJsLong of JsBigDec")

  def toJsIntOfJsBigDec: UserError = UserError("toJsInt of JsBigDec")

  def toJsBigIntOfJsBigDec: UserError = UserError("toJsBigInt of JsBigDec")

  def toJsDoubleOfJsBigDec: UserError = UserError("toJsDouble of JsBigDec")

  def toJsLongOfJsBigInt: UserError = UserError("toJsLong of JsBigInt")

  def toJsIntOfJsBigInt: UserError = UserError("toJsInt of JsBigInt")

  def toJsDoubleOfJsBigInt: UserError = UserError("toJsDouble of JsBigInt")

  def toJsLongOfJsNothing: UserError = UserError("toJsLong of JsNothing")

  def toJsNullOfJsNothing: UserError = UserError("toJsNull of JsNothing")

  def toJsStrOfJsNothing: UserError = UserError("toJsStr of JsNothing")

  def toJsIntOfJsNothing: UserError = UserError("toJsInt of JsNothing")

  def toJsBigIntOfJsNothing: UserError = UserError("toJsBigInt of JsNothing")

  def toJsBigDecOfJsNothing: UserError = UserError("toJsBigDec of JsNothing")

  def toJsBoolOfJsNothing: UserError = UserError("toJsBool of JsNothing")

  def toJsObjOfJsNothing: UserError = UserError("toJsObj of JsNothing")

  def toJsArrayOfJsNothing: UserError = UserError("toJsArray of JsNothing")

  def toJsDoubleOfJsNothing: UserError = UserError("toJsDouble of JsNothing")

  def toJsNumberOfJsNothing: UserError = UserError("toJsNumber of JsNothing")

  def toJsonOfJsNothing: UserError = UserError("toJson of JsNothing")

  def equalsOnJsSpec: UserError = UserError("JsSpecs cannot be tested for equality. They are made up of functions.")


}
