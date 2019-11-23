package value

private[value] case class UserError(code   : String,
                                    message: String
                                   ) extends UnsupportedOperationException(message)


object UserError
{


  def incOfEmptyPath: UserError = UserError("0000",
                                            "inc of an empty path"
                                            )

  def incOfKey(path: JsPath): UserError = UserError("0001",
                                                    s"inc of $path. Last position is not an index."
                                                    )

  def asKeyOfIndex: UserError = UserError("0002",
                                          s"asKey of Position of type Index."
                                          )

  def asIndexOfKey: UserError = UserError("0003",
                                          s"asIndex of Position of type Key."
                                          )

  def mapKeyOfIndex: UserError = UserError("0004",
                                           s"mapKey of Position of type Index."
                                           )

  def asJsLongOfJsStr: UserError = UserError("0005",
                                             s"asJsLong of JsStr"
                                             )

  def asJsNullOfJsStr: UserError = UserError("0006",
                                             s"asJsNull of JsStr"
                                             )

  def asJsIntOfJsStr: UserError = UserError("0007",
                                            s"asJsInt of JsStr"
                                            )

  def asJsBigIntOfJsStr: UserError = UserError("0008",
                                               s"asJsBigInt of JsStr"
                                               )

  def asJsBigDecOfJsStr: UserError = UserError("0009",
                                               s"asJsBigDec of JsStr"
                                               )

  def asJsBoolOfJsStr: UserError = UserError("0009",
                                             s"asJsBoll of JsStr"
                                             )

  def asJsObjOfJsStr: UserError = UserError("0010",
                                            s"asJsObj of JsStr"
                                            )

  def asJsDoubleOfJsStr: UserError = UserError("0011",
                                               s"asJsDouble of JsStr"
                                               )

  def asJsArrayOfJsStr: UserError = UserError("0012",
                                              s"asJsArray of JsStr"
                                              )

  def asJsNumberOfJsStr: UserError = UserError("0013",
                                               s"asJsNumber of JsStr"
                                               )

  def asJsonOfJsStr: UserError = UserError("0014",
                                           s"asJson of JsStr"
                                           )


  def asJsLongOfJsBool: UserError = UserError("0015",
                                              s"asJsLong of JsBool"
                                              )

  def asJsNullOfJsBool: UserError = UserError("0016",
                                              s"asJsNull of JsBool"
                                              )

  def asJsStrOfJsBool: UserError = UserError("00017",
                                             s"asJsStr of JsBool"
                                             )

  def asJsBigIntOfJsBool: UserError = UserError("0018",
                                                s"asJsBigInt of JsBool"
                                                )

  def asJsBigDecOfJsBool: UserError = UserError("0019",
                                                s"asJsBigDec of JsBool"
                                                )


  def asJsObjOfJsBool: UserError = UserError("0020",
                                             s"asJsObj of JsBool"
                                             )

  def asJsDoubleOfJsBool: UserError = UserError("0021",
                                                s"asJsDouble of JsBool"
                                                )

  def asJsArrayOfJsBool: UserError = UserError("0022",
                                               s"asJsArray of JsBool"
                                               )

  def asJsNumberOfJsBool: UserError = UserError("0023",
                                                s"asJsNumber of JsBool"
                                                )

  def asJsonOfJsBool: UserError = UserError("0024",
                                            s"asJson of JsBool"
                                            )

  def asJsIntOfJsBool: UserError = UserError("0025",
                                             s"asJsInt of JsBool"
                                             )


  def asJsLongOfJsNull: UserError = UserError("0026",
                                              s"asJsLong of JsNull"
                                              )

  def asJsBoolOfJsNull: UserError = UserError("0027",
                                              s"asJsNull of JsNull"
                                              )

  def asJsStrOfJsNull: UserError = UserError("00028",
                                             s"asJsStr of JsNull"
                                             )

  def asJsBigIntOfJsNull: UserError = UserError("0029",
                                                s"asJsBigInt of JsNull"
                                                )

  def asJsBigDecOfJsNull: UserError = UserError("0030",
                                                s"asJsBigDec of JsNull"
                                                )


  def asJsObjOfJsNull: UserError = UserError("0031",
                                             s"asJsObj of JNull"
                                             )

  def asJsDoubleOfJsNull: UserError = UserError("0032",
                                                s"asJsDouble of JsNull"
                                                )

  def asJsArrayOfJsNull: UserError = UserError("0033",
                                               s"asJsArray of JsNull"
                                               )

  def asJsNumberOfJsNull: UserError = UserError("0034",
                                                s"asJsNumber of JsNull"
                                                )

  def asJsonOfJsNull: UserError = UserError("0035",
                                            s"asJson of JsNull"
                                            )

  def asJsIntOfJsNull: UserError = UserError("0036",
                                             s"asJsInt of JsNull"
                                             )

  def asJsStrOfJsNumber: UserError = UserError("0037",
                                               s"asJsStr of JsNumber"
                                               )

  def asJsNullOfJsNumber: UserError = UserError("0038",
                                                s"asJsNull of JsNumber"
                                                )

