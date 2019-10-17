package json.specifications

import json.{ImmutableJsGen, JsPathGens}
import org.scalatest.PropSpec
import org.scalatest.prop.Checkers

private[specifications] class BasePropSpec extends PropSpec with Checkers
{
  val jsGen = ImmutableJsGen()
  val jsPathGen = JsPathGens()
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 10000,
                                                                                                       workers = 1
                                                                                                       )
}
