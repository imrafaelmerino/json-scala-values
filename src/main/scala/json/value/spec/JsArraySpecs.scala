package json.value.spec
import java.util.Objects.requireNonNull
import json.value.{JsArray, JsNumber, JsObj, JsValue}

/**
 * Factory of specs to define values as Json arrays
 */
object JsArraySpecs
{

  /**
   * spec to specify that a json.value is an array
   */
  val array = IsArray(elemNullable = false)

  /**
   * returns a spec to specify that a json.value is an array
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def array(nullable    : Boolean = false,
            required    : Boolean = true,
            elemNullable: Boolean = false
           ) = IsArray(nullable,
                       required,
                       elemNullable
                       )

  /**
   * spec to specify that a json.value is an array of integer numbers
   */
  val arrayOfInt = IsArrayOfInt(elemNullable = false)


  /**
   * returns a spec to specify that a json.value is an array of integer numbers
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfInt(nullable    : Boolean = false,
                 required    : Boolean = true,
                 elemNullable: Boolean = false
                ) = IsArrayOfInt(nullable,
                                 required,
                                 elemNullable
                                 )

  /**
   * spec to specify that a json.value is an array of long numbers
   */
  val arrayOfLong = IsArrayOfLong(elemNullable = false)

  /**
   * returns a spec to specify that a json.value is an array of long numbers
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfLong(nullable    : Boolean = false,
                  required    : Boolean = true,
                  elemNullable: Boolean = false
                 ) = IsArrayOfLong(nullable,
                                   required,
                                   elemNullable
                                   )

  /**
   * spec to specify that a json.value is an array of decimal numbers
   */
  val arrayOfDecimal = IsArrayOfDecimal(elemNullable = false)

  /**
   * returns a spec to specify that a json.value is an array of decimal numbers
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfDecimal(nullable    : Boolean = false,
                     required    : Boolean = true,
                     elemNullable: Boolean = false
                    ) = IsArrayOfDecimal(nullable,
                                         required,
                                         elemNullable
                                         )

  /**
   * spec to specify that a json.value is an array of integral numbers
   */
  val arrayOfIntegral = IsArrayOfIntegral(elemNullable = false)

  /**
   * returns a spec to specify that a json.value is an array of integral numbers
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfIntegral(nullable    : Boolean = false,
                      required    : Boolean = true,
                      elemNullable: Boolean = false
                     ) = IsArrayOfIntegral(nullable,
                                           required,
                                           elemNullable
                                           )

  /**
   * spec to specify that a json.value is an array of booleans
   */
  val arrayOfBool = IsArrayOfBool(elemNullable = false)


  /**
   * returns a spec to specify that a json.value is an array of boolean numbers
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfBool(nullable    : Boolean = false,
                  required    : Boolean = true,
                  elemNullable: Boolean = false
                 ) = IsArrayOfBool(nullable,
                                   required,
                                   elemNullable
                                   )

  /**
   * spec to specify that a json.value is an array of numbers
   */
  val arrayOfNumber = IsArrayOfNumber(elemNullable = false)


  /**
   * returns a spec to specify that a json.value is an array of numbers
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfNumber(nullable    : Boolean = false,
                    required    : Boolean = true,
                    elemNullable: Boolean = false
                   ) = IsArrayOfNumber(nullable,
                                       required,
                                       elemNullable
                                       )

  /**
   * spec to specify that a json.value is an array of strings
   */
  val arrayOfStr = IsArrayOfStr(elemNullable = false)

  /**
   * returns a spec to specify that a json.value is an array of strings
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfStr(nullable    : Boolean = false,
                 required    : Boolean = true,
                 elemNullable: Boolean = false
                ) = IsArrayOfStr(nullable,
                                 required,
                                 elemNullable
                                 )

  /**
   * spec to specify that a json.value is an array of Json objects
   */
  val arrayOfObj = IsArrayOfObj(elemNullable = false)

