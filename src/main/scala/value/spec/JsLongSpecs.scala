package value.spec

import Messages._
import value.Implicits._

//TODO Poner en  singletons los JsLongSpec, todos menos el generico que se crea un por funcion

object JsLongSpecs
{
  val long: JsSpec = IsLong()

  def long(minimum: Long,
           maximum   : Long,
           multipleOf: Long = 0
          ): JsSpec =
  {
    IsLongSuchThat((n: Long) =>
                   {
                     var errors: Seq[String] = Seq.empty
                     if (n < minimum)
                       errors = errors.appended(LONG_LOWER_THAN_MINIMUM(n,
                                                                        minimum
                                                                        )
                                                )
                     if (n > maximum)
                       errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(n,
                                                                          maximum
                                                                          )
                                                )
                     if (multipleOf != 0 && n % multipleOf != 0)
                       errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(n,
                                                                                  multipleOf
                                                                                  )
                                                )
                     if (errors.isEmpty) Valid
                     else Invalid(errors)
                   }

                   )

  }

  def longGT(exclusiveMinimum: Long,
             multipleOf      : Long = 0
            ): JsSpec =
  {
    IsLongSuchThat((n: Long) =>
                   {
                     var errors: Seq[String] = Seq.empty
                     if (n < exclusiveMinimum)
                       errors = errors.appended(LONG_LOWER_THAN_MINIMUM(n,
                                                                        exclusiveMinimum
                                                                        )
                                                )
                     if (multipleOf != 0 && n % multipleOf != 0)
                       errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(n,
                                                                                  multipleOf
                                                                                  )
                                                )

                     if (n == exclusiveMinimum)
                       errors = errors.appended(LONG_EQUAL_TO_EXCLUSIVE_MINIMUM(n,
                                                                                exclusiveMinimum
                                                                                )
                                                )
                     if (errors.isEmpty) Valid
                     else Invalid(errors)
                   }

                   )
  }

  def longGTE(minimum   : Long,
              multipleOf: Long = 0
             ): JsSpec =
  {
    IsLongSuchThat((n: Long) =>
                   {
                     var errors: Seq[String] = Seq.empty
                     if (n < minimum)
                       errors = errors.appended(LONG_LOWER_THAN_MINIMUM(n,
                                                                        minimum
                                                                        )
                                                )
                     if (multipleOf != 0 && n % multipleOf != 0)
                       errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(n,
                                                                                  multipleOf
                                                                                  )
                                                )
                     if (errors.isEmpty) Valid
                     else Invalid(errors)
                   }

                   )
  }

  def longLTE(maximum   : Long,
              multipleOf: Long = 0
             ): JsSpec =
  {
    IsLongSuchThat((n: Long) =>
                   {
                     var errors: Seq[String] = Seq.empty
                     if (n > maximum)
                       errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(n,
                                                                          maximum
                                                                          )
                                                )
                     if (multipleOf != 0 && n % multipleOf != 0)
                       errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(n,
                                                                                  multipleOf
                                                                                  )
                                                )
                     if (errors.isEmpty) Valid
                     else Invalid(errors)
                   }
                   )
  }

  def longLT(exclusiveMaximum: Long,
             multipleOf      : Long = 0
            ): JsSpec =
  {
    IsLongSuchThat((n: Long) =>
                   {
                     var errors: Seq[String] = Seq.empty
                     if (n > exclusiveMaximum)
                       errors = errors.appended(LONG_GREATER_THAN_MAXIMUM(n,
                                                                          exclusiveMaximum
                                                                          )
                                                )
                     if (multipleOf != 0 && n % multipleOf != 0)
                       errors = errors.appended(LONG_MULTIPLE_OF_NUMBER_NOT_FOUND(n,
                                                                                  multipleOf
                                                                                  )
                                                )

                     if (n == exclusiveMaximum)
                       errors.appended(LONG_EQUAL_TO_EXCLUSIVE_MAXIMUM(n,
                                                                       exclusiveMaximum
                                                                       )
                                       )

                     if (errors.isEmpty) Valid
                     else Invalid(errors)
                   }

                   )
  }

  def long(predicate: Long => Boolean,
           message  : Long => String
          ): JsSpec = IsLongSuchThat((n: Long) =>
                                       if (predicate.apply(n)) Valid else Invalid(message(n))
                                     )


}
