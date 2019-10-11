package json.specifications

import json.{JsElemGens, JsPathGens}
import org.scalatest.PropSpec
import org.scalatest.prop.Checkers

private[specifications] class BasePropSpec extends PropSpec with Checkers
{
  val jsGen = JsElemGens()
  val jsPathGen = JsPathGens()
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 1000,
                                                                                                       workers = 1
                                                                                                       )
}
