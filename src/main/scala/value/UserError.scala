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
  def asKeyOfIndex: UserError = UserError("asKey of Position of type Index."
                                          )

  /**
   * positions which are keys cant be converted into indexes
   *
   * @return an user error
   */
  def asIndexOfKey: UserError = UserError("asIndex of Position of type Key.")


  def mapKeyOfIndex: UserError = UserError("mapKey of Position of type Index.")

  def asJsLongOfJsStr: UserError = UserError("asJsLong of JsStr")

  def asJsNullOfJsStr: UserError = UserError("asJsNull of JsStr")

  def asJsIntOfJsStr: UserError = UserError("asJsInt of JsStr")

  def asJsBigIntOfJsStr: UserError = UserError("asJsBigInt of JsStr")

  def asJsBigDecOfJsStr: UserError = UserError("asJsBigDec of JsStr")

  def asJsBoolOfJsStr: UserError = UserError("asJsBoll of JsStr")

  def asJsObjOfJsStr: UserError = UserError("asJsObj of JsStr")

  def asJsDoubleOfJsStr: UserError = UserError("asJsDouble of JsStr")

  def asJsArrayOfJsStr: UserError = UserError("asJsArray of JsStr")

  def asJsNumberOfJsStr: UserError = UserError("asJsNumber of JsStr")

  def asJsonOfJsStr: UserError = UserError("asJson of JsStr")


  def asJsLongOfJsBool: UserError = UserError("asJsLong of JsBool")

  def asJsNullOfJsBool: UserError = UserError("asJsNull of JsBool")

  def asJsStrOfJsBool: UserError = UserError("asJsStr of JsBool")

  def asJsBigIntOfJsBool: UserError = UserError("asJsBigInt of JsBool")

  def asJsBigDecOfJsBool: UserError = UserError("asJsBigDec of JsBool")

  def asJsObjOfJsBool: UserError = UserError("asJsObj of JsBool")

  def asJsDoubleOfJsBool: UserError = UserError("asJsDouble of JsBool")

  def asJsArrayOfJsBool: UserError = UserError("asJsArray of JsBool")

  def asJsNumberOfJsBool: UserError = UserError("asJsNumber of JsBool")

  def asJsonOfJsBool: UserError = UserError("asJson of JsBool")

  def asJsIntOfJsBool: UserError = UserError("asJsInt of JsBool")

  def asJsLongOfJsNull: UserError = UserError("asJsLong of JsNull")

  def asJsBoolOfJsNull: UserError = UserError("asJsNull of JsNull")

  def asJsStrOfJsNull: UserError = UserError("asJsStr of JsNull")

  def asJsBigIntOfJsNull: UserError = UserError("asJsBigInt of JsNull")

  def asJsBigDecOfJsNull: UserError = UserError("asJsBigDec of JsNull")

  def asJsObjOfJsNull: UserError = UserError("asJsObj of JNull")

  def asJsDoubleOfJsNull: UserError = UserError("asJsDouble of JsNull")

  def asJsArrayOfJsNull: UserError = UserError("asJsArray of JsNull")

  def asJsNumberOfJsNull: UserError = UserError("asJsNumber of JsNull")

  def asJsonOfJsNull: UserError = UserError("asJson of JsNull")

  def asJsIntOfJsNull: UserError = UserError("asJsInt of JsNull")

  def asJsStrOfJsNumber: UserError = UserError("asJsStr of JsNumber")

  def asJsNullOfJsNumber: UserError = UserError("asJsNull of JsNumber")

  def asJsBoolOfJsNumber: UserError = UserError("asJsBool of JsNumber")

  def asJsObjOfJsNumber: UserError = UserError("asJsObj of JsNumber")

  def asJsArrayOfJsNumber: UserError = UserError("asJsArray of JsNumber")

  def asJsonOfJsNumber: UserError = UserError("asJson of JsNumber")

  def asJsLongOfJson: UserError = UserError("asJsLong of Json")

  def asJsNullOfJson: UserError = UserError("asJsNull of Json")

  def asJsIntOfJson: UserError = UserError("asJsInt of Json")

  def asJsBigIntOfJson: UserError = UserError("asJsBigInt of Json")

  def asJsBigDecOfJson: UserError = UserError("asJsBigDec of Json")

  def asJsBoolOfJson: UserError = UserError("asJsBool of Json")

  def asJsNumberOfJson: UserError = UserError("asJsNumber of Json")

  def asJsObjOfJsArray: UserError = UserError("asJsObj of JsArray")

  def asJsStrOfJson: UserError = UserError("asJsStr of Json")

  def asJsDoubleOfJson: UserError = UserError("asJsDouble of Json")

  def asJsArrayOfJsObj: UserError = UserError("asJsArray of JsObj")

  def asJsLongOfJsDouble: UserError = UserError("asJsLong of JsDouble")

  def asJsIntOfJsDouble: UserError = UserError("JsInt of JsDouble")

  def asJsBigIntOfJsDouble: UserError = UserError("asJsBigInt of JsDouble")

  def asJsIntOfJsLong: UserError = UserError("asJsInt of JsLong")

  def asJsLongOfJsBigDec: UserError = UserError("asJsLong of JsBigDec")

  def asJsIntOfJsBigDec: UserError = UserError("asJsInt of JsBigDec")

  def asJsBigIntOfJsBigDec: UserError = UserError("asJsBigInt of JsBigDec")

  def asJsDoubleOfJsBigDec: UserError = UserError("asJsDouble of JsBigDec")

  def asJsLongOfJsBigInt: UserError = UserError("asJsLong of JsBigInt")

  def asJsIntOfJsBigInt: UserError = UserError("asJsInt of JsBigInt")

  def asJsDoubleOfJsBigInt: UserError = UserError("asJsDouble of JsBigInt")

  def asJsLongOfJsNothing: UserError = UserError("asJsLong of JsNothing")

  def asJsNullOfJsNothing: UserError = UserError("asJsNull of JsNothing")

  def asJsStrOfJsNothing: UserError = UserError("asJsStr of JsNothing")

  def asJsIntOfJsNothing: UserError = UserError("asJsInt of JsNothing")

  def asJsBigIntOfJsNothing: UserError = UserError("asJsBigInt of JsNothing")

  def asJsBigDecOfJsNothing: UserError = UserError("asJsBigDec of JsNothing")

  def asJsBoolOfJsNothing: UserError = UserError("asJsBool of JsNothing")

  def asJsObjOfJsNothing: UserError = UserError("asJsObj of JsNothing")

  def asJsArrayOfJsNothing: UserError = UserError("asJsArray of JsNothing")

  def asJsDoubleOfJsNothing: UserError = UserError("asJsDouble of JsNothing")

  def asJsNumberOfJsNothing: UserError = UserError("asJsNumber of JsNothing")

  def asJsonOfJsNothing: UserError = UserError("asJson of JsNothing")

  def equalsOnJsSpec: UserError = UserError("JsSpecs cannot be tested for equality. They are made up of functions.")

  def asJsDoubleOfJsLong: UserError = UserError("asJsDouble of JsLong")

}
