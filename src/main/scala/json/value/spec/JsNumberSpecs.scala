package json.value.spec

import java.util.Objects.requireNonNull

import json.value.JsNumber

/**
 * Factory of specs to define values as numbers
 */
object JsNumberSpecs
{

  /**
   * spec to specify that a json.value is an integral number
   */
  val integral: JsSpec = integral()

  /**
   * returns a spec to specify that a json.value is an integral number
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def integral(nullable: Boolean = false,
               required: Boolean = true
              ): JsSpec = IsIntegral(nullable,
                                     required
                                     )

  /**
   * returns a spec to specify that a json.value is an integral number that satisfies a predicate
   * @param p the predicate the integral number has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the json.value is mandatory
   * @return  a spec
   */
  def integralSuchThat(p: BigInt => Result,
                       nullable: Boolean = false,
                       required: Boolean = true,
                      ): JsSpec = IsIntegralSuchThat(requireNonNull(p),
                                                     nullable,
                                                     required
                                                     )

  /**
   * spec to specify that a json.value is a decimal number
   */
  val decimal: JsSpec = decimal()

  /**
   * returns a spec to specify that a json.value is a decimal number
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def decimal(nullable: Boolean = false,
              required: Boolean = true
             ): JsSpec = IsDecimal(nullable,
                                   required
                                   )

  /**
   * returns a spec to specify that a json.value is a decimal number that satisfies a predicate
   * @param p the predicate the decimal number has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the json.value is mandatory
   * @return  a spec
   */
  def decimalSuchThat(p: BigDecimal => Result,
                      nullable: Boolean = false,
                      required: Boolean = true,
                     ): JsSpec = IsDecimalSuchThat(requireNonNull(p),
                                                   nullable,
                                                   required
                                                   )

  /**
   * spec to specify that a json.value is a number
   */
  val number: JsSpec = number()

  /**
   * returns a spec to specify that a json.value is a  number
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def number(nullable: Boolean = false,
             required: Boolean = true
            ): JsSpec = IsNumber(nullable,
                                 required
                                 )

  /**
   * returns a spec to specify that a json.value is a number that satisfies a predicate
   * @param p the predicate the number has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the json.value is mandatory
   * @return  a spec
   */
  def numberSuchThat(p: JsNumber => Result,
                     nullable: Boolean = false,
                     required: Boolean = true
                    ): JsSpec = IsNumberSuchThat(requireNonNull(p),
                                                 nullable,
                                                 required
                                                 )
  /**
   * spec to specify that a json.value is an integer number (32 bits precision)
   */
  val int: JsSpec = IsInt()

  /**
   * returns a spec to specify that a json.value is an integer number (32 bits precision)
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def int(nullable: Boolean = false,
          required: Boolean = true
         ): JsSpec = IsInt(nullable,
                           required
                           )

  /**
   * returns a spec to specify that a json.value is an integer number (32 bits precision) that satisfies a predicate
   * @param p the predicate the number has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the json.value is mandatory
   * @return  a spec
   */
  def intSuchThat(p: Int => Result,
                  nullable: Boolean = false,
                  required: Boolean = true
                 ): JsSpec = IsIntSuchThat(requireNonNull(p),
                                           nullable = nullable,
                                           required = required
                                           )

  /**
   * spec to specify that a json.value is a long number (64 bits precision)
   */
  val long: JsSpec = IsLong()

  /**
   * returns a spec to specify that a json.value is a long number (64 bits precision)
   * @param nullable if true, null is allowed
   * @param required if true, the json.value is mandatory
   * @return a spec
   */
  def long(nullable: Boolean = false,
           required: Boolean = true
          ) = IsLong(nullable,
                     required
                     )

  /**
   * returns a spec to specify that a json.value is a long number (64 bits precision) that satisfies a predicate
   * @param p the predicate the number has to be evaluated to true
   * @param nullable if true, null is allowed and the predicate is not evaluated
   * @param required if true, the json.value is mandatory
   * @return  a spec
   */
  def longSuchThat(p: Long => Result,
                   nullable: Boolean = false,
                   required: Boolean = true
                  ): JsSpec = IsLongSuchThat(requireNonNull(p),
                                             nullable = nullable,
                                             required = required
                                             )
}
