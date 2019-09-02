package jsonvalues.specifications

import jsonvalues.{JsElemGens, JsPathGens}
import org.scalatest.PropSpec
import org.scalatest.prop.Checkers

class BasePropSpec extends PropSpec with Checkers
{

  val jsGen = JsElemGens()
  val jsPathGen = JsPathGens()

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 1000,
                                                                                                       workers = 1
                                                                                                       )
}