  def asJsBoolOfJsNumber: UserError = UserError("0039",
                                                s"asJsBool of JsNumber"
                                                )

  def asJsObjOfJsNumber: UserError = UserError("0040",
                                               s"asJsObj of JsNumber"
                                               )

  def asJsArrayOfJsNumber: UserError = UserError("0041",
                                                 s"asJsArray of JsNumber"
                                                 )

  def asJsonOfJsNumber: UserError = UserError("0042",
                                              s"asJson of JsNumber"
                                              )

  def asJsLongOfJson: UserError = UserError("0043",
                                            s"asJsLong of Json"
                                            )

  def asJsNullOfJson: UserError = UserError("0044",
                                            s"asJsNull of Json"
                                            )

  def asJsIntOfJson: UserError = UserError("0045",
                                           s"asJsInt of Json"
                                           )

  def asJsBigIntOfJson: UserError = UserError("0046",
                                              s"asJsBigInt of Json"
                                              )

  def asJsBigDecOfJson: UserError = UserError("0047",
                                              s"asJsBigDec of Json"
                                              )

  def asJsBoolOfJson: UserError = UserError("0048",
                                            s"asJsBool of Json"
                                            )

  def asJsNumberOfJson: UserError = UserError("0049",
                                              s"asJsNumber of Json"
                                              )

  def asJsObjOfJsArray: UserError = UserError("0050",
                                              s"asJsObj of JsArray"
                                              )

  def asJsStrOfJson: UserError = UserError("0051",
                                           s"asJsStr of Json"
                                           )

  def asJsDoubleOfJson: UserError = UserError("0052",
                                              s"asJsDouble of Json"
                                              )

  def asJsArrayOfJsObj: UserError = UserError("0053",
                                              s"asJsArray of JsObj"
                                              )


  def asJsLongOfJsDouble: UserError = UserError("0054",
                                                s"asJsLong of JsDouble"
                                                )

  def asJsIntOfJsDouble: UserError = UserError("0055",
                                               s"JsInt of JsDouble"
                                               )

  def asJsBigIntOfJsDouble: UserError = UserError("0056",
                                                  s"asJsBigInt of JsDouble"
                                                  )

  def asJsIntOfJsLong: UserError = UserError("0057",
                                             s"asJsInt of JsLong"
                                             )

  def asJsLongOfJsBigDec: UserError = UserError("0058",
                                                s"asJsLong of JsBigDec"
                                                )

  def asJsIntOfJsBigDec: UserError = UserError("0059",
                                               s"asJsInt of JsBigDec"
                                               )

  def asJsBigIntOfJsBigDec: UserError = UserError("0060",
                                                  s"asJsBigInt of JsBigDec"
                                                  )

  def asJsDoubleOfJsBigDec: UserError = UserError("0061",
                                                  s"asJsDouble of JsBigDec"
                                                  )


  def asJsLongOfJsBigInt: UserError = UserError("0062",
                                                s"asJsLong of JsBigInt"
                                                )

  def asJsIntOfJsBigInt: UserError = UserError("0063",
                                               s"asJsInt of JsBigInt"
                                               )

  def asJsDoubleOfJsBigInt: UserError = UserError("0064",
                                                  s"asJsDouble of JsBigInt"
                                                  )

  def asJsLongOfJsNothing: UserError = UserError("0065",
                                                 s"asJsLong of JsNothing"
                                                 )

  def asJsNullOfJsNothing: UserError = UserError("0066",
                                                 s"asJsNull of JsNothing"
                                                 )

  def asJsStrOfJsNothing: UserError = UserError("0067",
                                                s"asJsStr of JsNothing"
                                                )

  def asJsIntOfJsNothing: UserError = UserError("0068",
                                                s"asJsInt of JsNothing"
                                                )

  def asJsBigIntOfJsNothing: UserError = UserError("0069",
                                                   s"asJsBigInt of JsNothing"
                                                   )

  def asJsBigDecOfJsNothing: UserError = UserError("0070",
                                                   s"asJsBigDec of JsNothing"
                                                   )

  def asJsBoolOfJsNothing: UserError = UserError("0071",
                                                 s"asJsBool of JsNothing"
                                                 )

  def asJsObjOfJsNothing: UserError = UserError("0072",
                                                s"asJsObj of JsNothing"
                                                )

  def asJsArrayOfJsNothing: UserError = UserError("0073",
                                                  s"asJsArray of JsNothing"
                                                  )

  def asJsDoubleOfJsNothing: UserError = UserError("0074",
                                                   s"asJsDouble of JsNothing"
                                                   )

  def asJsNumberOfJsNothing: UserError = UserError("0075",
                                                   s"asJsNumber of JsNothing"
                                                   )

  def asJsonOfJsNothing: UserError = UserError("0076",
                                               s"asJson of JsNothing"
                                               )


  def equalsOnJsSpec: UserError = UserError("0077",
                                            s"JsSpecs cannot be tested for equality. They are made up of functions."
                                            )

  def asJsDoubleOfJsLong: UserError = UserError("0078",
                                                s"asJsDouble of JsLong"
                                                )



}
