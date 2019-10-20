package jsonvalues.specifications

import jsonvalues.{ImmutableJsGen, JsPathGens}
import org.scalatest.PropSpec
import org.scalatestplus.scalacheck.Checkers

private[specifications] class BasePropSpec extends PropSpec with Checkers
{
  val jsGen = ImmutableJsGen()
  val jsPathGen = JsPathGens()
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 10000,
                                                                                                       workers = 1
                                                                                                       )
}
