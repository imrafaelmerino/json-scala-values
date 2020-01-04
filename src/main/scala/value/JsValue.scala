package value

/**
 * Every element in a Json is a JsValue.
 */
trait JsValue
{

  /**
   * Every implementation of this trait has an unique identifier.
   *
   * @return unique identifier of the type
   */
  def id: Int

  /**
   * returns true if this type is a decimal number
   *
   * @return {{{ isDouble || isBigDec }}}
   */
  def isDecimal: Boolean = isDouble || isBigDec

  /**
   * returns true if this type is an integral number
   *
   * @return {{{ isInt || isLong || isBigInt }}}
   */
  def isIntegral: Boolean = isInt || isLong || isBigInt

  /**
   * returns true is this type is an array or an object
   *
   * @return
   */
  def isJson: Boolean = isObj || isArr

  /**
   * returns true if this is a json that satisfy a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isJson(predicate: Json[_] =>
    Boolean
            ): Boolean = isJson && predicate(asJson)

  /**
   * returns true if this is neither an object nor an array
   *
   * @return
   */
  def isNotJson: Boolean = !isJson

  /**
   * returns true if this is a string
   *
   * @return
   */
  def isStr: Boolean

  /**
   * returns true if this is a string that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isStr(predicate: String => Boolean): Boolean = isStr && predicate(asJsStr.value)

  /**
   * returns true if this is an object
   *
   * @return
   */
  def isObj: Boolean

  /**
   * returns true if this is an object that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isObj(predicate: JsObj => Boolean): Boolean = isObj && predicate(asJsObj)

  /**
   * returns true if this is an array
   *
   * @return
   */
  def isArr: Boolean

  /**
   * returns true if this is an array that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isArr(predicate: JsArray => Boolean): Boolean = isArr && predicate(asJsArray)

  /**
   * returns true if this is a boolean
   *
   * @return
   */
  def isBool: Boolean

  /**
   * returns true if this is a number
   *
   * @return
   */
  def isNumber: Boolean

  /**
   * returns true if this is not a number
   *
   * @return
   */
  def isNotNumber: Boolean = !isNumber

  /**
   * returns true if this is an integer (32 bit precision number)
   *
   * @return
   */
  def isInt: Boolean

  /**
   * returns true if this is an integer that satisfies a predicate
   *
   * @param predicate the predicate
   * @return
   */
  def isInt(predicate: Int => Boolean): Boolean = isInt && predicate(asJsInt.value)

  /**
   * returns true if this is a long (62 bit precision number)
   *
   * @return true if this is a long and false otherwise. If this is an integer, it returns false.
   */
  def isLong: Boolean

  /**
   * returns true if this is a long that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a long that satisfies the predicate and false otherwise.
   *         If this is an integer, it returns false.
   */
  def isLong(predicate: Long => Boolean): Boolean = isLong && predicate(asJsLong.value)

  /**
   * returns true if this is a double
   *
   * @return
   */
  def isDouble: Boolean

  /**
   * returns true if this is a double that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a double that satisfies the predicate
   */
  def isDouble(predicate: Double => Boolean): Boolean = isDouble && predicate(asJsDouble.value)

  /**
   * returns true if this is a big integer.
   *
   * @return true if this is a big integer and false otherwise. If this is either an integer or a long, it
   *         returns false.
   */
  def isBigInt: Boolean

  /**
   * returns true if this is a big integer that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a big integer that satisfies the predicate. If this is either an integer or a long, it
   *         returns false.
   */
  def isIntegral(predicate: BigInt => Boolean): Boolean = isBigInt && predicate(asJsBigInt.value)

  /**
   * returns true if this is a big decimal.
   *
   * @return true if this is a big decimal and false otherwise. If this is a double, it
   *         returns false.
   */
  def isBigDec: Boolean

  /**
   * returns true if this is a big decimal that satisfies a predicate
   *
   * @param predicate the predicate
   * @return true if this is a big decimal that satisfies the predicate. If this is a double, it returns false
   */
  def isDecimal(predicate: BigDecimal => Boolean): Boolean = isBigDec && predicate(asJsBigDec.value)


  /**
   * returns true if this is [[JsNull]]
   *
   * @return true if this is [[JsNull]], false otherwise
   */
  def isNull: Boolean

