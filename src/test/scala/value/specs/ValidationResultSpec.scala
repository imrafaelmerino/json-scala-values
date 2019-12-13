package value.specs

import org.scalatest.FlatSpec
import value.spec.{Invalid, Valid}

class ValidationResultSpec extends FlatSpec
{



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
    assert(Invalid("error1").isInvalid(message=>message == "error1"))
  }

}
