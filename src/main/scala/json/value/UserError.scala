package json.value

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
  val incOfEmptyPath = UserError("inc of an empty path.")

  /**
   * inc on paths is only possible when the last position is an index
   *
   * @return an user error
   */
  def incOfKey(path: JsPath) = UserError(s"inc of $path. Last position is not an index.")

  /**
   * positions which are indexes cant be converted into keys
   *
   * @return an user error
   */
  val toKeyOfIndex = UserError("asKey of Position of type Index.")

  /**
   * positions which are keys cant be converted into indexes
   *
   * @return an user error
   */
  val toIndexOfKey = UserError("asIndex of Position of type Key.")

  val mapKeyOfIndex = UserError("mapKey of Position of type Index.")

  val toJsLongOfJsStr = UserError("toJsLong of JsStr.")

  val toJsNullOfJsStr = UserError("toJsNull of JsStr.")

  val toJsIntOfJsStr = UserError("toJsInt of JsStr.")

  val toJsBigIntOfJsStr = UserError("toJsBigInt of JsStr.")

  val toJsBigDecOfJsStr = UserError("toJsBigDec of JsStr.")

  val toJsBoolOfJsStr = UserError("toJsBoll of JsStr.")

  val toJsObjOfJsStr = UserError("toJsObj of JsStr.")

  val toJsDoubleOfJsStr = UserError("toJsDouble of JsStr.")

  val toJsArrayOfJsStr = UserError("toJsArray of JsStr.")

  val toJsNumberOfJsStr = UserError("toJsNumber of JsStr.")

  val toJsonOfJsStr = UserError("toJson of JsStr.")

  val toJsLongOfJsBool = UserError("toJsLong of JsBool.")

  val toJsNullOfJsBool = UserError("toJsNull of JsBool.")

  val toJsStrOfJsBool = UserError("toJsStr of JsBool.")

  val toJsBigIntOfJsBool = UserError("toJsBigInt of JsBool.")

  val toJsBigDecOfJsBool = UserError("toJsBigDec of JsBool.")

  val toJsObjOfJsBool = UserError("toJsObj of JsBool.")

  val toJsDoubleOfJsBool = UserError("toJsDouble of JsBool.")

  val toJsArrayOfJsBool = UserError("toJsArray of JsBool.")

  val toJsNumberOfJsBool = UserError("toJsNumber of JsBool.")

  val toJsonOfJsBool = UserError("toJson of JsBool.")

  val toJsIntOfJsBool = UserError("toJsInt of JsBool.")

  val toJsLongOfJsNull = UserError("toJsLong of JsNull.")

  val toJsBoolOfJsNull = UserError("toJsNull of JsNull.")

  val toJsStrOfJsNull = UserError("toJsStr of JsNull.")

  val toJsBigIntOfJsNull = UserError("toJsBigInt of JsNull.")

  val toJsBigDecOfJsNull = UserError("toJsBigDec of JsNull.")

  val toJsObjOfJsNull = UserError("toJsObj of JNull.")

  val toJsDoubleOfJsNull = UserError("toJsDouble of JsNull.")

  val toJsArrayOfJsNull = UserError("toJsArray of JsNull.")

  val toJsNumberOfJsNull = UserError("toJsNumber of JsNull.")

  val toJsonOfJsNull = UserError("toJson of JsNull.")

  val toJsIntOfJsNull = UserError("toJsInt of JsNull.")

  val toJsStrOfJsNumber = UserError("toJsStr of JsNumber.")

  val toJsNullOfJsNumber = UserError("toJsNull of JsNumber.")

  val toJsBoolOfJsNumber = UserError("toJsBool of JsNumber.")

  val toJsObjOfJsNumber = UserError("toJsObj of JsNumber.")

  val toJsArrayOfJsNumber = UserError("toJsArray of JsNumber.")

  val toJsonOfJsNumber = UserError("toJson of JsNumber.")

  val toJsLongOfJson = UserError("toJsLong of Json.")

  val toJsNullOfJson = UserError("toJsNull of Json.")

  val toJsIntOfJson = UserError("toJsInt of Json.")

  val toJsBigIntOfJson = UserError("toJsBigInt of Json.")

  val toJsBigDecOfJson = UserError("toJsBigDec of Json.")

  val toJsBoolOfJson = UserError("toJsBool of Json.")

  val toJsNumberOfJson = UserError("toJsNumber of Json.")

  val toJsObjOfJsArray = UserError("toJsObj of JsArray.")

  val toJsStrOfJson = UserError("toJsStr of Json.")
  val toJsPrimitiveOfJson = UserError("toJsPrimitive of Json.")

  val toJsDoubleOfJson = UserError("toJsDouble of Json.")

  val toJsArrayOfJsObj = UserError("toJsArray of JsObj.")

  val toJsLongOfJsDouble = UserError("toJsLong of JsDouble.")

  val toJsIntOfJsDouble = UserError("JsInt of JsDouble.")

  val toJsBigIntOfJsDouble = UserError("toJsBigInt of JsDouble.")

  val toJsIntOfJsLong = UserError("toJsInt of JsLong.")

  val toJsLongOfJsBigDec = UserError("toJsLong of JsBigDec.")

  val toJsIntOfJsBigDec = UserError("toJsInt of JsBigDec.")

  val toJsBigIntOfJsBigDec = UserError("toJsBigInt of JsBigDec.")

  val toJsDoubleOfJsBigDec = UserError("toJsDouble of JsBigDec.")

  val toJsLongOfJsBigInt = UserError("toJsLong of JsBigInt.")

  val toJsIntOfJsBigInt = UserError("toJsInt of JsBigInt.")

  val toJsDoubleOfJsBigInt = UserError("toJsDouble of JsBigInt.")

  val toJsLongOfJsNothing = UserError("toJsLong of JsNothing.")

  val toJsNullOfJsNothing = UserError("toJsNull of JsNothing.")
  val toJsPrimitiveOfJsNothing = UserError("toJsPrimitive of JsNothing.")

  val toJsStrOfJsNothing = UserError("toJsStr of JsNothing.")

  val toJsIntOfJsNothing = UserError("toJsInt of JsNothing.")

  val toJsBigIntOfJsNothing = UserError("toJsBigInt of JsNothing.")

  val toJsBigDecOfJsNothing = UserError("toJsBigDec of JsNothing.")

  val toJsBoolOfJsNothing = UserError("toJsBool of JsNothing.")

  val toJsObjOfJsNothing = UserError("toJsObj of JsNothing.")

  val toJsArrayOfJsNothing = UserError("toJsArray of JsNothing.")

  val toJsDoubleOfJsNothing = UserError("toJsDouble of JsNothing.")

  val toJsNumberOfJsNothing = UserError("toJsNumber of JsNothing.")

  val toJsonOfJsNothing = UserError("toJson of JsNothing.")

  val equalsOnJsSpec = UserError("JsSpecs cannot be tested for equality. They are made up of functions.")


}