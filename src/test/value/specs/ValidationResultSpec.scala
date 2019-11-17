package value.specs

import org.scalatest.FlatSpec
import value.spec.{Invalid, Valid}

class ValidationResultSpec extends FlatSpec
{

  "Invalid + Invalid" should "return an Invalid with all the messages in the correct order" in
  {

    val a = Invalid(Vector("error1",
                           "error2"
                           )
                    )
    val b = Invalid(Vector("error3",
                           "error4"
                           )
                    )

    assert(a + b == Invalid(Vector("error1",
                                   "error2",
                                   "error3",
                                   "error4"
                                   )
                            )
           )
  }

  "Invalid + Valid" should "return the Invalid" in
  {

    val a = Invalid(Vector("error1"))
    assert(a + Valid == a)
    assert(Valid + a == a)
  }

  "isValid" should "return true on Valid and false on any Invalid instance" in
  {
    assert(Valid.isValid)
    assert(!Invalid("error1").isValid)
  }

  "isInValid" should "return true on any Invalid instance and false on Invalid" in
  {
    assert(!Valid.isInvalid)
    assert(!Invalid("error1").isValid)
  }

  "Valid" should "be equals to Valid" in
  {
    assert(Valid == Valid)
  }

  "Valid" should "not be equals to any Invalid instance" in
  {
    assert(Invalid("error1") != Valid)
  }

  "isInvalid(predicate)" should "return true when the predicate is evaluated to true" in
  {
    assert(Invalid("error1").isInvalid((seq:Seq[String])=>seq.contains("error1")))
  }

}