  /**
   * returns true if this is [[JsNothing]]
   *
   * @return true if this is [[JsNothing]], false otherwise
   */
  def isNothing: Boolean

  /**
   * returns this value as a [[JsLong]] if it is a [[JsLong]] or a [[JsInt]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  {{{ isInt || isLong }}} can help to that purpose.
   *
   * @return this value as a [[JsLong]]
   */
  @throws(classOf[value.UserError])
  def asJsLong: JsLong


  /**
   * returns this value as a [[JsInt]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isInt }}} can help to that purpose.
   *
   * @return this value as a [[JsInt]]
   */
  @throws(classOf[value.UserError])
  def asJsInt: JsInt

  /**
   * returns this value as a [[JsBigInt]] if it's an integral number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isIntegral }}} can help to that purpose.
   *
   * @return this value as a [[JsBigInt]]
   */
  @throws(classOf[value.UserError])
  def asJsBigInt: JsBigInt

  /**
   * returns this value as a [[JsBigDec]] if it's a decimal number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isDecimal }}} can help to that purpose.
   *
   * @return this value as a [[JsBigDec]]
   */
  @throws(classOf[value.UserError])
  def asJsBigDec: JsBigDec

  /**
   * returns this value as a [[JsBool]] if it's a boolean, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isBool }}} can help to that purpose.
   *
   * @return this value as a [[JsBool]]
   */
  @throws(classOf[value.UserError])
  def asJsBool: JsBool

  /**
   * returns this value as a [[JsNull]] if it's null, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isNull }}} can help to that purpose.
   *
   * @return this value as a [[JsNull]]
   */
  @throws(classOf[value.UserError])
  def asJsNull: JsNull.type

  /**
   * returns this value as a [[JsObj]] if it's an object, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isObj }}} can help to that purpose.
   *
   * @return this value as a [[JsObj]]
   */
  @throws(classOf[value.UserError])
  def asJsObj: JsObj

  /**
   * returns this value as a [[JsStr]] if it's a string, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isStr }}} can help to that purpose.
   *
   * @return this value as a [[JsStr]]
   */
  @throws(classOf[value.UserError])
  def asJsStr: JsStr


  /**
   * returns this value as a [[JsDouble]] if it is a [[JsLong]] or a [[JsInt]] or a [[JsDouble]], throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition  {{{ isInt || isLong || isDouble }}} can help to that purpose.
   *
   * @return this value as a [[JsDouble]]
   */
  @throws(classOf[value.UserError])
  def asJsDouble: JsDouble

  /**
   * returns this value as a [[JsArray]] if it's an array, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isArr }}} can help to that purpose.
   *
   * @return this value as a [[JsArray]]
   */
  @throws(classOf[value.UserError])
  def asJsArray: JsArray

  /**
   * returns this value as a [[JsNumber]] if it's a number, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the invocation to this function doesn't fail. The guard
   * condition {{{ isNumber }}} can help to that purpose.
   *
   * @return this value as a [[JsNumber]]
   *
   */
  @throws(classOf[value.UserError])
  def asJsNumber: JsNumber

  /**
   * returns this value as a [[Json]] if it's an object or an array, throwing an UserError otherwise.
   * It's the responsibility of the caller to make sure the call to this function doesn't fail. The guard
   * condition {{{ isJson }}} can help to that purpose.
   *
   * @return this value as a [[Json]]
   */
  @throws(classOf[value.UserError])
  def asJson: Json[_]

  /**
   * if this is [[JsNull]], it returns a value computed by the default supplier. Otherwise,
   * it returns the result of applying the map function to this.
   *
   * @param default the supplier to compute the default value
   * @param map     the map function
   * @tparam T the type of the returned value
   * @return a value of type T
   */
  def mapIfNotNullOrElse[T](default: () => T,
                            map: JsValue => T
                           ): T = if (isNull) default() else map(this)

  def mapIfStr(f: String => String): JsValue = if (isStr) JsStr(f(asJsStr.value)) else this

  def mapIfLong(f: Long => Long): JsValue = if (isLong || isInt) JsLong(f(asJsLong.value)) else this

  def mapIfInt(f: Int => Int): JsValue = if (isInt) JsInt(f(asJsInt.value)) else this


}
