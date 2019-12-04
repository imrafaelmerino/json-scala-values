package value.spec

import Messages._
import value.Implicits._
//TODO Poner en  singletons los JsIntSpec, todos menos el generico que se crea un por funcion

object JsIntSpecs
{

  val int:JsSpec = int(nullable = false, optional = false)

  def int(nullable: Boolean ,
          optional: Boolean
         ): JsSpec = IsInt(nullable,
                           optional
                           )

  def int(minimum: Int,
          maximum   : Int,
          multipleOf: Int = 0,
          nullable  : Boolean = false,
          optional  : Boolean = false
         ): JsSpec =
  {
    IsIntSuchThat((n: Int) =>
                  {
                    var errors: Seq[String] = Seq.empty
                    if (n < minimum)
                      errors = errors.appended(INT_LOWER_THAN_MINIMUM(n,
                                                                      minimum
                                                                      )
                                               )
                    if (n > maximum)
                      errors = errors.appended(INT_GREATER_THAN_MAXIMUM(n,
                                                                        maximum
                                                                        )
                                               )
                    if (multipleOf != 0 && n % multipleOf != 0)
                      errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(n,
                                                                          multipleOf
                                                                          )
                                               )
                    if (errors.isEmpty) Valid
                    else Invalid(errors)
                  },
                  nullable,
                  optional
                  )


  }

  def intGT(exclusiveMinimum: Int,
            multipleOf      : Int = 0,
            nullable        : Boolean = false,
            optional        : Boolean = false
           ): JsSpec =
  {

    IsIntSuchThat((n: Int) =>
                  {
                    var errors: Seq[String] = Seq.empty
                    if (n < exclusiveMinimum)
                      errors = errors.appended(INT_LOWER_THAN_MINIMUM(n,
                                                                      exclusiveMinimum
                                                                      )
                                               )
                    if (multipleOf != 0 && n % multipleOf != 0)
                      errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(n,
                                                                          multipleOf
                                                                          )
                                               )

                    if (n == exclusiveMinimum)
                      errors.appended(INT_EQUAL_TO_EXCLUSIVE_MINIMUM(n,
                                                                     exclusiveMinimum
                                                                     )
                                      )
                    if (errors.isEmpty) Valid
                    else Invalid(errors)
                  },
                  nullable,
                  optional

                  )
  }

  def intGTE(minimum   : Int,
             multipleOf: Int = 0,
             nullable  : Boolean = false,
             optional  : Boolean = false
            ): JsSpec =
  {
    IsIntSuchThat((n: Int) =>
                  {

                    var errors: Seq[String] = Seq.empty
                    if (n < minimum)
                      errors = errors.appended(INT_LOWER_THAN_MINIMUM(n,
                                                                      minimum
                                                                      )
                                               )
                    if (multipleOf != 0 && n % multipleOf != 0)
                      errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(n,
                                                                          multipleOf
                                                                          )
                                               )
                    if (errors.isEmpty) Valid
                    else Invalid(errors)
                  },
                  nullable,
                  optional
                  )
  }

  def intLTE(maximum   : Int,
             multipleOf: Int = 0,
             nullable  : Boolean = false,
             optional  : Boolean = false
            ): JsSpec =
  {
    IsIntSuchThat((n: Int) =>
                  {
                    var errors: Seq[String] = Seq.empty
                    if (n > maximum)
                      errors = errors.appended(INT_GREATER_THAN_MAXIMUM(n,
                                                                        maximum
                                                                        )
                                               )
                    if (multipleOf != 0 && n % multipleOf != 0)
                      errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(n,
                                                                          multipleOf
                                                                          )
                                               )
                    if (errors.isEmpty) Valid
                    else Invalid(errors)
                  },
                  nullable,
                  optional
                  )
  }

  def intLT(exclusiveMaximum: Int,
            multipleOf: Int = 0,
            nullable        : Boolean = false,
            optional        : Boolean = false
           ): JsSpec =
  {
    IsIntSuchThat((n: Int) =>
                  {
                    var errors: Seq[String] = Seq.empty
                    if (n > exclusiveMaximum)
                      errors = errors.appended(INT_GREATER_THAN_MAXIMUM(n,
                                                                        exclusiveMaximum
                                                                        )
                                               )
                    if (multipleOf != 0 && n % multipleOf != 0)
                      errors = errors.appended(INT_NOT_MULTIPLE_OF_NUMBER(n,
                                                                          multipleOf
                                                                          )
                                               )
                    if (n == exclusiveMaximum)
                      errors = errors.appended(INT_EQUAL_TO_EXCLUSIVE_MAXIMUM(n,
                                                                              exclusiveMaximum
                                                                              )
                                               )
                    if (errors.isEmpty) Valid
                    else Invalid(errors)
                  },
                  nullable,
                  optional
                  )
  }

  def int(predicate: Int => Boolean,
          message  : Int => String
         ): JsSpec = IsIntSuchThat((n: Int) => if (predicate.apply(n)) Valid else Invalid(message(n)))


}