  /**
   * returns a spec to specify that a json.value is an array of Json objects
   *
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfObj(nullable    : Boolean = false,
                 required    : Boolean = true,
                 elemNullable: Boolean = false
                ) = IsArrayOfObj(nullable,
                                 required,
                                 elemNullable
                                 )

  /**
   * returns a spec to specify that a json.value is a Json array that conforms a specified spec
   *
   * @param spec     the specified Json array spec
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def conforms(spec    : JsArraySpec,
               nullable: Boolean = false,
               required: Boolean = true
              ): JsSpec = IsArraySpec(requireNonNull(spec),
                                      nullable = nullable,
                                      required = required
                                      )

  /**
   * returns a spec to specify that a json.value is a Json array which elements are objects that
   * conforms a specified spec
   *
   * @param spec         the specified Json object spec
   * @param nullable     if true, null is allowed
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOf(spec        : JsObjSpec,
              nullable    : Boolean = false,
              required    : Boolean = true,
              elemNullable: Boolean = false
             ): ArrayOfObjSpec = ArrayOfObjSpec(requireNonNull(spec),
                                                nullable,
                                                required,
                                                elemNullable
                                                )

  /**
   * returns a spec to specify that a json.value is an array of Json objects that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfObjSuchThat(p           : JsArray => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ) = IsArrayOfObjSuchThat(requireNonNull(p),
                                                 nullable,
                                                 required,
                                                 elemNullable
                                                 )

  /**
   * returns a spec to specify that a json.value is an array of Json objects, where each element of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each Json object has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedObj(p           : JsObj => Result,
                       nullable    : Boolean = false,
                       required    : Boolean = true,
                       elemNullable: Boolean = false
                      ) = IsArrayOfTestedObj(requireNonNull(p),
                                             nullable,
                                             required,
                                             elemNullable
                                             )

  /**
   * returns a spec to specify that a json.value is an array of integer that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfIntSuchThat(p           : JsArray => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ): JsSpec = IsArrayOfIntSuchThat(requireNonNull(p),
                                                         nullable,
                                                         required,
                                                         elemNullable
                                                         )

  /**
   * returns a spec to specify that a json.value is an array of integers, where each number of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each integer has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedInt(p           : Int => Result,
                       nullable    : Boolean = false,
                       required    : Boolean = true,
                       elemNullable: Boolean = false
                      ): JsSpec = IsArrayOfTestedInt(requireNonNull(p),
                                                     nullable,
                                                     required,
                                                     elemNullable
                                                     )

  /**
   * returns a spec to specify that a json.value is an array of integral numbers that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfIntegralSuchThat(p           : JsArray => Result,
                              nullable    : Boolean = false,
                              required    : Boolean = true,
                              elemNullable: Boolean = false
                             ): JsSpec = IsArrayOfIntegralSuchThat(requireNonNull(p),
                                                                   nullable,
                                                                   required,
                                                                   elemNullable
                                                                   )

  /**
   * returns a spec to specify that a json.value is an array of integral numbers, where each number of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each integral number has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedIntegral(p           : BigInt => Result,
                            nullable    : Boolean = false,
                            required    : Boolean = true,
                            elemNullable: Boolean = false
                           ): JsSpec = IsArrayOfTestedIntegral(requireNonNull(p),
                                                               nullable,
                                                               required,
                                                               elemNullable
                                                               )

  /**
   * returns a spec to specify that a json.value is an array of booleans that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfBoolSuchThat(p           : JsArray => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfBoolSuchThat(requireNonNull(p),
                                                           nullable,
                                                           required,
                                                           elemNullable
                                                           )

  /**
   * returns a spec to specify that a json.value is an array of strings that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfStrSuchThat(p           : JsArray => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ): JsSpec = IsArrayOfStrSuchThat(requireNonNull(p),
                                                         nullable,
                                                         required,
                                                         elemNullable
                                                         )

  /**
   * returns a spec to specify that a json.value is an array of strings, where each string of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each string has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedStr(p           : String => Result,
                       nullable    : Boolean = false,
                       required    : Boolean = true,
                       elemNullable: Boolean = false
                      ): JsSpec = IsArrayOfTestedStr(requireNonNull(p),
                                                     nullable,
                                                     required,
                                                     elemNullable
                                                     )

  /**
   * returns a spec to specify that a json.value is an array of longs that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfLongSuchThat(p           : JsArray => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfLongSuchThat(requireNonNull(p),
                                                           nullable,
                                                           required,
                                                           elemNullable
                                                           )

  /**
   * returns a spec to specify that a json.value is an array of longs, where each number of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each long has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedLong(p           : Long => Result,
                        nullable    : Boolean = false,
                        required    : Boolean = true,
                        elemNullable: Boolean = false
                       ): JsSpec = IsArrayOfTestedLong(requireNonNull(p),
                                                       nullable,
                                                       required,
                                                       elemNullable
                                                       )

  /**
   * returns a spec to specify that a json.value is an array of decimals that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfDecimalSuchThat(p           : JsArray => Result,
                             nullable    : Boolean = false,
                             required    : Boolean = true,
                             elemNullable: Boolean = false
                            ): JsSpec = IsArrayOfDecimalSuchThat(requireNonNull(p),
                                                                 nullable,
                                                                 required,
                                                                 elemNullable
                                                                 )

  /**
   * returns a spec to specify that a json.value is an array of decimals, where each number of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each decimal has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedDecimal(p           : BigDecimal => Result,
                           nullable    : Boolean = false,
                           required    : Boolean = true,
                           elemNullable: Boolean = false
                          ): JsSpec = IsArrayOfTestedDecimal(requireNonNull(p),
                                                             nullable,
                                                             required,
                                                             elemNullable
                                                             )

  /**
   * returns a spec to specify that a json.value is an array of numbers that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfNumberSuchThat(p           : JsArray => Result,
                            nullable    : Boolean = false,
                            required    : Boolean = true,
                            elemNullable: Boolean = false
                           ): JsSpec = IsArrayOfNumberSuchThat(requireNonNull(p),
                                                               nullable,
                                                               required,
                                                               elemNullable
                                                               )

  /**
   * returns a spec to specify that a json.value is an array of numbers, where each number of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each number has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedNumber(p           : JsNumber => Result,
                          nullable    : Boolean = false,
                          required    : Boolean = true,
                          elemNullable: Boolean = false
                         ): JsSpec = IsArrayOfTestedNumber(requireNonNull(p),
                                                           nullable,
                                                           required,
                                                           elemNullable
                                                           )

  /**
   * returns a spec to specify that a json.value is an array, where each json.value of the
   * array satisfies a predicate
   *
   * @param p            the predicate on which each json.value has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arrayOfTestedValue(p           : JsValue => Result,
                         nullable    : Boolean = false,
                         required    : Boolean = true,
                         elemNullable: Boolean = false
                        ): JsSpec = IsArrayOfTestedValue(requireNonNull(p),
                                                         nullable,
                                                         required,
                                                         elemNullable
                                                         )


  /**
   * returns a spec to specify that a json.value is an array that satisfies a predicate
   *
   * @param p            the predicate on which the Json array has to be evaluated to true
   * @param nullable     if true, null is allowed and the predicate is not evaluated
   * @param required     if true, the json.value is mandatory
   * @param elemNullable if true, the array can contain null values
   * @return a spec
   */
  def arraySuchThat(p           : JsArray => Result,
                    nullable    : Boolean = false,
                    required    : Boolean = true,
                    elemNullable: Boolean = false
                   ): JsSpec = IsArrayOfValueSuchThat(requireNonNull(p),
                                                      nullable,
                                                      required,
                                                      elemNullable
                                                      )

}
